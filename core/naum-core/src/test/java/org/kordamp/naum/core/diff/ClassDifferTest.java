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
import org.kordamp.naum.core.model.ClassInfo;

import java.io.Closeable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.kordamp.naum.core.diff.ClassDiffer.KEY_CLASS_ANNOTATION_ADDED;
import static org.kordamp.naum.core.diff.ClassDiffer.KEY_CLASS_ANNOTATION_REMOVED;
import static org.kordamp.naum.core.diff.ClassDiffer.KEY_CLASS_INTERFACE_ADDED;
import static org.kordamp.naum.core.diff.ClassDiffer.KEY_CLASS_INTERFACE_REMOVED;
import static org.kordamp.naum.core.diff.ClassDiffer.KEY_CLASS_MODIFIERS_MODIFIED;
import static org.kordamp.naum.core.diff.ClassDiffer.KEY_CLASS_SUPERCLASS_MODIFIED;
import static org.kordamp.naum.core.diff.ClassDiffer.KEY_CLASS_TYPE_MODIFIED;
import static org.kordamp.naum.core.diff.ClassDiffer.KEY_CLASS_VERSION_MODIFIED;
import static org.kordamp.naum.core.diff.ClassDiffer.classDiffer;
import static org.kordamp.naum.core.diff.Diff.Severity.ERROR;
import static org.kordamp.naum.core.diff.Diff.Type.ADDED;
import static org.kordamp.naum.core.diff.Diff.Type.REMOVED;
import static org.kordamp.naum.core.diff.Diff.diff;
import static org.kordamp.naum.core.model.AnnotationInfo.annotationInfo;
import static org.kordamp.naum.core.model.ClassInfo.newAnnotation;
import static org.kordamp.naum.core.model.ClassInfo.newClass;
import static org.kordamp.naum.core.model.ClassInfo.newInterface;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_7;
import static org.objectweb.asm.Opcodes.V1_8;

/**
 * @author Andres Almiray
 */
@RunWith(JUnitParamsRunner.class)
public class ClassDifferTest extends AbstractDifferTestCase {
    private static final String CLASSNAME = "org.example.Foo";
    private static final String SUPER_CLASSNAME = "org.example.Bar";
    private static final String JAVA_IO_SERIALIZABLE = Serializable.class.getName();
    private static final String JAVA_IO_CLONEABLE = Cloneable.class.getName();
    private static final String JAVA_IO_CLOSEABLE = Closeable.class.getName();

    @Test
    @Parameters(method = "classStructure")
    @TestCaseName("{method}[{index}] - {0}")
    public void classesDifferInStructure(String testName, ClassInfo previous, ClassInfo next, Collection<Diff> expected) {
        ClassDiffer differ = classDiffer(previous, next);
        Collection<Diff> actual = differ.diff();
        assertThat(actual, hasSize(greaterThan(0)));
        assertThat(actual, equalTo(expected));
    }

    private Object[] classStructure() {
        return new Object[]{
            new Object[]{
                "type",
                newInterface()
                    .name(CLASSNAME)
                    .build(),
                newAnnotation()
                    .name(CLASSNAME)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_CLASS_TYPE_MODIFIED)
                        .messageArg(CLASSNAME)
                        .messageArg("interface")
                        .messageArg("annotation")
                        .build()
                )
            },

            new Object[]{
                "version",
                newClass()
                    .name(CLASSNAME)
                    .version(V1_7)
                    .build(),
                newClass()
                    .name(CLASSNAME)
                    .version(V1_8)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(Diff.Severity.WARNING)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_CLASS_VERSION_MODIFIED)
                        .messageArg(CLASSNAME)
                        .messageArg(V1_7)
                        .messageArg(V1_8)
                        .build()
                )
            },

            new Object[]{
                "superclass",
                newClass()
                    .name(CLASSNAME)
                    .build(),
                newClass()
                    .name(CLASSNAME)
                    .superclass(SUPER_CLASSNAME)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_CLASS_SUPERCLASS_MODIFIED)
                        .messageArg(CLASSNAME)
                        .messageArg(JAVA_LANG_OBJECT)
                        .messageArg(SUPER_CLASSNAME)
                        .build()
                )
            },

            new Object[]{
                "modifiers",
                newClass()
                    .name(CLASSNAME)
                    .modifiers(ACC_PUBLIC)
                    .build(),
                newClass()
                    .name(CLASSNAME)
                    .modifiers(ACC_PRIVATE)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_CLASS_MODIFIERS_MODIFIED)
                        .messageArg(CLASSNAME)
                        .messageArg("public")
                        .messageArg(ACC_PUBLIC)
                        .messageArg("private")
                        .messageArg(ACC_PRIVATE)
                        .build()
                )
            },

            new Object[]{
                "interfaces - removed",
                newClass()
                    .name(CLASSNAME)
                    .iface(JAVA_IO_SERIALIZABLE)
                    .build(),
                newClass()
                    .name(CLASSNAME)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_CLASS_INTERFACE_REMOVED)
                        .messageArg(CLASSNAME)
                        .messageArg(JAVA_IO_SERIALIZABLE)
                        .build()
                )
            },

            new Object[]{
                "interfaces - added",
                newClass()
                    .name(CLASSNAME)
                    .build(),
                newClass()
                    .name(CLASSNAME)
                    .iface(JAVA_IO_SERIALIZABLE)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_CLASS_INTERFACE_ADDED)
                        .messageArg(CLASSNAME)
                        .messageArg(JAVA_IO_SERIALIZABLE)
                        .build()
                )
            },

            new Object[]{
                "interfaces - modified",
                newClass()
                    .name(CLASSNAME)
                    .iface(JAVA_IO_SERIALIZABLE)
                    .iface(JAVA_IO_CLONEABLE)
                    .build(),
                newClass()
                    .name(CLASSNAME)
                    .iface(JAVA_IO_SERIALIZABLE)
                    .iface(JAVA_IO_CLOSEABLE)
                    .build(),
                asList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_CLASS_INTERFACE_REMOVED)
                        .messageArg(CLASSNAME)
                        .messageArg(JAVA_IO_CLONEABLE)
                        .build(),
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_CLASS_INTERFACE_ADDED)
                        .messageArg(CLASSNAME)
                        .messageArg(JAVA_IO_CLOSEABLE)
                        .build()
                )
            },

            new Object[]{
                "annotations-added",
                newClass()
                    .name(CLASSNAME)
                    .build(),
                newClass()
                    .name(CLASSNAME)
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_A).build()),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_CLASS_ANNOTATION_ADDED)
                        .messageArg(CLASSNAME)
                        .messageArg("@" + ANNOTATION_A)
                        .build()
                )
            },

            new Object[]{
                "annotations-removed",
                newClass()
                    .name(CLASSNAME)
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_A).build()),
                newClass()
                    .name(CLASSNAME)
                    .build(),
                Collections.singletonList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_CLASS_ANNOTATION_REMOVED)
                        .messageArg(CLASSNAME)
                        .messageArg("@" + ANNOTATION_A)
                        .build()
                )
            },

            new Object[]{
                "annotations-modified",
                newClass()
                    .name(CLASSNAME)
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_A).build()),
                newClass()
                    .name(CLASSNAME)
                    .build()
                    .addToAnnotations(annotationInfo().name(ANNOTATION_B).build()),
                asList(
                    diff()
                        .severity(ERROR)
                        .type(REMOVED)
                        .messageKey(KEY_CLASS_ANNOTATION_REMOVED)
                        .messageArg(CLASSNAME)
                        .messageArg("@" + ANNOTATION_A)
                        .build(),
                    diff()
                        .severity(ERROR)
                        .type(ADDED)
                        .messageKey(KEY_CLASS_ANNOTATION_ADDED)
                        .messageArg(CLASSNAME)
                        .messageArg("@" + ANNOTATION_B)
                        .build()
                )
            }
        };
    }
}
