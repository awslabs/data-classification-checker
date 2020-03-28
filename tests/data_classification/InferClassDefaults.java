// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazon.checkerframework.checker.data_classification.qual.*;
import java.util.List;

/**
 * Test that inferring class defaults work correctly.
 */
// :: warning: (inconsistent.constructor.type)
public class InferClassDefaults {
    @Confidential String member;
    
    static void acceptClassWithInferredTypeCorrect(@Confidential InferClassDefaults i) { }

    static void acceptClassWithInferredTypeCorrect2(InferClassDefaults i) { 
        @Confidential InferClassDefaults i2 = i;
        
        // :: error: assignment.type.incompatible
        @Public InferClassDefaults i3 = i;
    }
    
    // :: warning: (inconsistent.constructor.type)
    private class Container {
        @Confidential InferClassDefaults i = new InferClassDefaults();

        // :: warning: (cast.unsafe.constructor.invocation)
        @Public InferClassDefaults i2 = new @Public InferClassDefaults();

        // By contrast, the line below does, in fact, fail, so in order to get around the defaulting the user
        // has to purposely change the type of the new object with an incorrect annotation.
        // :: error: assignment.type.incompatible
        @Public InferClassDefaults i3 = new InferClassDefaults();
    }

    static void acceptClassWithInferredTypeCorrect3(@Confidential Container i) { }

    static void acceptClassWithInferredTypeCorrect4(Container i) {
        @Confidential Container i2 = i;

        // :: error: assignment.type.incompatible
        @Public Container i3 = i;
    }

    // :: warning: (inconsistent.constructor.type)
    @Confidential class ConfidentialObject {

    }

    static void testConfidentialObject(ConfidentialObject co) {
        @Confidential ConfidentialObject co2 = co;

        // :: error: assignment.type.incompatible
        @Public ConfidentialObject co3 = co;
    }

    // :: warning: (inconsistent.constructor.type)
    class ObjectWithListOfConfidentialThings {
        List<@Confidential Object> confidentialThings;
    }

    static void testObjectWithListOfCondientialThings(ObjectWithListOfConfidentialThings o) {
        @Confidential ObjectWithListOfConfidentialThings o2 = o;

        // :: error: assignment.type.incompatible
        @Public ObjectWithListOfConfidentialThings o3 = o;
    }

    // :: warning: (inconsistent.constructor.type)
    class ObjectWithArrayOfConfidentialThings {
        @Confidential Object[] confidentialThings;
    }

    static void testObjectWithArrayOfConfidentialThings(ObjectWithArrayOfConfidentialThings o) {
        @Confidential ObjectWithArrayOfConfidentialThings o2 = o;

        // :: error: assignment.type.incompatible
        @Public ObjectWithArrayOfConfidentialThings o3 = o;
    }

    // :: warning: (inconsistent.constructor.type)
    class ObjectWithListOfArraysOfConfidentialThings {
        List<@Confidential Object[]> confidentialThings;
    }

    static void testObjectWithListOfArraysOfConfidentialThings(ObjectWithListOfArraysOfConfidentialThings o) {
        @Confidential ObjectWithListOfArraysOfConfidentialThings o2 = o;

        // :: error: assignment.type.incompatible
        @Public ObjectWithListOfArraysOfConfidentialThings o3 = o;
    }

    // :: warning: (inconsistent.constructor.type)
    class ObjectWithListOfConfidentialArraysThings {
        List<Object @Confidential []> confidentialThings;
    }

    static void testObjectWithListOfConfidentialArraysThings(ObjectWithListOfConfidentialArraysThings o) {
        @Confidential ObjectWithListOfConfidentialArraysThings o2 = o;

        // :: error: assignment.type.incompatible
        @Public ObjectWithListOfConfidentialArraysThings o3 = o;
    }
}
