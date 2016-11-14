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
package org.kordamp.naum.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Almiray
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class AnnotatedInfo<S extends AnnotatedInfo<S>> extends NamedInfo<S> {

    @Getter
    private final List<AnnotationInfo> annotations = new ArrayList<>();

    protected AnnotatedInfo(String name) {
        super(name);
    }

    public S addToAnnotations(AnnotationInfo annotation) {
        annotations.add(annotation);
        Collections.sort(annotations);
        return self();
    }
}
