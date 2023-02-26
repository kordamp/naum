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

import org.kordamp.naum.core.model.AnnotationInfo;
import org.kordamp.naum.core.model.AnnotationValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Andres Almiray
 * @author Maxim Moschko
 * @author Stephan Classen
 * @author Vitaly Tsaplin
 * @author Alexey Dubrovskiy
 * @since 0.1.0
 */
public class AnnotationDiffer implements Differ<AnnotationInfo> {
    public static final String KEY_ANNOTATION_VALUE_ADDED = "annotation.value.added";
    public static final String KEY_ANNOTATION_VALUE_REMOVED = "annotation.value.removed";
    public static final String KEY_ANNOTATION_VALUE_MODIFIED = "annotation.value.modified";

    private final AnnotationInfo previous;
    private final AnnotationInfo next;

    @Override
    public Collection<Diff> diff() {
        if (previous.getContentHash().equals(next.getContentHash())) {
            return Collections.emptyList();
        }

        List<Diff> list = new ArrayList<>();

        // 1. values
        checkValues(list);

        return list;
    }

    private void checkValues(Collection<Diff> list) {
        Set<String> prevKeySet = previous.getValues().keySet();
        Set<String> nextKeySet = next.getValues().keySet();

        Set<String> removedKeys = new LinkedHashSet<>(prevKeySet);
        removedKeys.removeAll(nextKeySet);

        Collection<String> addedKeys = new LinkedHashSet<>(nextKeySet);
        addedKeys.removeAll(prevKeySet);

        Collection<String> sameKeys = new LinkedHashSet<>(nextKeySet);
        sameKeys.retainAll(prevKeySet);

        for (String key : removedKeys) {
            list.add(Diff.diff()
                .severity(Diff.Severity.ERROR)
                .type(Diff.Type.REMOVED)
                .messageKey(KEY_ANNOTATION_VALUE_REMOVED)
                .messageArg(getElementName())
                .messageArg(key)
                .build()
            );
        }

        for (String key : addedKeys) {
            list.add(Diff.diff()
                .severity(Diff.Severity.ERROR)
                .type(Diff.Type.ADDED)
                .messageKey(KEY_ANNOTATION_VALUE_ADDED)
                .messageArg(getElementName())
                .messageArg(key)
                .build()
            );
        }

        for (String key : sameKeys) {
            AnnotationValue prevValue = previous.getValues().get(key);
            AnnotationValue nextValue = next.getValues().get(key);
            if (!Objects.equals(prevValue, nextValue)) {
                list.add(
                    Diff.diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.MODIFIED)
                        .messageKey(KEY_ANNOTATION_VALUE_MODIFIED)
                        .messageArg(getElementName())
                        .messageArg(key)
                        .messageArg(prevValue.getType())
                        .messageArg(prevValue.getValue())
                        .messageArg(nextValue.getType())
                        .messageArg(nextValue.getValue())
                        .build()
                );
            }
        }
    }

    private AnnotationDiffer(final AnnotationInfo previous, final AnnotationInfo next) {
        this.previous = previous;
        this.next = next;
    }

    public static AnnotationDiffer annotationDiffer(final AnnotationInfo previous, final AnnotationInfo next) {
        return new AnnotationDiffer(previous, next);
    }

    public AnnotationInfo getPrevious() {
        return this.previous;
    }

    public AnnotationInfo getNext() {
        return this.next;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AnnotationDiffer)) return false;
        final AnnotationDiffer other = (AnnotationDiffer) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$previous = this.getPrevious();
        final Object other$previous = other.getPrevious();
        if (this$previous == null ? other$previous != null : !this$previous.equals(other$previous)) return false;
        final Object this$next = this.getNext();
        final Object other$next = other.getNext();
        if (this$next == null ? other$next != null : !this$next.equals(other$next)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AnnotationDiffer;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $previous = this.getPrevious();
        result = result * PRIME + ($previous == null ? 43 : $previous.hashCode());
        final Object $next = this.getNext();
        result = result * PRIME + ($next == null ? 43 : $next.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "AnnotationDiffer(previous=" + this.getPrevious() + ", next=" + this.getNext() + ")";
    }
}
