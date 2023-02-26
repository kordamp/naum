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
package org.kordamp.naum.core.processor.annotation;

import static org.kordamp.naum.core.processor.annotation.WithEnumArrayValueAnnotation.AnotherEnum.BAR;
import static org.kordamp.naum.core.processor.annotation.WithEnumArrayValueAnnotation.AnotherEnum.FOO;
import static org.kordamp.naum.core.processor.annotation.WithEnumArrayValueAnnotation.AnotherEnum.GARTEN;
import static org.kordamp.naum.core.processor.annotation.WithEnumArrayValueAnnotation.AnotherEnum.NAUM;
import static org.kordamp.naum.core.processor.annotation.WithEnumArrayValueAnnotation.AnotherEnum.PIZZA;

/**
 * @author Stephan Classen
 */
@WithEnumArrayValueAnnotation.DefaultEnumArrayValueAnnotation
@WithEnumArrayValueAnnotation.EmptyEnumArrayValueAnnotation({})
@WithEnumArrayValueAnnotation.SingleEnumArrayValueAnnotation(GARTEN)
@WithEnumArrayValueAnnotation.CustomEnumArrayValueAnnotation({PIZZA, BAR})
class WithEnumArrayValueAnnotation {

    @interface DefaultEnumArrayValueAnnotation {
        AnotherEnum[] value() default {NAUM, FOO};
    }

    @interface EmptyEnumArrayValueAnnotation {
        AnotherEnum[] value();
    }

    @interface SingleEnumArrayValueAnnotation {
        AnotherEnum[] value();
    }

    @interface CustomEnumArrayValueAnnotation {
        AnotherEnum[] value();
    }

    enum AnotherEnum {
        NAUM, PIZZA, FOO, BAR, GARTEN
    }
}
