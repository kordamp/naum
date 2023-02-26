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

import org.kordamp.naum.core.model.ConstructorInfo;

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
public class ConstructorDiffer extends AbstractMemberDiffer<ConstructorInfo> {
    public static final String KEY_CONSTRUCTOR_MODIFIERS_MODIFIED = "constructor.modifiers.modified";
    public static final String KEY_CONSTRUCTOR_EXCEPTION_REMOVED = "constructor.exception.removed";
    public static final String KEY_CONSTRUCTOR_EXCEPTION_ADDED = "constructor.exception.added";
    public static final String KEY_CONSTRUCTOR_ANNOTATION_REMOVED = "constructor.annotation.removed";
    public static final String KEY_CONSTRUCTOR_ANNOTATION_ADDED = "constructor.annotation.added";

    private final ConstructorInfo previous;
    private final ConstructorInfo next;

    @Override
    public Collection<Diff> diff() {
        if (previous.getContentHash().equals(next.getContentHash())) {
            return Collections.emptyList();
        }

        List<Diff> list = new ArrayList<>();

        // 1. modifiers
        checkModifiers(list, "constructor");

        // 2. arguments

        // 3. exceptions
        checkExceptions(list);

        // 4. annotations
        checkAnnotations(list, "constructor");

        return list;
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
                        .messageKey(KEY_CONSTRUCTOR_EXCEPTION_REMOVED)
                        .messageArg(e)
                        .build());
            }
            // anything left in n was added
            for (String e : n) {
                list.add(
                    Diff.diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.ADDED)
                        .messageKey(KEY_CONSTRUCTOR_EXCEPTION_ADDED)
                        .messageArg(e)
                        .build());
            }
        }
    }

    private ConstructorDiffer(final ConstructorInfo previous, final ConstructorInfo next) {
        this.previous = previous;
        this.next = next;
    }

    public static ConstructorDiffer constructorDiffer(final ConstructorInfo previous, final ConstructorInfo next) {
        return new ConstructorDiffer(previous, next);
    }

    public ConstructorInfo getPrevious() {
        return this.previous;
    }

    public ConstructorInfo getNext() {
        return this.next;
    }

    @Override
    public String toString() {
        return "ConstructorDiffer(previous=" + this.getPrevious() + ", next=" + this.getNext() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ConstructorDiffer)) return false;
        final ConstructorDiffer other = (ConstructorDiffer) o;
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
        return other instanceof ConstructorDiffer;
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
