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
@WithPrimitiveArrayValueAnnotation.ByteArrayAnnotation(emptyByteArray = {}, singleByteArray = 0, customByteArray = {1, 2})
@WithPrimitiveArrayValueAnnotation.CharArrayAnnotation(emptyCharArray = {}, singleCharArray = 1, customCharArray = {2, 3})
@WithPrimitiveArrayValueAnnotation.ShortArrayAnnotation(emptyShortArray = {}, singleShortArray = 2, customShortArray = {3, 4})
@WithPrimitiveArrayValueAnnotation.IntArrayAnnotation(emptyIntArray = {}, singleIntArray = 3, customIntArray = {4, 5})
@WithPrimitiveArrayValueAnnotation.LongArrayAnnotation(emptyLongArray = {}, singleLongArray = 4, customLongArray = {5, 6})
@WithPrimitiveArrayValueAnnotation.FloatArrayAnnotation(emptyFloatArray = {}, singleFloatArray = 6.1f, customFloatArray = {6.2f, 6.3f})
@WithPrimitiveArrayValueAnnotation.DoubleArrayAnnotation(emptyDoubleArray = {}, singleDoubleArray = 7.1, customDoubleArray = {7.2, 7.3})
class WithPrimitiveArrayValueAnnotation {

    @interface ByteArrayAnnotation {
        byte[] defaultByteArray() default {1, 2, 3};

        byte[] emptyByteArray();

        byte[] singleByteArray();

        byte[] customByteArray();
    }

    @interface CharArrayAnnotation {
        char[] defaultCharArray() default {1, 2, 3};

        char[] emptyCharArray();

        char[] singleCharArray();

        char[] customCharArray();
    }

    @interface ShortArrayAnnotation {
        short[] defaultShortArray() default {1, 2, 3};

        short[] emptyShortArray();

        short[] singleShortArray();

        short[] customShortArray();
    }

    @interface IntArrayAnnotation {
        int[] deafultIntArray() default {1, 2, 3};

        int[] emptyIntArray();

        int[] singleIntArray();

        int[] customIntArray();
    }

    @interface LongArrayAnnotation {
        long[] defaultLongArray() default {1, 2, 3};

        long[] emptyLongArray();

        long[] singleLongArray();

        long[] customLongArray();
    }

    @interface FloatArrayAnnotation {
        float[] defaultFloatArray() default {1, 2, 3};

        float[] emptyFloatArray();

        float[] singleFloatArray();

        float[] customFloatArray();
    }

    @interface DoubleArrayAnnotation {
        double[] defaultDoubleArray() default {1, 2, 3};

        double[] emptyDoubleArray();

        double[] singleDoubleArray();

        double[] customDoubleArray();
    }
}
