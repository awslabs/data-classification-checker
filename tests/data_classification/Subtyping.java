// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazon.checkerframework.checker.data_classification.qual.Confidential;
import com.amazon.checkerframework.checker.data_classification.qual.Critical;
import com.amazon.checkerframework.checker.data_classification.qual.HighlyConfidential;
import com.amazon.checkerframework.checker.data_classification.qual.Public;
import com.amazon.checkerframework.checker.data_classification.qual.Restricted;


/**
 * This tests that the expected subtyping relationships in the DCC hold.
 */
// :: warning: (inconsistent.constructor.type)
class Subtyping {
    /**
     * An unannotated method. Should only accept public data.
     * @param o public data
     */
    void unannotatedMethod(Object o) { }

    /**
     * A method explcitly annotated as Public.
     * @param o public data
     */
    void publicMethod(@Public Object o) { }

    /**
     * A method explcitly annotated as Confidential.
     * @param o confidential data
     */
    void confidentialMethod(@Confidential Object o) { }

    /**
     * A method explcitly annotated as Highly Confidential.
     * @param o highly confidential data
     */
    void highlyConfidentialMethod(@HighlyConfidential Object o) { }
    
    /**
     * A method explcitly annotated as restricted.
     * @param o restricted data
     */
    void restrictedMethod(@Restricted Object o) { }

    /**
     * A method explcitly annotated as critical.
     * @param o critical data
     */
    void criticalMethod(@Critical Object o) { }

    /**
     * A method that calls each of the methods above with every kind of data.
     */
    void callMethods(@Public Object pub, @Confidential Object con, @HighlyConfidential Object hcon, @Restricted Object
                     res, @Critical Object crit) {
        
        // call the unannotated method

        unannotatedMethod(pub);
        // :: error: (argument.type.incompatible)
        unannotatedMethod(con);
        // :: error: (argument.type.incompatible)
        unannotatedMethod(hcon);
        // :: error: (argument.type.incompatible)
        unannotatedMethod(res);
        // :: error: (argument.type.incompatible)
        unannotatedMethod(crit);

        // call the public method
        
        publicMethod(pub);
        // :: error: (argument.type.incompatible)
        publicMethod(con);
        // :: error: (argument.type.incompatible)
        publicMethod(hcon);
        // :: error: (argument.type.incompatible)
        publicMethod(res);
        // :: error: (argument.type.incompatible)
        publicMethod(crit);
        
        // call the confidential method

        confidentialMethod(pub);
        confidentialMethod(con);
        // :: error: (argument.type.incompatible)
        confidentialMethod(hcon);
        // :: error: (argument.type.incompatible)
        confidentialMethod(res);
        // :: error: (argument.type.incompatible)
        confidentialMethod(crit);

        // call the highly confidential method

        highlyConfidentialMethod(pub);
        highlyConfidentialMethod(con);
        highlyConfidentialMethod(hcon);
        // :: error: (argument.type.incompatible)
        highlyConfidentialMethod(res);
        // :: error: (argument.type.incompatible)
        highlyConfidentialMethod(crit);

        // call the restricted method

        restrictedMethod(pub);
        restrictedMethod(con);
        restrictedMethod(hcon);
        restrictedMethod(res);
        // :: error: (argument.type.incompatible)
        restrictedMethod(crit);

        // call the critical method

        criticalMethod(pub);
        criticalMethod(con);
        criticalMethod(hcon);
        criticalMethod(res);
        criticalMethod(crit);
    }
}
