/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kordamp.naum.processor.klass;

import org.junit.Test;
import org.kordamp.naum.model.AnnotationInfo;
import org.kordamp.naum.model.ClassInfo;
import org.kordamp.naum.model.ConstructorInfo;
import org.kordamp.naum.model.InnerClassInfo;
import org.kordamp.naum.model.MethodInfo;
import org.kordamp.naum.model.Modifiers;
import org.kordamp.naum.processor.AbstractProcessorTest;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.kordamp.naum.model.Opcodes.ACC_ABSTRACT;
import static org.kordamp.naum.model.Opcodes.ACC_DEFAULT;
import static org.kordamp.naum.model.Opcodes.ACC_PUBLIC;
import static org.kordamp.naum.model.Opcodes.ACC_STATIC;
import static org.kordamp.naum.model.Opcodes.ACC_SUPER;
import static org.kordamp.naum.model.Opcodes.V1_8;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class ClassTest extends AbstractProcessorTest {
    @Test
    public void loadAndCheckPlainClass() throws Exception {
        loadAndCheck("org/kordamp/naum/processor/klass/PlainClass.class", (klass) -> {
            assertThat(klass, equalTo(classInfoFor("org.kordamp.naum.processor.klass.PlainClass")));
        });
    }

    @Test
    public void loadAndCheckPlainClassWithInterface() throws Exception {
        loadAndCheck("org/kordamp/naum/processor/klass/PlainClassWithInterface.class", (klass) -> {
            assertThat(klass, equalTo(iclassInfoFor("org.kordamp.naum.processor.klass.PlainClassWithInterface", "java.io.Serializable")));
        });
    }

    @Test
    public void loadAndCheckPlainClassWithSuper() throws Exception {
        loadAndCheck("org/kordamp/naum/processor/klass/PlainClassWithSuper.class", (klass) -> {
            assertThat(klass, equalTo(sclassInfoFor("org.kordamp.naum.processor.klass.PlainClassWithSuper", "org.kordamp.naum.processor.klass.PlainSuper")));
        });
    }

    @Test
    public void loadAndCheckPlainClassWithSuperAndInterface() throws Exception {
        loadAndCheck("org/kordamp/naum/processor/klass/PlainClassWithSuperAndInterface.class", (klass) -> {
            assertThat(klass, equalTo(siclassInfoFor("org.kordamp.naum.processor.klass.PlainClassWithSuperAndInterface", "org.kordamp.naum.processor.klass.PlainSuper", "java.io.Serializable")));
        });
    }

    @Test
    public void loadAndCheckTypedClass() throws Exception {
        loadAndCheck("org/kordamp/naum/processor/klass/TypedClass.class", (klass) -> {
            assertThat(klass, equalTo(typedClassInfoFor("org.kordamp.naum.processor.klass.TypedClass", "<T>")));
        });
    }

    @Test
    public void loadAndCheckTypedClassWithBound() throws Exception {
        loadAndCheck("org/kordamp/naum/processor/klass/TypedClassWithBound.class", (klass) -> {
            assertThat(klass, equalTo(typedClassInfoFor("org.kordamp.naum.processor.klass.TypedClassWithBound", "<T extends java.lang.Number>")));
        });
    }

    @Test
    public void loadAndCheckClassWithAnnotation() throws Exception {
        ClassInfo classInfo = classInfoFor("org.kordamp.naum.processor.klass.ClassWithAnnotation");
        classInfo.addToAnnotations(AnnotationInfo.builder()
            .name("javax.inject.Named")
            .build());
        loadAndCheck("org/kordamp/naum/processor/klass/ClassWithAnnotation.class", (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckClassWithAnnotationValue() throws Exception {
        ClassInfo classInfo = classInfoFor("org.kordamp.naum.processor.klass.ClassWithAnnotationValue");
        classInfo.addToAnnotations(AnnotationInfo.builder()
            .name("javax.inject.Named")
            .value("value", "value")
            .build());
        loadAndCheck("org/kordamp/naum/processor/klass/ClassWithAnnotationValue.class", (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckClassWithInnerClasses() throws Exception {
        ClassInfo classInfo = classInfoFor("org.kordamp.naum.processor.klass.ClassWithInnerClasses");
        classInfo.addToClasses(InnerClassInfo.builder()
            .name("org.kordamp.naum.processor.klass.ClassWithInnerClasses$StaticMemberClass")
            .modifiers(ACC_PUBLIC | ACC_STATIC)
            .build());
        classInfo.addToClasses(InnerClassInfo.builder()
            .name("org.kordamp.naum.processor.klass.ClassWithInnerClasses$MemberClass")
            .modifiers(ACC_PUBLIC)
            .build());
        classInfo.addToClasses(InnerClassInfo.builder()
            .name("org.kordamp.naum.processor.klass.ClassWithInnerClasses$1MethodMemberClass")
            .build());
        classInfo.addToClasses(InnerClassInfo.builder()
            .name("org.kordamp.naum.processor.klass.ClassWithInnerClasses$1")
            .build());
        classInfo.addToMethods(MethodInfo.builder()
            .name("method")
            .modifiers(ACC_PUBLIC)
            .build());
        loadAndCheck("org/kordamp/naum/processor/klass/ClassWithInnerClasses.class", (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckInterface() throws Exception {
        ClassInfo classInfo = ClassInfo.newInterface()
            .name("org.kordamp.naum.processor.klass.Interface")
            .build();
        classInfo.addToMethods(MethodInfo.builder()
            .name("method")
            .modifiers(ACC_PUBLIC | ACC_ABSTRACT)
            .build());
        classInfo.addToMethods(MethodInfo.builder()
            .name("static_method")
            .modifiers(ACC_PUBLIC | ACC_STATIC)
            .build());
        classInfo.addToMethods(MethodInfo.builder()
            .name("default_method")
            .modifiers(ACC_PUBLIC | ACC_DEFAULT)
            .build());
        loadAndCheck("org/kordamp/naum/processor/klass/Interface.class", (klass) -> {
            assertTrue("Class " + klass.getName() + " should be an interface", Modifiers.isInterface(klass.getModifiers()));
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckClassWithConstructors() throws Exception {
        ClassInfo classInfo = classInfoBuilderFor("org.kordamp.naum.processor.klass.ClassWithConstructors")
            .modifiers(ACC_PUBLIC | ACC_SUPER)
            .build();
        classInfo.addToConstructors(ConstructorInfo.builder()
            .modifiers(ACC_PUBLIC)
            .build());
        classInfo.addToConstructors(ConstructorInfo.builder()
            .modifiers(ACC_PUBLIC)
            .argumentTypes("int")
            .build());
        classInfo.addToConstructors(ConstructorInfo.builder()
            .modifiers(ACC_PUBLIC)
            .argumentTypes("java.util.Map<java.lang.String, java.lang.Object>")
            .build());
        classInfo.addToConstructors(ConstructorInfo.builder()
            .modifiers(ACC_PUBLIC)
            .argumentTypes("boolean")
            .exceptions(new String[]{IllegalArgumentException.class.getName()})
            .build());
        classInfo.addToConstructors(ConstructorInfo.builder()
            .modifiers(ACC_PRIVATE)
            .argumentTypes(Object.class.getName())
            .build());
        ConstructorInfo constructor = ConstructorInfo.builder()
            .modifiers(ACC_PRIVATE)
            .argumentTypes("java.lang.Object, java.lang.Object")
            .build();
        constructor.addToAnnotations(AnnotationInfo.builder()
            .name(Named.class.getName())
            .build());
        classInfo.addToConstructors(constructor);
        loadAndCheck("org/kordamp/naum/processor/klass/ClassWithConstructors.class", (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckPlainAnnotation() throws Exception {
        ClassInfo classInfo = ClassInfo.newAnnotation()
            .name("org.kordamp.naum.processor.klass.PlainAnnotation")
            .iface(Annotation.class.getName())
            .build();
        loadAndCheck("org/kordamp/naum/processor/klass/PlainAnnotation.class", (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckAnnotatedAnnotation() throws Exception {
        ClassInfo classInfo = ClassInfo.newAnnotation()
            .name("org.kordamp.naum.processor.klass.AnnotatedAnnotation")
            .iface(Annotation.class.getName())
            .build();
        classInfo.addToAnnotations(AnnotationInfo.builder()
            .name(Retention.class.getName())
            .enumValue("value", new AnnotationInfo.EnumEntry(RetentionPolicy.class.getName(), "SOURCE"))
            .build());
        classInfo.addToAnnotations(AnnotationInfo.builder()
            .name(Target.class.getName())
            //.value("value", new ElementType[]{ElementType.TYPE, ElementType.FIELD})
            .build());
        loadAndCheck("org/kordamp/naum/processor/klass/AnnotatedAnnotation.class", (klass) -> {
            System.out.println(klass);
            System.out.println(classInfo);
            assertThat(klass.getContentHash(), equalTo(classInfo.getContentHash()));
            assertThat(klass, equalTo(classInfo));
        });
    }

    private static ClassInfo.ClassInfoBuilder classInfoBuilderFor(String className) {
        return ClassInfo.builder()
            .name(className)
            .superclass(Object.class.getName())
            .version(V1_8)
            .modifiers(ACC_PUBLIC | ACC_SUPER);
    }

    private static ClassInfo classInfoFor(String className) {
        ClassInfo classInfo = classInfoBuilderFor(className).build();
        classInfo.addToConstructors(ConstructorInfo.builder()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo iclassInfoFor(String className, String... interfaces) {
        ClassInfo classInfo = classInfoBuilderFor(className)
            .interfaces(interfaces)
            .build();
        classInfo.addToConstructors(ConstructorInfo.builder()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo sclassInfoFor(String className, String superclass) {
        ClassInfo classInfo = classInfoBuilderFor(className)
            .superclass(superclass)
            .build();
        classInfo.addToConstructors(ConstructorInfo.builder()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo siclassInfoFor(String className, String superclass, String... interfaces) {
        ClassInfo classInfo = classInfoBuilderFor(className)
            .superclass(superclass)
            .interfaces(interfaces)
            .build();
        classInfo.addToConstructors(ConstructorInfo.builder()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo typedClassInfoFor(String className, String formalParameters) {
        ClassInfo classInfo = classInfoBuilderFor(className)
            .typeParameters(formalParameters)
            .build();
        classInfo.addToConstructors(ConstructorInfo.builder()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }
}
