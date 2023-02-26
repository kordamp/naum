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

import org.kordamp.naum.core.model.ClassInfo;
import org.kordamp.naum.core.model.InnerClassInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
public class ClassDiffer extends AbstractMemberDiffer<ClassInfo> {
    public static final String KEY_CLASS_VERSION_MODIFIED = "class.version.modified";
    public static final String KEY_CLASS_SUPERCLASS_MODIFIED = "class.superclass.modified";
    public static final String KEY_CLASS_INTERFACE_REMOVED = "class.interface.removed";
    public static final String KEY_CLASS_INTERFACE_ADDED = "class.interface.added";
    public static final String KEY_CLASS_INNERCLASS_REMOVED = "class.innerclass.removed";
    public static final String KEY_CLASS_INNERCLASS_ADDED = "class.innerclass.added";
    public static final String KEY_CLASS_MODIFIERS_MODIFIED = "class.modifiers.modified";
    public static final String KEY_CLASS_TYPE_MODIFIED = "class.type.modified";
    public static final String KEY_CLASS_ANNOTATION_REMOVED = "class.annotation.removed";
    public static final String KEY_CLASS_ANNOTATION_ADDED = "class.annotation.added";

    private final ClassInfo previous;
    private final ClassInfo next;

    @Override
    public Collection<Diff> diff() {
        if (previous.getContentHash().equals(next.getContentHash())) {
            return Collections.emptyList();
        }

        List<Diff> list = new ArrayList<>();

        // 0. type => class | interface | enum | annotation
        checkType(list);

        // 1. version
        checkVersion(list);

        // 2. modifiers
        checkModifiers(list, "class");

        // 3. superclass
        checkSuperclass(list);

        // 4. interfaces
        checkInterfaces(list);

        // 5. type parameters

        // 6. annotations
        checkAnnotations(list, "class");

        // 7. constructors

        // 8. fields

        // 9. methods

        // 10. inner classes
        checkInnerClasses(list);

        return list;
    }

    private void checkType(Collection<Diff> list) {
        if (previous.getType() != next.getType()) {
            list.add(
                Diff.diff()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.MODIFIED)
                    .messageKey(KEY_CLASS_TYPE_MODIFIED)
                    .messageArg(getElementName())
                    .messageArg(previous.getType().name().toLowerCase())
                    .messageArg(next.getType().name().toLowerCase())
                    .build());
        }
    }

    private void checkVersion(Collection<Diff> list) {
        if (previous.getVersion() != next.getVersion()) {
            list.add(
                Diff.diff()
                    .severity(Diff.Severity.WARNING)
                    .type(Diff.Type.MODIFIED)
                    .messageKey(KEY_CLASS_VERSION_MODIFIED)
                    .messageArg(getElementName())
                    .messageArg(previous.getVersion())
                    .messageArg(next.getVersion())
                    .build());
        }
    }

    private void checkSuperclass(Collection<Diff> list) {
        if (!previous.getSuperclass().equals(next.getSuperclass())) {
            list.add(
                Diff.diff()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.MODIFIED)
                    .messageKey(KEY_CLASS_SUPERCLASS_MODIFIED)
                    .messageArg(getElementName())
                    .messageArg(previous.getSuperclass())
                    .messageArg(next.getSuperclass())
                    .build());
        }
    }

    private void checkInterfaces(Collection<Diff> list) {
        if (!Arrays.equals(previous.getInterfaces(), next.getInterfaces())) {
            List<String> p = new ArrayList<>(asList(previous.getInterfaces()));
            List<String> n = new ArrayList<>(asList(next.getInterfaces()));

            List<String> c = new ArrayList<>();
            for (String i : p) {
                if (n.contains(i)) {
                    c.add(i);
                }
            }

            p.removeAll(c);
            n.removeAll(c);

            // anything left in p was removed
            for (String i : p) {
                list.add(
                    Diff.diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.REMOVED)
                        .messageKey(KEY_CLASS_INTERFACE_REMOVED)
                        .messageArg(getElementName())
                        .messageArg(i)
                        .build());
            }
            // anything left in n was added
            for (String i : n) {
                list.add(
                    Diff.diff()
                        .severity(Diff.Severity.ERROR)
                        .type(Diff.Type.ADDED)
                        .messageKey(KEY_CLASS_INTERFACE_ADDED)
                        .messageArg(getElementName())
                        .messageArg(i)
                        .build());
            }
        }
    }

    private void checkInnerClasses(final Collection<Diff> list) {
        Map<String, InnerClassInfo> p = innerClassesAsMap(getPrevious().getClasses());
        Map<String, InnerClassInfo> n = innerClassesAsMap(getNext().getClasses());

        // 1. remove equal elements
        Set<String> hashes = new HashSet<>(p.keySet());
        hashes.forEach(hash -> {
            if (n.containsKey(hash)) {
                p.remove(hash);
                n.remove(hash);
            }
        });

        // search for p.name == n.name
        // matches mean updates were made to that element
        hashes = new HashSet<>(p.keySet());
        hashes.forEach(hash -> {
            InnerClassInfo pic = p.get(hash);
            n.values().stream()
                .filter(it -> it.getName().equals(pic.getName()))
                .findAny()
                .ifPresent(nic -> {
                    p.remove(hash);
                    n.remove(nic.getContentHash());
                    checkModifiers(getPrevious(), getNext(), list, "innerclass");
                });
        });

        // anything left in p was removed
        for (InnerClassInfo c : p.values()) {
            list.add(
                Diff.diff()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.REMOVED)
                    .messageKey(KEY_CLASS_INNERCLASS_REMOVED)
                    .messageArg(getElementName())
                    .messageArg(c)
                    .build());
        }
        // anything left in n was added
        for (InnerClassInfo c : n.values()) {
            list.add(
                Diff.diff()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.ADDED)
                    .messageKey(KEY_CLASS_INNERCLASS_ADDED)
                    .messageArg(getElementName())
                    .messageArg(c)
                    .build());
        }
    }

    protected Map<String, InnerClassInfo> innerClassesAsMap(List<InnerClassInfo> innerClasses) {
        return innerClasses.stream()
            .collect(toMap(InnerClassInfo::getContentHash, identity()));
    }

    private ClassDiffer(final ClassInfo previous, final ClassInfo next) {
        this.previous = previous;
        this.next = next;
    }

    public static ClassDiffer classDiffer(final ClassInfo previous, final ClassInfo next) {
        return new ClassDiffer(previous, next);
    }

    public ClassInfo getPrevious() {
        return this.previous;
    }

    public ClassInfo getNext() {
        return this.next;
    }

    @Override
    public String toString() {
        return "ClassDiffer(previous=" + this.getPrevious() + ", next=" + this.getNext() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ClassDiffer)) return false;
        final ClassDiffer other = (ClassDiffer) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$previous = this.getPrevious();
        final Object other$previous = other.getPrevious();
        if (this$previous == null ? other$previous != null : !this$previous.equals(other$previous)) return false;
        final Object this$next = this.getNext();
        final Object other$next = other.getNext();
        if (this$next == null ? other$next != null : !this$next.equals(other$next)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ClassDiffer;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $previous = this.getPrevious();
        result = result * PRIME + ($previous == null ? 43 : $previous.hashCode());
        final Object $next = this.getNext();
        result = result * PRIME + ($next == null ? 43 : $next.hashCode());
        return result;
    }
}
