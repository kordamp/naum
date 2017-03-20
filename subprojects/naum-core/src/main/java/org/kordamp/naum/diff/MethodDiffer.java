/*
 * Copyright 2016-2017 the original author or authors.
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

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kordamp.naum.model.MethodInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.kordamp.naum.diff.Diff.Severity.ERROR;
import static org.kordamp.naum.diff.Diff.Type.MODIFIED;

/**
 * @author Andres Almiray
 */
@Data(staticConstructor = "methodDiffer")
@EqualsAndHashCode(callSuper = true)
public class MethodDiffer extends AbstractMemberDiffer<MethodInfo> {
    public static final String KEY_METHOD_MODIFIERS_MODIFIED = "method.modifiers.modified";
    public static final String KEY_METHOD_TYPE_MODIFIED = "method.type.modified";
    public static final String KEY_METHOD_EXCEPTION_REMOVED = "method.exception.removed";
    public static final String KEY_METHOD_EXCEPTION_ADDED = "method.exception.added";
    public static final String KEY_METHOD_ANNOTATION_REMOVED = "method.annotation.removed";
    public static final String KEY_METHOD_ANNOTATION_ADDED = "method.annotation.added";

    private final MethodInfo previous;
    private final MethodInfo next;

    @Override
    public Collection<Diff> diff() {
        if (previous.getContentHash().equals(next.getContentHash())) {
            return Collections.emptyList();
        }

        List<Diff> list = new ArrayList<>();

        // 1. modifiers
        checkModifiers(list, "method");

        // 2. type
        checkReturnType(list);

        // 3. type parameters

        // 4. arguments

        // 5. exceptions
        checkExceptions(list);

        // 6. annotations
        checkAnnotations(list, "method");

        return list;
    }

    private void checkReturnType(List<Diff> list) {
        if (!previous.getReturnType().equals(next.getReturnType())) {
            list.add(
                Diff.diff()
                    .severity(ERROR)
                    .type(MODIFIED)
                    .messageKey(KEY_METHOD_TYPE_MODIFIED)
                    .messageArg(getElementName())
                    .messageArg(previous.getReturnType())
                    .messageArg(next.getReturnType())
                    .build());
        }
    }

    private void checkExceptions(Collection<Diff> list) {
        if (!Arrays.equals(previous.getExceptions(), next.getExceptions())) {
            List<String> p = new ArrayList<>(asList(previous.getExceptions()));
            List<String> n = new ArrayList<>(asList(next.getExceptions()));

            List<String> c = new ArrayList<>();
            for (String i : p) {
                if (n.contains(i)) {
                    c.add(i);
                }
            }

            p.removeAll(c);
            n.removeAll(c);

            // anything left in p was removed
            for (String e : p) {
                list.add(
                    Diff.diff()
                        .severity(ERROR)
                        .type(Diff.Type.REMOVED)
                        .messageKey(KEY_METHOD_EXCEPTION_REMOVED)
                        .messageArg(getElementName())
                        .messageArg(e)
                        .build());
            }
            // anything left in n was added
            for (String e : n) {
                list.add(
                    Diff.diff()
                        .severity(ERROR)
                        .type(Diff.Type.ADDED)
                        .messageKey(KEY_METHOD_EXCEPTION_ADDED)
                        .messageArg(getElementName())
                        .messageArg(e)
                        .build());
            }
        }
    }
}
