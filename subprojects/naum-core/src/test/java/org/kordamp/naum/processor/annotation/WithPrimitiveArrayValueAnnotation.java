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
@WithPrimitiveArrayValueAnnotation.ByteArrayAnnotation(emptyByteArray = {}, customByteArray = {1, 2})
@WithPrimitiveArrayValueAnnotation.CharArrayAnnotation(emptyCharArray = {}, customCharArray = {2, 3})
@WithPrimitiveArrayValueAnnotation.ShortArrayAnnotation(emptyShortArray = {}, customShortArray = {3, 4})
@WithPrimitiveArrayValueAnnotation.IntArrayAnnotation(emptyIntArray = {}, customIntArray = {4, 5})
@WithPrimitiveArrayValueAnnotation.LongArrayAnnotation(emptyLongArray = {}, customLongArray = {5, 6})
@WithPrimitiveArrayValueAnnotation.FloatArrayAnnotation(emptyFloatArray = {}, customFloatArray = {6.1f, 6.2f})
@WithPrimitiveArrayValueAnnotation.DoubleArrayAnnotation(emptyDoubleArray = {}, customDoubleArray = {7.1, 7.2})
class WithPrimitiveArrayValueAnnotation {

    @interface ByteArrayAnnotation {
        byte[] defaultByteArray() default {1, 2, 3};

        byte[] emptyByteArray();

        byte[] customByteArray();
    }

    @interface CharArrayAnnotation {
        char[] defaultCharArray() default {1, 2, 3};

        char[] emptyCharArray();

        char[] customCharArray();
    }

    @interface ShortArrayAnnotation {
        short[] defaultShortArray() default {1, 2, 3};

        short[] emptyShortArray();

        short[] customShortArray();
    }

    @interface IntArrayAnnotation {
        int[] deafultIntArray() default {1, 2, 3};

        int[] emptyIntArray();

        int[] customIntArray();
    }

    @interface LongArrayAnnotation {
        long[] defaultLongArray() default {1, 2, 3};

        long[] emptyLongArray();

        long[] customLongArray();
    }

    @interface FloatArrayAnnotation {
        float[] defaultFloatArray() default {1, 2, 3};

        float[] emptyFloatArray();

        float[] customFloatArray();
    }

    @interface DoubleArrayAnnotation {
        double[] defaultDoubleArray() default {1, 2, 3};

        double[] emptyDoubleArray();

        double[] customDoubleArray();
    }
}
