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
import org.kordamp.naum.core.model.ConstructorInfo;

import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.kordamp.naum.core.diff.ConstructorDiffer.KEY_CONSTRUCTOR_ANNOTATION_ADDED;
import static org.kordamp.naum.core.diff.ConstructorDiffer.KEY_CONSTRUCTOR_ANNOTATION_REMOVED;
import static org.kordamp.naum.core.diff.ConstructorDiffer.KEY_CONSTRUCTOR_EXCEPTION_ADDED;
import static org.kordamp.naum.core.diff.ConstructorDiffer.KEY_CONSTRUCTOR_EXCEPTION_REMOVED;
import static org.kordamp.naum.core.diff.ConstructorDiffer.KEY_CONSTRUCTOR_MODIFIERS_MODIFIED;
import static org.kordamp.naum.core.diff.ConstructorDiffer.constructorDiffer;
import static org.kordamp.naum.core.diff.Diff.Severity.ERROR;
import static org.kordamp.naum.core.diff.Diff.Type.ADDED;
import static org.kordamp.naum.core.diff.Diff.Type.REMOVED;
import static org.kordamp.naum.core.diff.Diff.diff;
import static org.kordamp.naum.core.model.AnnotationInfo.annotationInfo;
import static org.kordamp.naum.core.model.ConstructorInfo.NAME;
import static org.kordamp.naum.core.model.ConstructorInfo.constructorInfo;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * @author Andres Almiray
 */
@RunWith(JUnitParamsRunner.class)
public class ConstructorDifferTest extends AbstractDifferTestCase {
    @Test
    @Parameters(method = "parameters")
    @TestCaseName("{constructor}[{index}] - {0}")
    public void constructorsDiffer(String testName, ConstructorInfo previous, ConstructorInfo next, Collection<Diff> expected) {
        ConstructorDiffer differ = constructorDiffer(previous, next);
        Collection<Diff> actual = differ.diff();
        assertThat(actual, hasSize(greaterThan(0)));
        assertThat(actual, equalTo(expected));
    }

    private Object[] parameters() {
        return new Object[]{
            new Object[]{
                "modifiers",
                constructorInfo()
                    .modifiers(ACC_PUBLIC)
                    .build(),
                constructorInfo()
                    .modifiers(ACC_PRIVATE)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_CONSTRUCTOR_MODIFIERS_MODIFIED)
                        .messageArg(NAME)
                        .messageArg("public")
                        .messageArg(ACC_PUBLIC)
                        .messageArg("private")
                        .messageArg(ACC_PRIVATE)
                        .build()
                )
            },

            new Object[]{
                "exceptions-added",
                constructorInfo()
                    .build(),
                constructorInfo()
                    .exceptions(new String[]{JAVA_LANG_RUNTIMEEXCEPTION})
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_CONSTRUCTOR_EXCEPTION_ADDED)
                        .messageArg(JAVA_LANG_RUNTIMEEXCEPTION)
                        .build()
                )
            },

            new Object[]{
                "exceptions-removed",
                constructorInfo()
                    .exceptions(new String[]{JAVA_LANG_RUNTIMEEXCEPTION})
                    .build(),
                constructorInfo()
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_CONSTRUCTOR_EXCEPTION_REMOVED)
                        .messageArg(JAVA_LANG_RUNTIMEEXCEPTION)
                        .build()
                )
            },

            new Object[]{
                "exceptions-modified",
                constructorInfo()
                    .exceptions(new String[]{JAVA_LANG_RUNTIMEEXCEPTION})
                    .build(),
                constructorInfo()
                    .exceptions(new String[]{JAVA_LANG_ILLEGALARGUMENTEXCEPTION})
                    .build(),
                asList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_CONSTRUCTOR_EXCEPTION_REMOVED)
                        .messageArg(JAVA_LANG_RUNTIMEEXCEPTION)
                        .build(),
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_CONSTRUCTOR_EXCEPTION_ADDED)
                        .messageArg(JAVA_LANG_ILLEGALARGUMENTEXCEPTION)
                        .build()
                )
            },

            new Object[]{
                "annotations-added",
                constructorInfo()
                    .build(),
                constructorInfo()
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_A).build()),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_CONSTRUCTOR_ANNOTATION_ADDED)
                        .messageArg(NAME)
                        .messageArg("@" + ANNOTATION_A)
                        .build()
                )
            },

            new Object[]{
                "annotations-removed",
                constructorInfo()
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_A).build()),
                constructorInfo()
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_CONSTRUCTOR_ANNOTATION_REMOVED)
                        .messageArg(NAME)
                        .messageArg("@" + ANNOTATION_A)
                        .build()
                )
            },

            new Object[]{
                "annotations-modified",
                constructorInfo()
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_A).build()),
                constructorInfo()
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_B).build()),
                asList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_CONSTRUCTOR_ANNOTATION_REMOVED)
                        .messageArg(NAME)
                        .messageArg("@" + ANNOTATION_A)
                        .build(),
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_CONSTRUCTOR_ANNOTATION_ADDED)
                        .messageArg(NAME)
                        .messageArg("@" + ANNOTATION_B)
                        .build()
                )
            }
        };
    }
}
