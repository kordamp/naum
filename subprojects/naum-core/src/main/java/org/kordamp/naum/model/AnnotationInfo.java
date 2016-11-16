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

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import javax.annotation.Nonnull;
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
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AnnotationInfo extends NamedInfo<AnnotationInfo> implements AnnotationValue {
    private final Map<String, AnnotationValue> values = new LinkedHashMap<>();

    private AnnotationInfo(String name) {
        super(name);
    }

    @Builder(builderMethodName = "annotationInfo")
    public static AnnotationInfo create(@Nonnull String name,
                                        @Nonnull @Singular Map<String, Object> values,
                                        @Nonnull @Singular Map<String, AnnotationValue> annotationValues) {
        if (name.startsWith("L") && name.endsWith(";")) {
            name = name.substring(1, name.length() - 1);
        }
        name = name.replace('/', '.');
        AnnotationInfo annotationInfo = new AnnotationInfo(name);
        if (values != null) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                AnnotationValue annotationValue = AnnotationValue.newSimpleValue(entry.getValue().getClass(), entry.getValue());
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

            int i = 0;
            for (Map.Entry<String, AnnotationValue> e : values.entrySet()) {
                if (i != 0) {
                    b.append(", ");
                }
                b.append(e.getKey())
                    .append("=");

                final String type = e.getValue().getType();
                String quotes = type.equals(String.class.getName()) ? "\"" : "";
                quotes = type.equals(Character.class.getName()) ? "'" : quotes;

                b.append(quotes)
                    .append(e.getValue())
                    .append(quotes);

                i++;
            }

            b.append(")");
        }

        return b.toString();
    }
}
