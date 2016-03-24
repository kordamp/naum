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
import lombok.ToString;

import java.util.Arrays;

/**
 * @author Andres Almiray
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MethodInfo extends MemberInfo {
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

    @Builder
    public static MethodInfo create(String name, int modifiers, String genericTypes, String returnType, String argumentTypes, String[] exceptions) {
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
                if (i != 0) { b.append(","); }
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
                if (i != 0) { b.append(","); }
                b.append(exceptions[i]);
            }
            b.append("]");
        } b.append("}");

        return b.toString();
    }
}
