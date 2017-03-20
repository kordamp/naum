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
package org.kordamp.naum.processor.annotation;

import static org.kordamp.naum.processor.annotation.WithEnumValueAnnotation.SomeEnum.NAUM;
import static org.kordamp.naum.processor.annotation.WithEnumValueAnnotation.SomeEnum.PIZZA;

/**
 * @author Stephan Classen
 */
@WithEnumValueAnnotation.DefaultEnumValueAnnotation
@WithEnumValueAnnotation.CustomEnumValueAnnotation(NAUM)
class WithEnumValueAnnotation {

    @interface DefaultEnumValueAnnotation {
        SomeEnum value() default PIZZA;
    }

    @interface CustomEnumValueAnnotation {
        SomeEnum value();
    }

    enum SomeEnum {
        NAUM, PIZZA
    }
}
