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
package org.kordamp.naum.core.diff;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kordamp.naum.core.model.MethodInfo;

import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.kordamp.naum.core.diff.Diff.Severity.ERROR;
import static org.kordamp.naum.core.diff.Diff.Type.ADDED;
import static org.kordamp.naum.core.diff.Diff.Type.REMOVED;
import static org.kordamp.naum.core.diff.Diff.diff;
import static org.kordamp.naum.core.diff.MethodDiffer.KEY_METHOD_ANNOTATION_ADDED;
import static org.kordamp.naum.core.diff.MethodDiffer.KEY_METHOD_ANNOTATION_REMOVED;
import static org.kordamp.naum.core.diff.MethodDiffer.KEY_METHOD_EXCEPTION_ADDED;
import static org.kordamp.naum.core.diff.MethodDiffer.KEY_METHOD_EXCEPTION_REMOVED;
import static org.kordamp.naum.core.diff.MethodDiffer.KEY_METHOD_MODIFIERS_MODIFIED;
import static org.kordamp.naum.core.diff.MethodDiffer.KEY_METHOD_TYPE_MODIFIED;
import static org.kordamp.naum.core.diff.MethodDiffer.methodDiffer;
import static org.kordamp.naum.core.model.AnnotationInfo.annotationInfo;
import static org.kordamp.naum.core.model.MethodInfo.methodInfo;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * @author Andres Almiray
 */
@RunWith(JUnitParamsRunner.class)
public class MethodDifferTest extends AbstractDifferTestCase {
    private static final String METHODNAME = "foo";
    private static final String JAVA_LANG_INTEGER = "java.lang.Integer";

    @Test
    @Parameters(method = "parameters")
    @TestCaseName("{method}[{index}] - {0}")
    public void methodsDiffer(String testName, MethodInfo previous, MethodInfo next, Collection<Diff> expected) {
        MethodDiffer differ = methodDiffer(previous, next);
        Collection<Diff> actual = differ.diff();
        assertThat(actual, hasSize(greaterThan(0)));
        assertThat(actual, equalTo(expected));
    }

    private Object[] parameters() {
        return new Object[]{
            new Object[]{
                "modifiers",
                methodInfo()
                    .name(METHODNAME)
                    .modifiers(ACC_PUBLIC)
                    .returnType(JAVA_LANG_OBJECT)
                    .build(),
                methodInfo()
                    .name(METHODNAME)
                    .modifiers(ACC_PRIVATE)
                    .returnType(JAVA_LANG_OBJECT)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_METHOD_MODIFIERS_MODIFIED)
                        .messageArg(METHODNAME)
                        .messageArg("public")
                        .messageArg(ACC_PUBLIC)
                        .messageArg("private")
                        .messageArg(ACC_PRIVATE)
                        .build()
                )
            },

            new Object[]{
                "type",
                methodInfo()
                    .name(METHODNAME)
                    .returnType(JAVA_LANG_OBJECT)
                    .build(),
                methodInfo()
                    .name(METHODNAME)
                    .returnType(JAVA_LANG_INTEGER)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_METHOD_TYPE_MODIFIED)
                        .messageArg(METHODNAME)
                        .messageArg(JAVA_LANG_OBJECT)
                        .messageArg(JAVA_LANG_INTEGER)
                        .build()
                )
            },

            new Object[]{
                "exceptions-added",
                methodInfo()
                    .name(METHODNAME)
                    .returnType(JAVA_LANG_OBJECT)
                    .build(),
                methodInfo()
                    .name(METHODNAME)
                    .returnType(JAVA_LANG_OBJECT)
                    .exceptions(new String[]{JAVA_LANG_RUNTIMEEXCEPTION})
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_METHOD_EXCEPTION_ADDED)
                        .messageArg(METHODNAME)
                        .messageArg(JAVA_LANG_RUNTIMEEXCEPTION)
                        .build()
                )
            },

            new Object[]{
                "exceptions-removed",
                methodInfo()
                    .name(METHODNAME)
                    .returnType(JAVA_LANG_OBJECT)
                    .exceptions(new String[]{JAVA_LANG_RUNTIMEEXCEPTION})
                    .build(),
                methodInfo()
                    .name(METHODNAME)
                    .returnType(JAVA_LANG_OBJECT)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_METHOD_EXCEPTION_REMOVED)
                        .messageArg(METHODNAME)
                        .messageArg(JAVA_LANG_RUNTIMEEXCEPTION)
                        .build()
                )
            },

            new Object[]{
                "exceptions-modified",
                methodInfo()
                    .name(METHODNAME)
                    .returnType(JAVA_LANG_OBJECT)
                    .exceptions(new String[]{JAVA_LANG_RUNTIMEEXCEPTION})
                    .build(),
                methodInfo()
                    .name(METHODNAME)
                    .returnType(JAVA_LANG_OBJECT)
                    .exceptions(new String[]{JAVA_LANG_ILLEGALARGUMENTEXCEPTION})
                    .build(),
                asList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_METHOD_EXCEPTION_REMOVED)
                        .messageArg(METHODNAME)
                        .messageArg(JAVA_LANG_RUNTIMEEXCEPTION)
                        .build(),
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_METHOD_EXCEPTION_ADDED)
                        .messageArg(METHODNAME)
                        .messageArg(JAVA_LANG_ILLEGALARGUMENTEXCEPTION)
                        .build()
                )
            },

            new Object[]{
                "annotations-added",
                methodInfo()
                    .name(METHODNAME)
                    .build(),
                methodInfo()
                    .name(METHODNAME)
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_A).build()),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_METHOD_ANNOTATION_ADDED)
                        .messageArg(METHODNAME)
                        .messageArg("@" + ANNOTATION_A)
                        .build()
                )
            },

            new Object[]{
                "annotations-removed",
                methodInfo()
                    .name(METHODNAME)
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_A).build()),
                methodInfo()
                    .name(METHODNAME)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_METHOD_ANNOTATION_REMOVED)
                        .messageArg(METHODNAME)
                        .messageArg("@" + ANNOTATION_A)
                        .build()
                )
            },

            new Object[]{
                "annotations-modified",
                methodInfo()
                    .name(METHODNAME)
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_A).build()),
                methodInfo()
                    .name(METHODNAME)
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_B).build()),
                asList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_METHOD_ANNOTATION_REMOVED)
                        .messageArg(METHODNAME)
                        .messageArg("@" + ANNOTATION_A)
                        .build(),
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_METHOD_ANNOTATION_ADDED)
                        .messageArg(METHODNAME)
                        .messageArg("@" + ANNOTATION_B)
                        .build()
                )
            }
        };
    }
}
