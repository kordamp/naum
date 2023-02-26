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
public class MethodInfo extends MemberInfo<MethodInfo> {
    private final String genericTypes;
    private final String returnType;
    private final String argumentTypes;
    private final String[] exceptions;

    private MethodInfo(String name, int modifiers, String genericTypes, String returnType, String argumentTypes, String[] exceptions) {
        super(name, modifiers);
        this.genericTypes = genericTypes;
        this.returnType = returnType;
        this.argumentTypes = argumentTypes;
        this.exceptions = exceptions;
    }

    private static MethodInfo create(String name, int modifiers, String genericTypes, String returnType, String argumentTypes, String[] exceptions) {
        String[] values = exceptions != null ? exceptions : EMPTY;
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].replace('/', '.');
        }
        Arrays.sort(values);

        genericTypes = genericTypes != null ? genericTypes : "";
        if (genericTypes.startsWith("<") && genericTypes.endsWith(">")) {
            genericTypes = genericTypes.substring(1, genericTypes.length() - 1);
        }
        returnType = returnType != null ? returnType : "void";
        argumentTypes = argumentTypes != null ? argumentTypes : "";

        return new MethodInfo(name, modifiers, genericTypes, returnType, argumentTypes, values);
    }

    @Override
    public String getContent() {
        StringBuilder b = new StringBuilder("M{N=").append(getName());

        if (genericTypes != null && genericTypes.length() > 0) {
            b.append("#G=")
                .append(genericTypes);
        }

        b.append("#T=")
            .append(returnType)
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

    public static class MethodInfoBuilder {
        private String name;
        private int modifiers;
        private String genericTypes;
        private String returnType;
        private String argumentTypes;
        private String[] exceptions;

        public MethodInfo.MethodInfoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MethodInfo.MethodInfoBuilder modifiers(int modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public MethodInfo.MethodInfoBuilder genericTypes(String genericTypes) {
            this.genericTypes = genericTypes;
            return this;
        }

        public MethodInfo.MethodInfoBuilder returnType(String returnType) {
            this.returnType = returnType;
            return this;
        }

        public MethodInfo.MethodInfoBuilder argumentTypes(String argumentTypes) {
            this.argumentTypes = argumentTypes;
            return this;
        }

        public MethodInfo.MethodInfoBuilder exceptions(String[] exceptions) {
            this.exceptions = exceptions;
            return this;
        }

        public MethodInfo build() {
            return MethodInfo.create(this.name, this.modifiers, this.genericTypes, this.returnType, this.argumentTypes, this.exceptions);
        }

        @Override
        public String toString() {
            return "MethodInfo.MethodInfoBuilder(name=" + this.name + ", modifiers=" + this.modifiers + ", genericTypes=" + this.genericTypes + ", returnType=" + this.returnType + ", argumentTypes=" + this.argumentTypes + ", exceptions=" + java.util.Arrays.deepToString(this.exceptions) + ")";
        }
    }

    public static MethodInfo.MethodInfoBuilder methodInfo() {
        return new MethodInfo.MethodInfoBuilder();
    }

    public String getGenericTypes() {
        return this.genericTypes;
    }

    public String getReturnType() {
        return this.returnType;
    }

    public String getArgumentTypes() {
        return this.argumentTypes;
    }

    public String[] getExceptions() {
        return this.exceptions;
    }

    @Override
    public String toString() {
        return "MethodInfo(super=" + super.toString() + ", genericTypes=" + this.getGenericTypes() + ", returnType=" + this.getReturnType() + ", argumentTypes=" + this.getArgumentTypes() + ", exceptions=" + java.util.Arrays.deepToString(this.getExceptions()) + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MethodInfo)) return false;
        final MethodInfo other = (MethodInfo) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$genericTypes = this.getGenericTypes();
        final Object other$genericTypes = other.getGenericTypes();
        if (this$genericTypes == null ? other$genericTypes != null : !this$genericTypes.equals(other$genericTypes))
            return false;
        final Object this$returnType = this.getReturnType();
        final Object other$returnType = other.getReturnType();
        if (this$returnType == null ? other$returnType != null : !this$returnType.equals(other$returnType))
            return false;
        final Object this$argumentTypes = this.getArgumentTypes();
        final Object other$argumentTypes = other.getArgumentTypes();
        if (this$argumentTypes == null ? other$argumentTypes != null : !this$argumentTypes.equals(other$argumentTypes))
            return false;
        if (!java.util.Arrays.deepEquals(this.getExceptions(), other.getExceptions())) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MethodInfo;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $genericTypes = this.getGenericTypes();
        result = result * PRIME + ($genericTypes == null ? 43 : $genericTypes.hashCode());
        final Object $returnType = this.getReturnType();
        result = result * PRIME + ($returnType == null ? 43 : $returnType.hashCode());
        final Object $argumentTypes = this.getArgumentTypes();
        result = result * PRIME + ($argumentTypes == null ? 43 : $argumentTypes.hashCode());
        result = result * PRIME + java.util.Arrays.deepHashCode(this.getExceptions());
        return result;
    }
}
