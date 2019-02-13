// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//@skip-test
import com.amazon.checkerframework.checker.data_classification.qual.*;

// This test should fail, but DCC currently allows it.
// This false negative is permitted because doing so allows
// us to use @Public as the true default, preventing many
// false positives.
class DeclaredType {
    void test() {
        DeclaredType t = new Bar();
    }

    // :: error: type.invalid
    @Critical class Bar extends DeclaredType { }
}
