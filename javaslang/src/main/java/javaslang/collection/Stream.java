/*     / \____  _    _  ____   ______  / \ ____  __    _ _____
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  / /  _  \   Javaslang
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/  \__/  /   Copyright 2014-now Daniel Dietrich
 * /___/\_/  \_/\____/\_/  \_/\__\/__/___\_/  \_//  \__/_____/    Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.*;
import javaslang.collection.Stream.Cons;
import javaslang.collection.Stream.Empty;
import javaslang.collection.StreamModule.*;
import javaslang.control.Match;
import javaslang.control.Option;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * An immutable {@code Stream} is lazy sequence of elements which may be infinitely long.
 * Its immutability makes it suitable for concurrent programming.
 * <p>
 * A {@code Stream} is composed of a {@code head} element and a lazy evaluated {@code tail} {@code Stream}.
 * <p>
 * There are two implementations of the {@code Stream} interface:
 * <ul>
 * <li>{@link Empty}, which represents the empty {@code Stream}.</li>
 * <li>{@link Cons}, which represents a {@code Stream} containing one or more elements.</li>
 * </ul>
 * Methods to obtain a {@code Stream}:
 * <pre>
 * <code>
 * // factory methods
 * Stream.empty()                  // = Stream.of() = Nil.instance()
 * Stream.of(x)                    // = new Cons&lt;&gt;(x, Nil.instance())
 * Stream.of(Object...)            // e.g. Stream.of(1, 2, 3)
 * Stream.ofAll(Iterable)          // e.g. Stream.ofAll(List.of(1, 2, 3)) = 1, 2, 3
 * Stream.ofAll(&lt;primitive array&gt;) // e.g. List.ofAll(new int[] {1, 2, 3}) = 1, 2, 3
 *
 * // int sequences
 * Stream.from(0)                  // = 0, 1, 2, 3, ...
 * Stream.range(0, 3)              // = 0, 1, 2
 * Stream.rangeClosed(0, 3)        // = 0, 1, 2, 3
 *
 * // generators
 * Stream.cons(Object, Supplier)   // e.g. Stream.cons(current, () -&gt; next(current));
 * Stream.gen(Supplier)            // e.g. Stream.gen(Math::random);
 * Stream.gen(Object, Function)    // e.g. Stream.gen(1, i -&gt; i * 2);
 * </code>
 * </pre>
 *
 * Factory method applications:
 *
 * <pre>
 * <code>
 * Stream&lt;Integer&gt;       s1 = Stream.of(1);
 * Stream&lt;Integer&gt;       s2 = Stream.of(1, 2, 3);
 *                       // = Stream.of(new Integer[] {1, 2, 3});
 *
 * Stream&lt;int[]&gt;         s3 = Stream.ofAll(new int[] {1, 2, 3});
 * Stream&lt;List&lt;Integer&gt;&gt; s4 = Stream.ofAll(List.of(1, 2, 3));
 *
 * Stream&lt;Integer&gt;       s5 = Stream.ofAll(new int[] {1, 2, 3});
 * Stream&lt;Integer&gt;       s6 = Stream.ofAll(List.of(1, 2, 3));
 *
 * // cuckoo's egg
 * Stream&lt;Integer[]&gt;     s7 = Stream.&lt;Integer[]&gt; of(new Integer[] {1, 2, 3});
 * </code>
 * </pre>
 *
 * Example: Generating prime numbers
 *
 * <pre>
 * <code>
 * // = Stream(2L, 3L, 5L, 7L, ...)
 * Stream.gen(2L, PrimeNumbers::nextPrimeFrom)
 *
 * // helpers
 *
 * static long nextPrimeFrom(long num) {
 *     return Stream.from(num + 1).find(PrimeNumbers::isPrime).get();
 * }
 *
 * static boolean isPrime(long num) {
 *     return !Stream.rangeClosed(2L, (long) Math.sqrt(num)).exists(d -&gt; num % d == 0);
 * }
 * </code>
 * </pre>
 *
 * See Okasaki, Chris: <em>Purely Functional Data Structures</em> (p. 34 ff.). Cambridge, 2003.
 *
 * @param <T> component type of this Stream
 * @author Daniel Dietrich, Jörgen Andersson, Ruslan Sennov
 * @since 1.1.0
 */
public interface Stream<T> extends LinearSeq<T> {

    long serialVersionUID = 1L;

    /**
     * Returns a {@link java.util.stream.Collector} which may be used in conjunction with
     * {@link java.util.stream.Stream#collect(java.util.stream.Collector)} to obtain a {@link javaslang.collection.Stream}.
     *
     * @param <T> Component type of the Stream.
     * @return A javaslang.collection.Stream Collector.
     */
    static <T> Collector<T, ArrayList<T>, Stream<T>> collector() {
        final Supplier<ArrayList<T>> supplier = ArrayList::new;
        final BiConsumer<ArrayList<T>, T> accumulator = ArrayList::add;
        final BinaryOperator<ArrayList<T>> combiner = (left, right) -> {
            left.addAll(right);
            return left;
        };
        final Function<ArrayList<T>, Stream<T>> finisher = Stream::ofAll;
        return Collector.of(supplier, accumulator, combiner, finisher);
    }

    /**
     * Returns an infinitely long Stream of int values starting from {@code from}.
     * <p>
     * The {@code Stream} extends to {@code Integer.MIN_VALUE} when passing {@code Integer.MAX_VALUE}.
     *
     * @param value a start int value
     * @return a new Stream of int values starting from {@code from}
     */
    static Stream<Integer> from(int value) {
        return Stream.ofAll(Iterator.from(value));
    }

    /**
     * Returns an infinitely long Stream of long values starting from {@code from}.
     * <p>
     * The {@code Stream} extends to {@code Integer.MIN_VALUE} when passing {@code Long.MAX_VALUE}.
     *
     * @param value a start long value
     * @return a new Stream of long values starting from {@code from}
     */
    static Stream<Long> from(long value) {
        return Stream.ofAll(Iterator.from(value));
    }

    /**
     * Generates an (theoretically) infinitely long Stream using a value Supplier.
     *
     * @param supplier A Supplier of Stream values
     * @param <T>      value type
     * @return A new Stream
     */
    static <T> Stream<T> gen(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        return Stream.ofAll(Iterator.gen(supplier));
    }

    /**
     * Generates a (theoretically) infinitely long Stream using a function to calculate the next value
     * based on the previous.
     *
     * @param seed The first value in the Stream
     * @param f    A function to calculate the next value based on the previous
     * @param <T>  value type
     * @return A new Stream
     */
    static <T> Stream<T> gen(T seed, Function<? super T, ? extends T> f) {
        Objects.requireNonNull(f, "f is null");
        return Stream.ofAll(Iterator.gen(seed, f));
    }

    /**
     * Constructs a Stream of a head element and a tail supplier.
     *
     * @param head         The head element of the Stream
     * @param tailSupplier A supplier of the tail values. To end the stream, return {@link Stream#empty}.
     * @param <T>          value type
     * @return A new Stream
     */
    @SuppressWarnings("unchecked")
    static <T> Stream<T> cons(T head, Supplier<? extends Stream<? extends T>> tailSupplier) {
        Objects.requireNonNull(tailSupplier, "tailSupplier is null");
        return new Cons<>(head, (Supplier<Stream<T>>) tailSupplier);
    }

    /**
     * Returns the single instance of Nil. Convenience method for {@code Nil.instance()}.
     * <p>
     * Note: this method intentionally returns type {@code Stream} and not {@code Nil}. This comes handy when folding.
     * If you explicitly need type {@code Nil} use {@linkplain Empty#instance()}.
     *
     * @param <T> Component type of Nil, determined by type inference in the particular context.
     * @return The empty list.
     */
    static <T> Stream<T> empty() {
        return Empty.instance();
    }

    /**
     * Returns a singleton {@code Stream}, i.e. a {@code Stream} of one element.
     *
     * @param element An element.
     * @param <T>     The component type
     * @return A new Stream instance containing the given element
     */
    static <T> Stream<T> of(T element) {
        return new Cons<>(element, Empty::instance);
    }

    /**
     * Creates a Stream of the given elements.
     *
     * <pre><code>  Stream.of(1, 2, 3, 4)
     * = Nil.instance().prepend(4).prepend(3).prepend(2).prepend(1)
     * = new Cons(1, new Cons(2, new Cons(3, new Cons(4, Nil.instance()))))</code></pre>
     *
     * @param <T>      Component type of the Stream.
     * @param elements Zero or more elements.
     * @return A list containing the given elements in the same order.
     */
    @SafeVarargs
    static <T> Stream<T> of(T... elements) {
        Objects.requireNonNull(elements, "elements is null");
        return Stream.ofAll(new Iterator<T>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < elements.length;
            }

            @Override
            public T next() {
                return elements[i++];
            }
        });
    }

    /**
     * Returns a Stream containing {@code n} values of a given Function {@code f}
     * over a range of integer values from 0 to {@code n - 1}.
     *
     * @param <T> Component type of the Stream
     * @param n The number of elements in the Stream
     * @param f The Function computing element values
     * @return A Stream consisting of elements {@code f(0),f(1), ..., f(n - 1)}
     * @throws NullPointerException if {@code f} is null
     */
    static <T> Stream<T> tabulate(int n, Function<? super Integer, ? extends T> f) {
        Objects.requireNonNull(f, "f is null");
        return Stream.ofAll(Collections.tabulate(n, f));
    }

    /**
     * Returns a Stream containing {@code n} values supplied by a given Supplier {@code s}.
     *
     * @param <T> Component type of the Stream
     * @param n The number of elements in the Stream
     * @param s The Supplier computing element values
     * @return A Stream of size {@code n}, where each element contains the result supplied by {@code s}.
     * @throws NullPointerException if {@code s} is null
     */
    static <T> Stream<T> fill(int n, Supplier<? extends T> s) {
        Objects.requireNonNull(s, "s is null");
        return Stream.ofAll(Collections.fill(n, s));
    }

    /**
     * Creates a Stream of the given elements.
     *
     * @param <T>      Component type of the Stream.
     * @param elements An Iterable of elements.
     * @return A list containing the given elements in the same order.
     */
    @SuppressWarnings("unchecked")
    static <T> Stream<T> ofAll(Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements is null");
        if (elements instanceof Stream) {
            return (Stream<T>) elements;
        } else {
            return StreamFactory.create(elements.iterator());
        }
    }

    /**
     * Creates a Stream based on the elements of a boolean array.
     *
     * @param array a boolean array
     * @return A new Stream of Boolean values
     */
    static Stream<Boolean> ofAll(boolean[] array) {
        Objects.requireNonNull(array, "array is null");
        return Stream.ofAll(Iterator.ofAll(array));
    }

    /**
     * Creates a Stream based on the elements of a byte array.
     *
     * @param array a byte array
     * @return A new Stream of Byte values
     */
    static Stream<Byte> ofAll(byte[] array) {
        Objects.requireNonNull(array, "array is null");
        return Stream.ofAll(Iterator.ofAll(array));
    }

    /**
     * Creates a Stream based on the elements of a char array.
     *
     * @param array a char array
     * @return A new Stream of Character values
     */
    static Stream<Character> ofAll(char[] array) {
        Objects.requireNonNull(array, "array is null");
        return Stream.ofAll(Iterator.ofAll(array));
    }

    /**
     * Creates a Stream based on the elements of a double array.
     *
     * @param array a double array
     * @return A new Stream of Double values
     */
    static Stream<Double> ofAll(double[] array) {
        Objects.requireNonNull(array, "array is null");
        return Stream.ofAll(Iterator.ofAll(array));
    }

    /**
     * Creates a Stream based on the elements of a float array.
     *
     * @param array a float array
     * @return A new Stream of Float values
     */
    static Stream<Float> ofAll(float[] array) {
        Objects.requireNonNull(array, "array is null");
        return Stream.ofAll(Iterator.ofAll(array));
    }

    /**
     * Creates a Stream based on the elements of an int array.
     *
     * @param array an int array
     * @return A new Stream of Integer values
     */
    static Stream<Integer> ofAll(int[] array) {
        Objects.requireNonNull(array, "array is null");
        return Stream.ofAll(Iterator.ofAll(array));
    }

    /**
     * Creates a Stream based on the elements of a long array.
     *
     * @param array a long array
     * @return A new Stream of Long values
     */
    static Stream<Long> ofAll(long[] array) {
        Objects.requireNonNull(array, "array is null");
        return Stream.ofAll(Iterator.ofAll(array));
    }

    /**
     * Creates a Stream based on the elements of a short array.
     *
     * @param array a short array
     * @return A new Stream of Short values
     */
    static Stream<Short> ofAll(short[] array) {
        Objects.requireNonNull(array, "array is null");
        return Stream.ofAll(Iterator.ofAll(array));
    }

    static Stream<Character> range(char from, char toExclusive) {
        return Stream.ofAll(Iterator.range(from, toExclusive));
    }

    static Stream<Character> rangeBy(char from, char toExclusive, int step) {
        return Stream.ofAll(Iterator.rangeBy(from, toExclusive, step));
    }

    static Stream<Double> rangeBy(double from, double toExclusive, double step) {
        return Stream.ofAll(Iterator.rangeBy(from, toExclusive, step));
    }

    /**
     * Creates a Stream of int numbers starting from {@code from}, extending to {@code toExclusive - 1}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Stream.range(0, 0)  // = Stream()
     * Stream.range(2, 0)  // = Stream()
     * Stream.range(-2, 2) // = Stream(-2, -1, 0, 1)
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toExclusive the last number + 1
     * @return a range of int values as specified or {@code Nil} if {@code from >= toExclusive}
     */
    static Stream<Integer> range(int from, int toExclusive) {
        return Stream.ofAll(Iterator.range(from, toExclusive));
    }

    /**
     * Creates a Stream of int numbers starting from {@code from}, extending to {@code toExclusive - 1},
     * with {@code step}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Stream.rangeBy(1, 3, 1)  // = Stream(1, 2)
     * Stream.rangeBy(1, 4, 2)  // = Stream(1, 3)
     * Stream.rangeBy(4, 1, -2) // = Stream(4, 2)
     * Stream.rangeBy(4, 1, 2)  // = Stream()
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toExclusive the last number + 1
     * @param step        the step
     * @return a range of long values as specified or {@code Nil} if<br>
     * {@code from >= toInclusive} and {@code step > 0} or<br>
     * {@code from <= toInclusive} and {@code step < 0}
     * @throws IllegalArgumentException if {@code step} is zero
     */
    static Stream<Integer> rangeBy(int from, int toExclusive, int step) {
        return Stream.ofAll(Iterator.rangeBy(from, toExclusive, step));
    }

    /**
     * Creates a Stream of long numbers starting from {@code from}, extending to {@code toExclusive - 1}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Stream.range(0L, 0L)  // = Stream()
     * Stream.range(2L, 0L)  // = Stream()
     * Stream.range(-2L, 2L) // = Stream(-2L, -1L, 0L, 1L)
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toExclusive the last number + 1
     * @return a range of long values as specified or {@code Nil} if {@code from >= toExclusive}
     */
    static Stream<Long> range(long from, long toExclusive) {
        return Stream.ofAll(Iterator.range(from, toExclusive));
    }

    /**
     * Creates a Stream of long numbers starting from {@code from}, extending to {@code toExclusive - 1},
     * with {@code step}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Stream.rangeBy(1L, 3L, 1L)  // = Stream(1L, 2L)
     * Stream.rangeBy(1L, 4L, 2L)  // = Stream(1L, 3L)
     * Stream.rangeBy(4L, 1L, -2L) // = Stream(4L, 2L)
     * Stream.rangeBy(4L, 1L, 2L)  // = Stream()
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toExclusive the last number + 1
     * @param step        the step
     * @return a range of long values as specified or {@code Nil} if<br>
     * {@code from >= toInclusive} and {@code step > 0} or<br>
     * {@code from <= toInclusive} and {@code step < 0}
     * @throws IllegalArgumentException if {@code step} is zero
     */
    static Stream<Long> rangeBy(long from, long toExclusive, long step) {
        return Stream.ofAll(Iterator.rangeBy(from, toExclusive, step));
    }

    static Stream<Character> rangeClosed(char from, char toInclusive) {
        return Stream.ofAll(Iterator.rangeClosed(from, toInclusive));
    }

    static Stream<Character> rangeClosedBy(char from, char toInclusive, int step) {
        return Stream.ofAll(Iterator.rangeClosedBy(from, toInclusive, step));
    }

    static Stream<Double> rangeClosedBy(double from, double toInclusive, double step) {
        return Stream.ofAll(Iterator.rangeClosedBy(from, toInclusive, step));
    }

    /**
     * Creates a Stream of int numbers starting from {@code from}, extending to {@code toInclusive}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Stream.rangeClosed(0, 0)  // = Stream(0)
     * Stream.rangeClosed(2, 0)  // = Stream()
     * Stream.rangeClosed(-2, 2) // = Stream(-2, -1, 0, 1, 2)
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toInclusive the last number
     * @return a range of int values as specified or {@code Nil} if {@code from > toInclusive}
     */
    static Stream<Integer> rangeClosed(int from, int toInclusive) {
        return Stream.ofAll(Iterator.rangeClosed(from, toInclusive));
    }

    /**
     * Creates a Stream of int numbers starting from {@code from}, extending to {@code toInclusive},
     * with {@code step}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Stream.rangeClosedBy(1, 3, 1)  // = Stream(1, 2, 3)
     * Stream.rangeClosedBy(1, 4, 2)  // = Stream(1, 3)
     * Stream.rangeClosedBy(4, 1, -2) // = Stream(4, 2)
     * Stream.rangeClosedBy(4, 1, 2)  // = Stream()
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toInclusive the last number
     * @param step        the step
     * @return a range of int values as specified or {@code Nil} if<br>
     * {@code from > toInclusive} and {@code step > 0} or<br>
     * {@code from < toInclusive} and {@code step < 0}
     * @throws IllegalArgumentException if {@code step} is zero
     */
    static Stream<Integer> rangeClosedBy(int from, int toInclusive, int step) {
        return Stream.ofAll(Iterator.rangeClosedBy(from, toInclusive, step));
    }

    /**
     * Creates a Stream of long numbers starting from {@code from}, extending to {@code toInclusive}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Stream.rangeClosed(0L, 0L)  // = Stream(0L)
     * Stream.rangeClosed(2L, 0L)  // = Stream()
     * Stream.rangeClosed(-2L, 2L) // = Stream(-2L, -1L, 0L, 1L, 2L)
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toInclusive the last number
     * @return a range of long values as specified or {@code Nil} if {@code from > toInclusive}
     */
    static Stream<Long> rangeClosed(long from, long toInclusive) {
        return Stream.ofAll(Iterator.rangeClosed(from, toInclusive));
    }

    /**
     * Creates a Stream of long numbers starting from {@code from}, extending to {@code toInclusive},
     * with {@code step}.
     * <p>
     * Examples:
     * <pre>
     * <code>
     * Stream.rangeClosedBy(1L, 3L, 1L)  // = Stream(1L, 2L, 3L)
     * Stream.rangeClosedBy(1L, 4L, 2L)  // = Stream(1L, 3L)
     * Stream.rangeClosedBy(4L, 1L, -2L) // = Stream(4L, 2L)
     * Stream.rangeClosedBy(4L, 1L, 2L)  // = Stream()
     * </code>
     * </pre>
     *
     * @param from        the first number
     * @param toInclusive the last number
     * @param step        the step
     * @return a range of int values as specified or {@code Nil} if<br>
     * {@code from > toInclusive} and {@code step > 0} or<br>
     * {@code from < toInclusive} and {@code step < 0}
     * @throws IllegalArgumentException if {@code step} is zero
     */
    static Stream<Long> rangeClosedBy(long from, long toInclusive, long step) {
        return Stream.ofAll(Iterator.rangeClosedBy(from, toInclusive, step));
    }

    /**
     * Repeats an element infinitely often.
     *
     * @param t   An element
     * @param <T> Element type
     * @return A new Stream containing infinite {@code t}'s.
     */
    static <T> Stream<T> repeat(T t) {
        return Stream.ofAll(Iterator.repeat(t));
    }

    @Override
    default Stream<T> append(T element) {
        if (isEmpty()) {
            return Stream.of(element);
        } else {
            // decoupling tail from `this`, see https://github.com/javaslang/javaslang/issues/824#issuecomment-158690009
            final Lazy<Stream<T>> tail = ((Cons<T>) this).tail;
            return new Cons<>(head(), () -> tail.get().append(element));
        }
    }

    @Override
    default Stream<T> appendAll(Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements is null");
        return isEmpty() ? Stream.ofAll(elements) : new Cons<>(head(), () -> tail().appendAll(elements));
    }

    /**
     * Appends itself to the end of stream with {@code mapper} function.
     * <p>
     * <strong>Example:</strong>
     * <p>
     * Well known scala code for Fibonacci infinite sequence
     * <pre>
     * <code>
     * val fibs:Stream[Int] = 0 #:: 1 #:: (fibs zip fibs.tail).map{ t =&gt; t._1 + t._2 }
     * </code>
     * </pre>
     * can be transformed to
     * <pre>
     * <code>
     * Stream.of(0, 1).appendSelf(self -&gt; self.zip(self.tail()).map(t -&gt; t._1 + t._2));
     * </code>
     * </pre>
     *
     * @param mapper an mapper
     * @return a new Stream
     */
    default Stream<T> appendSelf(Function<? super Stream<T>, ? extends Stream<T>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return isEmpty() ? this : new AppendSelf<>((Cons<T>) this, mapper).stream();
    }

    @Override
    default Stream<T> clear() {
        return Empty.instance();
    }

    @Override
    default Stream<Stream<T>> combinations() {
        return Stream.rangeClosed(0, length()).map(this::combinations).flatMap(Function.identity());
    }

    @Override
    default Stream<Stream<T>> combinations(int k) {
        return Combinations.apply(this, Math.max(k, 0));
    }

    @Override
    default Stream<Tuple2<T, T>> crossProduct() {
        return crossProduct(this);
    }

    @Override
    default Stream<Stream<T>> crossProduct(int power) {
        return Collections.crossProduct(this, power).map(Stream::ofAll).toStream();
    }

    @Override
    default <U> Stream<Tuple2<T, U>> crossProduct(Iterable<? extends U> that) {
        Objects.requireNonNull(that, "that is null");
        final Stream<U> other = Stream.ofAll(that);
        return flatMap(a -> other.map((Function<U, Tuple2<T, U>>) b -> Tuple.of(a, b)));
    }

    /**
     * Repeat the elements of this Stream infinitely.
     * <p>
     * Example:
     * <pre>
     * <code>
     * // = 1, 2, 3, 1, 2, 3, 1, 2, 3, ...
     * Stream.of(1, 2, 3).cycle();
     * </code>
     * </pre>
     *
     * @return A new Stream containing this elements cycled.
     */
    default Stream<T> cycle() {
        return appendSelf(Function.identity());
    }

    @Override
    default Stream<T> distinct() {
        return distinctBy(Function.identity());
    }

    @Override
    default Stream<T> distinctBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "comparator is null");
        final java.util.Set<T> seen = new java.util.TreeSet<>(comparator);
        return filter(seen::add);
    }

    @Override
    default <U> Stream<T> distinctBy(Function<? super T, ? extends U> keyExtractor) {
        final java.util.Set<U> seen = new java.util.HashSet<>();
        return filter(t -> seen.add(keyExtractor.apply(t)));
    }

    @Override
    default Stream<T> drop(long n) {
        Stream<T> stream = this;
        while (n-- > 0 && !stream.isEmpty()) {
            stream = stream.tail();
        }
        return stream;
    }

    @Override
    default Stream<T> dropRight(long n) {
        if (n <= 0) {
            return this;
        } else {
            return DropRight.apply(take(n).toList(), List.empty(), drop(n));
        }
    }

    @Override
    default Stream<T> dropUntil(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return dropWhile(predicate.negate());
    }

    @Override
    default Stream<T> dropWhile(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        Stream<T> stream = this;
        while (!stream.isEmpty() && predicate.test(stream.head())) {
            stream = stream.tail();
        }
        return stream;
    }

    @Override
    default Stream<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        Stream<T> stream = this;
        while (!stream.isEmpty() && !predicate.test(stream.head())) {
            stream = stream.tail();
        }
        final Stream<T> finalStream = stream;
        return stream.isEmpty() ? stream : new Cons<>(stream.head(), () -> finalStream.tail().filter(predicate));
    }

    @Override
    default <U> Stream<U> flatMap(Function<? super T, ? extends Iterable<? extends U>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return isEmpty() ? Empty.instance() : Stream.ofAll(new Iterator<U>() {

            final Iterator<? extends T> inputs = Stream.this.iterator();
            java.util.Iterator<? extends U> current = java.util.Collections.emptyIterator();

            @Override
            public boolean hasNext() {
                boolean currentHasNext;
                while (!(currentHasNext = current.hasNext()) && inputs.hasNext()) {
                    current = mapper.apply(inputs.next()).iterator();
                }
                return currentHasNext;
            }

            @Override
            public U next() {
                return current.next();
            }
        });
    }

    @Override
    default T get(int index) {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("get(" + index + ") on Nil");
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("get(" + index + ")");
        }
        Stream<T> stream = this;
        for (int i = index - 1; i >= 0; i--) {
            stream = stream.tail();
            if (stream.isEmpty()) {
                throw new IndexOutOfBoundsException(String.format("get(%s) on Stream of size %s", index, index - i));
            }
        }
        return stream.head();
    }

    @Override
    default <C> Map<C, Stream<T>> groupBy(Function<? super T, ? extends C> classifier) {
        Objects.requireNonNull(classifier, "classifier is null");
        return iterator().groupBy(classifier).map((c, it) -> Tuple.of(c, Stream.ofAll(it)));
    }

    @Override
    default Iterator<Stream<T>> grouped(long size) {
        return sliding(size, size);
    }

    @Override
    default boolean hasDefiniteSize() {
        return false;
    }

    @Override
    default Option<T> headOption() {
        return isEmpty() ? Option.none() : Option.some(head());
    }

    @Override
    default int indexOf(Object element, int from) {
        int index = 0;
        for (Stream<T> stream = this; !stream.isEmpty(); stream = stream.tail(), index++) {
            if (index >= from && Objects.equals(stream.head(), element)) {
                return index;
            }
        }
        return -1;
    }

    @Override
    default Stream<T> init() {
        if (isEmpty()) {
            throw new UnsupportedOperationException("init of empty stream");
        } else {
            final Stream<T> tail = tail();
            if (tail.isEmpty()) {
                return Empty.instance();
            } else {
                return new Cons<>(head(), tail::init);
            }
        }
    }

    @Override
    default Option<Stream<T>> initOption() {
        return isEmpty() ? Option.none() : Option.some(init());
    }

    @Override
    default Stream<T> insert(int index, T element) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("insert(" + index + ", e)");
        } else if (index == 0) {
            return new Cons<>(element, () -> this);
        } else if (isEmpty()) {
            throw new IndexOutOfBoundsException("insert(" + index + ", e) on Nil");
        } else {
            return new Cons<>(head(), () -> tail().insert(index - 1, element));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    default Stream<T> insertAll(int index, Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements is null");
        if (index < 0) {
            throw new IndexOutOfBoundsException("insertAll(" + index + ", elements)");
        } else if (index == 0) {
            return isEmpty() ? Stream.ofAll(elements) : Stream.ofAll((Iterable<T>) elements).appendAll(this);
        } else if (isEmpty()) {
            throw new IndexOutOfBoundsException("insertAll(" + index + ", elements) on Nil");
        } else {
            return new Cons<>(head(), () -> tail().insertAll(index - 1, elements));
        }
    }

    @Override
    default Stream<T> intersperse(T element) {
        if (isEmpty()) {
            return this;
        } else {
            return new Cons<>(head(), () -> {
                final Stream<T> tail = tail();
                return tail.isEmpty() ? tail : new Cons<>(element, () -> tail.intersperse(element));
            });
        }
    }

    @Override
    default boolean isTraversableAgain() {
        return true;
    }

    @Override
    default int lastIndexOf(T element, int end) {
        int result = -1, index = 0;
        for (Stream<T> stream = this; index <= end && !stream.isEmpty(); stream = stream.tail(), index++) {
            if (Objects.equals(stream.head(), element)) {
                result = index;
            }
        }
        return result;
    }

    @Override
    default int length() {
        return foldLeft(0, (n, ignored) -> n + 1);
    }

    @Override
    default <U> Stream<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (isEmpty()) {
            return Empty.instance();
        } else {
            return new Cons<>(mapper.apply(head()), () -> tail().map(mapper));
        }
    }

    @Override
    default Match.MatchValue.Of<Stream<T>> match() {
        return Match.of(this);
    }

    @Override
    default Stream<T> padTo(int length, T element) {
        if (length <= 0) {
            return this;
        } else if (isEmpty()) {
            return Stream.ofAll(Iterator.gen(() -> element).take(length));
        } else {
            return new Cons<>(head(), () -> tail().padTo(length - 1, element));
        }
    }

    @Override
    default Stream<T> patch(int from, Iterable<? extends T> that, int replaced) {
        from = from < 0 ? 0 : from;
        replaced = replaced < 0 ? 0 : replaced;
        Stream<T> result = take(from).appendAll(that);
        from += replaced;
        result = result.appendAll(drop(from));
        return result;
    }

    @Override
    default Tuple2<Stream<T>, Stream<T>> partition(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return Tuple.of(filter(predicate), filter(predicate.negate()));
    }

    @Override
    default Stream<T> peek(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (isEmpty()) {
            return this;
        } else {
            final T head = head();
            action.accept(head);
            return new Cons<>(head, () -> tail().peek(action));
        }
    }

    @Override
    default Stream<Stream<T>> permutations() {
        if (isEmpty()) {
            return Empty.instance();
        } else {
            final Stream<T> tail = tail();
            if (tail.isEmpty()) {
                return Stream.of(this);
            } else {
                final Stream<Stream<T>> zero = Empty.instance();
                return distinct().foldLeft(zero, (xs, x) -> {
                    final Function<Stream<T>, Stream<T>> prepend = l -> l.prepend(x);
                    return xs.appendAll(remove(x).permutations().map(prepend));
                });
            }
        }
    }

    @Override
    default Stream<T> prepend(T element) {
        return new Cons<>(element, () -> this);
    }

    @SuppressWarnings("unchecked")
    @Override
    default Stream<T> prependAll(Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements is null");
        return Stream.ofAll((Iterable<T>) elements).appendAll(this);
    }

    @Override
    default Stream<T> remove(T element) {
        if (isEmpty()) {
            return this;
        } else {
            final T head = head();
            return Objects.equals(head, element) ? tail() : new Cons<>(head, () -> tail().remove(element));
        }
    }

    @Override
    default Stream<T> removeFirst(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        if (isEmpty()) {
            return this;
        } else {
            final T head = head();
            return predicate.test(head) ? tail() : new Cons<>(head, () -> tail().removeFirst(predicate));
        }
    }

    @Override
    default Stream<T> removeLast(Predicate<T> predicate) {
        return isEmpty() ? this : reverse().removeFirst(predicate).reverse();
    }

    @Override
    default Stream<T> removeAt(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("removeAt(" + index + ")");
        } else if (index == 0) {
            return tail();
        } else if (isEmpty()) {
            throw new IndexOutOfBoundsException("removeAt() on Nil");
        } else {
            return new Cons<>(head(), () -> tail().removeAt(index - 1));
        }
    }

    @Override
    default Stream<T> removeAll(T removed) {
        return filter(e -> !Objects.equals(e, removed));
    }

    @SuppressWarnings("unchecked")
    @Override
    default Stream<T> removeAll(Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements is null");
        final Stream<T> distinct = Stream.ofAll((Iterable<T>) elements).distinct();
        return filter(e -> !distinct.contains(e));
    }

    @Override
    default Stream<T> replace(T currentElement, T newElement) {
        if (isEmpty()) {
            return this;
        } else {
            final T head = head();
            if (Objects.equals(head, currentElement)) {
                return new Cons<>(newElement, this::tail);
            } else {
                return new Cons<>(head, () -> tail().replace(currentElement, newElement));
            }
        }
    }

    @Override
    default Stream<T> replaceAll(T currentElement, T newElement) {
        if (isEmpty()) {
            return this;
        } else {
            final T head = head();
            final T newHead = Objects.equals(head, currentElement) ? newElement : head;
            return new Cons<>(newHead, () -> tail().replaceAll(currentElement, newElement));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    default Stream<T> retainAll(Iterable<? extends T> elements) {
        Objects.requireNonNull(elements, "elements is null");
        if (isEmpty()) {
            return this;
        } else {
            final Stream<T> retained = Stream.ofAll((Iterable<T>) elements).distinct();
            return filter(retained::contains);
        }
    }

    @Override
    default Stream<T> reverse() {
        return isEmpty() ? this : foldLeft(Stream.empty(), Stream::prepend);
    }

    @Override
    default Stream<T> scan(T zero, BiFunction<? super T, ? super T, ? extends T> operation) {
        return scanLeft(zero, operation);
    }

    @Override
    default <U> Stream<U> scanLeft(U zero, BiFunction<? super U, ? super T, ? extends U> operation) {
        Objects.requireNonNull(operation, "operation is null");
        // lazily streams the elements of an iterator
        return Stream.ofAll(iterator().scanLeft(zero, operation));
    }

    // not lazy!
    @Override
    default <U> Stream<U> scanRight(U zero, BiFunction<? super T, ? super U, ? extends U> operation) {
        Objects.requireNonNull(operation, "operation is null");
        return Collections.scanRight(this, zero, operation, Stream.empty(), Stream::prepend, Function.identity());
    }

    @Override
    default Stream<T> slice(long beginIndex, long endIndex) {
        if (beginIndex >= endIndex || isEmpty()) {
            return empty();
        } else {
            final long lowerBound = Math.max(beginIndex, 0);
            if (lowerBound == 0) {
                return new Cons<>(head(), () -> tail().slice(0, endIndex - 1));
            } else {
                return tail().slice(lowerBound - 1, endIndex - 1);
            }
        }
    }

    @Override
    default Iterator<Stream<T>> sliding(long size) {
        return sliding(size, 1);
    }

    @Override
    default Iterator<Stream<T>> sliding(long size, long step) {
        return iterator().sliding(size, step).map(Stream::ofAll);
    }

    @Override
    default Stream<T> sorted() {
        return isEmpty() ? this : toJavaStream().sorted().collect(Stream.collector());
    }

    @Override
    default Stream<T> sorted(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "comparator is null");
        return isEmpty() ? this : toJavaStream().sorted(comparator).collect(Stream.collector());
    }

    @Override
    default <U extends Comparable<? super U>> Stream<T> sortBy(Function<? super T, ? extends U> mapper) {
        return sortBy(U::compareTo, mapper);
    }

    @Override
    default <U> Stream<T> sortBy(Comparator<? super U> comparator, Function<? super T, ? extends U> mapper) {
        final Function<? super T, ? extends U> domain = Function1.of(mapper::apply).memoized();
        return toJavaStream()
                .sorted((e1, e2) -> comparator.compare(domain.apply(e1), domain.apply(e2)))
                .collect(collector());
    }

    @Override
    default Tuple2<Stream<T>, Stream<T>> span(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return Tuple.of(takeWhile(predicate), dropWhile(predicate));
    }

    @Override
    default Tuple2<Stream<T>, Stream<T>> splitAt(long n) {
        return Tuple.of(take(n), drop(n));
    }

    @Override
    default Tuple2<Stream<T>, Stream<T>> splitAt(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return Tuple.of(takeWhile(predicate.negate()), dropWhile(predicate.negate()));
    }

    @Override
    default Tuple2<Stream<T>, Stream<T>> splitAtInclusive(Predicate<? super T> predicate) {
        final Tuple2<Stream<T>, Stream<T>> split = splitAt(predicate);
        if (split._2.isEmpty()) {
            return split;
        } else {
            return Tuple.of(split._1.append(split._2.head()), split._2.tail());
        }
    }

    @Override
    default Spliterator<T> spliterator() {
        // the focus of the Stream API is on random-access collections of *known size*
        return Spliterators.spliterator(iterator(), length(), Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    @Override
    default String stringPrefix() {
        return "Stream";
    }

    @Override
    default Stream<T> subSequence(int beginIndex) {
        if (beginIndex < 0) {
            throw new IndexOutOfBoundsException("subSequence(" + beginIndex + ")");
        }
        Stream<T> result = this;
        for (int i = 0; i < beginIndex; i++, result = result.tail()) {
            if (result.isEmpty()) {
                throw new IndexOutOfBoundsException(
                        String.format("subSequence(%s) on Stream of size %s", beginIndex, i));
            }
        }
        return result;
    }

    @Override
    default Stream<T> subSequence(int beginIndex, int endIndex) {
        if (beginIndex < 0 || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException(String.format("subSequence(%s, %s)", beginIndex, endIndex));
        }
        if (beginIndex == endIndex) {
            return Empty.instance();
        } else if (isEmpty()) {
            throw new IndexOutOfBoundsException("subSequence of Nil");
        } else if (beginIndex == 0) {
            return new Cons<>(head(), () -> tail().subSequence(0, endIndex - 1));
        } else {
            return tail().subSequence(beginIndex - 1, endIndex - 1);
        }
    }

    @Override
    Stream<T> tail();

    @Override
    default Option<Stream<T>> tailOption() {
        return isEmpty() ? Option.none() : Option.some(tail());
    }

    @Override
    default Stream<T> take(long n) {
        if (n < 1 || isEmpty()) {
            return Empty.instance();
        } else {
            return new Cons<>(head(), () -> tail().take(n - 1));
        }
    }

    @Override
    default Stream<T> takeRight(long n) {
        Stream<T> right = this;
        Stream<T> remaining = drop(n);
        while (!remaining.isEmpty()) {
            right = right.tail();
            remaining = remaining.tail();
        }
        return right;
    }

    @Override
    default Stream<T> takeUntil(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return takeWhile(predicate.negate());
    }

    @Override
    default Stream<T> takeWhile(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        if (isEmpty()) {
            return Empty.instance();
        } else {
            final T head = head();
            if (predicate.test(head)) {
                return new Cons<>(head, () -> tail().takeWhile(predicate));
            } else {
                return Empty.instance();
            }
        }
    }

    @Override
    default <U> Stream<U> unit(Iterable<? extends U> iterable) {
        return Stream.ofAll(iterable);
    }

    @Override
    default <T1, T2> Tuple2<Stream<T1>, Stream<T2>> unzip(
            Function<? super T, Tuple2<? extends T1, ? extends T2>> unzipper) {
        Objects.requireNonNull(unzipper, "unzipper is null");
        final Stream<Tuple2<? extends T1, ? extends T2>> stream = map(unzipper);
        final Stream<T1> stream1 = stream.map(t -> t._1);
        final Stream<T2> stream2 = stream.map(t -> t._2);
        return Tuple.of(stream1, stream2);
    }

    @Override
    default <T1, T2, T3> Tuple3<Stream<T1>, Stream<T2>, Stream<T3>> unzip3(
            Function<? super T, Tuple3<? extends T1, ? extends T2, ? extends T3>> unzipper) {
        Objects.requireNonNull(unzipper, "unzipper is null");
        final Stream<Tuple3<? extends T1, ? extends T2, ? extends T3>> stream = map(unzipper);
        final Stream<T1> stream1 = stream.map(t -> t._1);
        final Stream<T2> stream2 = stream.map(t -> t._2);
        final Stream<T3> stream3 = stream.map(t -> t._3);
        return Tuple.of(stream1, stream2, stream3);
    }

    @Override
    default Stream<T> update(int index, T element) {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("update(" + index + ", e) on Nil");
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException("update(" + index + ", e)");
        }
        Stream<T> preceding = Empty.instance();
        Stream<T> tail = this;
        for (int i = index; i > 0; i--, tail = tail.tail()) {
            if (tail.isEmpty()) {
                throw new IndexOutOfBoundsException("update at " + index);
            }
            preceding = preceding.prepend(tail.head());
        }
        if (tail.isEmpty()) {
            throw new IndexOutOfBoundsException("update at " + index);
        }
        // skip the current head element because it is replaced
        return preceding.reverse().appendAll(tail.tail().prepend(element));
    }

    @Override
    default <U> Stream<Tuple2<T, U>> zip(Iterable<U> iterable) {
        Objects.requireNonNull(iterable, "iterable is null");
        return Stream.ofAll(iterator().zip(iterable));
    }

    @Override
    default <U> Stream<Tuple2<T, U>> zipAll(Iterable<U> iterable, T thisElem, U thatElem) {
        Objects.requireNonNull(iterable, "iterable is null");
        return Stream.ofAll(iterator().zipAll(iterable, thisElem, thatElem));
    }

    @Override
    default Stream<Tuple2<T, Long>> zipWithIndex() {
        return Stream.ofAll(iterator().zipWithIndex());
    }

    /**
     * The empty Stream.
     * <p>
     * This is a singleton, i.e. not Cloneable.
     *
     * @param <T> Component type of the Stream.
     * @since 1.1.0
     */
    final class Empty<T> implements Stream<T>, Serializable {

        private static final long serialVersionUID = 1L;

        private static final Empty<?> INSTANCE = new Empty<>();

        // hidden
        private Empty() {
        }

        /**
         * Returns the singleton empty Stream instance.
         *
         * @param <T> Component type of the Stream
         * @return The empty Stream
         */
        @SuppressWarnings("unchecked")
        public static <T> Empty<T> instance() {
            return (Empty<T>) INSTANCE;
        }

        @Override
        public T head() {
            throw new NoSuchElementException("head of empty stream");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Iterator<T> iterator() {
            return Iterator.empty();
        }

        @Override
        public Stream<T> tail() {
            throw new UnsupportedOperationException("tail of empty stream");
        }

        @Override
        public boolean equals(Object o) {
            return o == this;
        }

        @Override
        public int hashCode() {
            return Traversable.hash(this);
        }

        @Override
        public String toString() {
            return stringPrefix() + "()";
        }

        /**
         * Instance control for object serialization.
         *
         * @return The singleton instance of Nil.
         * @see java.io.Serializable
         */
        private Object readResolve() {
            return INSTANCE;
        }
    }

    /**
     * Non-empty {@code Stream}, consisting of a {@code head}, a {@code tail} and an optional
     * {@link java.lang.AutoCloseable}.
     *
     * @param <T> Component type of the Stream.
     * @since 1.1.0
     */
    // DEV NOTE: class declared final because of serialization proxy pattern.
    // (see Effective Java, 2nd ed., p. 315)
    final class Cons<T> implements Stream<T>, Serializable {

        private static final long serialVersionUID = 1L;

        private final T head;
        private final Lazy<Stream<T>> tail;

        /**
         * Creates a new {@code Stream} consisting of a head element and a lazy trailing {@code Stream}.
         *
         * @param head A head element
         * @param tail A tail {@code Stream} supplier, {@linkplain Empty} denotes the end of the {@code Stream}
         */
        private Cons(T head, Supplier<Stream<T>> tail) {
            Objects.requireNonNull(tail, "tail is null");
            this.head = head;
            this.tail = Lazy.of(tail);
        }

        @Override
        public T head() {
            return head;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Iterator<T> iterator() {
            return new StreamIterator<>(this);
        }

        @Override
        public Stream<T> tail() {
            return tail.get();
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof Stream) {
                Stream<?> stream1 = this;
                Stream<?> stream2 = (Stream<?>) o;
                while (!stream1.isEmpty() && !stream2.isEmpty()) {
                    final boolean isEqual = Objects.equals(stream1.head(), stream2.head());
                    if (!isEqual) {
                        return false;
                    }
                    stream1 = stream1.tail();
                    stream2 = stream2.tail();
                }
                return stream1.isEmpty() && stream2.isEmpty();
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Traversable.hash(this);
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder(stringPrefix()).append("(");
            Stream<T> stream = this;
            while (stream != null && !stream.isEmpty()) {
                final Cons<T> cons = (Cons<T>) stream;
                builder.append(cons.head);
                if (cons.tail.isEvaluated()) {
                    stream = cons.tail.get();
                    if (!stream.isEmpty()) {
                        builder.append(", ");
                    }
                } else {
                    builder.append(", ?");
                    stream = null;
                }
            }
            return builder.append(")").toString();
        }

        /**
         * {@code writeReplace} method for the serialization proxy pattern.
         * <p>
         * The presence of this method causes the serialization system to emit a SerializationProxy instance instead of
         * an instance of the enclosing class.
         *
         * @return A SerialiationProxy for this enclosing class.
         */
        private Object writeReplace() {
            return new SerializationProxy<>(this);
        }

        /**
         * {@code readObject} method for the serialization proxy pattern.
         * <p>
         * Guarantees that the serialization system will never generate a serialized instance of the enclosing class.
         *
         * @param stream An object serialization stream.
         * @throws java.io.InvalidObjectException This method will throw with the message "Proxy required".
         */
        private void readObject(ObjectInputStream stream) throws InvalidObjectException {
            throw new InvalidObjectException("Proxy required");
        }

        /**
         * A serialization proxy which, in this context, is used to deserialize immutable, linked Streams with final
         * instance fields.
         *
         * @param <T> The component type of the underlying stream.
         */
        // DEV NOTE: The serialization proxy pattern is not compatible with non-final, i.e. extendable,
        // classes. Also, it may not be compatible with circular object graphs.
        private static final class SerializationProxy<T> implements Serializable {

            private static final long serialVersionUID = 1L;

            // the instance to be serialized/deserialized
            private transient Cons<T> stream;

            /**
             * Constructor for the case of serialization, called by {@link Cons#writeReplace()}.
             * <p>
             * The constructor of a SerializationProxy takes an argument that concisely represents the logical state of
             * an instance of the enclosing class.
             *
             * @param stream a Cons
             */
            SerializationProxy(Cons<T> stream) {
                this.stream = stream;
            }

            /**
             * Write an object to a serialization stream.
             *
             * @param s An object serialization stream.
             * @throws java.io.IOException If an error occurs writing to the stream.
             */
            private void writeObject(ObjectOutputStream s) throws IOException {
                s.defaultWriteObject();
                s.writeInt(stream.length());
                for (Stream<T> l = stream; !l.isEmpty(); l = l.tail()) {
                    s.writeObject(l.head());
                }
            }

            /**
             * Read an object from a deserialization stream.
             *
             * @param s An object deserialization stream.
             * @throws ClassNotFoundException If the object's class read from the stream cannot be found.
             * @throws InvalidObjectException If the stream contains no stream elements.
             * @throws IOException            If an error occurs reading from the stream.
             */
            private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
                s.defaultReadObject();
                final int size = s.readInt();
                if (size <= 0) {
                    throw new InvalidObjectException("No elements");
                }
                Stream<T> temp = Empty.instance();
                for (int i = 0; i < size; i++) {
                    @SuppressWarnings("unchecked")
                    final T element = (T) s.readObject();
                    temp = temp.append(element);
                }
                // DEV-NOTE: Cons is deserialized
                stream = (Cons<T>) temp;
            }

            /**
             * {@code readResolve} method for the serialization proxy pattern.
             * <p>
             * Returns a logically equivalent instance of the enclosing class. The presence of this method causes the
             * serialization system to translate the serialization proxy back into an instance of the enclosing class
             * upon deserialization.
             *
             * @return A deserialized instance of the enclosing class.
             */
            private Object readResolve() {
                return stream;
            }
        }
    }
}

interface StreamModule {

    final class AppendSelf<T> {

        private final Cons<T> self;

        AppendSelf(Cons<T> self, Function<? super Stream<T>, ? extends Stream<T>> mapper) {
            this.self = appendAll(self, mapper);
        }

        private Cons<T> appendAll(Cons<T> stream, Function<? super Stream<T>, ? extends Stream<T>> mapper) {
            return (Cons<T>) Stream.cons(stream.head(), () -> {
                final Stream<T> tail = stream.tail();
                return tail.isEmpty() ? mapper.apply(self) : appendAll((Cons<T>) tail, mapper);
            });
        }

        Cons<T> stream() {
            return self;
        }
    }

    interface Combinations {

        static <T> Stream<Stream<T>> apply(Stream<T> elements, int k) {
            if (k == 0) {
                return Stream.of(Stream.empty());
            } else {
                return elements.zipWithIndex().flatMap(
                        t -> apply(elements.drop(t._2 + 1), (k - 1)).map((Stream<T> c) -> c.prepend(t._1))
                );
            }
        }
    }

    interface DropRight {

        // works with infinite streams by buffering elements
        static <T> Stream<T> apply(List<T> front, List<T> rear, Stream<T> remaining) {
            if (remaining.isEmpty()) {
                return remaining;
            } else if (front.isEmpty()) {
                return apply(rear.reverse(), List.empty(), remaining);
            } else {
                return Stream.cons(front.head(),
                        () -> apply(front.tail(), rear.prepend(remaining.head()), remaining.tail()));
            }
        }
    }

    interface StreamFactory {

        static <T> Stream<T> create(java.util.Iterator<? extends T> iterator) {
            return iterator.hasNext() ? Stream.cons(iterator.next(), () -> create(iterator)) : Empty.instance();
        }
    }

    final class StreamIterator<T> extends AbstractIterator<T> {

        private Supplier<Stream<T>> current;

        StreamIterator(Cons<T> stream) {
            this.current = () -> stream;
        }

        @Override
        public boolean hasNext() {
            return !current.get().isEmpty();
        }

        @Override
        public T getNext() {
            final Stream<T> stream = current.get();
            // DEV-NOTE: we make the stream even more lazy because the next head must not be evaluated on hasNext()
            current = stream::tail;
            return stream.head();
        }
    }
}
