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

import org.kordamp.naum.core.model.AnnotatedInfo;
import org.kordamp.naum.core.model.AnnotationInfo;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public abstract class AbstractDiffer<T extends AnnotatedInfo> implements Differ<T> {
    protected void checkAnnotations(Collection<Diff> list, String keyPrefix) {
        checkAnnotations(getPrevious(), getNext(), list, keyPrefix);
    }

    protected void checkAnnotations(T previous, T next, Collection<Diff> list, String keyPrefix) {
        List<AnnotationInfo> p = previous.getAnnotations();
        List<AnnotationInfo> n = next.getAnnotations();

        Set<AnnotationInfo> removed = new LinkedHashSet<>(p);
        removed.removeAll(n);

        Set<AnnotationInfo> added = new LinkedHashSet<>(n);
        added.removeAll(p);

        for (AnnotationInfo a : removed) {
            list.add(Diff.diff()
                .severity(Diff.Severity.ERROR)
                .type(Diff.Type.REMOVED)
                .messageKey(keyPrefix + ".annotation.removed")
                .messageArg(getElementName())
                .messageArg("@" + a.getName())
                .build()
            );
        }

        for (AnnotationInfo a : added) {
            list.add(Diff.diff()
                .severity(Diff.Severity.ERROR)
                .type(Diff.Type.ADDED)
                .messageKey(keyPrefix + ".annotation.added")
                .messageArg(getElementName())
                .messageArg("@" + a.getName())
                .build()
            );
        }
    }
}
