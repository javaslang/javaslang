/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.Tuple2;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class HashMapTest extends AbstractMapTest {

    @Override
    protected String className() {
        return "HashMap";
    }

    @Override
    <T1, T2> java.util.Map<T1, T2> javaEmptyMap() {
        return new java.util.HashMap<>();
    }

    @Override
    protected <T1 extends Comparable<? super T1>, T2> Map<T1, T2> emptyMap() {
        return HashMap.empty();
    }

    @Override
    protected <T> Collector<Tuple2<Integer, T>, ArrayList<Tuple2<Integer, T>>, ? extends Map<Integer, T>> mapCollector() {
        return HashMap.<Integer, T> collector();
    }

    @Override
    protected final <K extends Comparable<? super K>, V> HashMap<K, V> mapOfTuples(Tuple2<? extends K, ? extends V> t1, Tuple2<? extends K, ? extends V> t2, Tuple2<? extends K, ? extends V> t3) {
        return HashMap.ofEntries(t1, t2, t3);
    }

    @Override
    protected final <K extends Comparable<? super K>, V> HashMap<K, V> mapOfEntries(java.util.Map.Entry<? extends K, ? extends V> e2, java.util.Map.Entry<? extends K, ? extends V> e1, java.util.Map.Entry<? extends K, ? extends V> e3) {
        return HashMap.ofEntries(e1, e2, e3);
    }

    @Override
    protected <K extends Comparable<? super K>, V> HashMap<K, V> mapOf(K key, V value) {
        return HashMap.of(key, value);
    }

    @Override
    protected <K extends Comparable<? super K>, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        return HashMap.of(k1, v1, k2, v2);
    }

    @Override
    protected <K extends Comparable<? super K>, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
        return HashMap.of(k1, v1, k2, v2, k3, v3);
    }

    @Override
    protected <K extends Comparable<? super K>, V> HashMap<K, V> mapTabulate(int n, Function<? super Integer, ? extends Tuple2<? extends K, ? extends V>> f) {
        return HashMap.tabulate(n, f);
    }

    @Override
    protected <K extends Comparable<? super K>, V> HashMap<K, V> mapFill(int n, Supplier<? extends Tuple2<? extends K, ? extends V>> s) {
        return HashMap.fill(n, s);
    }

    // -- static narrow

    @Test
    public void shouldNarrowHashMap() {
        final HashMap<Integer, Double> int2doubleMap = mapOf(1, 1.0d);
        final HashMap<Number, Number> number2numberMap = HashMap.narrow(int2doubleMap);
        final int actual = number2numberMap.put(new BigDecimal("2"), new BigDecimal("2.0")).values().sum().intValue();
        assertThat(actual).isEqualTo(3);
    }

    @Test
    public void shouldWrapMap() {
        java.util.Map<Integer, Integer> source = new java.util.HashMap<>();
        source.put(1, 2);
        source.put(3, 4);
        assertThat(HashMap.ofAll(source)).isEqualTo(emptyIntInt().put(1, 2).put(3, 4));
    }
}
