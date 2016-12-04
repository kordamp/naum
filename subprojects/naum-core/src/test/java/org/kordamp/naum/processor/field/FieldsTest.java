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

import org.hamcrest.Matcher;
import org.junit.Test;
import org.kordamp.naum.model.FieldInfo;
import org.kordamp.naum.processor.AbstractProcessorTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.kordamp.naum.NamedInfoMatcher.asMatchers;
import static org.kordamp.naum.model.AnnotationInfo.annotationInfo;
import static org.kordamp.naum.model.FieldInfo.fieldInfo;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

public class FieldsTest extends AbstractProcessorTest {
    @Test
    public void loadAndCheckPublicPrimitiveFieldsClass() throws Exception {
        loadAndCheckFields(PublicPrimitiveFieldsClass.class, (fields) -> {
            assertThat(fields, contains(fields(Integer.TYPE, ACC_PUBLIC)));
        });
    }

    @Test
    public void loadAndCheckPublicReferenceFieldsClass() throws Exception {
        loadAndCheckFields(PublicReferenceFieldsClass.class, (fields) -> {
            assertThat(fields, contains(fields(Integer.class, ACC_PUBLIC)));
        });
    }

    @Test
    public void loadAndCheckProtectedPrimitiveFieldsClass() throws Exception {
        loadAndCheckFields(ProtectedPrimitiveFieldsClass.class, (fields) -> {
            assertThat(fields, contains(fields(Integer.TYPE, ACC_PROTECTED)));
        });
    }

    @Test
    public void loadAndCheckProtectedReferenceFieldsClass() throws Exception {
        loadAndCheckFields(ProtectedReferenceFieldsClass.class, (fields) -> {
            assertThat(fields, contains(fields(Integer.class, ACC_PROTECTED)));
        });
    }

    @Test
    public void loadAndCheckPrivatePrimitiveFieldsClass() throws Exception {
        loadAndCheckFields(PrivatePrimitiveFieldsClass.class, (fields) -> {
            assertThat(fields, contains(fields(Integer.TYPE, ACC_PRIVATE)));
        });
    }

    @Test
    public void loadAndCheckPrivateReferenceFieldsClass() throws Exception {
        loadAndCheckFields(PrivateReferenceFieldsClass.class, (fields) -> {
            assertThat(fields, contains(fields(Integer.class, ACC_PRIVATE)));
        });
    }

    @Test
    public void loadAndCheckPackagePrimitiveFieldsClass() throws Exception {
        loadAndCheckFields(PackagePrimitiveFieldsClass.class, (fields) -> {
            assertThat(fields, contains(fields(Integer.TYPE, 0)));
        });
    }

    @Test
    public void loadAndCheckPackageReferenceFieldsClass() throws Exception {
        loadAndCheckFields(PackageReferenceFieldsClass.class, (fields) -> {
            assertThat(fields, contains(fields(Integer.class, 0)));
        });
    }

    @Test
    public void loadAndCheckFieldsWithGenerics() throws Exception {
        List<FieldInfo> expectedFields = new ArrayList<>();
        expectedFields.add(fieldInfo()
            .name("fieldWithBoundTypes")
            .type("java.util.Map<? extends java.lang.String, ? extends java.lang.Number>")
            .modifiers(ACC_PUBLIC)
            .build());
        expectedFields.add(fieldInfo()
            .name("fieldWithGenericType")
            .type("A")
            .modifiers(ACC_PUBLIC)
            .build());
        expectedFields.add(fieldInfo()
            .name("fieldWithGenericTypes")
            .type("java.util.Map<java.lang.String, java.lang.Number>")
            .modifiers(ACC_PUBLIC)
            .build());
        expectedFields.add(fieldInfo()
            .name("fieldWithWildcards")
            .type("java.util.Map<?, ?>")
            .modifiers(ACC_PUBLIC)
            .build());

        loadAndCheckFields(FieldsWithGenerics.class, (fields) -> {
            assertThat(fields, contains(asMatchers(expectedFields)));
        });
    }

    @Test
    public void loadAndCheckFieldsWithAnnotations() throws Exception {
        List<FieldInfo> expectedFields = new ArrayList<>();
        FieldInfo field = fieldInfo()
            .name("field")
            .type("int")
            .modifiers(ACC_PUBLIC)
            .build();
        field.addToAnnotations(annotationInfo()
            .name("javax.inject.Named")
            .build());
        expectedFields.add(field);
        field = fieldInfo()
            .name("field_value")
            .type("int")
            .modifiers(ACC_PUBLIC)
            .build();
        field.addToAnnotations(annotationInfo()
            .name("javax.inject.Named")
            .value("value", "value")
            .build());
        expectedFields.add(field);

        loadAndCheckFields(FieldsWithAnnotations.class, (fields) -> {
            assertThat(fields, contains(asMatchers(expectedFields)));
        });
    }

    private static List<Matcher<? super FieldInfo>> fields(Class<?> type, int modifiers) {
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
        return asMatchers(fields);
    }

    private void loadAndCheckFields(Class<?> clazz, FieldsCheck checks) throws Exception {
        loadAndCheck(clazz, (klass) -> {
            checks.check(klass.getFields());
        });
    }

    private interface FieldsCheck {
        void check(List<FieldInfo> annotations);
    }
}
