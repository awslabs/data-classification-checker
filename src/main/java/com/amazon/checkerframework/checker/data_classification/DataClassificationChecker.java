// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.amazon.checkerframework.checker.data_classification;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.source.SuppressWarningsKeys;

/**
 * A specialized checker for Data Classification.
 *
 * It permits developers to annotate their data with its data
 * classification level, and enforce that data is properly used
 * if they correctly annotate their methods.
 */
@SuppressWarningsKeys({"data_classification", "dataClassification"})
public class DataClassificationChecker extends BaseTypeChecker { }
