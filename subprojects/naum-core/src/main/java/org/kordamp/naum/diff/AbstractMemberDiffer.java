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

import org.kordamp.naum.model.MemberInfo;

import java.util.Collection;

import static org.kordamp.naum.model.Modifiers.modifiersAsString;

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
                    .messageArg(getElementName())
                    .messageArg(modifiersAsString(previous.getModifiers()))
                    .messageArg(previous.getModifiers())
                    .messageArg(modifiersAsString(next.getModifiers()))
                    .messageArg(next.getModifiers())
                    .build());
        }
    }
}
