// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import java.util.List;

@SuppressWarnings(
        "list.access.unsafe.high") // The Index Checker correctly issues this warning here.
// :: warning: (inconsistent.constructor.type)
class Throw {
    <E extends Exception> void throwTypeVar(E ex) {
        try {
            throw ex;
        } catch (Exception e) {
        }
    }

    void throwWildcard(List<? extends Exception> list) {
        try {
            throw list.get(0);
        } catch (Exception e) {

        }
    }
}
