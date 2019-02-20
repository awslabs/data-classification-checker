// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import java.io.File;
import java.util.List;
import java.util.Properties;
 
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;
 
/**
 * Test runner that uses the Checker Framework's tooling.
 */
public class DataClassificationTest extends CheckerFrameworkPerDirectoryTest {
    public DataClassificationTest(List<File> testFiles) {
        super(
	      testFiles,
	      com.amazon.checkerframework.checker.data_classification.DataClassificationChecker.class,
	      "data_classification",
	      "-Anomsgtext",
	      "-nowarn",
              "-Astubs=src/main/java/com/amazon/checkerframework/checker/data_classification/jdk.astub");
    }
 
    @Parameters
	public static String[] getTestDirs() {
        return new String[] {"data_classification"};
    }
}
