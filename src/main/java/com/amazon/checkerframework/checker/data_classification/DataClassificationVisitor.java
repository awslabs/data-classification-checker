// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.amazon.checkerframework.checker.data_classification;

import com.amazon.checkerframework.checker.data_classification.qual.Critical;
import com.amazon.checkerframework.checker.data_classification.qual.Public;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeParameterBounds;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.ElementUtils;
import org.checkerframework.javacutil.TreeUtils;
import org.checkerframework.javacutil.TypesUtils;

/**
 * A visitor class is responsible for issuing errors. DCC extends the default visitor to change the
 * defaults on how exception parameters are typechecked.
 */
public class DataClassificationVisitor
        extends BaseTypeVisitor<DataClassificationAnnotatedTypeFactory> {

    /**
     * A default constructor. Just calls the default visitor's constructor.
     *
     * @param checker the checker implementation itself. Contains hooks into the ATF, etc.
     */
    public DataClassificationVisitor(final BaseTypeChecker checker) {
        super(checker);
    }

    /**
     * Overrides the default lower bound for exception parameters. By default, this is top
     * (@Critical). This prevents DCC from defaulting exception parameters to @Public. Overriding
     * this method changes the expected lower bound to be @Public, permitting that default.
     *
     * @return a singleton set containing the @Public annotation
     */
    @Override
    protected Set<? extends AnnotationMirror> getExceptionParameterLowerBoundAnnotations() {
        return Collections.singleton(AnnotationBuilder.fromClass(elements, Public.class));
    }

    /**
     * Overrides the default upper bound for thrown exceptions. By default, this is the same as the
     * result of getExceptionParameterLowerBoundAnnotations, which defaults to top. This is
     * overridden to keep the default top, since that other method is also overridden.
     *
     * @return a singleton set containing the @Critical annotation
     */
    @Override
    protected Set<? extends AnnotationMirror> getThrowUpperBoundAnnotations() {
        return Collections.singleton(AnnotationBuilder.fromClass(elements, Critical.class));
    }

    /**
     * This is a bit of a hack. This method controls whether the {@link
     * org.checkerframework.common.basetype.BaseTypeValidator} actually issues type.invalid errors,
     * and is typically guarded by a subtyping check. I disabled it here because the framework
     * doesn't provide a way to specify defaults on type declarations.
     *
     * @see <a
     *     href="https://groups.google.com/forum/#!msg/checker-framework-dev/vk2V6ZFKPLk/v3hENw-e7gsJ">
     *     this discussion</a>
     * @param declarationType the declared type
     * @param useType the used type
     * @param tree the context
     * @return always true - all type uses are valid to DCC
     */
    @Override
    public boolean isValidUse(
            final AnnotatedTypeMirror.AnnotatedDeclaredType declarationType,
            final AnnotatedTypeMirror.AnnotatedDeclaredType useType,
            final Tree tree) {
        return true;
    }

    /**
     * Relaxes the common assignment check in two ways:
     *
     * <ol>
     *   <li>If the varType is @Poly and the valueType is some non-poly type, replace the varType
     *       with the class bound from the underlying type.
     *   <li>Similarly, if the valueType is @Poly and the varType is some non-poly type, replace the
     *       valueType with the class bound from the underlying type.
     * </ol>
     */
    @Override
    protected void commonAssignmentCheck(
            final AnnotatedTypeMirror varType,
            final AnnotatedTypeMirror valueType,
            final Tree valueTree,
            @CompilerMessageKey final String errorKey) {
        if (isPolyWithNoArgs(varType) && isNotPolyIgnoringValues(valueType)) {
            replacePoly(varType, valueTree);
        } else if (isNotPolyIgnoringValues(varType) && isPolyWithNoArgs(valueType)) {
            replacePoly(valueType, valueTree);
        }
        super.commonAssignmentCheck(varType, valueType, valueTree, errorKey);
    }

    /**
     * Replaces the polymorphic annotation in atm with the least upper bound of the annotations
     * associated with the underlying classes, and any other resolvable polymorphic annotations in
     * scope.
     *
     * @param atm an annotated type mirror with a polymorphic annotation in DCC's type system which
     *     is a candidate for replacement
     * @param localTree a tree in the same scope as atm; used to determine the enclosing method
     */
    private void replacePoly(final AnnotatedTypeMirror atm, final Tree localTree) {
        List<TypeMirror> underlyingTypes = new ArrayList<>();

        // Collect the types associated with the parameters and receiver of the enclosing method;
        // mirrors polymorphic resolution. Ignore @poly("use") by using isPolyWithNoArgs(), which
        // only looks for @PolyClassification with no arguments.
        MethodTree enclosingMethod = TreeUtils.enclosingMethod(atypeFactory.getPath(localTree));
        if (enclosingMethod != null) {
            ExecutableElement execElem = TreeUtils.elementFromDeclaration(enclosingMethod);
            AnnotatedTypeMirror.AnnotatedExecutableType methodSignature =
                    atypeFactory.fromElement(execElem);
            for (AnnotatedTypeMirror param : methodSignature.getParameterTypes()) {
                if (isPolyWithNoArgs(param)) {
                    underlyingTypes.add(param.getUnderlyingType());
                }
            }
            if (methodSignature.getReceiverType() != null) {
                if (isPolyWithNoArgs(methodSignature.getReceiverType())) {
                    underlyingTypes.add(methodSignature.getReceiverType().getUnderlyingType());
                }
            }
        }

        // Always include the underlying type of the input ATM. For fields and other non-method code
        // (or
        // code in methods with no polymorphic arguments), this will be the only type used for
        // resolution.
        underlyingTypes.add(atm.getUnderlyingType());

        // The annotation that will replace the polymorphic annotation in the computation.
        AnnotationMirror finalClassAnnotation = atypeFactory.getCanonicalPublicAnnotation();

        for (TypeMirror underlyingType : underlyingTypes) {
            // For each type, first get the underlying class as an element.
            Element underlyingClassElem = TypesUtils.getTypeElement(underlyingType);
            if (underlyingClassElem != null) {
                // And then use the ATF to produce the type the user wrote on the class - and those
                // inferred by DataClassificationTypeFactory#fromElement.
                AnnotatedTypeMirror classDefaultType =
                        atypeFactory.fromElement(underlyingClassElem);
                if (classDefaultType != null) {
                    // If the class does have a default type (all should...), try to read an DCC
                    // annotation from it.
                    AnnotationMirror classDefaultAnnotation =
                            classDefaultType.getAnnotationInHierarchy(
                                    atypeFactory.getCanonicalPublicAnnotation());
                    if (classDefaultAnnotation != null) {
                        // If there was an DCC annotation, LUB it with whatever else has been found
                        // so far.
                        finalClassAnnotation =
                                atypeFactory
                                        .getQualifierHierarchy()
                                        .leastUpperBound(
                                                finalClassAnnotation, classDefaultAnnotation);
                    }
                }
            }
        }
        // Take the final LUB and then replace the original polymorphic annotation with it.
        atm.replaceAnnotation(finalClassAnnotation);
    }

    /**
     * Checks whether the given annotation contains an DCC @PolyClassification annotation. Note that
     * this only returns true if the annotation is @PolyClassification with no arguments.
     *
     * @param atm the type mirror to check
     * @return whether atm contains @PolyClassification
     */
    private boolean isPolyWithNoArgs(@Nullable final AnnotatedTypeMirror atm) {
        if (atm == null) {
            return false;
        }
        AnnotationMirror anm = atm.getAnnotationInHierarchy(atypeFactory.getCanonicalPublicAnnotation());
        if (anm == null) {
            return false;
        }
        return AnnotationUtils.areSame(anm, atypeFactory.getPolyAnnotation());
    }

    /**
     * Checks whether the given annotation is NOT @PolyClassification. This is NOT the inverse of
     * isPolyWithNoArgs, because they both return false on an @PolyClassification annotation with an
     * argument. They also both return false when null is passed.
     *
     * @param atm the type mirror to check
     * @return whether atm does not contains @PolyClassification
     */
    private boolean isNotPolyIgnoringValues(@Nullable final AnnotatedTypeMirror atm) {
        if (atm == null) {
            return false;
        }
        AnnotationMirror anm =
                atm.getAnnotationInHierarchy(atypeFactory.getCanonicalPublicAnnotation());
        return !AnnotationUtils.areSameByName(anm, atypeFactory.getPolyAnnotation());
    }

    /**
     * Searches through a type for user-written (i.e. non-Public) annotations. Returns true if any
     * are found.
     *
     * @param atm the type to search
     * @return true if the type or any of its components has a non-public annotation
     */
    private boolean hasNonPublic(final AnnotatedTypeMirror atm) {
        boolean result = false;
        switch (atm.getKind()) {
            case DECLARED:
                AnnotatedTypeMirror.AnnotatedDeclaredType atmD =
                        (AnnotatedTypeMirror.AnnotatedDeclaredType) atm;
                for (AnnotatedTypeMirror component : atmD.getTypeArguments()) {
                    if (hasNonPublic(component)) {
                        result = true;
                    }
                }
                break;
            case ARRAY:
                AnnotatedTypeMirror.AnnotatedArrayType atmA =
                        (AnnotatedTypeMirror.AnnotatedArrayType) atm;
                result = hasNonPublic(atmA.getComponentType());
                break;
            case WILDCARD:
                return false;
            case TYPEVAR:
                return false;
            default:
                break;
        }
        return result || !atm.hasAnnotation(Public.class);
    }

    /**
     * DCC defaults implicit upper bounds of type variables to @Public. This is the correct thing to
     * do in unannotated code, but causes false positive errors whenever a user writes a type
     * annotation on the upper bound of a type variable (for instance, by declaring a list of highly
     * confidential strings). Since this is common, this code disables that check for user-written
     * annotations.
     */
    @Override
    protected void checkTypeArguments(
            final Tree toptree,
            final List<? extends AnnotatedTypeParameterBounds> paramBounds,
            final List<? extends AnnotatedTypeMirror> typeargs,
            final List<? extends Tree> typeargTrees) {
        List<AnnotatedTypeParameterBounds> newParamBounds = new ArrayList<>();
        List<AnnotatedTypeMirror> newTypeArgs = new ArrayList<>();
        for (int i = 0; i < typeargs.size(); i++) {
            AnnotatedTypeMirror atm = typeargs.get(i);
            if (!hasNonPublic(atm)) {
                newParamBounds.add(paramBounds.get(i));
                newTypeArgs.add(atm);
            }
        }
        if (!newTypeArgs.isEmpty()) {
            super.checkTypeArguments(toptree, newParamBounds, newTypeArgs, typeargTrees);
        }
    }

    /**
     * Skip the standard subtyping check on method calls iff the enclosing class of the
     * declaration's type is a supertype of the methodCallReceiver.
     *
     * <p>In other words, permit the enclosing class' annotation to substitute for the definition.
     *
     * <p>This avoid needing to write corresponding receiver annotations on every method in a class
     * with an DCC annotation, which is super annoying for users.
     */
    @Override
    protected boolean skipReceiverSubtypeCheck(
            final MethodInvocationTree node,
            final AnnotatedTypeMirror methodDefinitionReceiver,
            final AnnotatedTypeMirror methodCallReceiver) {

        ExecutableElement definition = TreeUtils.elementFromUse(node);
        Element enclosingClass = ElementUtils.enclosingClass(definition);
        AnnotatedTypeMirror classType = atypeFactory.getAnnotatedType(enclosingClass);

        AnnotationMirror classAnno =
                classType.getAnnotationInHierarchy(atypeFactory.getCanonicalPublicAnnotation());
        AnnotationMirror methodCallReceiverAnno =
                methodCallReceiver.getAnnotationInHierarchy(
                        atypeFactory.getCanonicalPublicAnnotation());

        // if either is null, default to conservatively not skipping the check
        if (methodCallReceiverAnno == null || classAnno == null) {
            return false;
        }
        return atypeFactory.getQualifierHierarchy().isSubtype(methodCallReceiverAnno, classAnno);
    }
}
