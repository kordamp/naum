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
package org.kordamp.naum.processor;

import org.kordamp.naum.model.AnnotationInfo;
import org.kordamp.naum.model.MethodInfo;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author Andres Almiray
 */
public class MethodProcessor extends MethodVisitor {
    private final MethodInfo method;

    public MethodProcessor(MethodInfo method) {
        super(Opcodes.ASM5);
        this.method = method;
    }

    public MethodInfo getMethod() {
        return method;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationInfo annotation = AnnotationInfo.annotationInfo()
            .name(desc)
            .build();
        method.addToAnnotations(annotation);
        return new AnnotationProcessor(annotation);
    }
}
