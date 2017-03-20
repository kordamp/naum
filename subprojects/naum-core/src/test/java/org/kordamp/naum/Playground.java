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
package org.kordamp.naum;

import org.kordamp.naum.model.AnnotationInfo;
import org.kordamp.naum.model.ClassInfo;
import org.kordamp.naum.model.ConstructorInfo;
import org.kordamp.naum.model.FieldInfo;
import org.kordamp.naum.model.InnerClassInfo;
import org.kordamp.naum.model.MethodInfo;
import org.kordamp.naum.processor.ClassProcessor;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Andres Almiray
 */
public class Playground {
    public static void main(String[] args) throws Exception {
        process(Playground.class.getClassLoader().getResourceAsStream("org/kordamp/naum/Foo.class"));
        System.out.println("**********************");
        //process(Test.class.getClassLoader().getResourceAsStream("org/kordamp/naum/IFace2.class"));
    }

    private static void process(InputStream stream) throws IOException {
        ClassReader cr = new ClassReader(stream);
        ClassProcessor classProcessor = new ClassProcessor();
        cr.accept(classProcessor, ClassReader.SKIP_CODE);

        for (ClassInfo ci : classProcessor.getClasses()) {
            System.out.println(ci.getContent());
            //System.out.println(ci.getContentHash());
            for (AnnotationInfo ai : ci.getAnnotations()) {
                System.out.println(ai.getContent());
                System.out.println(ai.asString());
                //System.out.println(ai.getContent());
                //System.out.println(ai.getContentHash());
            }
            for (FieldInfo fi : ci.getFields()) {
                System.out.println(fi.getContent());
                //System.out.println(fi.getContent());
                //System.out.println(fi.getContentHash());
            }
            for (ConstructorInfo cti : ci.getConstructors()) {
                System.out.println(cti.getContent());
                //System.out.println(cti.getContent());
                //System.out.println(cti.getContentHash());
            }
            for (MethodInfo mi : ci.getMethods()) {
                System.out.println(mi.getContent());
                //System.out.println(mi.getContent());
                //System.out.println(mi.getContentHash());
            }
            for (InnerClassInfo ic : ci.getClasses()) {
                System.out.println(ic.getContent());
                //System.out.println(ic.getContent());
                //System.out.println(ic.getContentHash());
            }
        }
    }
}
