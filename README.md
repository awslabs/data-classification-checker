## CheckerFramework type system for Data Classification

A Java compiler plugin that checks that that data of a certain classification
can only be passed to functions that are also classified as being able to
handle that data.

This plugin works via pluggable type-checking:  you specify types (data
classification) and the plugin verifies the code.  The plugin is *sound*,
meaning that it never misses an error: if the checker reports that the code is
safe, you can be confident that it is.  However, it might report false positive
warnings.


### How do I run it?

To build the plugin and run its tests:
```./gradlew check```
This should result in a `BUILD SUCCESSFUL` message.

For a quick start, try running it on a single file which will highlight the
checks that the checker is making and how they are failing:

(Note that you may need to set your `JAVA_HOME` environment variable to point
to your JDK8 home directory then call `javac` using `$JAVA_HOME/bin/javac`)

```plain
./gradlew assemble
./gradlew copyDependencies

javac -cp \
./build/libs/data_classification_checker.jar:dependencies/checker-2.10.0.jar \
-processor com.amazon.checkerframework.checker.data_classification.DataClassificationChecker \
tests/data_classification/Aliases.java
```

which will output something like the following:

```plain
./build/libs/data_classification_checker.jar:dependencies/checker-2.10.0.jar \
-processor com.amazon.checkerframework.checker.data_classification.DataClassificationChecker \
tests/data_classification/Aliases.java
warning: You do not seem to be using the distributed annotated JDK.  To fix the problem, supply javac an argument like:  -Xbootclasspath/p:.../checker/dist/ .  Currently using: jdk8.jar
tests/data_classification/Aliases.java:60: error: [argument.type.incompatible] incompatible types in argument.
        unannotatedMethod(con);
                          ^
  found   : @Confidential Object
  required: @Public Object
tests/data_classification/Aliases.java:62: error: [argument.type.incompatible] incompatible types in argument.
        unannotatedMethod(hcon);
                          ^
  found   : @HighlyConfidential Object
  required: @Public Object
tests/data_classification/Aliases.java:64: error: [argument.type.incompatible] incompatible types in argument.
        unannotatedMethod(res);
                          ^
  found   : @Restricted Object
  required: @Public Object
tests/data_classification/Aliases.java:66: error: [argument.type.incompatible] incompatible types in argument.
        unannotatedMethod(crit);
                          ^
  found   : @Critical Object
  required: @Public Object
tests/data_classification/Aliases.java:72: error: [argument.type.incompatible] incompatible types in argument.
        publicMethod(con);
                     ^
  found   : @Confidential Object
  required: @Public Object
tests/data_classification/Aliases.java:74: error: [argument.type.incompatible] incompatible types in argument.
        publicMethod(hcon);
                     ^
  found   : @HighlyConfidential Object
  required: @Public Object
tests/data_classification/Aliases.java:76: error: [argument.type.incompatible] incompatible types in argument.
        publicMethod(res);
                     ^
  found   : @Restricted Object
  required: @Public Object
tests/data_classification/Aliases.java:78: error: [argument.type.incompatible] incompatible types in argument.
        publicMethod(crit);
                     ^
  found   : @Critical Object
  required: @Public Object
tests/data_classification/Aliases.java:85: error: [argument.type.incompatible] incompatible types in argument.
        confidentialMethod(hcon);
                           ^
  found   : @HighlyConfidential Object
  required: @Confidential Object
tests/data_classification/Aliases.java:87: error: [argument.type.incompatible] incompatible types in argument.
        confidentialMethod(res);
                           ^
  found   : @Restricted Object
  required: @Confidential Object
tests/data_classification/Aliases.java:89: error: [argument.type.incompatible] incompatible types in argument.
        confidentialMethod(crit);
                           ^
  found   : @Critical Object
  required: @Confidential Object
tests/data_classification/Aliases.java:97: error: [argument.type.incompatible] incompatible types in argument.
        highlyConfidentialMethod(res);
                                 ^
  found   : @Restricted Object
  required: @HighlyConfidential Object
tests/data_classification/Aliases.java:99: error: [argument.type.incompatible] incompatible types in argument.
        highlyConfidentialMethod(crit);
                                 ^
  found   : @Critical Object
  required: @HighlyConfidential Object
tests/data_classification/Aliases.java:108: error: [argument.type.incompatible] incompatible types in argument.
        restrictedMethod(crit);
                         ^
  found   : @Critical Object
  required: @Restricted Object
14 errors
1 warning
```

## License

This library is licensed under the Apache 2.0 License.
