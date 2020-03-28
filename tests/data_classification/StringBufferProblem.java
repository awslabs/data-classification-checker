// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// A simple test to make sure that the StringBuffer problem is being handled correctly
// by @Poly("use")

import com.amazon.checkerframework.checker.data_classification.qual.*;

// :: warning: (inconsistent.constructor.type)
public class StringBufferProblem {
    void critical_critical(@Critical StringBuffer buffer, @Critical String string) {
        buffer.append(string);
    }

    void critical_public(@Critical StringBuffer buffer, @Public String string) {
        buffer.append(string);
    }

    void public_critical(@Public StringBuffer buffer, @Critical String string) {
        // :: error: argument.type.incompatible
        buffer.append(string);
    }

    void public_public(@Public StringBuffer buffer, @Public String string) {
        buffer.append(string);
    }
}
