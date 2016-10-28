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

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.kordamp.naum.model.AnnotationInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.kordamp.naum.diff.Diff.Severity.ERROR;
import static org.kordamp.naum.diff.Diff.Type.ADDED;
import static org.kordamp.naum.diff.Diff.Type.MODIFIED;

/**
 * @author Andres Almiray
 * @author Maxim Moschko
 * @author Stephan Classen
 */
@Data(staticConstructor = "annotationDiffer")
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

        // 2. enumValues

        // 3. annotations

        return list;
    }

    private void checkValues(List<Diff> inList) {
        Set<String> prevKeySet = previous.getValues().keySet();
        Set<String> nextKeySet = next.getValues().keySet();

        Collection<String> removedKeys = CollectionUtils.subtract(prevKeySet, nextKeySet);
        Collection<String> addedKeys = CollectionUtils.subtract(nextKeySet, prevKeySet);
        Collection<String> sameKeys = CollectionUtils.intersection(nextKeySet, prevKeySet);

        for (String key : addedKeys) {
            inList.add(Diff.diff()
                .severity(ERROR)
                .type(ADDED)
                .messageKey(KEY_ANNOTATION_VALUE_ADDED)
                .messageArg(getElementName())
                .messageArg(key)
                .build()
            );
        }
        for (String key : removedKeys) {
            inList.add(Diff.diff()
                .severity(ERROR)
                .type(Diff.Type.REMOVED)
                .messageKey(KEY_ANNOTATION_VALUE_REMOVED)
                .messageArg(getElementName())
                .messageArg(key)
                .build()
            );
        }
        for (String key : sameKeys) {
            Object prevValue = previous.getValues().get(key);
            Object nextValue = next.getValues().get(key);
            if (!Objects.equals(prevValue, nextValue)) {
                inList.add(
                    Diff.diff()
                        .severity(ERROR)
                        .type(MODIFIED)
                        .messageKey(KEY_ANNOTATION_VALUE_MODIFIED)
                        .messageArg(getElementName())
                        .messageArg(key)
                        .messageArg(prevValue.getClass().getName())
                        .messageArg(prevValue)
                        .messageArg(nextValue.getClass().getName())
                        .messageArg(nextValue)
                        .build()
                );
            }
        }
    }
}
