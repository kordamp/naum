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
/**
 * @author Andres Almiray
 * @since 0.1.0
 */
module org.kordamp.naum.core {
    requires org.objectweb.asm;
    
    exports org.kordamp.naum.core;
    exports org.kordamp.naum.util;
    exports org.kordamp.naum.core.diff;
    exports org.kordamp.naum.core.model;
    exports org.kordamp.naum.core.processor;
}