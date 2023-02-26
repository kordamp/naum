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
package org.kordamp.naum.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.kordamp.naum.core.model.Opcodes.ACC_ABSTRACT;
import static org.kordamp.naum.core.model.Opcodes.ACC_ANNOTATION;
import static org.kordamp.naum.core.model.Opcodes.ACC_INTERFACE;
import static org.kordamp.naum.core.model.Opcodes.ACC_PUBLIC;
import static org.kordamp.naum.core.model.Opcodes.V1_8;
import static org.objectweb.asm.Opcodes.ACC_ENUM;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_SYNCHRONIZED;

/**
 * @author Andres Almiray
 * @since 0.1.0
 */
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
        ANNOTATION
    }

    private ClassInfo(String name, Type type, int version, int modifiers, String typeParameters, String superclass, String[] interfaces) {
        super(name, modifiers);
        this.version = version;
        this.typeParameters = typeParameters;
        this.superclass = superclass;
        this.interfaces = interfaces;
        this.type = type;
    }

    private static ClassInfo create(String name, int version, int modifiers, String typeParameters, String superclass, List<String> ifaces, String[] interfaces) {
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
                if (i != 0) {
                    b.append(",");
                }
                b.append(getAnnotations().get(i).getContent());
            }
            b.append("]");
        }

        if (interfaces.length > 0) {
            b.append("#I=[");
            for (int i = 0; i < interfaces.length; i++) {
                if (i != 0) {
                    b.append(",");
                }
                b.append(interfaces[i]);
            }
            b.append("]");
        }

        if (!getFields().isEmpty()) {
            b.append("#F=[");
            for (int i = 0; i < getFields().size(); i++) {
                if (i != 0) {
                    b.append(",");
                }
                b.append(getFields().get(i).getContent());
            }
            b.append("]");
        }

        if (!getConstructors().isEmpty()) {
            b.append("#CT=[");
            for (int i = 0; i < getConstructors().size(); i++) {
                if (i != 0) {
                    b.append(",");
                }
                b.append(getConstructors().get(i).getContent());
            }
            b.append("]");
        }

        if (!getMethods().isEmpty()) {
            b.append("#M=[");
            for (int i = 0; i < getMethods().size(); i++) {
                if (i != 0) {
                    b.append(",");
                }
                b.append(getMethods().get(i).getContent());
            }
            b.append("]");
        }

        if (!getClasses().isEmpty()) {
            b.append("#IC=[");
            for (int i = 0; i < getClasses().size(); i++) {
                if (i != 0) {
                    b.append(",");
                }
                b.append(getClasses().get(i).getContent());
            }
            b.append("]");
        }

        b.append("}");

        return b.toString();
    }

    public static class ClassInfoBuilder {
        private String name;
        private int version;
        private int modifiers;
        private String typeParameters;
        private String superclass;
        private List<String> ifaces;
        private String[] interfaces;

        public ClassInfo.ClassInfoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ClassInfo.ClassInfoBuilder version(int version) {
            this.version = version;
            return this;
        }

        public ClassInfo.ClassInfoBuilder modifiers(int modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public ClassInfo.ClassInfoBuilder typeParameters(String typeParameters) {
            this.typeParameters = typeParameters;
            return this;
        }

        public ClassInfo.ClassInfoBuilder superclass(String superclass) {
            this.superclass = superclass;
            return this;
        }

        public ClassInfo.ClassInfoBuilder iface(String iface) {
            if (this.ifaces == null) this.ifaces = new ArrayList<>();
            this.ifaces.add(iface);
            return this;
        }

        public ClassInfo.ClassInfoBuilder ifaces(java.util.Collection<? extends String> ifaces) {
            if (ifaces == null) {
                throw new NullPointerException("ifaces cannot be null");
            }
            if (this.ifaces == null) this.ifaces = new ArrayList<>();
            this.ifaces.addAll(ifaces);
            return this;
        }

        public ClassInfo.ClassInfoBuilder clearIfaces() {
            if (this.ifaces != null) this.ifaces.clear();
            return this;
        }

        public ClassInfo.ClassInfoBuilder interfaces(String[] interfaces) {
            this.interfaces = interfaces;
            return this;
        }

        public ClassInfo build() {
            java.util.List<String> ifaces;
            switch (this.ifaces == null ? 0 : this.ifaces.size()) {
                case 0:
                    ifaces = Collections.emptyList();
                    break;
                case 1:
                    ifaces = Collections.singletonList(this.ifaces.get(0));
                    break;
                default:
                    ifaces = Collections.unmodifiableList(new ArrayList<>(this.ifaces));
            }
            return ClassInfo.create(this.name, this.version, this.modifiers, this.typeParameters, this.superclass, ifaces, this.interfaces);
        }

        @Override
        public String toString() {
            return "ClassInfo.ClassInfoBuilder(name=" + this.name + ", version=" + this.version + ", modifiers=" + this.modifiers + ", typeParameters=" + this.typeParameters + ", superclass=" + this.superclass + ", ifaces=" + this.ifaces + ", interfaces=" + java.util.Arrays.deepToString(this.interfaces) + ")";
        }
    }

    public static ClassInfo.ClassInfoBuilder classInfo() {
        return new ClassInfo.ClassInfoBuilder();
    }

    public int getVersion() {
        return this.version;
    }

    public String getTypeParameters() {
        return this.typeParameters;
    }

    public String getSuperclass() {
        return this.superclass;
    }

    public String[] getInterfaces() {
        return this.interfaces;
    }

    public List<FieldInfo> getFields() {
        return this.fields;
    }

    public List<ConstructorInfo> getConstructors() {
        return this.constructors;
    }

    public List<MethodInfo> getMethods() {
        return this.methods;
    }

    public List<InnerClassInfo> getClasses() {
        return this.classes;
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "ClassInfo(super=" + super.toString() + ", version=" + this.getVersion() + ", typeParameters=" + this.getTypeParameters() + ", superclass=" + this.getSuperclass() + ", interfaces=" + java.util.Arrays.deepToString(this.getInterfaces()) + ", fields=" + this.getFields() + ", constructors=" + this.getConstructors() + ", methods=" + this.getMethods() + ", classes=" + this.getClasses() + ", type=" + this.getType() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ClassInfo)) return false;
        final ClassInfo other = (ClassInfo) o;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        if (this.getVersion() != other.getVersion()) return false;
        final Object this$typeParameters = this.getTypeParameters();
        final Object other$typeParameters = other.getTypeParameters();
        if (this$typeParameters == null ? other$typeParameters != null : !this$typeParameters.equals(other$typeParameters))
            return false;
        final Object this$superclass = this.getSuperclass();
        final Object other$superclass = other.getSuperclass();
        if (this$superclass == null ? other$superclass != null : !this$superclass.equals(other$superclass))
            return false;
        if (!java.util.Arrays.deepEquals(this.getInterfaces(), other.getInterfaces())) return false;
        final Object this$fields = this.getFields();
        final Object other$fields = other.getFields();
        if (this$fields == null ? other$fields != null : !this$fields.equals(other$fields)) return false;
        final Object this$constructors = this.getConstructors();
        final Object other$constructors = other.getConstructors();
        if (this$constructors == null ? other$constructors != null : !this$constructors.equals(other$constructors))
            return false;
        final Object this$methods = this.getMethods();
        final Object other$methods = other.getMethods();
        if (this$methods == null ? other$methods != null : !this$methods.equals(other$methods)) return false;
        final Object this$classes = this.getClasses();
        final Object other$classes = other.getClasses();
        if (this$classes == null ? other$classes != null : !this$classes.equals(other$classes)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ClassInfo;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        result = result * PRIME + this.getVersion();
        final Object $typeParameters = this.getTypeParameters();
        result = result * PRIME + ($typeParameters == null ? 43 : $typeParameters.hashCode());
        final Object $superclass = this.getSuperclass();
        result = result * PRIME + ($superclass == null ? 43 : $superclass.hashCode());
        result = result * PRIME + Arrays.deepHashCode(this.getInterfaces());
        final Object $fields = this.getFields();
        result = result * PRIME + ($fields == null ? 43 : $fields.hashCode());
        final Object $constructors = this.getConstructors();
        result = result * PRIME + ($constructors == null ? 43 : $constructors.hashCode());
        final Object $methods = this.getMethods();
        result = result * PRIME + ($methods == null ? 43 : $methods.hashCode());
        final Object $classes = this.getClasses();
        result = result * PRIME + ($classes == null ? 43 : $classes.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        return result;
    }
}
