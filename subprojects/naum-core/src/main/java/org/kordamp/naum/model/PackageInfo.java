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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Almiray
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PackageInfo extends AnnotatedInfo {
    private final List<InnerClassInfo> classes = new ArrayList<>();

    private PackageInfo(String name) {
        super(name);
    }

    @Builder(builderMethodName = "packageInfo")
    public static PackageInfo create(@Nonnull String name) {
        return new PackageInfo(name.replace('/', '.'));
    }

    public void addToClasses(InnerClassInfo klass) {
        classes.add(klass);
        Collections.sort(classes);
    }

    @Override
    public String getContent() {
        StringBuilder b = new StringBuilder("P{N=")
            .append(getName());

        if (!getAnnotations().isEmpty()) {
            b.append("#A=[");
            for (int i = 0; i < getAnnotations().size(); i++) {
                if (i != 0) { b.append(","); }
                b.append(getAnnotations().get(i).getContent());
            }
            b.append("]");
        }

        if (!getClasses().isEmpty()) {
            b.append("#C=[");
            for (int i = 0; i < getClasses().size(); i++) {
                if (i != 0) { b.append(","); }
                b.append(getClasses().get(i).getContent());
            }
            b.append("]");
        }

        b.append("}");

        return b.toString();
    }
}
