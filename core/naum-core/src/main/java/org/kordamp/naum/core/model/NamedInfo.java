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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Stephan Classen
 * @since 0.1.0
 */
public abstract class NamedInfo<S extends NamedInfo<S>> implements Comparable<S> {
    protected static final String[] EMPTY = new String[0];
    private final String name;

    private String contentHash;

    protected final S self() {
        return (S) this;
    }

    @Override
    public int compareTo(NamedInfo o) {
        if (o == null) {
            return -1;
        }
        return name.compareTo(o.name);
    }

    public abstract String getContent();

    public final String getContentHash() {
        if (contentHash == null) {
            contentHash = toSHA1(getContent());
        }
        return contentHash;
    }

    private static String toSHA1(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(content.getBytes());

            String result = "";
            for (byte b : bytes) {
                result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public NamedInfo(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof NamedInfo)) return false;
        final NamedInfo<?> other = (NamedInfo<?>) o;
        if (!other.canEqual(this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NamedInfo;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "NamedInfo(name=" + this.getName() + ")";
    }

    public String getName() {
        return this.name;
    }
}
