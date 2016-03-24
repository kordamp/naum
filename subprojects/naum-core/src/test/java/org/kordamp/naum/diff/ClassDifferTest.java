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
package org.kordamp.naum.diff;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kordamp.naum.model.ClassInfo;

import java.io.Closeable;
import java.io.Serializable;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.kordamp.naum.diff.ClassDiffer.KEY_CLASS_INTERFACE_ADDED;
import static org.kordamp.naum.diff.ClassDiffer.KEY_CLASS_INTERFACE_REMOVED;
import static org.kordamp.naum.diff.ClassDiffer.KEY_CLASS_MODIFIERS_MODIFIED;
import static org.kordamp.naum.diff.ClassDiffer.KEY_CLASS_SUPERCLASS_MODIFIED;
import static org.kordamp.naum.diff.ClassDiffer.KEY_CLASS_TYPE_MODIFIED;
import static org.kordamp.naum.diff.ClassDiffer.KEY_CLASS_VERSION_MODIFIED;
import static org.kordamp.naum.diff.ClassDiffer.classDiffer;
import static org.kordamp.naum.diff.Diff.diff;
import static org.kordamp.naum.model.ClassInfo.classInfo;
import static org.kordamp.naum.model.ClassInfo.newInterface;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_7;
import static org.objectweb.asm.Opcodes.V1_8;

/**
 * @author Andres Almiray
 */
@RunWith(JUnitParamsRunner.class)
public class ClassDifferTest {
    private static final String CLASSNAME = "org.example.Foo";
    private static final String SUPER_CLASSNAME = "org.example.Bar";
    private static final String JAVA_LANG_OBJECT = "java.lang.Object";
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
                ClassInfo.newAnnotation()
                    .name(CLASSNAME)
                    .build(),
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_CLASS_TYPE_MODIFIED)
                        .messageArg(CLASSNAME)
                        .messageArg("interface")
                        .messageArg("annotation")
                        .build()
                )
            },

            /*
            new Object[]{
                "version",
                classInfo()
                    .name(CLASSNAME)
                    .version(V1_7)
                    .build(),
                classInfo()
                    .name(CLASSNAME)
                    .version(V1_8)
                    .build(),
                asList(
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
                classInfo()
                    .name(CLASSNAME)
                    .build(),
                classInfo()
                    .name(CLASSNAME)
                    .superclass(SUPER_CLASSNAME)
                    .build(),
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
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
                classInfo()
                    .name(CLASSNAME)
                    .modifiers(ACC_PUBLIC)
                    .build(),
                classInfo()
                    .name(CLASSNAME)
                    .modifiers(ACC_PRIVATE)
                    .build(),
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
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
                classInfo()
                    .name(CLASSNAME)
                    .iface(JAVA_IO_SERIALIZABLE)
                    .build(),
                classInfo()
                    .name(CLASSNAME)
                    .build(),
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.REMOVED)
                        .messageKey(KEY_CLASS_INTERFACE_REMOVED)
                        .messageArg(CLASSNAME)
                        .messageArg(JAVA_IO_SERIALIZABLE)
                        .build()
                )
            },

            new Object[]{
                "interfaces - added",
                classInfo()
                    .name(CLASSNAME)
                    .build(),
                classInfo()
                    .name(CLASSNAME)
                    .iface(JAVA_IO_SERIALIZABLE)
                    .build(),
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.ADDED)
                        .messageKey(KEY_CLASS_INTERFACE_ADDED)
                        .messageArg(CLASSNAME)
                        .messageArg(JAVA_IO_SERIALIZABLE)
                        .build()
                )
            },

            new Object[]{
                "interfaces - mixed",
                classInfo()
                    .name(CLASSNAME)
                    .iface(JAVA_IO_SERIALIZABLE)
                    .iface(JAVA_IO_CLONEABLE)
                    .build(),
                classInfo()
                    .name(CLASSNAME)
                    .iface(JAVA_IO_SERIALIZABLE)
                    .iface(JAVA_IO_CLOSEABLE)
                    .build(),
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.REMOVED)
                        .messageKey(KEY_CLASS_INTERFACE_REMOVED)
                        .messageArg(CLASSNAME)
                        .messageArg(JAVA_IO_CLONEABLE)
                        .build(),
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.ADDED)
                        .messageKey(KEY_CLASS_INTERFACE_ADDED)
                        .messageArg(CLASSNAME)
                        .messageArg(JAVA_IO_CLOSEABLE)
                        .build()
                )
            }
            */
        };
    }
}
