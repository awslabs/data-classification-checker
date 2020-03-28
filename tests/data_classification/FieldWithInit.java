// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// :: warning: (inconsistent.constructor.type)
class FieldWithInit {
    @SuppressWarnings("nullness") // Don't want to depend on Nullness Checker
    Object f = foo();

    Object foo(/*@UnknownInitialization @Raw*/ FieldWithInit this) {
        return new Object();
    }
}
