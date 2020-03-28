// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.amazon.checkerframework.checker.data_classification.qual.*;

import java.util.ArrayList;
import java.util.List;

public interface InterfaceInference {

    String getString();

}

class CriticalImplementation implements InterfaceInference {

    public @Critical String myMember;

    @Override
    public String getString() {
        return "I'm a safe String";
    }
}

// :: warning: (inconsistent.constructor.type)
class PublicImplementation implements InterfaceInference {
    String myMember;

    @Override
    public String getString() {
        return myMember;
    }
}

// :: warning: (inconsistent.constructor.type)
class Main {
    public static void doThings1() {
        List<InterfaceInference> myList = new ArrayList<>();
        myList.add(new PublicImplementation());
        // :: error: argument.type.incompatible
        myList.add(new CriticalImplementation());

        // I can treat any of this as Critical if I want.
        @Critical InterfaceInference i = myList.get(0);
        InterfaceInference i2 = myList.get(0);

        CriticalImplementation criticalImplementation = (CriticalImplementation) myList.get(0);
        PublicImplementation publicImplementation = (PublicImplementation) myList.get(0);
    }

    public static void doThings2() {
        List<@Critical InterfaceInference> myList = new ArrayList<>();
        myList.add(new PublicImplementation());
        myList.add(new CriticalImplementation());

        InterfaceInference i = myList.get(0);

        if (i instanceof CriticalImplementation) {
            CriticalImplementation ci = (CriticalImplementation) i;
        } else if (i instanceof PublicImplementation) {
            PublicImplementation pi = (PublicImplementation) i;
        }

        // I must treat these as critical, though.
        @Critical InterfaceInference i2 = myList.get(0);
        // :: error: assignment.type.incompatible
        @Public InterfaceInference i3 = myList.get(0);
    }
}
