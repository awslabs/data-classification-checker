// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazon.checkerframework.checker.data_classification.qual.*;

public class SimpleLog {
    // :: warning: (inconsistent.constructor.type)
    public SimpleLog() {
        try {
            int i = 0;
        } catch (@Public Exception e) {
            throw new RuntimeException("", e);
        }
    }
}
