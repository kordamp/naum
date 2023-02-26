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

import java.util.Arrays;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public class ConstructorInfo extends MemberInfo<ConstructorInfo> {
    public static final String NAME = "<init>";

    private final String argumentTypes;
    private final String[] exceptions;

    private ConstructorInfo(int modifiers, String argumentTypes, String[] exceptions) {
        super(NAME, modifiers);
        this.argumentTypes = argumentTypes;
        this.exceptions = exceptions;
    }

    private static ConstructorInfo create(int modifiers, String argumentTypes, String[] exceptions) {
        String[] values = exceptions != null ? exceptions : EMPTY;
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].replace('/', '.');
        }
        Arrays.sort(values);
        argumentTypes = argumentTypes == null ? "" : argumentTypes;
        return new ConstructorInfo(modifiers, argumentTypes, values);
    }

    @Override
    public String getContent() {
        StringBuilder b = new StringBuilder("CT{")
            .append("D=")
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

        if (argumentTypes != null && argumentTypes.length() > 0) {
            b.append("#R=")
                .append(argumentTypes);
        }

        if (exceptions.length > 0) {
            b.append("#E=[");
            for (int i = 0; i < exceptions.length; i++) {
                if (i != 0) {
                    b.append(",");
                }
                b.append(exceptions[i]);
            }
            b.append("]");
        }
        b.append("}");

        return b.toString();
    }


    public static class ConstructorInfoBuilder {
        private int modifiers;
        private String argumentTypes;
        private String[] exceptions;

        public ConstructorInfo.ConstructorInfoBuilder modifiers(int modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public ConstructorInfo.ConstructorInfoBuilder argumentTypes(String argumentTypes) {
            this.argumentTypes = argumentTypes;
            return this;
        }

        public ConstructorInfo.ConstructorInfoBuilder exceptions(String[] exceptions) {
            this.exceptions = exceptions;
            return this;
        }

        public ConstructorInfo build() {
            return ConstructorInfo.create(this.modifiers, this.argumentTypes, this.exceptions);
        }

        @Override
        public String toString() {
            return "ConstructorInfo.ConstructorInfoBuilder(modifiers=" + this.modifiers + ", argumentTypes=" + this.argumentTypes + ", exceptions=" + java.util.Arrays.deepToString(this.exceptions) + ")";
        }
    }

    public static ConstructorInfo.ConstructorInfoBuilder constructorInfo() {
        return new ConstructorInfo.ConstructorInfoBuilder();
    }

    public String getArgumentTypes() {
        return this.argumentTypes;
    }

    public String[] getExceptions() {
        return this.exceptions;
    }

    @Override
    public String toString() {
        return "ConstructorInfo(super=" + super.toString() + ", argumentTypes=" + this.getArgumentTypes() + ", exceptions=" + java.util.Arrays.deepToString(this.getExceptions()) + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ConstructorInfo)) return false;
        final ConstructorInfo other = (ConstructorInfo) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$argumentTypes = this.getArgumentTypes();
        final Object other$argumentTypes = other.getArgumentTypes();
        if (this$argumentTypes == null ? other$argumentTypes != null : !this$argumentTypes.equals(other$argumentTypes))
            return false;
        if (!java.util.Arrays.deepEquals(this.getExceptions(), other.getExceptions())) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ConstructorInfo;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $argumentTypes = this.getArgumentTypes();
        result = result * PRIME + ($argumentTypes == null ? 43 : $argumentTypes.hashCode());
        result = result * PRIME + java.util.Arrays.deepHashCode(this.getExceptions());
        return result;
    }
}
