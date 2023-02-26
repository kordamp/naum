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
package org.kordamp.naum.core.processor;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * A {@link SignatureVisitor} that prints a disassembled view of the signature
 * it visits.
 *
 * @author Eugene Kuleshov
 * @author Eric Bruneton
 * @since 0.1.0
 */
public final class CustomTraceSignatureVisitor extends SignatureVisitor {

    private final StringBuffer declaration;

    private boolean isInterface;

    private boolean seenFormalParameter;

    private boolean seenInterfaceBound;

    private boolean seenParameter;

    private boolean seenInterface;

    private StringBuffer returnType;

    private StringBuffer superclass;

    private String formalParameters;

    private final Stack<StringBuffer> exceptions = new Stack<>();

    private final Stack<StringBuffer> interfaces = new Stack<>();

    /**
     * Stack used to keep track of class types that have arguments. Each element
     * of this stack is a boolean encoded in one bit. The top of the stack is
     * the lowest order bit. Pushing false = *2, pushing true = *2+1, popping =
     * /2.
     */
    private int argumentStack;

    /**
     * Stack used to keep track of array class types. Each element of this stack
     * is a boolean encoded in one bit. The top of the stack is the lowest order
     * bit. Pushing false = *2, pushing true = *2+1, popping = /2.
     */
    private int arrayStack;

    private String separator = "";

    public CustomTraceSignatureVisitor(final int access) {
        super(Opcodes.ASM9);
        isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        this.declaration = new StringBuffer();
    }

    private CustomTraceSignatureVisitor(final StringBuffer buf) {
        super(Opcodes.ASM9);
        this.declaration = buf;
    }

    @Override
    public void visitFormalTypeParameter(final String name) {
        declaration.append(seenFormalParameter ? ", " : "<").append(name);
        seenFormalParameter = true;
        seenInterfaceBound = false;
    }

    @Override
    public SignatureVisitor visitClassBound() {
        separator = " extends ";
        startType();
        return this;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        separator = seenInterfaceBound ? ", " : " extends ";
        seenInterfaceBound = true;
        startType();
        return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        endFormals();
        separator = " extends ";
        if (superclass == null) {
            superclass = new StringBuffer();
        }
        return new CustomTraceSignatureVisitor(superclass);
    }

    @Override
    public SignatureVisitor visitInterface() {
        separator = seenInterface ? ", " : isInterface ? " extends "
            : " implements ";
        seenInterface = true;
        interfaces.push(new StringBuffer());
        return new CustomTraceSignatureVisitor(interfaces.peek());
    }

    @Override
    public SignatureVisitor visitParameterType() {
        endFormals();
        if (seenParameter) {
            declaration.append(", ");
        } else {
            seenParameter = true;
            declaration.append('(');
        }
        startType();
        return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        endFormals();
        if (seenParameter) {
            seenParameter = false;
        } else {
            declaration.append('(');
        }
        declaration.append(')');
        returnType = new StringBuffer();
        return new CustomTraceSignatureVisitor(returnType);
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        exceptions.push(new StringBuffer());
        return new CustomTraceSignatureVisitor(exceptions.peek());
    }

    @Override
    public void visitBaseType(final char descriptor) {
        switch (descriptor) {
            case 'V':
                declaration.append("void");
                break;
            case 'B':
                declaration.append("byte");
                break;
            case 'J':
                declaration.append("long");
                break;
            case 'Z':
                declaration.append("boolean");
                break;
            case 'I':
                declaration.append("int");
                break;
            case 'S':
                declaration.append("short");
                break;
            case 'C':
                declaration.append("char");
                break;
            case 'F':
                declaration.append("float");
                break;
            // case 'D':
            default:
                declaration.append("double");
                break;
        }
        endType();
    }

    @Override
    public void visitTypeVariable(final String name) {
        declaration.append(name);
        endType();
    }

    @Override
    public SignatureVisitor visitArrayType() {
        startType();
        arrayStack |= 1;
        return this;
    }

    @Override
    public void visitClassType(final String name) {
        if ("java/lang/Object".equals(name)) {
            // Map<java.lang.Object,java.util.List>
            // or
            // abstract public V get(Object key); (seen in Dictionary.class)
            // should have Object
            // but java.lang.String extends java.lang.Object is unnecessary
            boolean needObjectClass = argumentStack % 2 != 0 || seenParameter;
            if (needObjectClass) {
                declaration.append(separator).append(name.replace('/', '.'));
            }
        } else {
            declaration.append(separator).append(name.replace('/', '.'));
        }
        separator = "";
        argumentStack *= 2;
    }

    @Override
    public void visitInnerClassType(final String name) {
        if (argumentStack % 2 != 0) {
            declaration.append('>');
        }
        argumentStack /= 2;
        declaration.append('.');
        declaration.append(separator).append(name.replace('/', '.'));
        separator = "";
        argumentStack *= 2;
    }

    @Override
    public void visitTypeArgument() {
        if (argumentStack % 2 == 0) {
            ++argumentStack;
            declaration.append('<');
        } else {
            declaration.append(", ");
        }
        declaration.append('?');
    }

    @Override
    public SignatureVisitor visitTypeArgument(final char tag) {
        if (argumentStack % 2 == 0) {
            ++argumentStack;
            declaration.append('<');
        } else {
            declaration.append(", ");
        }

        if (tag == EXTENDS) {
            declaration.append("? extends ");
        } else if (tag == SUPER) {
            declaration.append("? super ");
        }

        startType();
        return this;
    }

    @Override
    public void visitEnd() {
        if (argumentStack % 2 != 0) {
            declaration.append('>');
        }
        argumentStack /= 2;
        endType();
    }

    public String getDeclaration() {
        return declaration.toString();
    }

    public String getReturnType() {
        return returnType == null ? null : returnType.toString();
    }

    public Collection<String> getExceptions() {
        List<String> values = new ArrayList<>();
        exceptions.stream().map(StringBuffer::toString).forEach(values::add);
        Collections.sort(values);
        return values;
    }

    public String getTypeOrSuperclass() {
        return superclass.toString();
    }

    public String getFormalParameters() {
        return formalParameters == null ? null : formalParameters;
    }

    public Collection<String> getInterfaces() {
        List<String> values = new ArrayList<>();
        interfaces.stream().map(StringBuffer::toString).forEach(values::add);
        Collections.sort(values);
        return values;
    }

    // -----------------------------------------------

    private void endFormals() {
        if (seenFormalParameter) {
            declaration.append('>');
            seenFormalParameter = false;
            formalParameters = declaration.toString();
        }
    }

    private void startType() {
        arrayStack *= 2;
    }

    private void endType() {
        if (arrayStack % 2 == 0) {
            arrayStack /= 2;
        } else {
            while (arrayStack % 2 != 0) {
                arrayStack /= 2;
                declaration.append("[]");
            }
        }
    }
}
