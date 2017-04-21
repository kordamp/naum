/*
 * Copyright 2016-2017 the original author or authors.
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
import org.kordamp.naum.model.ClassInfo;
import org.kordamp.naum.model.ConstructorInfo;
import org.kordamp.naum.model.EnumValue;
import org.kordamp.naum.model.Modifiers;
import org.kordamp.naum.processor.AbstractProcessorTest;

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
import static org.kordamp.naum.model.AnnotationInfo.annotationInfo;
import static org.kordamp.naum.model.AnnotationValue.newArrayValue;
import static org.kordamp.naum.model.AnnotationValue.newEnumValue;
import static org.kordamp.naum.model.ClassInfo.classInfo;
import static org.kordamp.naum.model.ClassInfo.newEnum;
import static org.kordamp.naum.model.ClassInfo.newInterface;
import static org.kordamp.naum.model.ConstructorInfo.constructorInfo;
import static org.kordamp.naum.model.FieldInfo.fieldInfo;
import static org.kordamp.naum.model.InnerClassInfo.innerClassInfo;
import static org.kordamp.naum.model.MethodInfo.methodInfo;
import static org.kordamp.naum.model.Opcodes.ACC_ABSTRACT;
import static org.kordamp.naum.model.Opcodes.ACC_DEFAULT;
import static org.kordamp.naum.model.Opcodes.ACC_PUBLIC;
import static org.kordamp.naum.model.Opcodes.ACC_STATIC;
import static org.kordamp.naum.model.Opcodes.ACC_SUPER;
import static org.kordamp.naum.model.Opcodes.V1_8;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class ClassTest extends AbstractProcessorTest {
    @Test
    public void loadAndCheckPlainClass() throws Exception {
        loadAndCheck(PlainClass.class, (klass) -> {
            assertThat(klass, equalTo(classInfoFor(PlainClass.class)));
        });
    }

    @Test
    public void loadAndCheckPlainClassWithInterface() throws Exception {
        loadAndCheck(PlainClassWithInterface.class, (klass) -> {
            assertThat(klass, equalTo(iclassInfoFor(PlainClassWithInterface.class, "java.io.Serializable")));
        });
    }

    @Test
    public void loadAndCheckPlainClassWithSuper() throws Exception {
        loadAndCheck(PlainClassWithSuper.class, (klass) -> {
            assertThat(klass, equalTo(sclassInfoFor(PlainClassWithSuper.class, "org.kordamp.naum.processor.klass.PlainSuper")));
        });
    }

    @Test
    public void loadAndCheckPlainClassWithSuperAndInterface() throws Exception {
        loadAndCheck(PlainClassWithSuperAndInterface.class, (klass) -> {
            assertThat(klass, equalTo(siclassInfoFor(PlainClassWithSuperAndInterface.class, "org.kordamp.naum.processor.klass.PlainSuper", "java.io.Serializable")));
        });
    }

    @Test
    public void loadAndCheckTypedClass() throws Exception {
        loadAndCheck(TypedClass.class, (klass) -> {
            assertThat(klass, equalTo(typedClassInfoFor(TypedClass.class, "<T>")));
        });
    }

    @Test
    public void loadAndCheckTypedClassWithBound() throws Exception {
        loadAndCheck(TypedClassWithBound.class, (klass) -> {
            assertThat(klass, equalTo(typedClassInfoFor(TypedClassWithBound.class, "<T extends java.lang.Number>")));
        });
    }

    @Test
    public void loadAndCheckClassWithAnnotation() throws Exception {
        ClassInfo classInfo = classInfoFor(ClassWithAnnotation.class);
        classInfo.addToAnnotations(annotationInfo()
            .name("javax.inject.Named")
            .build());
        loadAndCheck(ClassWithAnnotation.class, (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckClassWithAnnotationValue() throws Exception {
        ClassInfo classInfo = classInfoFor(ClassWithAnnotationValue.class);
        classInfo.addToAnnotations(annotationInfo()
            .name("javax.inject.Named")
            .value("value", "value")
            .build());
        loadAndCheck(ClassWithAnnotationValue.class, (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckClassWithInnerClasses() throws Exception {
        ClassInfo classInfo = classInfoFor(ClassWithInnerClasses.class);
        classInfo.addToClasses(innerClassInfo()
            .name("org.kordamp.naum.processor.klass.ClassWithInnerClasses$StaticMemberClass")
            .modifiers(ACC_PUBLIC | ACC_STATIC)
            .build());
        classInfo.addToClasses(innerClassInfo()
            .name("org.kordamp.naum.processor.klass.ClassWithInnerClasses$MemberClass")
            .modifiers(ACC_PUBLIC)
            .build());
        classInfo.addToClasses(innerClassInfo()
            .name("org.kordamp.naum.processor.klass.ClassWithInnerClasses$1MethodMemberClass")
            .build());
        classInfo.addToClasses(innerClassInfo()
            .name("org.kordamp.naum.processor.klass.ClassWithInnerClasses$1")
            .build());
        classInfo.addToMethods(methodInfo()
            .name("method")
            .modifiers(ACC_PUBLIC)
            .build());
        loadAndCheck(ClassWithInnerClasses.class, (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckInterface() throws Exception {
        ClassInfo classInfo = newInterface()
            .name("org.kordamp.naum.processor.klass.Interface")
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
        loadAndCheck(Interface.class, (klass) -> {
            assertTrue("Class " + klass.getName() + " should be an interface", klass.isInterface());
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckClassWithConstructors() throws Exception {
        ClassInfo classInfo = classInfoBuilderFor(ClassWithConstructors.class)
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
        loadAndCheck(ClassWithConstructors.class, (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckPlainAnnotation() throws Exception {
        ClassInfo classInfo = ClassInfo.newAnnotation()
            .name("org.kordamp.naum.processor.klass.PlainAnnotation")
            .iface(Annotation.class.getName())
            .build();
        loadAndCheck(PlainAnnotation.class, (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckAnnotatedAnnotation() throws Exception {
        ClassInfo classInfo = ClassInfo.newAnnotation()
            .name("org.kordamp.naum.processor.klass.AnnotatedAnnotation")
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
        loadAndCheck(AnnotatedAnnotation.class, (klass) -> {
            assertThat(klass.getContentHash(), equalTo(classInfo.getContentHash()));
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckPlainEnum() throws Exception {
        loadAndCheck(PlainEnum.class, (klass) -> {
            klass.getFields().forEach(f -> System.out.println(Modifiers.modifiersAsString(f.getModifiers())));
            assertThat(klass, equalTo(enumFor(PlainEnum.class, "ONE", "TWO")));
        });
    }

    private static ClassInfo.ClassInfoBuilder classInfoBuilderFor(Class<?> clazz) {
        return classInfo()
            .name(clazz.getName())
            .superclass(Object.class.getName())
            .version(V1_8)
            .modifiers(ACC_PUBLIC | ACC_SUPER);
    }

    private static ClassInfo enumFor(Class<?> clazz, String... fieldNames) {
        ClassInfo classInfo = newEnum()
            .name(clazz.getName())
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PRIVATE)
            .build());
        for (String fieldName : fieldNames) {
            classInfo.addToFields(
                fieldInfo()
                    .name(fieldName)
                    .type(clazz.getName())
                    .modifiers(ACC_PUBLIC | ACC_STATIC | ACC_FINAL | ACC_ENUM)
                    .build()
            );
        }
        classInfo.addToMethods(methodInfo()
            .name("valueOf")
            .modifiers(ACC_PUBLIC | ACC_STATIC)
            .returnType(clazz.getName())
            .argumentTypes("java.lang.String")
            .build());
        classInfo.addToMethods(methodInfo()
            .name("values")
            .modifiers(ACC_PUBLIC | ACC_STATIC)
            .returnType(clazz.getName() + "[]")
            .build());
        return classInfo;
    }

    private static ClassInfo classInfoFor(Class<?> clazz) {
        ClassInfo classInfo = classInfoBuilderFor(clazz).build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo iclassInfoFor(Class<?> clazz, String... interfaces) {
        ClassInfo classInfo = classInfoBuilderFor(clazz)
            .interfaces(interfaces)
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo sclassInfoFor(Class<?> clazz, String superclass) {
        ClassInfo classInfo = classInfoBuilderFor(clazz)
            .superclass(superclass)
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo siclassInfoFor(Class<?> clazz, String superclass, String... interfaces) {
        ClassInfo classInfo = classInfoBuilderFor(clazz)
            .superclass(superclass)
            .interfaces(interfaces)
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }

    private static ClassInfo typedClassInfoFor(Class<?> clazz, String formalParameters) {
        ClassInfo classInfo = classInfoBuilderFor(clazz)
            .typeParameters(formalParameters)
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        return classInfo;
    }
}
