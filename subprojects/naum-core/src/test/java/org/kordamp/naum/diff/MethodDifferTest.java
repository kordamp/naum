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
package org.kordamp.naum.diff;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kordamp.naum.model.MethodInfo;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.kordamp.naum.diff.Diff.diff;
import static org.kordamp.naum.diff.MethodDiffer.KEY_METHOD_EXCEPTION_ADDED;
import static org.kordamp.naum.diff.MethodDiffer.KEY_METHOD_EXCEPTION_REMOVED;
import static org.kordamp.naum.diff.MethodDiffer.KEY_METHOD_MODIFIERS_MODIFIED;
import static org.kordamp.naum.diff.MethodDiffer.KEY_METHOD_TYPE_MODIFIED;
import static org.kordamp.naum.diff.MethodDiffer.methodDiffer;
import static org.kordamp.naum.model.MethodInfo.methodInfo;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

/**
 * @author Andres Almiray
 */
@RunWith(JUnitParamsRunner.class)
public class MethodDifferTest {
    private static final String METHODNAME = "foo";
    private static final String JAVA_LANG_OBJECT = "java.lang.Object";
    private static final String JAVA_LANG_INTEGER = "java.lang.Integer";
    private static final String JAVA_LANG_RUNTIMEEXCEPTION = "java.lang.RuntimeException";
    private static final String JAVA_LANG_ILLEGALARGUMENTEXCEPTION = "java.lang.IllegalArgumentException";

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
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
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
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_METHOD_TYPE_MODIFIED)
                        .messageArg(METHODNAME)
                        .messageArg(JAVA_LANG_OBJECT)
                        .messageArg(JAVA_LANG_INTEGER)
                        .build()
                )
            },

            new Object[] {
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
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.ADDED)
                        .messageKey(KEY_METHOD_EXCEPTION_ADDED)
                        .messageArg(METHODNAME)
                        .messageArg(JAVA_LANG_RUNTIMEEXCEPTION)
                        .build()
                )
            },

            new Object[] {
                "exceptions-remove",
                methodInfo()
                    .name(METHODNAME)
                    .returnType(JAVA_LANG_OBJECT)
                    .exceptions(new String[]{JAVA_LANG_RUNTIMEEXCEPTION})
                    .build(),
                methodInfo()
                    .name(METHODNAME)
                    .returnType(JAVA_LANG_OBJECT)
                    .build(),
                asList(
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.REMOVED)
                        .messageKey(KEY_METHOD_EXCEPTION_REMOVED)
                        .messageArg(METHODNAME)
                        .messageArg(JAVA_LANG_RUNTIMEEXCEPTION)
                        .build()
                )
            },

            new Object[] {
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
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.REMOVED)
                        .messageKey(KEY_METHOD_EXCEPTION_REMOVED)
                        .messageArg(METHODNAME)
                        .messageArg(JAVA_LANG_RUNTIMEEXCEPTION)
                        .build(),
                    diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.ADDED)
                        .messageKey(KEY_METHOD_EXCEPTION_ADDED)
                        .messageArg(METHODNAME)
                        .messageArg(JAVA_LANG_ILLEGALARGUMENTEXCEPTION)
                        .build()
                )
            }
        };
    }
}
