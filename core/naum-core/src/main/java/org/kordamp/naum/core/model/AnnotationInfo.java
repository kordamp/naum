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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andres Almiray
 * @author Stephan Classen
 * @author Vitaly Tsaplin
 * @author Alexey Dubrovskiy
 * @since 0.1.0
 */
public class AnnotationInfo extends NamedInfo<AnnotationInfo> implements AnnotationValue {
    private final Map<String, AnnotationValue> values = new LinkedHashMap<>();

    private AnnotationInfo(String name) {
        super(name);
    }

    private static AnnotationInfo create(String name, Map<String, Object> values, Map<String, AnnotationValue> annotationValues) {
        if (name.startsWith("L") && name.endsWith(";")) {
            name = name.substring(1, name.length() - 1);
        }
        name = name.replace('/', '.');
        AnnotationInfo annotationInfo = new AnnotationInfo(name);
        if (values != null) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                AnnotationValue annotationValue = AnnotationValue.newSimpleValue(entry.getValue());
                annotationInfo.getValues().put(entry.getKey(), annotationValue);
            }
        }
        if (annotationValues != null) {
            annotationInfo.getValues().putAll(annotationValues);
        }
        return annotationInfo;
    }

    @Override
    public String getType() {
        return getName();
    }

    @Override
    public Object getValue() {
        return values;
    }

    @Override
    public String getValueAsString() {
        return values.toString();
    }

    @Override
    public String getContent() {
        StringBuilder b = new StringBuilder("A{N=")
            .append(getName());

        if (!values.isEmpty()) {
            b.append("#V=")
                .append(getValueContent());
        }

        b.append("}");

        return b.toString();
    }

    private String getValueContent() {
        final List<String> keys = new ArrayList<>(values.keySet());
        Collections.sort(keys);
        final StringBuilder b = new StringBuilder("{");

        boolean isFirst = true;
        for (String key : keys) {
            if (!isFirst) {
                b.append(", ");
            }
            b.append(key)
                .append("=")
                .append(values.get(key));

            isFirst = false;
        }

        b.append("}");

        return b.toString();
    }

    public String asString() {
        StringBuilder b = new StringBuilder("@")
            .append(getName());

        if (values.size() > 0) {
            b.append("(");

            boolean firstValue = true;
            for (Map.Entry<String, AnnotationValue> e : values.entrySet()) {
                if (!firstValue) {
                    b.append(", ");
                }
                b.append(e.getKey())
                    .append("=");

                if (e instanceof ArrayValue) {
                    b.append("[");
                    boolean firstInArray = true;
                    for (AnnotationValue value : ((ArrayValue) e).getValue()) {
                        if (!firstInArray) {
                            b.append(", ");
                        }
                        appendValue(b, value);
                        firstInArray = false;
                    }
                } else {
                    appendValue(b, e.getValue());
                }
                firstValue = false;
            }

            b.append(")");
        }

        return b.toString();
    }

    private void appendValue(StringBuilder b, AnnotationValue value) {
        final String type = value.getType();
        String quotes = type.equals(String.class.getName()) ? "\"" : "";
        quotes = type.equals(Character.class.getName()) ? "'" : quotes;
        b.append(quotes).append(value).append(quotes);
    }

    public static class AnnotationInfoBuilder {
        private String name;
        private List<String> values$key;
        private List<Object> values$value;
        private List<String> annotationValues$key;
        private List<AnnotationValue> annotationValues$value;

        public AnnotationInfo.AnnotationInfoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AnnotationInfo.AnnotationInfoBuilder value(String valueKey, Object valueValue) {
            if (this.values$key == null) {
                this.values$key = new ArrayList<>();
                this.values$value = new ArrayList<>();
            }
            this.values$key.add(valueKey);
            this.values$value.add(valueValue);
            return this;
        }

        public AnnotationInfo.AnnotationInfoBuilder values(final Map<? extends String, ? extends Object> values) {
            if (values == null) {
                throw new NullPointerException("values cannot be null");
            }
            if (this.values$key == null) {
                this.values$key = new ArrayList<>();
                this.values$value = new ArrayList<>();
            }
            for (final Map.Entry<? extends String, ? extends Object> entry : values.entrySet()) {
                this.values$key.add(entry.getKey());
                this.values$value.add(entry.getValue());
            }
            return this;
        }

        public AnnotationInfo.AnnotationInfoBuilder clearValues() {
            if (this.values$key != null) {
                this.values$key.clear();
                this.values$value.clear();
            }
            return this;
        }

        public AnnotationInfo.AnnotationInfoBuilder annotationValue(String annotationValueKey, AnnotationValue annotationValueValue) {
            if (this.annotationValues$key == null) {
                this.annotationValues$key = new ArrayList<>();
                this.annotationValues$value = new ArrayList<>();
            }
            this.annotationValues$key.add(annotationValueKey);
            this.annotationValues$value.add(annotationValueValue);
            return this;
        }

        public AnnotationInfo.AnnotationInfoBuilder annotationValues(Map<? extends String, ? extends AnnotationValue> annotationValues) {
            if (annotationValues == null) {
                throw new NullPointerException("annotationValues cannot be null");
            }
            if (this.annotationValues$key == null) {
                this.annotationValues$key = new ArrayList<>();
                this.annotationValues$value = new ArrayList<>();
            }
            for (Map.Entry<? extends String, ? extends AnnotationValue> entry : annotationValues.entrySet()) {
                this.annotationValues$key.add(entry.getKey());
                this.annotationValues$value.add(entry.getValue());
            }
            return this;
        }

        public AnnotationInfo.AnnotationInfoBuilder clearAnnotationValues() {
            if (this.annotationValues$key != null) {
                this.annotationValues$key.clear();
                this.annotationValues$value.clear();
            }
            return this;
        }

        public AnnotationInfo build() {
            Map<String, Object> values;
            switch (this.values$key == null ? 0 : this.values$key.size()) {
                case 0:
                    values = Collections.emptyMap();
                    break;
                case 1:
                    values = Collections.singletonMap(this.values$key.get(0), this.values$value.get(0));
                    break;
                default:
                    values = new LinkedHashMap<>(this.values$key.size() < 1073741824 ? 1 + this.values$key.size() + (this.values$key.size() - 3) / 3 : Integer.MAX_VALUE);
                    for (int $i = 0; $i < this.values$key.size(); $i++)
                        values.put(this.values$key.get($i), this.values$value.get($i));
                    values = Collections.unmodifiableMap(values);
            }
            Map<String, AnnotationValue> annotationValues;
            switch (this.annotationValues$key == null ? 0 : this.annotationValues$key.size()) {
                case 0:
                    annotationValues = Collections.emptyMap();
                    break;
                case 1:
                    annotationValues = Collections.singletonMap(this.annotationValues$key.get(0), this.annotationValues$value.get(0));
                    break;
                default:
                    annotationValues = new LinkedHashMap<>(this.annotationValues$key.size() < 1073741824 ? 1 + this.annotationValues$key.size() + (this.annotationValues$key.size() - 3) / 3 : Integer.MAX_VALUE);
                    for (int $i = 0; $i < this.annotationValues$key.size(); $i++)
                        annotationValues.put(this.annotationValues$key.get($i), this.annotationValues$value.get($i));
                    annotationValues = Collections.unmodifiableMap(annotationValues);
            }
            return AnnotationInfo.create(this.name, values, annotationValues);
        }

        @Override
        public String toString() {
            return "AnnotationInfo.AnnotationInfoBuilder(name=" + this.name + ", values$key=" + this.values$key + ", values$value=" + this.values$value + ", annotationValues$key=" + this.annotationValues$key + ", annotationValues$value=" + this.annotationValues$value + ")";
        }
    }

    public static AnnotationInfo.AnnotationInfoBuilder annotationInfo() {
        return new AnnotationInfo.AnnotationInfoBuilder();
    }

    public Map<String, AnnotationValue> getValues() {
        return this.values;
    }

    @Override
    public String toString() {
        return "AnnotationInfo(super=" + super.toString() + ", values=" + this.getValues() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof AnnotationInfo)) return false;
        final AnnotationInfo other = (AnnotationInfo) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$values = this.getValues();
        final Object other$values = other.getValues();
        if (this$values == null ? other$values != null : !this$values.equals(other$values)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AnnotationInfo;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $values = this.getValues();
        result = result * PRIME + ($values == null ? 43 : $values.hashCode());
        return result;
    }
}
