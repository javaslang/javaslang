/* ____  ______________  ________________________  __________
 * \   \/   /      \   \/   /   __/   /      \   \/   /      \
 *  \______/___/\___\______/___/_____/___/\___\______/___/\___\
 *
 * Copyright 2019 Vavr, http://vavr.io
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
package io.vavr;

import io.vavr.collection.Iterator;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;

/**
 * Extension of the well-known Java {@link java.lang.Iterable} in the sense that a rich
 * Vavr {@link io.vavr.collection.Iterator} is returned by {@link #iterator()}.
 *
 * @param <T> the type of Iterator elements
 */
public interface Iterable<T> extends java.lang.Iterable<T> {

    /**
     * Returns an {@link Iterator} that allows us to perform intermediate, sequential
     * operations known from Vavr's collection library.
     *
     * @return an {@link Iterator} instance. It is not necessarily a new instance, like that returned by {@link Iterator#empty()}.
     */
    @Override
    Iterator<T> iterator();

    /**
     * Performs an action on each element. In contrast to {@link #forEach(Consumer)},
     * additionally the element's index is passed to the given {@code action}.
     * <p>
     * Please note that subsequent calls to {@code forEachWithIndex} might lead to different iteration orders,
     * depending on the underlying {@code Iterable} implementation.
     * <p>
     * Please also note that {@code forEachWithIndex} might loop infinitely,
     * depending on the underlying {@code Iterable} implementation.
     *
     * @param action A {@link ObjIntConsumer}
     * @throws NullPointerException if {@code action} is null
     */
    default void forEachWithIndex(ObjIntConsumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        int index = 0;
        for (T t : this) {
            action.accept(t, index++);
        }
    }

    // `Iterable<T>` must not have a generic type bound, see TraversableTest ShouldJustCompile
    default <C> C to(Function<? super java.lang.Iterable<T>, C> fromIterable) {
        Objects.requireNonNull(fromIterable, "fromIterable is null");
        return fromIterable.apply(this);
    }

}
