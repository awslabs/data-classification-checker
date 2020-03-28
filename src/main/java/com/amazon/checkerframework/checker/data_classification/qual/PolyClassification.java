// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.amazon.checkerframework.checker.data_classification.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.PolymorphicQualifier;

/**
 * A polymorphic qualifier for DCC. Polymorphic qualifiers are used to write types for methods that
 * can operate on data of any classification, but have some constraints on the relationship between
 * the classification of their parameters and/or return type.
 *
 * <p>For example, consider an identity function that returns its argument unchanged. To keep the
 * data classification of the parameter on the return type, we would use this annotation. Basically,
 * this annotation means "in this method signature, all instances of @PolyClassification will
 * resolve to a single classification level".
 *
 * <p>Code example:
 *
 * <p>{@code @PolyClassification Object identity(@PolyClassification Object obj) { return obj; }}
 *
 * <p>No matter what classification level data has when it is passed in, it will have the same level
 * when it comes out.
 *
 * <p>For an argument for which the polymorphic type should be enforced on use, but whose type
 * should not change the resolved type, use {@code @PolyClassification("use")}.
 *
 * <p>For example, consider the signature of {@code StringBuilder.append}:
 *
 * <pre>{@code
 * {@literal @}PolyClassification StringBuilder append({@literal @}PolyClassification StringBuilder this,
 *                                          {@literal @}PolyClassification("use") String s);
 * }</pre>
 *
 * This signature means that the type of the StringBuilder must be at least as strong as the type of
 * the String appended: that is, that, for instance, an {@code @Critical String} cannot be appended
 * to an {@code @Public StringBuilder}.
 */
@Retention(RetentionPolicy.RUNTIME)
@PolymorphicQualifier(Critical.class)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface PolyClassification {
    /*
     * <p>Write {@code @PolyClassification("use")} on a method parameter that should have the same type as
     * {@code @PolyClassification} without affecting the instantiation of {@code @PolyClassification}. For example, a
     * method that is annotated as {@code void method_name (@PolyClassification a, @PolyClassification("use") b)} would
     * not allow the method invocation {@code method_name(@Public a, @Confidential b)}.
     */
    String value() default "";
}
