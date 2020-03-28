// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazon.checkerframework.checker.data_classification.qual.Confidential;

// :: warning: (inconsistent.constructor.type)
class Enums {
    // :: warning: (inconsistent.constructor.type)
    public enum VarFlags {
        @Confidential IS_PARAM,
        NO_DUPS
    };

    // :: error: assignment.type.incompatible
    VarFlags vf = VarFlags.IS_PARAM;
    VarFlags vf2 = VarFlags.NO_DUPS;
}
