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
package org.kordamp.naum.core.diff;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
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

    public static Diff create(Severity severity, Type type, String messageKey, List<Object> messageArgs) {
        Diff diff = new Diff(severity, type, messageKey);
        if (messageArgs != null) {
            diff.getMessageArgs().addAll(messageArgs);
        }
        return diff;
    }

    public String asString() {
        return "Diff{" + "severity=" + severity +
            ", type=" + type +
            ", messageKey='" + messageKey + '\'' +
            ", messageArgs=" + messageArgs +
            '}';
    }

    public static class DiffBuilder {
        private Severity severity;
        private Type type;
        private String messageKey;
        private java.util.ArrayList<Object> messageArgs;

        public Diff.DiffBuilder severity(final Severity severity) {
            this.severity = severity;
            return this;
        }

        public Diff.DiffBuilder type(final Type type) {
            this.type = type;
            return this;
        }

        public Diff.DiffBuilder messageKey(final String messageKey) {
            this.messageKey = messageKey;
            return this;
        }

        public Diff.DiffBuilder messageArg(final Object messageArg) {
            if (this.messageArgs == null) this.messageArgs = new java.util.ArrayList<Object>();
            this.messageArgs.add(messageArg);
            return this;
        }

        public Diff.DiffBuilder messageArgs(final java.util.Collection<? extends Object> messageArgs) {
            if (messageArgs == null) {
                throw new NullPointerException("messageArgs cannot be null");
            }
            if (this.messageArgs == null) this.messageArgs = new java.util.ArrayList<Object>();
            this.messageArgs.addAll(messageArgs);
            return this;
        }

        public Diff.DiffBuilder clearMessageArgs() {
            if (this.messageArgs != null) this.messageArgs.clear();
            return this;
        }

        public Diff build() {
            java.util.List<Object> messageArgs;
            switch (this.messageArgs == null ? 0 : this.messageArgs.size()) {
            case 0:
                messageArgs = java.util.Collections.emptyList();
                break;
            case 1:
                messageArgs = java.util.Collections.singletonList(this.messageArgs.get(0));
                break;
            default:
                messageArgs = java.util.Collections.unmodifiableList(new java.util.ArrayList<Object>(this.messageArgs));
            }
            return Diff.create(this.severity, this.type, this.messageKey, messageArgs);
        }

        @Override
        public String toString() {
            return "Diff.DiffBuilder(severity=" + this.severity + ", type=" + this.type + ", messageKey=" + this.messageKey + ", messageArgs=" + this.messageArgs + ")";
        }
    }

    public static Diff.DiffBuilder diff() {
        return new Diff.DiffBuilder();
    }

    public Diff(final Severity severity, final Type type, final String messageKey) {
        this.severity = severity;
        this.type = type;
        this.messageKey = messageKey;
    }

    public Severity getSeverity() {
        return this.severity;
    }

    public Type getType() {
        return this.type;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public List<Object> getMessageArgs() {
        return this.messageArgs;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Diff)) return false;
        final Diff other = (Diff) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$severity = this.getSeverity();
        final Object other$severity = other.getSeverity();
        if (this$severity == null ? other$severity != null : !this$severity.equals(other$severity)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$messageKey = this.getMessageKey();
        final Object other$messageKey = other.getMessageKey();
        if (this$messageKey == null ? other$messageKey != null : !this$messageKey.equals(other$messageKey)) return false;
        final Object this$messageArgs = this.getMessageArgs();
        final Object other$messageArgs = other.getMessageArgs();
        if (this$messageArgs == null ? other$messageArgs != null : !this$messageArgs.equals(other$messageArgs)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Diff;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $severity = this.getSeverity();
        result = result * PRIME + ($severity == null ? 43 : $severity.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $messageKey = this.getMessageKey();
        result = result * PRIME + ($messageKey == null ? 43 : $messageKey.hashCode());
        final Object $messageArgs = this.getMessageArgs();
        result = result * PRIME + ($messageArgs == null ? 43 : $messageArgs.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Diff(severity=" + this.getSeverity() + ", type=" + this.getType() + ", messageKey=" + this.getMessageKey() + ", messageArgs=" + this.getMessageArgs() + ")";
    }
}
