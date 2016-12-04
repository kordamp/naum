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
package org.kordamp.naum.processor.field;

import org.junit.Test;
import org.kordamp.naum.model.ClassInfo;
import org.kordamp.naum.model.FieldInfo;
import org.kordamp.naum.model.Opcodes;
import org.kordamp.naum.processor.AbstractProcessorTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.kordamp.naum.model.AnnotationInfo.annotationInfo;
import static org.kordamp.naum.model.ClassInfo.classInfo;
import static org.kordamp.naum.model.ConstructorInfo.constructorInfo;
import static org.kordamp.naum.model.FieldInfo.fieldInfo;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;

public class FieldsTest extends AbstractProcessorTest {
    @Test
    public void loadAndCheckPublicPrimitiveFieldsClass() throws Exception {
        loadAndCheck(PublicPrimitiveFieldsClass.class, (klass) -> {
            assertThat(klass, equalTo(classInfoFor(PublicPrimitiveFieldsClass.class, fields(Integer.TYPE, ACC_PUBLIC))));
        });
    }

    @Test
    public void loadAndCheckPublicReferenceFieldsClass() throws Exception {
        loadAndCheck(PublicReferenceFieldsClass.class, (klass) -> {
            assertThat(klass, equalTo(classInfoFor(PublicReferenceFieldsClass.class, fields(Integer.class, ACC_PUBLIC))));
        });
    }

    @Test
    public void loadAndCheckProtectedPrimitiveFieldsClass() throws Exception {
        loadAndCheck(ProtectedPrimitiveFieldsClass.class, (klass) -> {
            assertThat(klass, equalTo(classInfoFor(ProtectedPrimitiveFieldsClass.class, fields(Integer.TYPE, ACC_PROTECTED))));
        });
    }

    @Test
    public void loadAndCheckProtectedReferenceFieldsClass() throws Exception {
        loadAndCheck(ProtectedReferenceFieldsClass.class, (klass) -> {
            assertThat(klass, equalTo(classInfoFor(ProtectedReferenceFieldsClass.class, fields(Integer.class, ACC_PROTECTED))));
        });
    }

    @Test
    public void loadAndCheckPrivatePrimitiveFieldsClass() throws Exception {
        loadAndCheck(PrivatePrimitiveFieldsClass.class, (klass) -> {
            assertThat(klass, equalTo(classInfoFor(PrivatePrimitiveFieldsClass.class, fields(Integer.TYPE, ACC_PRIVATE))));
        });
    }

    @Test
    public void loadAndCheckPrivateReferenceFieldsClass() throws Exception {
        loadAndCheck(PrivateReferenceFieldsClass.class, (klass) -> {
            assertThat(klass, equalTo(classInfoFor(PrivateReferenceFieldsClass.class, fields(Integer.class, ACC_PRIVATE))));
        });
    }

    @Test
    public void loadAndCheckPackagePrimitiveFieldsClass() throws Exception {
        loadAndCheck(PackagePrimitiveFieldsClass.class, (klass) -> {
            assertThat(klass, equalTo(classInfoFor(PackagePrimitiveFieldsClass.class, fields(Integer.TYPE, 0))));
        });
    }

    @Test
    public void loadAndCheckPackageReferenceFieldsClass() throws Exception {
        loadAndCheck(PackageReferenceFieldsClass.class, (klass) -> {
            assertThat(klass, equalTo(classInfoFor(PackageReferenceFieldsClass.class, fields(Integer.class, 0))));
        });
    }

    @Test
    public void loadAndCheckFieldsWithGenerics() throws Exception {
        List<FieldInfo> fields = new ArrayList<>();
        fields.add(fieldInfo()
            .name("fieldWithGenericType")
            .type("A")
            .modifiers(ACC_PUBLIC)
            .build());
        fields.add(fieldInfo()
            .name("fieldWithWildcards")
            .type("java.util.Map<?, ?>")
            .modifiers(ACC_PUBLIC)
            .build());
        fields.add(fieldInfo()
            .name("fieldWithGenericTypes")
            .type("java.util.Map<java.lang.String, java.lang.Number>")
            .modifiers(ACC_PUBLIC)
            .build());
        fields.add(fieldInfo()
            .name("fieldWithBoundTypes")
            .type("java.util.Map<? extends java.lang.String, ? extends java.lang.Number>")
            .modifiers(ACC_PUBLIC)
            .build());

        ClassInfo classInfo = classInfoBuilderFor(FieldsWithGenerics.class)
            .typeParameters("<A extends java.lang.Number>")
            .build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        for (FieldInfo field : fields) {
            classInfo.addToFields(field);
        }

        loadAndCheck(FieldsWithGenerics.class, (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    @Test
    public void loadAndCheckFieldsWithAnnotations() throws Exception {
        List<FieldInfo> fields = new ArrayList<>();
        FieldInfo field = fieldInfo()
            .name("field")
            .type("int")
            .modifiers(ACC_PUBLIC)
            .build();
        field.addToAnnotations(annotationInfo()
            .name("javax.inject.Named")
            .build());
        fields.add(field);
        field = fieldInfo()
            .name("field_value")
            .type("int")
            .modifiers(ACC_PUBLIC)
            .build();
        field.addToAnnotations(annotationInfo()
            .name("javax.inject.Named")
            .value("value", "value")
            .build());
        fields.add(field);

        ClassInfo classInfo = classInfoBuilderFor(FieldsWithAnnotations.class).build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());/*nm─*/
        for (FieldInfo f : fields) {
            classInfo.addToFields(f);
        }

        loadAndCheck(FieldsWithAnnotations.class, (klass) -> {
            assertThat(klass, equalTo(classInfo));
        });
    }

    private static ClassInfo.ClassInfoBuilder classInfoBuilderFor(Class<?> clazz) {
        return classInfo()
            .name(clazz.getName())
            .superclass(Object.class.getName())
            .version(Opcodes.V1_8)
            .modifiers(ACC_PUBLIC | ACC_SUPER);
    }

    private static ClassInfo classInfoFor(Class<?> clazz, List<FieldInfo> fields) {
        ClassInfo classInfo = classInfoBuilderFor(clazz).build();
        classInfo.addToConstructors(constructorInfo()
            .modifiers(ACC_PUBLIC)
            .build());
        for (FieldInfo field : fields) {
            classInfo.addToFields(field);
        }
        return classInfo;
    }

    private static List<FieldInfo> fields(Class<?> type, int modifiers) {
        List<FieldInfo> fields = new ArrayList<>();
        fields.add(fieldInfo()
            .name("field")
            .type(type.getName())
            .modifiers(modifiers)
            .value(type.isPrimitive() ? 42 : null)
            .build());
        fields.add(fieldInfo()
            .name("final_field")
            .type(type.getName())
            .modifiers(modifiers | ACC_FINAL)
            .value(type.isPrimitive() ? 42 : null)
            .build());
        fields.add(fieldInfo()
            .name("static_field")
            .type(type.getName())
            .modifiers(modifiers | ACC_STATIC)
            .value(type.isPrimitive() ? 42 : null)
            .build());
        fields.add(fieldInfo()
            .name("static_final_field")
            .type(type.getName())
            .modifiers(modifiers | ACC_STATIC | ACC_FINAL)
            .value(type.isPrimitive() ? 42 : null)
            .build());
        return fields;
    }
}
