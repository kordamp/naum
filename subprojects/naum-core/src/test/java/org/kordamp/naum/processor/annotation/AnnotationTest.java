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
package org.kordamp.naum.processor.annotation;

import org.junit.Test;
import org.kordamp.naum.model.AnnotationInfo;
import org.kordamp.naum.model.AnnotationInfo.EnumEntry;
import org.kordamp.naum.processor.AbstractProcessorTest;
import org.kordamp.naum.processor.annotation.WithAnnotationValueAnnotation.CustomAnnotationValueAnnotation;
import org.kordamp.naum.processor.annotation.WithAnnotationValueAnnotation.DefaultAnnotationValueAnnotation;
import org.kordamp.naum.processor.annotation.WithAnnotationValueAnnotation.InnerAnnotation;
import org.kordamp.naum.processor.annotation.WithEnumValueAnnotation.CustomEnumValueAnnotation;
import org.kordamp.naum.processor.annotation.WithEnumValueAnnotation.DefaultEnumValueAnnotation;
import org.kordamp.naum.processor.annotation.WithEnumValueAnnotation.SomeEnum;
import org.kordamp.naum.processor.annotation.WithRetentionClassAnnotation.PlainClassAnnotation;
import org.kordamp.naum.processor.annotation.WithRetentionRuntimeAnnotation.PlainRuntimeAnnotation;
import org.kordamp.naum.processor.annotation.WithStringValueAnnotation.CustomStringValueAnnotation;
import org.kordamp.naum.processor.annotation.WithStringValueAnnotation.EmptyDefaultStringValueAnnotation;
import org.kordamp.naum.processor.annotation.WithStringValueAnnotation.NonEmptyDefaultStringValueAnnotation;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.kordamp.naum.AnnotatedInfoMatcher.annotatedInfo;
import static org.kordamp.naum.model.AnnotationInfo.annotationInfo;
import static org.kordamp.naum.model.AnnotationInfo.newEnumEntry;
import static org.kordamp.naum.processor.annotation.WithEnumValueAnnotation.SomeEnum.PIZZA;

/**
 * @author Stephan Classen
 */
public class AnnotationTest extends AbstractProcessorTest {

    @Test
    public void loadAndCheckWithRetentionSourceAnnotation() throws Exception {
        loadAndCheckAnnotations(WithRetentionSourceAnnotation.class, (annotations) -> {
            assertThat(annotations.size(), is(0));
        });
    }

    @Test
    public void loadAndCheckWithRetentionClassAnnotation() throws Exception {
        final AnnotationInfo annotation = annotationInfo().name(PlainClassAnnotation.class.getName()).build();

        loadAndCheckAnnotations(WithRetentionClassAnnotation.class, (annotations) -> {
            assertThat(annotations, contains(annotatedInfo(annotation)));
        });
    }

    @Test
    public void loadAndCheckWithRetentionRuntimeAnnotation() throws Exception {
        final AnnotationInfo annotation = annotationInfo().name(PlainRuntimeAnnotation.class.getName()).build();

        loadAndCheckAnnotations(WithRetentionRuntimeAnnotation.class, (annotations) -> {
            assertThat(annotations, contains(annotatedInfo(annotation)));
        });
    }

    @Test
    public void loadAndCheckWithStringValueAnnotation() throws Exception {
        final AnnotationInfo emptyDefault = annotationInfo().name(EmptyDefaultStringValueAnnotation.class.getName()).build();
        final AnnotationInfo nonEmptyDefault = annotationInfo().name(NonEmptyDefaultStringValueAnnotation.class.getName()).build();
        final AnnotationInfo custom = annotationInfo().name(CustomStringValueAnnotation.class.getName()).value("value", "Pizza").build();

        loadAndCheckAnnotations(WithStringValueAnnotation.class, (annotations) -> {
            assertThat(annotations, contains(annotatedInfo(custom), annotatedInfo(emptyDefault), annotatedInfo(nonEmptyDefault)));
        });
    }

    @Test
    public void loadAndCheckWithEnumValueAnnotation() throws Exception {
        final AnnotationInfo defaultEnum = annotationInfo().name(DefaultEnumValueAnnotation.class.getName()).build();
        final EnumEntry enumValue = newEnumEntry(SomeEnum.class.getName(), PIZZA.name());
        final AnnotationInfo customEnum = annotationInfo().name(CustomEnumValueAnnotation.class.getName()).enumValue("value", enumValue).build();

        loadAndCheckAnnotations(WithEnumValueAnnotation.class, (annotations) -> {
            assertThat(annotations, contains(annotatedInfo(customEnum), annotatedInfo(defaultEnum)));
        });
    }

    @Test
    public void loadAndCheckWithAnnotationValueAnnotation() throws Exception {
        final AnnotationInfo defaultEnum = annotationInfo().name(DefaultAnnotationValueAnnotation.class.getName()).build();
        final AnnotationInfo innerAnnotation = annotationInfo().name(InnerAnnotation.class.getName()).value("value", "Pizza").build();
        final AnnotationInfo customEnum = annotationInfo().name(CustomAnnotationValueAnnotation.class.getName()).value("value", innerAnnotation).build();

        loadAndCheckAnnotations(WithAnnotationValueAnnotation.class, (annotations) -> {
            assertThat(annotations, contains(annotatedInfo(customEnum), annotatedInfo(defaultEnum)));
        });
    }

    private void loadAndCheckAnnotations(Class<?> clazz, AnnotationChecks checks) throws Exception {
        final String targetClassPath = clazz.getCanonicalName().replaceAll("\\.", "/");
        loadAndCheck(targetClassPath + ".class", (klass) -> {
            checks.check(klass.getAnnotations());
        });
    }

    public interface AnnotationChecks {
        void check(List<AnnotationInfo> annotations);
    }
}
