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

import static org.kordamp.naum.model.Opcodes.ACC_DEFAULT;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_ANNOTATION;
import static org.objectweb.asm.Opcodes.ACC_DEPRECATED;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_STRICT;
import static org.objectweb.asm.Opcodes.ACC_SYNCHRONIZED;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ACC_TRANSIENT;
import static org.objectweb.asm.Opcodes.ACC_VOLATILE;

/**
 * @author Andres Almiray
 */
public final class Modifiers {
    private Modifiers() {

    }

    public static boolean isAbstract(int modifiers) {
        return (modifiers & ACC_ABSTRACT) == ACC_ABSTRACT;
    }

    public static boolean isAnnotation(int modifiers) {
        return (modifiers & ACC_ANNOTATION) == ACC_ANNOTATION;
    }

    public static boolean isDefault(int modifiers) {
        return (modifiers & ACC_DEFAULT) == ACC_DEFAULT;
    }

    public static boolean isDeprecated(int modifiers) {
        return (modifiers & ACC_DEPRECATED) == ACC_DEPRECATED;
    }

    public static boolean isEnum(int modifiers) {
        return (modifiers & ACC_ENUM) == ACC_ENUM;
    }

    public static boolean isFinal(int modifiers) {
        return (modifiers & ACC_FINAL) == ACC_FINAL;
    }

    public static boolean isInterface(int modifiers) {
        return (modifiers & ACC_INTERFACE) == ACC_INTERFACE;
    }

    public static boolean isNative(int modifiers) {
        return (modifiers & ACC_NATIVE) == ACC_NATIVE;
    }

    public static boolean isPackagePrivate(int modifiers) {
        return !isPublic(modifiers) && !isProtected(modifiers) && isPrivate(modifiers);
    }

    public static boolean isPrivate(int modifiers) {
        return (modifiers & ACC_PRIVATE) == ACC_PRIVATE;
    }

    public static boolean isProtected(int modifiers) {
        return (modifiers & ACC_PROTECTED) == ACC_PROTECTED;
    }

    public static boolean isPublic(int modifiers) {
        return (modifiers & ACC_PUBLIC) == ACC_PUBLIC;
    }

    public static boolean isStatic(int modifiers) {
        return (modifiers & ACC_STATIC) == ACC_STATIC;
    }

    public static boolean isStrict(int modifiers) {
        return (modifiers & ACC_STRICT) == ACC_STRICT;
    }

    public static boolean isSynchronized(int modifiers) {
        return (modifiers & ACC_SYNCHRONIZED) == ACC_SYNCHRONIZED;
    }

    public static boolean isSynthetic(int modifiers) {
        return (modifiers & ACC_SYNTHETIC) == ACC_SYNTHETIC;
    }

    public static boolean isTransient(int modifiers) {
        return (modifiers & ACC_TRANSIENT) == ACC_TRANSIENT;
    }

    public static boolean isVolatile(int modifiers) {
        return (modifiers & ACC_VOLATILE) == ACC_VOLATILE;
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

        if (isEnum(modifiers)) {
            b.append("enum ");
        } else if (isAnnotation(modifiers)) {
            b.append("@interface ");
        } else if (isInterface(modifiers)) {
            b.append("interface ");
        } else if (isAbstract(modifiers)) {
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
