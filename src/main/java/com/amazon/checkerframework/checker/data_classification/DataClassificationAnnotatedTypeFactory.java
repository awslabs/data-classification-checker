// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.amazon.checkerframework.checker.data_classification;

import com.amazon.checkerframework.checker.data_classification.qual.AnyConfidentiality;
import com.amazon.checkerframework.checker.data_classification.qual.Confidential;
import com.amazon.checkerframework.checker.data_classification.qual.Critical;
import com.amazon.checkerframework.checker.data_classification.qual.HighlyConfidential;
import com.amazon.checkerframework.checker.data_classification.qual.NonConfidential;
import com.amazon.checkerframework.checker.data_classification.qual.NonCritical;
import com.amazon.checkerframework.checker.data_classification.qual.NonHighlyConfidential;
import com.amazon.checkerframework.checker.data_classification.qual.NonRestricted;
import com.amazon.checkerframework.checker.data_classification.qual.PolyClassification;
import com.amazon.checkerframework.checker.data_classification.qual.Public;
import com.amazon.checkerframework.checker.data_classification.qual.Restricted;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.poly.QualifierPolymorphism;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy.MultiGraphFactory;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.CollectionUtils;
import org.checkerframework.javacutil.ElementUtils;
import org.checkerframework.javacutil.TreeUtils;

/**
 * An AnnotatedTypeFactory for DCC. It is responsible for aliasing annotations, and for determining
 * the default qualifiers on classes from their members.
 */
public class DataClassificationAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    /** The canonical representations of the annotations supported by DCC. */
    private final AnnotationMirror critical = AnnotationBuilder.fromClass(elements, Critical.class),
            restricted = AnnotationBuilder.fromClass(elements, Restricted.class),
            highlyConfidential = AnnotationBuilder.fromClass(elements, HighlyConfidential.class),
            confidential = AnnotationBuilder.fromClass(elements, Confidential.class),
            publik = AnnotationBuilder.fromClass(elements, Public.class),
            poly = newPolyAnnotation(""),
            polyUse = newPolyAnnotation("use");

    /**
     * A boilerplate contructor. Follows the standard CF pattern. Also aliases annotations.
     *
     * @param checker the type checker instatiating this ATF
     */
    public DataClassificationAnnotatedTypeFactory(final BaseTypeChecker checker) {
        super(checker);
        addAliasedAnnotation(AnyConfidentiality.class, critical);
        addAliasedAnnotation(NonCritical.class, restricted);
        addAliasedAnnotation(NonRestricted.class, highlyConfidential);
        addAliasedAnnotation(NonHighlyConfidential.class, confidential);
        addAliasedAnnotation(NonConfidential.class, publik);
        this.postInit();
    }

    /**
     * Return the canonical version of the @Public annotation.
     *
     * <p>Intended for use with AnnotatedTypeMirror#getAnnotationInHierarchy, to avoid needing to
     * make the canonical fields above non-private.
     *
     * @return the canonical version of the @Public annotation
     */
    public AnnotationMirror getCanonicalPublicAnnotation() {
        return publik;
    }

    /** @return the canonical version of the @PolyClassification annotation. */
    public AnnotationMirror getPolyAnnotation() {
        return poly;
    }

    /** @return the canonical version of the @PolyClassification("use") annotation. */
    public AnnotationMirror getPolyUseAnnotation() {
        return polyUse;
    }

    /**
     * Creates an AnnotationMirror for {@code @PolyClassification} with {@code arg} as its value.
     *
     * @param arg the argument that will assigned to the value field of the created annotation
     * @return the created AnnotationMirror
     */
    private AnnotationMirror newPolyAnnotation(final String arg) {
        AnnotationBuilder builder = new AnnotationBuilder(processingEnv, PolyClassification.class);
        builder.setValue("value", arg);
        return builder.build();
    }

    @Override
    public QualifierPolymorphism createQualifierPolymorphism() {
        return new ClassificationPolymorphism(processingEnv, this);
    }

    /**
     * Need to explicitly state which qualifiers the checker actually supports because the default
     * search procedure finds the aliases as well, and issues an error because the aliases aren't
     * real annotations.
     */
    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        return new LinkedHashSet<>(
                Arrays.asList(
                        Critical.class,
                        Restricted.class,
                        HighlyConfidential.class,
                        Confidential.class,
                        Public.class,
                        PolyClassification.class));
    }

    /**
     * The qualifier hierarchy has to be overridden so that poly with arguments can have the right
     * subtyping relationship.
     */
    private class DataClassificationQualifierHierarchy extends MultiGraphQualifierHierarchy {

        /**
         * Default constructor.
         *
         * @param f the multigraph factory
         */
        protected DataClassificationQualifierHierarchy(final MultiGraphFactory f) {
            super(f);
        }

        @Override
        public boolean isSubtype(final AnnotationMirror subType, final AnnotationMirror superType) {
            // Rules:
            // 1. if the superType is top, always return true
            // 2. if both arguments are poly with args, return true iff the argument is equal
            // 3. if the superType is poly with args, return false if the subType is poly, and
            // otherwise
            //    treat the poly with args as a regular poly qual
            // 4. and vice-versa for the subType
            // 5. everything else use the standard rules
            if (isPolyWithArgs(superType) && isPolyWithArgs(subType)) {
                return AnnotationUtils.areSame(subType, superType);
            } else if (isPolyWithArgs(superType)) {
                if (AnnotationUtils.areSame(subType, poly)) {
                    return false;
                } else {
                    return super.isSubtype(subType, poly);
                }
            } else if (isPolyWithArgs(subType)) {
                if (AnnotationUtils.areSame(superType, poly)) {
                    return false;
                } else {
                    return super.isSubtype(poly, superType);
                }
            } else {
                return super.isSubtype(subType, superType);
            }
        }

        /**
         * Common check whether a1 is @PolyClassification("...") for any non-empty"...".
         *
         * @param a1 the annotation to check
         * @return true if so, false otherwise.
         */
        private boolean isPolyWithArgs(final AnnotationMirror a1) {
            if (a1 != null && AnnotationUtils.areSameByClass(a1, PolyClassification.class)) {
                String arg = AnnotationUtils.getElementValue(a1, "value", String.class, true);
                return !"".equals(arg);
            }
            return false;
        }
    }

    @Override
    public QualifierHierarchy createQualifierHierarchy(final MultiGraphFactory factory) {
        return new DataClassificationQualifierHierarchy(factory);
    }

    /**
     * It's necessary to cache these intermediate results for correctness - when looking up the type
     * of a member, the type factory usually checks the class' type (by calling the method
     * overridden below). So we cache the type that was actually written, and return it on those
     * calls; we then update the type to the inferred type and re-cache the new inferred type for
     * performance reasons.
     *
     * <p>This cache effectively replaces part of the element cache used by AnnotatedTypeFactory.
     */
    private final Map<Element, AnnotatedTypeMirror> classCache =
            CollectionUtils.createLRUCache(getCacheSize());

    /**
     * This method is called when determining the "user-written" type to assign to a program
     * element. The version in AnnotatedTypeFactory (which this overrides) also adds implicit
     * annotations, which is why this method has been overridden.
     *
     * <p>The implicit type of a class is the least upper bound of the return types of all of its
     * methods, the type of all of its fields, and the programmer-written annotation on the class
     * declaration.
     *
     * <p>This rule ensures that a "container" class that has access to sensitive data is itself
     * considered sensitive.
     */
    @Override
    public AnnotatedTypeMirror fromElement(final Element elt) {

        // Always prefer the classCache over recomputation.
        if (classCache.containsKey(elt)) {
            return classCache.get(elt);
        }

        // Use the tree so that we have access to members
        Tree decl = declarationFromElement(elt);
        if (decl != null && decl.getKind() == Tree.Kind.CLASS) {
            ClassTree tree = (ClassTree) decl;

            // Get the type that would have been resolved: the user-written class annotations, if
            // there are any.
            AnnotatedTypeMirror type = super.fromElement(elt);
            classCache.put(elt, type);

            // Use an annotation mirror throughout here because that's what
            // QualifierHierachy#leastUpperBound requires
            AnnotationMirror inferredClassLowerbound =
                    type.getAnnotationInHierarchy(getCanonicalPublicAnnotation());
            // If the class is unannotated, assume public
            if (inferredClassLowerbound == null) {
                inferredClassLowerbound = getCanonicalPublicAnnotation();
            }

            // For each member of the class that's a field or a method, update the inferred type
            // with either the type
            // of the field or the return type of the method.
            for (Tree member : tree.getMembers()) {
                switch (member.getKind()) {
                    case METHOD:
                        MethodTree methodTree = (MethodTree) member;
                        ExecutableElement execElem = TreeUtils.elementFromDeclaration(methodTree);
                        if (ElementUtils.isStatic(execElem)) {
                            break;
                        }
                        if (execElem.getReturnType() == null) {
                            break;
                        }

                        AnnotatedTypeMirror.AnnotatedExecutableType methodSignature =
                                super.fromElement(execElem);
                        AnnotationMirror returnAnno =
                                findLeastUpperBoundOfType(
                                        methodSignature.getReturnType(),
                                        getCanonicalPublicAnnotation());

                        if (returnAnno == null || AnnotationUtils.areSameByName(returnAnno, poly)) {
                            break;
                        }
                        inferredClassLowerbound =
                                getQualifierHierarchy()
                                        .leastUpperBound(inferredClassLowerbound, returnAnno);
                        break;
                    case VARIABLE:
                        Element fieldElt = TreeUtils.elementFromTree(member);
                        if (fieldElt == null) {
                            break;
                        }
                        if (ElementUtils.isStatic(fieldElt)) {
                            break;
                        }
                        AnnotationMirror fieldAnno =
                                findLeastUpperBoundOfType(
                                        super.fromElement(fieldElt),
                                        getCanonicalPublicAnnotation());

                        if (fieldAnno == null) {
                            break;
                        }
                        inferredClassLowerbound =
                                getQualifierHierarchy()
                                        .leastUpperBound(inferredClassLowerbound, fieldAnno);
                        break;
                    default:
                        break;
                }
            }
            // Replace the annotation in the type and return it after updating the cache.
            type.replaceAnnotation(inferredClassLowerbound);
            classCache.put(elt, type);
            return type;
        }

        return super.fromElement(elt);
    }

    /**
     * @param type the type to lub
     * @param canonicalHierarchyAnno an annotation in the hierarchy of interest
     * @return the least upper bound of the passed type and all its component types
     */
    @Nullable private AnnotationMirror findLeastUpperBoundOfType(
            final AnnotatedTypeMirror type, final AnnotationMirror canonicalHierarchyAnno) {
        if (type.getKind() == TypeKind.ARRAY) {
            AnnotatedTypeMirror.AnnotatedArrayType arType =
                    (AnnotatedTypeMirror.AnnotatedArrayType) type;
            AnnotationMirror arLub =
                    findLeastUpperBoundOfType(arType.getComponentType(), canonicalHierarchyAnno);
            AnnotationMirror anno = type.getAnnotationInHierarchy(canonicalHierarchyAnno);
            if (arLub == null) {
                return anno;
            } else if (anno == null) {
                return arLub;
            } else {
                return getQualifierHierarchy().leastUpperBound(anno, arLub);
            }
        } else if (type.getKind() == TypeKind.DECLARED) {
            AnnotatedTypeMirror.AnnotatedDeclaredType declaredType =
                    (AnnotatedTypeMirror.AnnotatedDeclaredType) type;
            AnnotationMirror result = type.getAnnotationInHierarchy(canonicalHierarchyAnno);
            List<AnnotatedTypeMirror> typeVars = declaredType.getTypeArguments();
            for (AnnotatedTypeMirror var : typeVars) {
                AnnotationMirror varLub = findLeastUpperBoundOfType(var, canonicalHierarchyAnno);
                if (varLub != null) {
                    if (result == null) {
                        result = varLub;
                    } else {
                        result = getQualifierHierarchy().leastUpperBound(result, varLub);
                    }
                }
            }
            return result;
        } else {
            return type.getAnnotationInHierarchy(canonicalHierarchyAnno);
        }
    }
}
