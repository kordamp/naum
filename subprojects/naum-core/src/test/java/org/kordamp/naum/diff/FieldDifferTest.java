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
import org.kordamp.naum.model.FieldInfo;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.kordamp.naum.diff.Diff.diff;
import static org.kordamp.naum.diff.FieldDiffer.KEY_FIELD_MODIFIERS_MODIFIED;
import static org.kordamp.naum.diff.FieldDiffer.KEY_FIELD_TYPE_MODIFIED;
import static org.kordamp.naum.diff.FieldDiffer.KEY_FIELD_VALUE_MODIFIED;
import static org.kordamp.naum.diff.FieldDiffer.fieldDiffer;
import static org.kordamp.naum.model.FieldInfo.fieldInfo;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;

/**
 * @author Andres Almiray
 */
@RunWith(JUnitParamsRunner.class)
public class FieldDifferTest {
    private static final String FIELDNAME = "var";
    private static final String JAVA_LANG_OBJECT = "java.lang.Object";
    private static final String JAVA_LANG_INTEGER = "java.lang.Integer";
    private static final String OBJECT_FIELD_VALUE = "value";

    @Test
    @Parameters(method = "parameters")
    @TestCaseName("{method}[{index}] - {0}")
    public void fieldsDiffer(String testName, FieldInfo previous, FieldInfo next, Collection<Diff> expected) {
        FieldDiffer differ = fieldDiffer(previous, next);
        Collection<Diff> actual = differ.diff();
        assertThat(actual, hasSize(greaterThan(0)));
        assertThat(actual, equalTo(expected));
    }

    private Object[] parameters() {
        return new Object[]{
            new Object[]{
                "modifiers",
                fieldInfo()
                    .name(FIELDNAME)
                    .modifiers(ACC_PUBLIC)
                    .type(JAVA_LANG_OBJECT)
                    .build(),
                fieldInfo()
                    .name(FIELDNAME)
                    .modifiers(ACC_PRIVATE)
                    .type(JAVA_LANG_OBJECT)
                    .build(),
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_FIELD_MODIFIERS_MODIFIED)
                        .messageArg(FIELDNAME)
                        .messageArg("public")
                        .messageArg(ACC_PUBLIC)
                        .messageArg("private")
                        .messageArg(ACC_PRIVATE)
                        .build()
                )
            },

            new Object[]{
                "type",
                fieldInfo()
                    .name(FIELDNAME)
                    .type(JAVA_LANG_OBJECT)
                    .build(),
                fieldInfo()
                    .name(FIELDNAME)
                    .type(JAVA_LANG_INTEGER)
                    .build(),
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_FIELD_TYPE_MODIFIED)
                        .messageArg(FIELDNAME)
                        .messageArg(JAVA_LANG_OBJECT)
                        .messageArg(JAVA_LANG_INTEGER)
                        .build()
                )
            },

            new Object[] {
                "value",
                fieldInfo()
                    .name(FIELDNAME)
                    .type(JAVA_LANG_OBJECT)
                    .modifiers(ACC_FINAL | ACC_STATIC)
                    .value(null)
                    .build(),
                fieldInfo()
                    .name(FIELDNAME)
                    .type(JAVA_LANG_OBJECT)
                    .modifiers(ACC_FINAL | ACC_STATIC)
                    .value(OBJECT_FIELD_VALUE)
                    .build(),
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_FIELD_VALUE_MODIFIED)
                        .messageArg(FIELDNAME)
                        .messageArg(null)
                        .messageArg(OBJECT_FIELD_VALUE)
                        .build()
                )
            },
        };
    }
}
