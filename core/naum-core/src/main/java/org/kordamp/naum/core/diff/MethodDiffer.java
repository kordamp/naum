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

import org.kordamp.naum.core.model.MethodInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
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
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.MODIFIED)
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
                        .severity(Diff.Severity.ERROR)
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
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.ADDED)
                        .messageKey(KEY_METHOD_EXCEPTION_ADDED)
                        .messageArg(getElementName())
                        .messageArg(e)
                        .build());
            }
        }
    }

    private MethodDiffer(final MethodInfo previous, final MethodInfo next) {
        this.previous = previous;
        this.next = next;
    }

    public static MethodDiffer methodDiffer(final MethodInfo previous, final MethodInfo next) {
        return new MethodDiffer(previous, next);
    }

    public MethodInfo getPrevious() {
        return this.previous;
    }

    public MethodInfo getNext() {
        return this.next;
    }

    @Override
    public String toString() {
        return "MethodDiffer(previous=" + this.getPrevious() + ", next=" + this.getNext() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MethodDiffer)) return false;
        final MethodDiffer other = (MethodDiffer) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$previous = this.getPrevious();
        final Object other$previous = other.getPrevious();
        if (this$previous == null ? other$previous != null : !this$previous.equals(other$previous)) return false;
        final Object this$next = this.getNext();
        final Object other$next = other.getNext();
        if (this$next == null ? other$next != null : !this$next.equals(other$next)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MethodDiffer;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $previous = this.getPrevious();
        result = result * PRIME + ($previous == null ? 43 : $previous.hashCode());
        final Object $next = this.getNext();
        result = result * PRIME + ($next == null ? 43 : $next.hashCode());
        return result;
    }
}
