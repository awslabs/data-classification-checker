// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazon.checkerframework.checker.data_classification.qual.Confidential;

// :: warning: (inconsistent.constructor.type)
class CallOutside {
    void doThings(@Confidential String data) {
        // :: error: (argument.type.incompatible)
        System.out.println(data);
    }
}
