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
public class ReceiverDefaulting {
    @Critical class CriticalObject {
        public @Critical String getString() {
            return "I'm a string";
        }
    }

    static void testCriticalReceiver(CriticalObject obj) {
        obj.getString();
    }

    @Confidential class ConfidentialObject {
        public @Confidential String getString() {
            return "I'm a string";
        }
    }

    @HighlyConfidential class HighlyConfidentialObject extends ConfidentialObject {

    }

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
