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
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Almiray
 */
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(exclude = "contentHash")
public abstract class AnnotatedInfo<S extends AnnotatedInfo<S>> implements Comparable<S> {
    protected static final String[] EMPTY = new String[0];

    @Getter
    private final String name;
    @Getter
    private final List<AnnotationInfo> annotations = new ArrayList<>();

    private String contentHash;

    protected final S self() {
        return (S) this;
    }

    @Override
    public int compareTo(AnnotatedInfo o) {
        if (o == null) { return -1; }
        return name.compareTo(o.name);
    }

    public S addToAnnotations(AnnotationInfo annotation) {
        annotations.add(annotation);
        Collections.sort(annotations);
        return self();
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
}
