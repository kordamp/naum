/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2023 The Naum authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.naum.core.processor.klass;

import org.junit.Test;
import org.kordamp.naum.core.model.ClassInfo;
import org.kordamp.naum.core.model.ConstructorInfo;
import org.kordamp.naum.core.model.EnumValue;
import org.kordamp.naum.core.processor.AbstractProcessorTest;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.kordamp.naum.core.model.AnnotationInfo.annotationInfo;
import static org.kordamp.naum.core.model.AnnotationValue.newArrayValue;
import static org.kordamp.naum.core.model.AnnotationValue.newEnumValue;
import static org.kordamp.naum.core.model.ClassInfo.classInfo;
import static org.kordamp.naum.core.model.ClassInfo.newEnum;
import static org.kordamp.naum.core.model.ClassInfo.newInterface;
import static org.kordamp.naum.core.model.ConstructorInfo.constructorInfo;
import static org.kordamp.naum.core.model.FieldInfo.fieldInfo;
import static org.kordamp.naum.core.model.InnerClassInfo.innerClassInfo;
import static org.kordamp.naum.core.model.MethodInfo.methodInfo;
import static org.kordamp.naum.core.model.Opcodes.ACC_ABSTRACT;
import static org.kordamp.naum.core.model.Opcodes.ACC_DEFAULT;
import static org.kordamp.naum.core.model.Opcodes.ACC_PUBLIC;
import static org.kordamp.naum.core.model.Opcodes.ACC_STATIC;
import static org.kordamp.naum.core.model.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.V11;

public class ClassTest extends AbstractProcessorTest {
    @Test
    public void loadAndCheckPlainClass() throws Exception {
        loadAndCheck("org/kordamp/naum/core/processor/klass/PlainClass.class", (klass) -> {
            assertThat(klass, equalTo(classInfoFor("org.kordamp.naum.core.processor.klass.PlainClass")));
        });
    }

    @Test
    public void loadAndCheckPlainClassWithInterface() throws Exception {
        loadAndCheck("org/kordamp/naum/core/processor/klass/PlainClassWithInterface.class", (klass) -> {
            assertThat(klass, equalTo(iclassInfoFor("org.kordamp.naum.core.processor.klass.PlainClassWithInterface", "java.io.Serializable")));
        });
    }

    @Test
    public void loadAndCheckPlainClassWithSuper() throws Exception {
        loadAndCheck("org/kordamp/naum/core/processor/klass/PlainClassWithSuper.class", (klass) -> {
            assertThat(klass, equalTo(sclassInfoFor("org.kordamp.naum.core.processor.klass.PlainClassWithSuper", "org.kordamp.naum.core.processor.klass.PlainSuper")));
        });
    }

    @Test
    public void loadAndCheckPlainClassWithSuperAndInterface() throws Exception {
        loadAndCheck("org/kordamp/naum/core/processor/klass/PlainClassWithSuperAndInterface.class", (klass) -> {
            assertThat(klass, equalTo(siclassInfoFor("org.kordamp.naum.core.processor.klass.PlainClassWithSuperAndInterface", "org.kordamp.naum.core.processor.klass.PlainSuper", "java.io.Serializable")));
        });
    }

    @Test
    public void loadAndCheckTypedClass() throws Exception {
        loadAndCheck("org/kordamp/naum/core/processor/klass/TypedClass.class", (klass) -> {
            assertThat(klass, equalTo(typedClassInfoFor("org.kordamp.naum.core.processor.klass.TypedClass", "<T>")));
        });
    }

    @Test
    public void loadAndCheckTypedClassWithBound() throws Exception {
        loadAndCheck("org/kordamp/naum/core/processor/klass/TypedClassWithBound.class", (klass) -> {
            assertThat(klass, equalTo(typedClassInfoFor("org.kordamp.naum.core.processor.klass.TypedClassWithBound", "<T extends java.lang.Number>")));
        });
    }

    @Test
    public void loadAndCheckClassWithAnnotation() throws Exception {
        ClassInfo classInfo = classInfoFor("org.kordamp.naum.core.processor.klass.ClassWithAnnotation");
        classInfo.addToAnnotations(annotationInfo()
            .name("javax.inject.Named")
            .build());
        loadAndCheck("org/kordamp/naum/core/processor/klass/ClassWithAnnotation.class", (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckClassWithAnnotationValue() throws Exception {
        ClassInfo classInfo = classInfoFor("org.kordamp.naum.core.processor.klass.ClassWithAnnotationValue");
        classInfo.addToAnnotations(annotationInfo()
            .name("javax.inject.Named")
            .value("value", "value")
            .build());
        loadAndCheck("org/kordamp/naum/core/processor/klass/ClassWithAnnotationValue.class", (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckClassWithInnerClasses() throws Exception {
        ClassInfo classInfo = classInfoFor("org.kordamp.naum.core.processor.klass.ClassWithInnerClasses");
        classInfo.addToClasses(innerClassInfo()
            .name("org.kordamp.naum.core.processor.klass.ClassWithInnerClasses$StaticMemberClass")
            .modifiers(ACC_PUBLIC | ACC_STATIC)
            .build());
        classInfo.addToClasses(innerClassInfo()
            .name("org.kordamp.naum.core.processor.klass.ClassWithInnerClasses$MemberClass")
            .modifiers(ACC_PUBLIC)
            .build());
        classInfo.addToClasses(innerClassInfo()
            .name("org.kordamp.naum.core.processor.klass.ClassWithInnerClasses$1MethodMemberClass")
            .build());
        classInfo.addToClasses(innerClassInfo()
            .name("org.kordamp.naum.core.processor.klass.ClassWithInnerClasses$1")
            .build());
        classInfo.addToMethods(methodInfo()
            .name("method")
            .modifiers(ACC_PUBLIC)
            .build());
        loadAndCheck("org/kordamp/naum/core/processor/klass/ClassWithInnerClasses.class", (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckInterface() throws Exception {
        ClassInfo classInfo = newInterface()
            .name("org.kordamp.naum.core.processor.klass.Interface")
            .version(V11)
            .build();
        classInfo.addToMethods(methodInfo()
            .name("method")
            .modifiers(ACC_PUBLIC | ACC_ABSTRACT)
            .build());
        classInfo.addToMethods(methodInfo()
            .name("static_method")
            .modifiers(ACC_PUBLIC | ACC_STATIC)
            .build());
        classInfo.addToMethods(methodInfo()
            .name("default_method")
            .modifiers(ACC_PUBLIC | ACC_DEFAULT)
            .build());
        loadAndCheck("org/kordamp/naum/core/processor/klass/Interface.class", (klass) -> {
            assertTrue("Class " + klass.getName() + " should be an interface", klass.isInterface());
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckClassWithConstructors() throws Exception {
        ClassInfo classInfo = classInfoBuilderFor("org.kordamp.naum.core.processor.klass.ClassWithConstructors")
            .modifiers(ACC_PUBLIC | ACC_SUPER)
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .argumentTypes("int")
            .build());
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .argumentTypes("java.util.Map<java.lang.String, java.lang.Object>")
            .build());
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .argumentTypes("boolean")
            .exceptions(new String[]{IllegalArgumentException.class.getName()})
            .build());
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PRIVATE)
            .argumentTypes(Object.class.getName())
            .build());
        ConstructorInfo constructor = constructorInfo()
            .modifiers(ACC_PRIVATE)
            .argumentTypes("java.lang.Object, java.lang.Object")
            .build();
        constructor.addToAnnotations(annotationInfo()
            .name(Named.class.getName())
            .build());
        classInfo.addToConstructors(constructor);
        loadAndCheck("org/kordamp/naum/core/processor/klass/ClassWithConstructors.class", (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckPlainAnnotation() throws Exception {
        ClassInfo classInfo = ClassInfo.newAnnotation()
            .name("org.kordamp.naum.core.processor.klass.PlainAnnotation")
            .version(V11)
            .iface(Annotation.class.getName())
            .build();
        loadAndCheck("org/kordamp/naum/core/processor/klass/PlainAnnotation.class", (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckAnnotatedAnnotation() throws Exception {
        ClassInfo classInfo = ClassInfo.newAnnotation()
            .name("org.kordamp.naum.core.processor.klass.AnnotatedAnnotation")
            .version(V11)
            .iface(Annotation.class.getName())
            .build();
        classInfo.addToAnnotations(annotationInfo()
            .name(Retention.class.getName())
            .annotationValue("value", new EnumValue(RetentionPolicy.class.getName(), "SOURCE"))
            .build());
        classInfo.addToAnnotations(annotationInfo()
            .name(Target.class.getName())
            .annotationValue("value", newArrayValue(asList(
                newEnumValue(ElementType.class.getName(), ElementType.TYPE.name()),
                newEnumValue(ElementType.class.getName(), ElementType.FIELD.name()))
            ))
            .build());
        loadAndCheck("org/kordamp/naum/core/processor/klass/AnnotatedAnnotation.class", (klass) -> {
            assertThat(klass.getContentHash(), equalTo(classInfo.getContentHash()));
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckPlainEnum() throws Exception {
        loadAndCheck("org/kordamp/naum/core/processor/klass/PlainEnum.class", (klass) -> {
            assertThat(klass, equalTo(enumFor("org.kordamp.naum.core.processor.klass.PlainEnum", "ONE", "TWO")));
        });
    }

    private static ClassInfo.ClassInfoBuilder classInfoBuilderFor(String className) {
        return classInfo()
            .name(className)
            .superclass(Object.class.getName())
            .version(V11)
            .modifiers(ACC_PUBLIC | ACC_SUPER);
    }

    private static ClassInfo enumFor(String className, String... fieldNames) {
        ClassInfo classInfo = newEnum()
            .name(className)
            .version(V11)
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PRIVATE)
            .build());
        for (String fieldName : fieldNames) {
            classInfo.addToFields(
                fieldInfo()
                    .name(fieldName)
                    .type(className)
                    .modifiers(ACC_PUBLIC | ACC_STATIC | ACC_FINAL | ACC_ENUM)
                    .build()
            );
        }
        classInfo.addToMethods(methodInfo()
            .name("valueOf")
            .modifiers(ACC_PUBLIC | ACC_STATIC)
            .returnType(className)
            .argumentTypes("java.lang.String")
            .build());
        classInfo.addToMethods(methodInfo()
            .name("values")
            .modifiers(ACC_PUBLIC | ACC_STATIC)
            .returnType(className + "[]")
            .build());
        return classInfo;
    }

    private static ClassInfo classInfoFor(String className) {
        ClassInfo classInfo = classInfoBuilderFor(className).build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo iclassInfoFor(String className, String... interfaces) {
        ClassInfo classInfo = classInfoBuilderFor(className)
            .interfaces(interfaces)
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo sclassInfoFor(String className, String superclass) {
        ClassInfo classInfo = classInfoBuilderFor(className)
            .superclass(superclass)
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo siclassInfoFor(String className, String superclass, String... interfaces) {
        ClassInfo classInfo = classInfoBuilderFor(className)
            .superclass(superclass)
            .interfaces(interfaces)
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo typedClassInfoFor(String className, String formalParameters) {
        ClassInfo classInfo = classInfoBuilderFor(className)
            .typeParameters(formalParameters)
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }
}
