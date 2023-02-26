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

import java.lang.reflect.Modifier;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public class FieldInfo extends MemberInfo<FieldInfo> {
    private final String type;
    private final Object value;

    private FieldInfo(String name, int modifiers, String type, Object value) {
        super(name, modifiers);
        this.type = type;
        this.value = value;
    }

    private static FieldInfo create(String name, int modifiers, String type, Object value) {
        value = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers) ? value : null;
        return new FieldInfo(name, modifiers, type, value);
    }

    @Override
    public String getContent() {
        StringBuilder b = new StringBuilder("F{N=")
            .append(getName())
            .append("#T=")
            .append(type)
            .append("#D=")
            .append(getModifiers());
        if (!getAnnotations().isEmpty()) {
            b.append("#A=[");
            for (int i = 0; i < getAnnotations().size(); i++) {
                if (i != 0) {
                    b.append(",");
                }
                b.append(getAnnotations().get(i).getContent());
            }
            b.append("]");
        }

        if (isClassConstant() && null != value) {
            b.append("#V=")
                .append(value);
        }
        b.append("}");
        return b.toString();
    }

    public boolean isClassConstant() {
        return Modifiers.isStatic(getModifiers()) && Modifiers.isFinal(getModifiers());
    }

    public static class FieldInfoBuilder {
        private String name;
        private int modifiers;
        private String type;
        private Object value;

        public FieldInfo.FieldInfoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FieldInfo.FieldInfoBuilder modifiers(int modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public FieldInfo.FieldInfoBuilder type(String type) {
            this.type = type;
            return this;
        }

        public FieldInfo.FieldInfoBuilder value(Object value) {
            this.value = value;
            return this;
        }

        public FieldInfo build() {
            return FieldInfo.create(this.name, this.modifiers, this.type, this.value);
        }

        @Override
        public String toString() {
            return "FieldInfo.FieldInfoBuilder(name=" + this.name + ", modifiers=" + this.modifiers + ", type=" + this.type + ", value=" + this.value + ")";
        }
    }

    public static FieldInfo.FieldInfoBuilder fieldInfo() {
        return new FieldInfo.FieldInfoBuilder();
    }

    public String getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "FieldInfo(super=" + super.toString() + ", type=" + this.getType() + ", value=" + this.getValue() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof FieldInfo)) return false;
        final FieldInfo other = (FieldInfo) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$value = this.getValue();
        final Object other$value = other.getValue();
        if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof FieldInfo;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $value = this.getValue();
        result = result * PRIME + ($value == null ? 43 : $value.hashCode());
        return result;
    }
}
