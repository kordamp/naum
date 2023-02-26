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

import java.util.List;

import static org.kordamp.naum.util.ObjectUtils.isNotEmpty;

/**
 * @author Stephan Classen
 * @since 0.1.0
 */
public class ArrayValue implements AnnotationValue {
    private final List<AnnotationValue> value;

    @Override
    public String getType() {
        if (isNotEmpty(value)) {
            Object head = value.get(0);
            return ((AnnotationValue) head).getType() + "[]";
        }
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public String getValueAsString() {
        return value.toString();
    }

    public ArrayValue(final List<AnnotationValue> value) {
        this.value = value;
    }

    public List<AnnotationValue> getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ArrayValue)) return false;
        final ArrayValue other = (ArrayValue) o;
        if (!other.canEqual(this)) return false;
        final Object this$value = this.getValue();
        final Object other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ArrayValue;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $value = this.getValue();
        result = result * PRIME + ($value == null ? 43 : $value.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ArrayValue(value=" + this.getValue() + ")";
    }
}
