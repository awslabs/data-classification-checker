// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.amazon.checkerframework.checker.data_classification.qual;

import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeUseLocation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated object may contain data
 * classified as PUBLIC.
 *
 * Such data can be handled by any method, so this is the bottom
 * qualifier in the hierarchy.
 *
 * This is also the default qualifier in the hierarchy. Data that
 * is not annotated explicitly with a classification will be
 * assumed to be public, and unannotated methods will be assumed to only
 * accept public data, and to also return public data.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf(Confidential.class)
@DefaultQualifierInHierarchy
@DefaultFor({
        TypeUseLocation.EXCEPTION_PARAMETER,
        TypeUseLocation.IMPLICIT_UPPER_BOUND
})
public @interface Public { }
