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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Andres Almiray
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class MemberInfo extends AnnotatedInfo {
    @Getter
    private final int modifiers;

    protected MemberInfo(String name, int modifiers) {
        super(name);
        this.modifiers = modifiers;
    }


    public static boolean isInnerClass(MemberInfo member) {
        return member.getName().contains("$");
    }
}
