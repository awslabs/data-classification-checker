// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazon.checkerframework.checker.data_classification.qual.Critical;
import com.amazon.checkerframework.checker.data_classification.qual.Public;

//@skip-test Fails due to an open CF bug wrt varargs.
// https://github.com/typetools/checker-framework/issues/1218

class Format {
    private @Public String sanitize(final @Critical String info) {
        // :: error: argument.type.incompatible
        @Public String clean = String.format("I am clean: %s", info);
        return clean;
    }
}