// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.amazon.checkerframework.checker.data_classification.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Used to indicate that the annotated element could contain any data that is not classified as
 * {@link Critical}.
 *
 * <p>Synonym for {@link Restricted}, with the same semantics.
 */
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface NonCritical {}
