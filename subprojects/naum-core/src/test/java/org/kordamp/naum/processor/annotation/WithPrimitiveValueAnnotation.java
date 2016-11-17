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

package org.kordamp.naum.processor.annotation;

/**
 * @author Stephan Classen
 */
@WithPrimitiveValueAnnotation.ByteValueAnnotation(byteValue = 1)
@WithPrimitiveValueAnnotation.CharValueAnnotation(charValue = 2)
@WithPrimitiveValueAnnotation.ShortValueAnnotation(shortValue = 3)
@WithPrimitiveValueAnnotation.IntValueAnnotation(intValue = 4)
@WithPrimitiveValueAnnotation.LongValueAnnotation(longValue = 5)
@WithPrimitiveValueAnnotation.FloatValueAnnotation(floatValue = 6.1f)
@WithPrimitiveValueAnnotation.DoubleValueAnnotation(doubleValue = 7.1)
class WithPrimitiveValueAnnotation {

    @interface ByteValueAnnotation {
        byte byteValue();
    }

    @interface CharValueAnnotation {
        char charValue();
    }

    @interface ShortValueAnnotation {
        short shortValue();
    }

    @interface IntValueAnnotation {
        int intValue();
    }

    @interface LongValueAnnotation {
        long longValue();
    }

    @interface FloatValueAnnotation {
        float floatValue();
    }

    @interface DoubleValueAnnotation {
        double doubleValue();
    }
}
