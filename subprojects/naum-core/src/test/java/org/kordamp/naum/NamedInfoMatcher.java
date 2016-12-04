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
package org.kordamp.naum;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.kordamp.naum.model.NamedInfo;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Stephan Classen
 */
public class NamedInfoMatcher<S extends NamedInfo<S>> extends BaseMatcher<S> {

    public static <T extends NamedInfo<T>> NamedInfoMatcher<T> namedInfo(T expected) {
        if (expected == null) {
            throw new NullPointerException();
        }

        return new NamedInfoMatcher<>(expected);
    }

    public static <T extends NamedInfo<T>> List<Matcher<? super T>> asMatchers(List<? extends T> infos) {
        return infos.stream().map(NamedInfoMatcher::namedInfo).collect(toList());
    }

    private final NamedInfo<?> expected;

    private NamedInfoMatcher(S expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof NamedInfo<?>) {
            final NamedInfo<?> actual = (NamedInfo<?>) item;
            return expected.getContentHash().equals(actual.getContentHash()) && expected.equals(actual);
        }

        return false;
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        if (item instanceof NamedInfo<?>) {
            final NamedInfo<?> actual = (NamedInfo<?>) item;
            description.appendText("was ").appendText(actual.getContent());
        } else {
            super.describeMismatch(item, description);
        }
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(expected.getContent());
    }
}
