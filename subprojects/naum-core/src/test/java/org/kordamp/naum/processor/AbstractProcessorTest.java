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

import org.kordamp.naum.model.ClassInfo;
import org.objectweb.asm.ClassReader;

import java.io.InputStream;
import java.util.List;

public class AbstractProcessorTest {
    public interface Checks {
        void check(ClassInfo klass);
    }

    protected void loadAndCheck(Class<?> clazz, Checks checks) throws Exception {
        for (ClassInfo klass : visitClassFile(clazz)) {
            checks.check(klass);
        }
    }

    private List<ClassInfo> visitClassFile(Class<?> clazz) throws Exception {
        final String targetClassPath = clazz.getCanonicalName().replaceAll("\\.", "/") + ".class";
        final InputStream stream = AbstractProcessorTest.class.getClassLoader().getResourceAsStream(targetClassPath);
        final ClassReader classReader = new ClassReader(stream);
        final ClassProcessor classProcessor = new ClassProcessor();

        classReader.accept(classProcessor, ClassReader.SKIP_CODE);
        return classProcessor.getClasses();
    }
}
