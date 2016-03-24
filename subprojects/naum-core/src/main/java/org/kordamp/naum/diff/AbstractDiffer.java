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

import org.kordamp.naum.model.AnnotatedInfo;
import org.kordamp.naum.model.AnnotationInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * @author Andres Almiray
 */
public abstract class AbstractDiffer<T extends AnnotatedInfo> implements Differ<T> {
    protected void diffAnnotations(Collection<Diff> list) {
        Map<String, AnnotationInfo> p = annotationsAsMap(getPrevious().getAnnotations());
        Map<String, AnnotationInfo> n = annotationsAsMap(getNext().getAnnotations());

        // 1. remove equal elements
        Set<String> hashes = new HashSet<>(p.keySet());
        hashes.forEach(hash -> {
            if (n.containsKey(hash)) {
                p.remove(hash);
                n.remove(hash);
            }
        });

        // search for p.name == n.name
        // matches mean updates were made to that element

        // anything left in p was removed
        for (AnnotationInfo a : p.values()) {
            list.add(
                Diff.builder()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.REMOVED)
                    .messageKey("class.annotation.removed")
                    .messageArg(a)
                    .build());
        }
        // anything left in n was added
        for (AnnotationInfo a : n.values()) {
            list.add(
                Diff.builder()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.ADDED)
                    .messageKey("class.annotation.added")
                    .messageArg(a)
                    .build());
        }
    }

    protected Map<String, AnnotationInfo> annotationsAsMap(List<AnnotationInfo> annotations) {
        return annotations.stream()
            .collect(toMap(AnnotationInfo::getContentHash, identity()));
    }
}
