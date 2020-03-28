// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// Should issue no errors

// :: warning: (inconsistent.constructor.type)
class Fields {
    int x = 5;
    String s = "hello world";
    Object o = new Object();
    static double d = 3.0;
}
