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
package org.kordamp.naum.processor;

import org.kordamp.naum.model.AnnotationInfo;
import org.kordamp.naum.model.ClassInfo;
import org.kordamp.naum.model.ConstructorInfo;
import org.kordamp.naum.model.FieldInfo;
import org.kordamp.naum.model.InnerClassInfo;
import org.kordamp.naum.model.MethodInfo;
import org.kordamp.naum.model.Opcodes;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.signature.SignatureReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * @author Andres Almiray
 */
public class ClassProcessor extends ClassVisitor {
    private static final String CONSTRUCTOR_NAME = "<init>";
    private static final String STATIC_INITIALIZER_NAME = "<clinit>";
    private static final String MAGIC_LAMBDA_IMPL_NAME = "java/lang/invoke/MethodHandles$Lookup";
    private static final String MAGIC_LAMBDA_EXPRESSION_NAME = "lambda$";
    private static final Pattern SIGNATURE_PATTERN = Pattern.compile("(.*)\\((.*)\\)");

    private final List<ClassInfo> classes = new ArrayList<>();
    private final Stack<ClassInfo> classStack = new Stack<>();

    public ClassProcessor() {
        super(Opcodes.ASM5);
    }

    public List<ClassInfo> getClasses() {
        return classes;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String signatureDesc = signature != null ? signature : "";

        String superClassName = superName;
        String[] ifaces = interfaces;
        String formalParameters = "";

        if (!("".equals(signatureDesc))) {
            SignatureReader r = new SignatureReader(signatureDesc);
            CustomTraceSignatureVisitor sv = new CustomTraceSignatureVisitor(Opcodes.ACC_PUBLIC);
            r.accept(sv);

            formalParameters = sv.getFormalParameters();
            String superclass = sv.getTypeOrSuperclass();
            superClassName = "".equals(superclass) ? "java.lang.Object" : superClassName;
            Collection<String> interfaceTypes = sv.getInterfaces();
            ifaces = interfaceTypes.toArray(new String[interfaceTypes.size()]);
        }

        ClassInfo klass = ClassInfo.classInfo()
            .name(name)
            .version(version)
            .typeParameters(formalParameters)
            .modifiers(access)
            .superclass(superClassName)
            .ifaces(asList(ifaces))
            .build();
        classStack.push(klass);
    }

    @Override
    public void visitEnd() {
        classes.add(classStack.pop());
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (MAGIC_LAMBDA_IMPL_NAME.equals(name)) {
            return;
        }

        classStack.peek().addToClasses(InnerClassInfo.innerClassInfo()
            .name(name)
            .modifiers(access)
            .build());
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.startsWith(MAGIC_LAMBDA_EXPRESSION_NAME)) {
            return null;
        }

        if (STATIC_INITIALIZER_NAME.equals(name)) {
            // TODO: handle static initializer block
            // must inspect code inside block
            return null;
        }

        ClassInfo owner = classStack.peek();

        String signatureDesc = signature != null ? signature : desc;
        SignatureReader r = new SignatureReader(signatureDesc);
        CustomTraceSignatureVisitor sv = new CustomTraceSignatureVisitor(Opcodes.ACC_PUBLIC);
        r.accept(sv);

        Matcher matcher = SIGNATURE_PATTERN.matcher(sv.getDeclaration());
        matcher.matches();

        if (CONSTRUCTOR_NAME.equals(name)) {
            ConstructorInfo constructor = ConstructorInfo.constructorInfo()
                .modifiers(access)
                .argumentTypes(matcher.group(2))
                .exceptions(exceptions)
                .build();
            owner.addToConstructors(constructor);
            return new ConstructorProcessor(constructor);
        }

        boolean ownerIsInterface = owner.isInterface();

        if (ownerIsInterface && access == Opcodes.ACC_PUBLIC) {
            access = access | Opcodes.ACC_DEFAULT;
        }

        MethodInfo method = MethodInfo.methodInfo()
            .name(name)
            .modifiers(access)
            .genericTypes(matcher.group(1))
            .returnType(sv.getReturnType())
            .argumentTypes(matcher.group(2))
            .exceptions(exceptions)
            .build();
        owner.addToMethods(method);
        return new MethodProcessor(method);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if ("$VALUES".equals(name) && classStack.peek().isEnum()) {
            return null;
        }

        String signatureDesc = signature != null ? signature : desc;
        SignatureReader r = new SignatureReader(signatureDesc);
        CustomTraceSignatureVisitor sv = new CustomTraceSignatureVisitor(Opcodes.ACC_PUBLIC);
        r.accept(sv);

        FieldInfo field = FieldInfo.fieldInfo()
            .name(name)
            .modifiers(access)
            .type(sv.getTypeOrSuperclass())
            .value(value)
            .build();
        classStack.peek().addToFields(field);
        return new FieldProcessor(field);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationInfo annotation = AnnotationInfo.annotationInfo()
            .name(desc)
            .build();
        classStack.peek().addToAnnotations(annotation);
        return new AnnotationProcessor(annotation);
    }
}
