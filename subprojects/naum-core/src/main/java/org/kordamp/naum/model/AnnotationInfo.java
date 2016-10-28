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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Andres Almiray
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AnnotationInfo extends AnnotatedInfo<AnnotationInfo> {
    private final Map<String, Object> values = new LinkedHashMap<>();
    private final Map<String, EnumEntry> enumValues = new LinkedHashMap<>();

    private AnnotationInfo(String name) {
        super(name);
    }

    @Builder(builderMethodName = "annotationInfo")
    public static AnnotationInfo create(@Nonnull String name, @Nonnull @Singular Map<String, Object> values, @Nonnull @Singular Map<String, EnumEntry> enumValues) {
        if (name.startsWith("L") && name.endsWith(";")) {
            name = name.substring(1, name.length() - 1);
        }
        name = name.replace('/', '.');
        AnnotationInfo annotationInfo = new AnnotationInfo(name);
        if (values != null) {
            annotationInfo.getValues().putAll(values);
        }
        if (enumValues != null) {
            annotationInfo.getEnumValues().putAll(enumValues);
        }
        return annotationInfo;
    }

    @Override
    public String getContent() {
        StringBuilder b = new StringBuilder("A{N=")
            .append(getName());
        if (!getAnnotations().isEmpty()) {
            b.append("#A=[");
            for (int i = 0; i < getAnnotations().size(); i++) {
                if (i != 0) { b.append(","); }
                b.append(getAnnotations().get(i).getContent());
            }
            b.append("]");
        }

        if (!values.isEmpty()) {
            b.append("#V=")
                .append(values);
        }
        if (!enumValues.isEmpty()) {
            b.append("#EV=")
                .append(enumValues);
        }

        b.append("}");

        return b.toString();
    }

    public String asString() {
        StringBuilder b = new StringBuilder("@")
            .append(getName());

        if (values.size() > 0 || enumValues.size() > 0) {
            b.append("(");

            int i = 0;
            for (Map.Entry<String, Object> e : values.entrySet()) {
                if (i != 0) {
                    b.append(", ");
                }
                b.append(e.getKey())
                    .append("=");

                String quotes = e.getValue() instanceof String ? "\"" : "";
                quotes = e.getValue() != null && Character.TYPE.isAssignableFrom(e.getValue().getClass()) ? "'" : quotes;

                b.append(quotes)
                    .append(e.getValue())
                    .append(quotes);

                i++;
            }

            i = 0;
            for (Map.Entry<String, EnumEntry> e : enumValues.entrySet()) {
                if (i != 0) {
                    b.append(", ");
                }
                b.append(e.getKey())
                    .append("=")
                    .append(e.getValue());

                i++;
            }

            b.append(")");
        }

        return b.toString();
    }

    public static EnumEntry newEnumEntry(String type, String value) {
        return new EnumEntry(type, value);
    }

    @Data
    public static class EnumEntry {
        private final String type;
        private final String value;

        public String toString() {
            return type + "." + value;
        }
    }
}
