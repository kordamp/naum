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

import org.apache.commons.collections4.CollectionUtils;
import org.kordamp.naum.model.AnnotatedInfo;
import org.kordamp.naum.model.AnnotationInfo;

import java.util.Collection;
import java.util.List;

import static org.kordamp.naum.diff.Diff.Severity.ERROR;
import static org.kordamp.naum.diff.Diff.Type.ADDED;
import static org.kordamp.naum.diff.Diff.Type.REMOVED;

/**
 * @author Andres Almiray
 */
public abstract class AbstractDiffer<T extends AnnotatedInfo> implements Differ<T> {
    protected void checkAnnotations(Collection<Diff> list, String keyPrefix) {
        checkAnnotations(getPrevious(), getNext(), list, keyPrefix);
    }

    protected void checkAnnotations(T previous, T next, Collection<Diff> list, String keyPrefix) {
        List<AnnotationInfo> p = previous.getAnnotations();
        List<AnnotationInfo> n = next.getAnnotations();

        Collection<AnnotationInfo> removed = CollectionUtils.subtract(p, n);
        Collection<AnnotationInfo> added = CollectionUtils.subtract(n, p);

        for (AnnotationInfo a : removed) {
            list.add(Diff.diff()
                .severity(ERROR)
                .type(REMOVED)
                .messageKey(keyPrefix + ".annotation.removed")
                .messageArg(getElementName())
                .messageArg("@" + a.getName())
                .build()
            );
        }

        for (AnnotationInfo a : added) {
            list.add(Diff.diff()
                .severity(ERROR)
                .type(ADDED)
                .messageKey(keyPrefix + ".annotation.added")
                .messageArg(getElementName())
                .messageArg("@" + a.getName())
                .build()
            );
        }

    }
}
