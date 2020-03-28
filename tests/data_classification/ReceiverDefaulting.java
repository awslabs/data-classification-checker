// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazon.checkerframework.checker.data_classification.qual.Confidential;
import com.amazon.checkerframework.checker.data_classification.qual.Critical;
import com.amazon.checkerframework.checker.data_classification.qual.HighlyConfidential;
import com.amazon.checkerframework.checker.data_classification.qual.Restricted;
import com.amazon.checkerframework.checker.data_classification.qual.Public;

/*
 * This class tests that DCC defaults the receiver objects of methods to the qualifier of the class in which
 * the method is defined instead of to @Public.
 */
// :: warning: (inconsistent.constructor.type)
public class ReceiverDefaulting {
    @Critical class CriticalObject {
        public @Critical String getString() {
            return "I'm a string";
        }
    }

    static void testCriticalReceiver(CriticalObject obj) {
        obj.getString();
    }

    // :: warning: (inconsistent.constructor.type)
    @Confidential class ConfidentialObject {
        public @Confidential String getString() {
            return "I'm a string";
        }
    }

    // :: warning: (inconsistent.constructor.type) :: error: (declaration.inconsistent.with.extends.clause)
    @HighlyConfidential class HighlyConfidentialObject extends ConfidentialObject {

    }

    // :: warning: (inconsistent.constructor.type) :: error: (declaration.inconsistent.with.extends.clause)
    @HighlyConfidential class HighlyConfidentialObject2 extends ConfidentialObject {
        @Override
        public @Confidential String getString() {
            return "I'm a fancy string";
        }
    }

    static void testHCReceiver(HighlyConfidentialObject obj, HighlyConfidentialObject2 obj2) {
        // :: error: method.invocation.invalid
        obj.getString();

        obj2.getString();
    }

    static void testStrongerObject(@Critical ConfidentialObject obj) {
        // :: error: method.invocation.invalid
        obj.getString();
    }
}
