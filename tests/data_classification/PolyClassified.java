// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazon.checkerframework.checker.data_classification.qual.PolyClassification;
import com.amazon.checkerframework.checker.data_classification.qual.Critical;
import com.amazon.checkerframework.checker.data_classification.qual.Public;
import com.amazon.checkerframework.checker.data_classification.qual.HighlyConfidential;
import com.amazon.checkerframework.checker.data_classification.qual.Restricted;
import com.amazon.checkerframework.checker.data_classification.qual.Confidential;

// :: warning: (inconsistent.constructor.type)
class PolyClassified {
    @PolyClassification Object identity(@PolyClassification Object object) { return object; }

    void test1(@Critical Object o) {
        @Critical Object o1 = identity(o);
    }
    void test2(@Restricted Object o) {
        @Restricted Object o1 = identity(o);
    }
    void test3(@HighlyConfidential Object o) {
        @HighlyConfidential Object o1 = identity(o);
    }
    void test4(@Confidential Object o) {
        @Confidential Object o1 = identity(o);
    }
    void test5(@Public Object o) {
        @Public Object o1 = identity(o);
    }
    void test6(Object o) {
        Object o1 = identity(o);
    }
}
