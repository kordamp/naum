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
public class InnerClassInfo extends MemberInfo<InnerClassInfo> {
    private InnerClassInfo(String name, int modifiers) {
        super(name, modifiers);
    }

    private static InnerClassInfo create(String name, int modifiers) {
        return new InnerClassInfo(name.replace('/', '.'), modifiers);
    }

    @Override
    public String getContent() {
        return "IC{N=" +
            getName() +
            "#D=" +
            getModifiers();
    }

    public static class InnerClassInfoBuilder {
        private String name;
        private int modifiers;

        public InnerClassInfo.InnerClassInfoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public InnerClassInfo.InnerClassInfoBuilder modifiers(int modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public InnerClassInfo build() {
            return InnerClassInfo.create(this.name, this.modifiers);
        }

        @Override
        public String toString() {
            return "InnerClassInfo.InnerClassInfoBuilder(name=" + this.name + ", modifiers=" + this.modifiers + ")";
        }
    }

    public static InnerClassInfo.InnerClassInfoBuilder innerClassInfo() {
        return new InnerClassInfo.InnerClassInfoBuilder();
    }

    @Override
    public String toString() {
        return "InnerClassInfo(super=" + super.toString() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof InnerClassInfo)) return false;
        final InnerClassInfo other = (InnerClassInfo) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof InnerClassInfo;
    }
}
