## CheckerFramework type system for Data Classification

A Java compiler plugin that checks that that data of a certain classification
can only be passed to functions that are also classified as being able to
handle that data.

### How does it work?

The Data Classification Checker (DCC) builds on the Checker Framework
(www.checkerframework.org), licensed under the GPL 2.0 with Classpath
Exception, a tool for building extensions to the Java compiler's typechecker.
A typechecker is perfect for checking classification handling, because
typecheckers are *sound*, meaning that they never miss errors, but might report
false positives. In other words, a typechecker over-approximates what your
program might do at runtime, so if the checker reports that the code is safe,
you can be confident that it is.

### How do I run it?

The CheckerFramework provides different build system integrations that are
described on their wiki. For a quick start, try running it on a single file:

```plain
./gradlew assemble
./gradlew copyDependencies

javac -cp \
./build/libs/data_classification_checker.jar:dependencies/checker-2.5.8.jar \
-processor com.amazon.checkerframework.checker.data_classification.DataClassificationChecker \
tests/data_classification/Aliases.java
```

which will output something like the following:

```plain
./build/libs/data_classification_checker.jar:dependencies/checker-2.5.8.jar \
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
