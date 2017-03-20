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
package org.kordamp.naum.diff;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andres Almiray
 */
@Data
public class Diff {
    public enum Severity {
        INFO,
        WARNING,
        ERROR
    }

    public enum Type {
        ADDED,
        REMOVED,
        MODIFIED
    }

    private final Severity severity;
    private final Type type;
    private final String messageKey;
    private final List<Object> messageArgs = new ArrayList<>();

    @Builder(builderMethodName = "diff")
    public static Diff create(@Nonnull Severity severity, @Nonnull Type type, @Nonnull String messageKey, @Nullable @Singular List<Object> messageArgs) {
        Diff diff = new Diff(severity, type, messageKey);
        if (messageArgs != null) {
            diff.getMessageArgs().addAll(messageArgs);
        }
        return diff;
    }

    public String asString() {
        final StringBuilder sb = new StringBuilder("Diff{");
        sb.append("severity=").append(severity);
        sb.append(", type=").append(type);
        sb.append(", messageKey='").append(messageKey).append('\'');
        sb.append(", messageArgs=").append(messageArgs);
        sb.append('}');
        return sb.toString();
    }
}
