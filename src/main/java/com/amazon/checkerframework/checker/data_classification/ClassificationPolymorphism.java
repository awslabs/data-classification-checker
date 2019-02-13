// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.amazon.checkerframework.checker.data_classification;

import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.poly.DefaultQualifierPolymorphism;
import org.checkerframework.framework.util.AnnotationMirrorMap;
import org.checkerframework.framework.util.AnnotationMirrorSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import java.util.Map;

/**
 * Implementation of poly("use") for DCC, based on the Determinism Checker.
 */
public class ClassificationPolymorphism extends DefaultQualifierPolymorphism {

    /**
     * A link back to the type factory so we have access to the poly qualifiers.
     */
    private final DataClassificationAnnotatedTypeFactory atypeFactory;

    /**
     * Default constructor.
     * @param env the processing environment. Should come from the factory.
     * @param factory the annotated type factory
     */
    public ClassificationPolymorphism(final ProcessingEnvironment env,
                                      final DataClassificationAnnotatedTypeFactory factory) {
        super(env, factory);
        atypeFactory = factory;
    }

    /**
     * Replaces {@code @PolyClassification} in {@code type} with the instantiations in {@code replacements}.
     * Replaces {@code @PolyClassification("use")} with the same annotation that
     * {@code @PolyClassification} resolves to.
     *
     * @param type annotated type whose poly annotations are replaced
     * @param replacements mapping from polymorphic annotation to instantiation
     */
    @Override
    protected void replace(final AnnotatedTypeMirror type,
                           final AnnotationMirrorMap<AnnotationMirrorSet> replacements) {
        if (type.hasAnnotation(atypeFactory.getPolyAnnotation())) {
            AnnotationMirrorSet quals = replacements.get(atypeFactory.getPolyAnnotation());
            type.replaceAnnotations(quals);
        } else if (type.hasAnnotation(atypeFactory.getPolyUseAnnotation())) {
            AnnotationMirrorSet quals = replacements.get(atypeFactory.getPolyAnnotation());
            if (!quals.contains(atypeFactory.getPolyAnnotation())) {
                type.replaceAnnotations(quals);
            }
        } else {
            for (Map.Entry<AnnotationMirror, AnnotationMirrorSet> pqentry : replacements.entrySet()) {
                AnnotationMirror poly = pqentry.getKey();
                if (type.hasAnnotation(poly)) {
                    type.removeAnnotation(poly);
                    AnnotationMirrorSet quals = pqentry.getValue();
                    type.replaceAnnotations(quals);
                }
            }
        }
    }
}
