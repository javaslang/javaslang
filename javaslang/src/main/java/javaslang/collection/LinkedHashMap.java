/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2016 Javaslang contributors
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.control.Option;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * An immutable {@code LinkedHashMap} implementation.
 *
 * @author Ruslan Sennov
 * @since 2.0.0
 */
public final class LinkedHashMap<K, V> extends AbstractMap<K, V, LinkedHashMap<K, V>> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final LinkedHashMap<?, ?> EMPTY = new LinkedHashMap<>(Queue.empty(), HashMap.empty());

    private final Queue<Tuple2<K, V>> list;
    private final HashMap<K, V> map;

    private LinkedHashMap(Queue<Tuple2<K, V>> list, HashMap<K, V> map) {
        this.list = list;
        this.map = map;
    }

    /**
     * Returns a {@link java.util.stream.Collector} which may be used in conjunction with
     * {@link java.util.stream.Stream#collect(java.util.stream.Collector)} to obtain a {@link javaslang.collection.LinkedHashMap}.
     *
     * @param <K> The key type
     * @param <V> The value type
     * @return A {@link javaslang.collection.LinkedHashMap} Collector.
     */
    public static <K, V> Collector<Tuple2<K, V>, ArrayList<Tuple2<K, V>>, LinkedHashMap<K, V>> collector() {
        final Supplier<ArrayList<Tuple2<K, V>>> supplier = ArrayList::new;
        final BiConsumer<ArrayList<Tuple2<K, V>>, Tuple2<K, V>> accumulator = ArrayList::add;
        final BinaryOperator<ArrayList<Tuple2<K, V>>> combiner = (left, right) -> {
            left.addAll(right);
            return left;
        };
        final Function<ArrayList<Tuple2<K, V>>, LinkedHashMap<K, V>> finisher = LinkedHashMap::ofEntries;
        return Collector.of(supplier, accumulator, combiner, finisher);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> empty() {
        return (LinkedHashMap<K, V>) EMPTY;
    }

    /**
     * Narrows a widened {@code LinkedHashMap<? extends K, ? extends V>} to {@code LinkedHashMap<K, V>}
     * by performing a type safe-cast. This is eligible because immutable/read-only
     * collections are covariant.
     *
     * @param linkedHashMap A {@code LinkedHashMap}.
     * @param <K>           Key type
     * @param <V>           Value type
     * @return the given {@code linkedHashMap} instance as narrowed type {@code LinkedHashMap<K, V>}.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> narrow(LinkedHashMap<? extends K, ? extends V> linkedHashMap) {
        return (LinkedHashMap<K, V>) linkedHashMap;
    }

    /**
     * Returns a singleton {@code LinkedHashMap}, i.e. a {@code LinkedHashMap} of one element.
     *
     * @param entry A map entry.
     * @param <K>   The key type
     * @param <V>   The value type
     * @return A new Map containing the given entry
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> of(Tuple2<? extends K, ? extends V> entry) {
        final HashMap<K, V> map = HashMap.of(entry);
        final Queue<Tuple2<K, V>> list = Queue.of((Tuple2<K, V>) entry);
        return new LinkedHashMap<>(list, map);
    }

    /**
     * Creates a LinkedHashMap of the given list of key-value pairs.
     *
     * @param pairs A list of key-value pairs
     * @param <K>   The key type
     * @param <V>   The value type
     * @return A new Map containing the given entries
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> of(Object... pairs) {
        Objects.requireNonNull(pairs, "pairs is null");
        if ((pairs.length & 1) != 0) {
            throw new IllegalArgumentException("Odd length of key-value pairs list");
        }
        HashMap<K, V> map = HashMap.empty();
        Queue<Tuple2<K, V>> list = Queue.empty();
        for (int i = 0; i < pairs.length; i += 2) {
            final K k = (K) pairs[i];
            final V v = (V) pairs[i + 1];
            map = map.put(k, v);
            list = list.append(Tuple.of(k, v));
        }
        return wrap(list, map);
    }

    /**
     * Returns a {@code LinkedHashMap}, from a source java.util.Map.
     *
     * @param map A map entry.
     * @param <K> The key type
     * @param <V> The value type
     * @return A new Map containing the given map
     */
    public static <K, V> LinkedHashMap<K, V> ofAll(java.util.Map<? extends K, ? extends V> map) {
        Objects.requireNonNull(map, "map is null");
        LinkedHashMap<K, V> result = LinkedHashMap.empty();
        for (java.util.Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            result = result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Returns a singleton {@code LinkedHashMap}, i.e. a {@code LinkedHashMap} of one element.
     *
     * @param key   A singleton map key.
     * @param value A singleton map value.
     * @param <K>   The key type
     * @param <V>   The value type
     * @return A new Map containing the given entry
     */
    public static <K, V> LinkedHashMap<K, V> of(K key, V value) {
        final HashMap<K, V> map = HashMap.of(key, value);
        final Queue<Tuple2<K, V>> list = Queue.of(Tuple.of(key, value));
        return new LinkedHashMap<>(list, map);
    }

    /**
     * Returns a LinkedHashMap containing {@code n} values of a given Function {@code f}
     * over a range of integer values from 0 to {@code n - 1}.
     *
     * @param <K> The key type
     * @param <V> The value type
     * @param n   The number of elements in the LinkedHashMap
     * @param f   The Function computing element values
     * @return A LinkedHashMap consisting of elements {@code f(0),f(1), ..., f(n - 1)}
     * @throws NullPointerException if {@code f} is null
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> tabulate(int n, Function<? super Integer, ? extends Tuple2<? extends K, ? extends V>> f) {
        Objects.requireNonNull(f, "f is null");
        return ofEntries(Collections.tabulate(n, (Function<? super Integer, ? extends Tuple2<K, V>>) f));
    }

    /**
     * Returns a LinkedHashMap containing {@code n} values supplied by a given Supplier {@code s}.
     *
     * @param <K> The key type
     * @param <V> The value type
     * @param n   The number of elements in the LinkedHashMap
     * @param s   The Supplier computing element values
     * @return A LinkedHashMap of size {@code n}, where each element contains the result supplied by {@code s}.
     * @throws NullPointerException if {@code s} is null
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> fill(int n, Supplier<? extends Tuple2<? extends K, ? extends V>> s) {
        Objects.requireNonNull(s, "s is null");
        return ofEntries(Collections.fill(n, (Supplier<? extends Tuple2<K, V>>) s));
    }

    /**
     * Creates a LinkedHashMap of the given entries.
     *
     * @param entries Map entries
     * @param <K>     The key type
     * @param <V>     The value type
     * @return A new Map containing the given entries
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> ofEntries(java.util.Map.Entry<? extends K, ? extends V>... entries) {
        HashMap<K, V> map = HashMap.empty();
        Queue<Tuple2<K, V>> list = Queue.empty();
        for (java.util.Map.Entry<? extends K, ? extends V> entry : entries) {
            final Tuple2<K, V> tuple = Tuple.of(entry.getKey(), entry.getValue());
            map = map.put(tuple);
            list = list.append(tuple);
        }
        return wrap(list, map);
    }

    /**
     * Creates a LinkedHashMap of the given entries.
     *
     * @param entries Map entries
     * @param <K>     The key type
     * @param <V>     The value type
     * @return A new Map containing the given entries
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> ofEntries(Tuple2<? extends K, ? extends V>... entries) {
        final HashMap<K, V> map = HashMap.ofEntries(entries);
        final Queue<Tuple2<K, V>> list = Queue.of((Tuple2<K, V>[]) entries);
        return wrap(list, map);
    }

    /**
     * Creates a LinkedHashMap of the given entries.
     *
     * @param entries Map entries
     * @param <K>     The key type
     * @param <V>     The value type
     * @return A new Map containing the given entries
     */
    @SuppressWarnings("unchecked")
    public static <K, V> LinkedHashMap<K, V> ofEntries(Iterable<? extends Tuple2<? extends K, ? extends V>> entries) {
        Objects.requireNonNull(entries, "entries is null");
        if (entries instanceof LinkedHashMap) {
            return (LinkedHashMap<K, V>) entries;
        } else {
            HashMap<K, V> map = HashMap.empty();
            Queue<Tuple2<K, V>> list = Queue.empty();
            for (Tuple2<? extends K, ? extends V> entry : entries) {
                map = map.put(entry);
                list = list.append((Tuple2<K, V>) entry);
            }
            return wrap(list, map);
        }
    }

    @Override
    LinkedHashMap<K, V> createFromEntries(Iterable<? extends Tuple2<? extends K, ? extends V>> entries) {
        return LinkedHashMap.ofEntries(entries);
    }

    @Override
    LinkedHashMap<K, V> emptyInstance() {
        return LinkedHashMap.empty();
    }

    @Override
    public <K2, V2> LinkedHashMap<K2, V2> bimap(Function<? super K, ? extends K2> keyMapper, Function<? super V, ? extends V2> valueMapper) {
        Objects.requireNonNull(keyMapper, "keyMapper is null");
        Objects.requireNonNull(valueMapper, "valueMapper is null");
        final Iterator<Tuple2<K2, V2>> entries = iterator().map(entry -> Tuple.of(keyMapper.apply(entry._1), valueMapper.apply(entry._2)));
        return LinkedHashMap.ofEntries(entries);
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public <K2, V2> LinkedHashMap<K2, V2> flatMap(BiFunction<? super K, ? super V, ? extends Iterable<Tuple2<K2, V2>>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return foldLeft(LinkedHashMap.<K2, V2> empty(), (acc, entry) -> {
            for (Tuple2<? extends K2, ? extends V2> mappedEntry : mapper.apply(entry._1, entry._2)) {
                acc = acc.put(mappedEntry);
            }
            return acc;
        });
    }

    @Override
    public Option<V> get(K key) {
        return map.get(key);
    }

    @Override
    public Tuple2<K, V> head() {
        return list.head();
    }

    @Override
    public LinkedHashMap<K, V> init() {
        if (isEmpty()) {
            throw new UnsupportedOperationException("init of empty LinkedHashMap");
        } else {
            return LinkedHashMap.ofEntries(list.init());
        }
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Iterator<Tuple2<K, V>> iterator() {
        return list.iterator();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public <K2, V2> LinkedHashMap<K2, V2> map(BiFunction<? super K, ? super V, Tuple2<K2, V2>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return foldLeft(LinkedHashMap.empty(), (acc, entry) -> acc.put(entry.map(mapper)));
    }

    @Override
    public <W> LinkedHashMap<K, W> mapValues(Function<? super V, ? extends W> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return map((k, v) -> Tuple.of(k, mapper.apply(v)));
    }

    @Override
    public LinkedHashMap<K, V> put(K key, V value) {
        Queue<Tuple2<K, V>> newList = list;
        HashMap<K, V> newMap = map;
        if (containsKey(key)) {
            newList = newList.filter(t -> !t._1.equals(key));
            newMap = newMap.remove(key);
        }
        newList = newList.append(Tuple.of(key, value));
        newMap = newMap.put(key, value);
        return new LinkedHashMap<>(newList, newMap);
    }

    @Override
    public LinkedHashMap<K, V> remove(K key) {
        if (containsKey(key)) {
            final Queue<Tuple2<K, V>> newList = list.removeFirst(t -> t._1.equals(key));
            final HashMap<K, V> newMap = map.remove(key);
            return wrap(newList, newMap);
        } else {
            return this;
        }
    }

    @Override
    public LinkedHashMap<K, V> retainAll(Iterable<? extends Tuple2<K, V>> elements) {
        Objects.requireNonNull(elements, "elements is null");
        LinkedHashMap<K, V> result = empty();
        for (Tuple2<K, V> entry : elements) {
            if (contains(entry)) {
                result = result.put(entry._1, entry._2);
            }
        }
        return result;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public LinkedHashMap<K, V> tail() {
        if (isEmpty()) {
            throw new UnsupportedOperationException("tail of empty LinkedHashMap");
        } else {
            return LinkedHashMap.ofEntries(list.tail());
        }
    }

    @Override
    public java.util.LinkedHashMap<K, V> toJavaMap() {
        return toJavaMap(java.util.LinkedHashMap::new, t -> t);
    }

    @Override
    public Seq<V> values() {
        return map.values();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof LinkedHashMap) {
            final LinkedHashMap<?, ?> that = (LinkedHashMap<?, ?>) o;
            return this.list.equals(that.list);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    private Object readResolve() {
        return isEmpty() ? EMPTY : this;
    }

    @Override
    public String stringPrefix() {
        return "LinkedHashMap";
    }

    @Override
    public String toString() {
        return mkString(stringPrefix() + "(", ", ", ")");
    }

    private static <K, V> LinkedHashMap<K, V> wrap(Queue<Tuple2<K, V>> list, HashMap<K, V> map) {
        return list.isEmpty() ? empty() : new LinkedHashMap<>(list, map);
    }
}
