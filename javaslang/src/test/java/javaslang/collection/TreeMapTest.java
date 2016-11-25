/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.Tuple;
import javaslang.Tuple2;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.*;
import java.util.stream.Stream;

import static java.util.Comparator.nullsFirst;
import static javaslang.API.*;
import static javaslang.Serializables.deserialize;
import static javaslang.Serializables.serialize;
import static javaslang.collection.Comparators.naturalComparator;

public class TreeMapTest extends AbstractSortedMapTest {

    @Override
    protected String className() {
        return "TreeMap";
    }

    @Override
    <T1, T2> java.util.Map<T1, T2> javaEmptyMap() {
        return new java.util.TreeMap<>();
    }

    @Override
    protected <T1 extends Comparable<? super T1>, T2> TreeMap<T1, T2> emptyMap() {
        return TreeMap.empty();
    }

    @Override
    protected boolean emptyMapShouldBeSingleton() {
        return false;
    }

    @Override
    protected boolean emptyShouldBeSingleton() {
        return false;
    }

    @Override
    protected <T> Collector<Tuple2<Integer, T>, ArrayList<Tuple2<Integer, T>>, ? extends Map<Integer, T>> mapCollector() {
        return TreeMap.collector();
    }

    @SuppressWarnings("varargs")
    @SafeVarargs
    @Override
    protected final <K extends Comparable<? super K>, V> TreeMap<K, V> mapOfTuples(Tuple2<? extends K, ? extends V>... entries) {
        return TreeMap.ofEntries(entries);
    }

    @SuppressWarnings("varargs")
    @SafeVarargs
    @Override
    protected final <K extends Comparable<? super K>, V> TreeMap<K, V> mapOfEntries(java.util.Map.Entry<? extends K, ? extends V>... entries) {
        return TreeMap.ofEntries(entries);
    }

    @Override
    protected <K extends Comparable<? super K>, V> TreeMap<K, V> mapOf(K key, V value) {
        return TreeMap.of(key, value);
    }

    @Override
    protected <K extends Comparable<? super K>, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        return TreeMap.of(k1, v1, k2, v2);
    }

    @Override
    protected <K extends Comparable<? super K>, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
        return TreeMap.of(k1, v1, k2, v2, k3, v3);
    }

    @Override
    protected <K extends Comparable<? super K>, V> Map<K, V> mapOfNullKey(K k1, V v1, K k2, V v2) {
        return TreeMap.of(nullsFirst(naturalComparator()), k1, v1, k2, v2);
    }

    @Override
    protected <K extends Comparable<? super K>, V> Map<K, V> mapOfNullKey(K k1, V v1, K k2, V v2, K k3, V v3) {
        return TreeMap.of(nullsFirst(naturalComparator()), k1, v1, k2, v2, k3, v3);
    }

    @Override
    protected <T, K extends Comparable<? super K>, V> Map<K, V> mapOf(Stream<? extends T> stream, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        return TreeMap.ofAll(stream, keyMapper, valueMapper);
    }

    @Override
    protected <K extends Comparable<? super K>, V> TreeMap<K, V> mapTabulate(int n, Function<? super Integer, ? extends Tuple2<? extends K, ? extends V>> f) {
        return TreeMap.tabulate(n, f);
    }

    @Override
    protected <K extends Comparable<? super K>, V> TreeMap<K, V> mapFill(int n, Supplier<? extends Tuple2<? extends K, ? extends V>> s) {
        return TreeMap.fill(n, s);
    }

    // -- static factories

    @Test
    public void shouldCreateOfEntriesUsingNoComparator() {
        final List<Tuple2<Integer, String>> entries = List(Tuple(1, "a"), Tuple(2, "b"));
        final TreeMap<Integer, String> map = TreeMap.ofEntries(entries);
        assertThat(map.toList()).isEqualTo(entries);
    }

    @Test
    public void shouldCreateOfEntriesUsingNaturalComparator() {
        final List<Tuple2<Integer, String>> entries = List(Tuple(1, "a"), Tuple(2, "b"));
        final TreeMap<Integer, String> map = TreeMap.ofEntries(naturalComparator(), entries);
        assertThat(map.toList()).isEqualTo(entries);
    }

    // -- static narrow

    @Test
    public void shouldNarrowTreeMap() {
        final TreeMap<Integer, Double> int2doubleMap = mapOf(1, 1.0d);
        final TreeMap<Integer, Number> number2numberMap = TreeMap.narrow(int2doubleMap);
        final int actual = number2numberMap.put(2, new BigDecimal("2.0")).values().sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    @Test
    public void shouldScan() {
        final TreeMap<String, Integer> tm = TreeMap.ofEntries(Tuple.of("one", 1), Tuple.of("two", 2));
        final TreeMap<String, Integer> result = tm.scan(Tuple.of("z", 0), (t1, t2) -> Tuple.of(t1._1 + t2._1, t1._2 + t2._2));
        assertThat(result).isEqualTo(TreeMap.ofEntries(Tuple.of("z", 0), Tuple.of("zone", 1), Tuple.of("zonetwo", 3)));
    }

    @Test
    public void shouldScanLeft() {
        final TreeMap<String, Integer> tm = TreeMap.ofEntries(Tuple.of("one", 1), Tuple.of("two", 2));
        final Seq<Tuple2<String, Integer>> result = tm.scanLeft(Tuple.of("z", 0), (t1, t2) -> Tuple.of(t1._1 + t2._1, t1._2 + t2._2));
        assertThat(result).isEqualTo(List.of(Tuple.of("z", 0), Tuple.of("zone", 1), Tuple.of("zonetwo", 3)));
    }

    @Test
    public void shouldScanRight() {
        final TreeMap<String, Integer> tm = TreeMap.ofEntries(Tuple.of("one", 1), Tuple.of("two", 2));
        final Seq<String> result = tm.scanRight("z", (t1, acc) -> acc + CharSeq.of(t1._1).reverse());
        assertThat(result).isEqualTo(List.of("zowteno", "zowt", "z"));
    }

    @Test
    public void shouldWrapMap() {
        final java.util.Map<Integer, Integer> source = new java.util.HashMap<>();
        source.put(1, 2);
        source.put(3, 4);
        assertThat(TreeMap.ofAll(source)).isEqualTo(emptyIntInt().put(1, 2).put(3, 4));
    }

    // -- ofAll

    @Test
    public void shouldCreateKeyComparatorForJavaUtilMap() {
        final TreeMap<String, Integer> actual = TreeMap.ofAll(mapOfTuples(Tuple.of("c", 0), Tuple.of("a", 0), Tuple.of("b", 0)).toJavaMap());
        final List<String> expected = List.of("a", "b", "c");
        assertThat(actual.keySet().toList()).isEqualTo(expected);
    }

    @Test
    public void shouldSerializeDeserializeNonEmptyMap() {
        final Object expected = TreeMap.ofAll(Collections.singletonMap(0, 1));
        final Object actual = deserialize(serialize(expected));
        assertThat(actual).isEqualTo(expected);
    }

    // -- flatMap

    @Test
    public void shouldReturnATreeMapWithCorrectComparatorWhenFlatMappingToEmpty() {

        final TreeMap<Integer, String> testee = TreeMap.of(Comparator.naturalOrder(), 1, "1", 2, "2");
        assertThat(testee.head()).isEqualTo(Tuple(1, "1"));

        final TreeMap<Integer, String> actual = testee.flatMap(Comparator.reverseOrder(), (k, v) -> List.empty());
        assertThat(actual).isEmpty();

        final TreeMap<Integer, String> actualSorted = actual.put(1, "1").put(2, "2");
        assertThat(actualSorted.head()).isEqualTo(Tuple(2, "2"));
    }

    // -- map

    @Test
    public void shouldReturnModifiedKeysMapWithNonUniqueMapperAndPredictableOrder() {
        final TreeMap<Integer, String> actual = TreeMap
                .of(3, "3", 1, "1", 2, "2")
                .mapKeys(Integer::toHexString).mapKeys(String::length);
        final TreeMap<Integer, String> expected = TreeMap.of(1, "3");
        assertThat(actual).isEqualTo(expected);
    }

    // -- obsolete tests

    @Override
    public void shouldPreserveSingletonInstanceOnDeserialization() {
        // The empty TreeMap encapsulates a comparator and therefore cannot be a singleton
    }

}
