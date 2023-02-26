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

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public abstract class MemberInfo<S extends MemberInfo<S>> extends AnnotatedInfo<S> {
    private final int modifiers;

    protected MemberInfo(String name, int modifiers) {
        super(name);
        this.modifiers = modifiers;
    }

    public static boolean isInnerClass(MemberInfo member) {
        return member.getName().contains("$");
    }

    @Override
    public String toString() {
        return "MemberInfo(super=" + super.toString() + ", modifiers=" + this.getModifiers() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof MemberInfo)) return false;
        final MemberInfo<?> other = (MemberInfo<?>) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        if (this.getModifiers() != other.getModifiers()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof MemberInfo;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        result = result * PRIME + this.getModifiers();
        return result;
    }

    public int getModifiers() {
        return this.modifiers;
    }
}
