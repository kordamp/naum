/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import org.kordamp.naum.processor.AbstractProcessorTest;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.kordamp.naum.model.AnnotationInfo.annotationInfo;

/**
 * @author Stephan Classen
 */
public class AnnotationTest extends AbstractProcessorTest {

    @Test
    public void loadAndCheckPlainAnnotationWithRetentionSource() throws Exception {
        loadAndCheckAnnotations(WithPlainSourceAnno.class, (annotations) -> {
            assertThat(annotations.size(), is(0));
        });
    }

    @Test
    public void loadAndCheckPlainAnnotationWithRetentionClass() throws Exception {
        final AnnotationInfo expectedAnnotation = annotationInfo().name(PlainClassAnnotation.class.getName()).build();

        loadAndCheckAnnotations(WithPlainClassAnno.class, (annotations) -> {
            assertThat(annotations.size(), is(1));
            AnnotationInfo actualAnnotation = annotations.get(0);
            assertThat(expectedAnnotation.getContentHash(), equalTo(actualAnnotation.getContentHash()));
            assertThat(expectedAnnotation, equalTo(actualAnnotation));
        });
    }

    @Test
    public void loadAndCheckPlainAnnotationWithRetentionRuntime() throws Exception {
        final AnnotationInfo expectedAnnotation = annotationInfo().name(PlainRuntimeAnnotation.class.getName()).build();

        loadAndCheckAnnotations(WithPlainRuntimeAnno.class, (annotations) -> {
            assertThat(annotations.size(), is(1));
            AnnotationInfo actualAnnotation = annotations.get(0);
            assertThat(expectedAnnotation.getContentHash(), equalTo(actualAnnotation.getContentHash()));
            assertThat(expectedAnnotation, equalTo(actualAnnotation));
        });
    }

    private void loadAndCheckAnnotations(Class<?> clazz, AnnotationChecks checks) throws Exception {
        String targetClassPath = clazz.getCanonicalName().replaceAll("\\.", "/");
        loadAndCheck(targetClassPath + ".class", (klass) -> {
            checks.check(klass.getAnnotations());
        });
    }

    public interface AnnotationChecks {
        void check(List<AnnotationInfo> annotations);
    }
}
