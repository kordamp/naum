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
package org.kordamp.naum.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public abstract class AnnotatedInfo<S extends AnnotatedInfo<S>> extends NamedInfo<S> {
    private final List<AnnotationInfo> annotations = new ArrayList<>();

    protected AnnotatedInfo(String name) {
        super(name);
    }

    public S addToAnnotations(AnnotationInfo annotation) {
        annotations.add(annotation);
        Collections.sort(annotations);
        return self();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AnnotatedInfo)) return false;
        final AnnotatedInfo<?> other = (AnnotatedInfo<?>) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$annotations = this.getAnnotations();
        final Object other$annotations = other.getAnnotations();
        if (this$annotations == null ? other$annotations != null : !this$annotations.equals(other$annotations)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AnnotatedInfo;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $annotations = this.getAnnotations();
        result = result * PRIME + ($annotations == null ? 43 : $annotations.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "AnnotatedInfo(super=" + super.toString() + ", annotations=" + this.getAnnotations() + ")";
    }

    public List<AnnotationInfo> getAnnotations() {
        return this.annotations;
    }
}
