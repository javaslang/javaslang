/*  __    __  __  __    __  ___
 * \  \  /  /    \  \  /  /  __/
 *  \  \/  /  /\  \  \/  /  /
 *   \____/__/  \__\____/__/
 *
 * Copyright 2014-2017 Vavr, http://vavr.io
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
package io.vavr.collection;

import io.vavr.Function2;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.function.BiFunction;

// Subclasses of this contract test need to provide special cases
// to check that the sum of a collection of items is correct according to a monoid.
//
// Subclass this class, then use the JUnit Parameterized Test pattern.
// For each special case in the @Parameters object, expose the input collection
// of items with the method items() and the expected sum with the method
// expectedSum(). Expose the monoid for your value type with the method
// monoid().
//
// See the existing subclasses of this class as an example of how to wire up
// the special case data.
public abstract class SumArbitraryValueObjectsTest<ValueType> {
    // Every special case is a list of items with its expected sum.
    @Test
    public void checkSum() throws Exception {
        Assertions.assertThat(sum(items(), monoid())).isEqualTo(expectedSum());
    }

    protected abstract Traversable<ValueType> items();
    protected abstract ValueType expectedSum();
    protected abstract Monoid<ValueType> monoid();

    // REFACTOR Production code. Not sure where it goes yet. Traversable? or Monoid?
    // Traversable.sum(Monoid) sounds better to me.
    private ValueType sum(Traversable<ValueType> items, Monoid<ValueType> monoid) {
        return items.foldLeft(monoid.identityElement(), monoid.addFunction());
    }

    // REFACTOR Production code. I assume it goes alongside Traversable.
    // A Monoid describes a Value Object type in enough detail that we can
    // sum a Traversable of those Value Objects.
    public interface Monoid<ValueType> {
        ValueType identityElement();
        // REFACTOR Should we keep "add" in the name as a helpful metaphor?
        // Or is it better to just call this the binary operation?
        // I find the concrete name more helpful, but this operation could
        // also be like "multiply", so that could cause confusion for people
        // already comfortable with the Monoid concept.
        Function2<ValueType, ValueType, ValueType> addFunction();
    }
}
