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
package org.kordamp.naum.model;

import org.objectweb.asm.Type;

import java.util.List;

/**
 * @author Andres Almiray
 * @author Stephan Classen
 * @author Vitaly Tsaplin
 * @author Alexey Dubrovskiy
 */
public interface AnnotationValue {
    String getType();

    Object getValue();

    String getValueAsString();

    static EnumValue newEnumValue(String type, String value) {
        return new EnumValue(type, value);
    }

    static SimpleValue newSimpleValue(String type, Object value) {
        return new SimpleValue(type, value);
    }

    static SimpleValue newSimpleValue(Object value) {
        Type t = Type.getType(value.getClass());
        return new SimpleValue(t.getClassName(), value);
    }

    static ArrayValue newArrayValue(List<AnnotationValue> value) {
        return new ArrayValue(value);
    }
}
