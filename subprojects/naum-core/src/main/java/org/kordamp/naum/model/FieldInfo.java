/*
 * Copyright 2016-2017 the original author or authors.
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Modifier;

import static org.kordamp.naum.model.Modifiers.isFinal;
import static org.kordamp.naum.model.Modifiers.isStatic;

/**
 * @author Andres Almiray
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FieldInfo extends MemberInfo<FieldInfo> {
    private final String type;
    private final Object value;

    private FieldInfo(String name, int modifiers, String type, Object value) {
        super(name, modifiers);
        this.type = type;
        this.value = value;
    }

    @Builder(builderMethodName = "fieldInfo")
    public static FieldInfo create(@Nonnull String name, int modifiers, @Nonnull String type, @Nullable Object value) {
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
                if (i != 0) { b.append(","); }
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
        return isStatic(getModifiers()) && isFinal(getModifiers());
    }
}
