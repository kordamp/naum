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

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kordamp.naum.model.FieldInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Andres Almiray
 */
@Data(staticConstructor = "fieldDiffer")
@EqualsAndHashCode(callSuper = true)
public class FieldDiffer extends AbstractMemberDiffer<FieldInfo> {
    public static final String KEY_FIELD_MODIFIERS_MODIFIED = "field.modifiers.modified";
    public static final String KEY_FIELD_TYPE_MODIFIED = "field.type.modified";
    public static final String KEY_FIELD_VALUE_MODIFIED = "field.value.modified";

    private final FieldInfo previous;
    private final FieldInfo next;

    @Override
    public Collection<Diff> diff() {
        if (previous.getContentHash().equals(next.getContentHash())) {
            return Collections.emptyList();
        }

        List<Diff> list = new ArrayList<>();

        // 1. modifiers
        checkModifiers(list, "field");

        // 2. type
        checkType(list);

        // 3. value
        checkValue(list);

        // 4. annotations

        return list;
    }

    private boolean isEquals(Object a, Object b) {
        if (a==null && b==null) {
            return true;
        }
        if (a==null ^ b==null) {
            return false;
        }
        return a.equals(b);
    }

    private void checkValue(List<Diff> list) {
        if (!isEquals(previous.getValue(), next.getValue())) {
            list.add(
                Diff.diff()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.MODIFIED)
                    .messageKey(KEY_FIELD_VALUE_MODIFIED)
                    .messageArg(previous.getName())
                    .messageArg(previous.getValue())
                    .messageArg(next.getValue())
                    .build());
        }
    }

    private void checkType(List<Diff> list) {
        if (!previous.getType().equals(next.getType())) {
            list.add(
                Diff.diff()
                    .severity(Diff.Severity.ERROR)
                    .type(Diff.Type.MODIFIED)
                    .messageKey(KEY_FIELD_TYPE_MODIFIED)
                    .messageArg(previous.getName())
                    .messageArg(previous.getType())
                    .messageArg(next.getType())
                    .build());
        }
    }
}
