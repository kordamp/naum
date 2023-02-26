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

import org.kordamp.naum.core.model.FieldInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Almiray
 * @author Jochen Theodorou
 * @since 0.1.0
 */
public class FieldDiffer extends AbstractMemberDiffer<FieldInfo> {
    public static final String KEY_FIELD_MODIFIERS_MODIFIED = "field.modifiers.modified";
    public static final String KEY_FIELD_TYPE_MODIFIED = "field.type.modified";
    public static final String KEY_FIELD_VALUE_MODIFIED = "field.value.modified";
    public static final String KEY_FIELD_ANNOTATION_REMOVED = "field.annotation.removed";
    public static final String KEY_FIELD_ANNOTATION_ADDED = "field.annotation.added";

    private final FieldInfo previous;
    private final FieldInfo next;

    @Override
    public Collection<Diff> diff() {
        if (previous.getContentHash().equals(next.getContentHash())) {
            return Collections.emptyList();
        }

        List<Diff> list = new ArrayList<>();

        // 1. modifiers
        checkModifiers(list, "field");

        // 2. type
        checkType(list);

        // 3. value
        checkValue(list);

        // 4. annotations
        checkAnnotations(list, "field");

        return list;
    }

    private boolean isEquals(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null ^ b == null) {
            return false;
        }
        return a.equals(b);
    }

    private void checkValue(List<Diff> list) {
        if (!isEquals(previous.getValue(), next.getValue())) {
            list.add(
                Diff.diff()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.MODIFIED)
                    .messageKey(KEY_FIELD_VALUE_MODIFIED)
                    .messageArg(getElementName())
                    .messageArg(previous.getValue())
                    .messageArg(next.getValue())
                    .build());
        }
    }

    private void checkType(List<Diff> list) {
        if (!previous.getType().equals(next.getType())) {
            list.add(
                Diff.diff()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.MODIFIED)
                    .messageKey(KEY_FIELD_TYPE_MODIFIED)
                    .messageArg(getElementName())
                    .messageArg(previous.getType())
                    .messageArg(next.getType())
                    .build());
        }
    }

    private FieldDiffer(final FieldInfo previous, final FieldInfo next) {
        this.previous = previous;
        this.next = next;
    }

    public static FieldDiffer fieldDiffer(final FieldInfo previous, final FieldInfo next) {
        return new FieldDiffer(previous, next);
    }

    public FieldInfo getPrevious() {
        return this.previous;
    }

    public FieldInfo getNext() {
        return this.next;
    }

    @Override
    public String toString() {
        return "FieldDiffer(previous=" + this.getPrevious() + ", next=" + this.getNext() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof FieldDiffer)) return false;
        final FieldDiffer other = (FieldDiffer) o;
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
        return other instanceof FieldDiffer;
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
