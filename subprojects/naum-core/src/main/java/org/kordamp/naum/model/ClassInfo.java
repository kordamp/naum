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

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.kordamp.naum.model.Opcodes.ACC_ABSTRACT;
import static org.kordamp.naum.model.Opcodes.ACC_ANNOTATION;
import static org.kordamp.naum.model.Opcodes.ACC_INTERFACE;
import static org.kordamp.naum.model.Opcodes.ACC_PUBLIC;
import static org.kordamp.naum.model.Opcodes.V1_8;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_SYNCHRONIZED;

/**
 * @author Andres Almiray
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ClassInfo extends MemberInfo<ClassInfo> {
    private static final String[] EMPTY_INTERFACES = new String[0];

    private final int version;
    private final String typeParameters;
    private final String superclass;
    private final String[] interfaces;
    private final List<FieldInfo> fields = new ArrayList<>();
    private final List<ConstructorInfo> constructors = new ArrayList<>();
    private final List<MethodInfo> methods = new ArrayList<>();
    private final List<InnerClassInfo> classes = new ArrayList<>();
    private final Type type;

    public enum Type {
        CLASS,
        INTERFACE,
        ENUM,
        ANNOTATION;
    }

    private ClassInfo(String name, Type type, int version, int modifiers, String typeParameters, String superclass, String[] interfaces) {
        super(name, modifiers);
        this.version = version;
        this.typeParameters = typeParameters;
        this.superclass = superclass;
        this.interfaces = interfaces;
        this.type = type;
    }

    @Builder(builderMethodName = "classInfo")
    public static ClassInfo create(@Nonnull String name, int version, int modifiers, String typeParameters, @Nonnull String superclass, @Nonnull @Singular List<String> ifaces, @Nonnull String[] interfaces) {
        version = version == 0 ? V1_8 : version;
        typeParameters = typeParameters == null ? "" : typeParameters;
        superclass = superclass == null ? "java.lang.Object" : superclass;

        List<String> list = new ArrayList<>();
        if (ifaces != null) {
            list.addAll(ifaces);
        }
        if (interfaces != null) {
            list.addAll(Arrays.asList(interfaces));
        }

        String[] array = list.toArray(new String[list.size()]);
        for (int i = 0; i < array.length; i++) {
            array[i] = array[i].replace('/', '.');
        }
        Arrays.sort(array);

        Type type = Type.CLASS;
        if (Modifiers.isEnum(modifiers)) {
            type = Type.ENUM;
            modifiers = modifiers - (ACC_ENUM + ACC_FINAL + ACC_SYNCHRONIZED);
        } else if (Modifiers.isAnnotation(modifiers)) {
            type = Type.ANNOTATION;
            modifiers = modifiers - (ACC_INTERFACE + ACC_ABSTRACT + ACC_ANNOTATION);
        } else if (Modifiers.isInterface(modifiers)) {
            type = Type.INTERFACE;
            modifiers = modifiers - (ACC_INTERFACE + ACC_ABSTRACT);
        }

        return new ClassInfo(name.replace('/', '.'), type, version, modifiers, typeParameters, superclass.replace('/', '.'), array);
    }

    public boolean isClass() {
        return type == Type.CLASS;
    }

    public boolean isInterface() {
        return type == Type.INTERFACE;
    }

    public boolean isEnum() {
        return type == Type.ENUM;
    }

    public boolean isAnnotation() {
        return type == Type.ANNOTATION;
    }

    public static ClassInfoBuilder newClass() {
        return newClass(ACC_PUBLIC);
    }

    public static ClassInfoBuilder newClass(int visibilityAccess) {
        return ClassInfo.classInfo()
            .modifiers(visibilityAccess);
    }

    public static ClassInfoBuilder newInterface() {
        return newInterface(ACC_PUBLIC);
    }

    public static ClassInfoBuilder newInterface(int visibilityAccess) {
        return ClassInfo.classInfo()
            .modifiers(visibilityAccess | ACC_INTERFACE | ACC_ABSTRACT);
    }

    public static ClassInfoBuilder newAnnotation() {
        return newAnnotation(ACC_PUBLIC);
    }

    public static ClassInfoBuilder newAnnotation(int visibilityAccess) {
        return ClassInfo.classInfo()
            .modifiers(visibilityAccess | ACC_INTERFACE | ACC_ABSTRACT | ACC_ANNOTATION);
    }

    public static ClassInfoBuilder newEnum() {
        return newEnum(ACC_PUBLIC);
    }

    public static ClassInfoBuilder newEnum(int visibilityAccess) {
        return ClassInfo.classInfo()
            .superclass(Enum.class.getName())
            .modifiers(visibilityAccess | ACC_ENUM | ACC_FINAL | ACC_SYNCHRONIZED);
    }

    public ClassInfo addToFields(FieldInfo field) {
        fields.add(field);
        if (!isEnum()) {
            Collections.sort(fields);
        }
        return this;
    }

    public ClassInfo addToConstructors(ConstructorInfo constructor) {
        constructors.add(constructor);
        Collections.sort(constructors);
        return this;
    }

    public ClassInfo addToMethods(MethodInfo method) {
        methods.add(method);
        Collections.sort(methods);
        return this;
    }

    public ClassInfo addToClasses(InnerClassInfo klass) {
        classes.add(klass);
        Collections.sort(classes);
        return this;
    }

    @Override
    public String getContent() {
        StringBuilder b = new StringBuilder("C{N=")
            .append(getName())
            .append("#T=")
            .append(type)
            .append("#V=")
            .append(version);

        if (typeParameters != null && typeParameters.length() > 0) {
            b.append("#P=")
                .append(typeParameters);
        }

        b.append("#S=")
            .append(superclass)
            .append("#D=")
            .append(getModifiers());

        if (!getAnnotations().isEmpty()) {
            b.append("#A=[");
            for (int i = 0; i < getAnnotations().size(); i++) {
                if (i != 0) { b.append(","); }
                b.append(getAnnotations().get(i).getContent());
            }
            b.append("]");
        }

        if (interfaces.length > 0) {
            b.append("#I=[");
            for (int i = 0; i < interfaces.length; i++) {
                if (i != 0) { b.append(","); }
                b.append(interfaces[i]);
            }
            b.append("]");
        }

        if (!getFields().isEmpty()) {
            b.append("#F=[");
            for (int i = 0; i < getFields().size(); i++) {
                if (i != 0) { b.append(","); }
                b.append(getFields().get(i).getContent());
            }
            b.append("]");
        }

        if (!getConstructors().isEmpty()) {
            b.append("#CT=[");
            for (int i = 0; i < getConstructors().size(); i++) {
                if (i != 0) { b.append(","); }
                b.append(getConstructors().get(i).getContent());
            }
            b.append("]");
        }

        if (!getMethods().isEmpty()) {
            b.append("#M=[");
            for (int i = 0; i < getMethods().size(); i++) {
                if (i != 0) { b.append(","); }
                b.append(getMethods().get(i).getContent());
            }
            b.append("]");
        }

        if (!getClasses().isEmpty()) {
            b.append("#IC=[");
            for (int i = 0; i < getClasses().size(); i++) {
                if (i != 0) { b.append(","); }
                b.append(getClasses().get(i).getContent());
            }
            b.append("]");
        }

        b.append("}");

        return b.toString();
    }
}
