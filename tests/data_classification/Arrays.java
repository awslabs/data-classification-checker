// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// Tests to make sure that DCC defaults arrays correctly.
// Should issue no errors.
// :: warning: (inconsistent.constructor.type)
class Arrays {

    public static final int[] intArr = {1, 2, 3};

    String[] strArr = new String[] {"a", "b"};

    String[] strArr2 = new String[2];

    void test() {
        String[] arr = new String[] {"a", "b"};
        String[] arr2 = new String[2];
    }
}
