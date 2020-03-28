// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazon.checkerframework.checker.data_classification.qual.*;

// :: warning: (inconsistent.constructor.type)
class SimpleGenerics {
    Map<String, @HighlyConfidential String> field;

    void test() {
	    Map<String, @HighlyConfidential String> map = new HashMap<>();
	    for (Map.Entry<String, @HighlyConfidential String> e : map.entrySet()) {  }

	    // :: error: (enhancedfor.type.incompatible)
	    for (Map.Entry<String, String> e : map.entrySet()) { }
    }
    
    void test2() {
        Map<String, List<@HighlyConfidential String>> map = new HashMap<>();
        for (Map.Entry<String, List<@HighlyConfidential String>> e : map.entrySet()) {  }

        // :: error: (enhancedfor.type.incompatible)
        for (Map.Entry<String, List<String>> e : map.entrySet()) { }
    }
}
