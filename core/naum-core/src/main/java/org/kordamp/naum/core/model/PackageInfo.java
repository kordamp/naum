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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public class PackageInfo extends AnnotatedInfo<PackageInfo> {
    private final List<InnerClassInfo> classes = new ArrayList<>();

    private PackageInfo(String name) {
        super(name);
    }

    private static PackageInfo create(String name) {
        return new PackageInfo(name.replace('/', '.'));
    }

    public PackageInfo addToClasses(InnerClassInfo klass) {
        classes.add(klass);
        Collections.sort(classes);
        return this;
    }

    @Override
    public String getContent() {
        StringBuilder b = new StringBuilder("P{N=")
            .append(getName());

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

        if (!getClasses().isEmpty()) {
            b.append("#C=[");
            for (int i = 0; i < getClasses().size(); i++) {
                if (i != 0) {
                    b.append(",");
                }
                b.append(getClasses().get(i).getContent());
            }
            b.append("]");
        }

        b.append("}");

        return b.toString();
    }

    public static class PackageInfoBuilder {
        private String name;

        public PackageInfo.PackageInfoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PackageInfo build() {
            return PackageInfo.create(this.name);
        }

        @Override
        public String toString() {
            return "PackageInfo.PackageInfoBuilder(name=" + this.name + ")";
        }
    }

    public static PackageInfo.PackageInfoBuilder packageInfo() {
        return new PackageInfo.PackageInfoBuilder();
    }

    public List<InnerClassInfo> getClasses() {
        return this.classes;
    }

    @Override
    public String toString() {
        return "PackageInfo(super=" + super.toString() + ", classes=" + this.getClasses() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof PackageInfo)) return false;
        final PackageInfo other = (PackageInfo) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$classes = this.getClasses();
        final Object other$classes = other.getClasses();
        if (this$classes == null ? other$classes != null : !this$classes.equals(other$classes)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PackageInfo;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $classes = this.getClasses();
        result = result * PRIME + ($classes == null ? 43 : $classes.hashCode());
        return result;
    }
}
