// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazon.checkerframework.checker.data_classification.qual.*;

// :: warning: (inconsistent.constructor.type)
public class ToString {

    private @HighlyConfidential String highlyConfidentialMember = "Batman";

    @Override
    public @PolyClassification String toString(@PolyClassification ToString this) {
        return highlyConfidentialMember;
    }

    static @PolyClassification Object test(@PolyClassification ToString t) {

        // All of these are okay, because t is @HC
        @HighlyConfidential Object s = t;
        Object r = t;
        @Critical Object obj = t;

        // :: error: assignment.type.incompatible
        @Public Object r1 = r;
        // :: error: argument.type.incompatible
        System.out.println(t);

        return t;
    }

    public static void main(String[] args) {
        ToString f = new ToString();
        // :: error: argument.type.incompatible
        System.err.println("a" + f);
        // :: error: argument.type.incompatible
        System.err.println("b" + f.toString());

        Object o = new @Confidential Object();
        // :: error: argument.type.incompatible
        System.out.println(o.toString());
    }
}
