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
package org.kordamp.naum.diff;

import org.kordamp.naum.model.MemberInfo;

import java.util.Collection;

import static org.kordamp.naum.model.Modifiers.isAbstract;
import static org.kordamp.naum.model.Modifiers.isDefault;
import static org.kordamp.naum.model.Modifiers.isFinal;
import static org.kordamp.naum.model.Modifiers.isNative;
import static org.kordamp.naum.model.Modifiers.isPrivate;
import static org.kordamp.naum.model.Modifiers.isProtected;
import static org.kordamp.naum.model.Modifiers.isPublic;
import static org.kordamp.naum.model.Modifiers.isStatic;
import static org.kordamp.naum.model.Modifiers.isStrict;
import static org.kordamp.naum.model.Modifiers.isSynchronized;
import static org.kordamp.naum.model.Modifiers.isTransient;
import static org.kordamp.naum.model.Modifiers.isVolatile;

/**
 * @author Andres Almiray
 */
public abstract class AbstractMemberDiffer<T extends MemberInfo> extends AbstractDiffer<T> {
    protected void checkModifiers(Collection<Diff> list, String keyPrefix) {
        checkModifiers(getPrevious(), getNext(), list, keyPrefix);
    }

    protected <M extends MemberInfo> void checkModifiers(M previous, M next, Collection<Diff> list, String keyPrefix) {
        if (getPrevious().getModifiers() != getNext().getModifiers()) {
            list.add(
                Diff.diff()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.MODIFIED)
                    .messageKey(keyPrefix + ".modifiers.modified")
                    .messageArg(previous.getName())
                    .messageArg(modifiersAsString(previous.getModifiers()))
                    .messageArg(previous.getModifiers())
                    .messageArg(modifiersAsString(next.getModifiers()))
                    .messageArg(next.getModifiers())
                    .build());
        }
    }

    public static String modifiersAsString(int modifiers) {
        StringBuilder b = new StringBuilder();

        if (isPublic(modifiers)) {
            b.append("public ");
        } else if (isProtected(modifiers)) {
            b.append("protected ");
        } else if (isPrivate(modifiers)) {
            b.append("private ");
        }

        if (isStatic(modifiers)) {
            b.append("static ");
        }
        if (isFinal(modifiers)) {
            b.append("final ");
        }
        if (isAbstract(modifiers)) {
            b.append("abstract ");
        }
        if (isDefault(modifiers)) {
            b.append("default ");
        }
        if (isVolatile(modifiers)) {
            b.append("volatile ");
        }
        if (isTransient(modifiers)) {
            b.append("transient ");
        }
        if (isSynchronized(modifiers)) {
            b.append("synchronized ");
        }
        if (isStrict(modifiers)) {
            b.append("strict ");
        }
        if (isNative(modifiers)) {
            b.append("native ");
        }

        return b.toString().trim();
    }
}
