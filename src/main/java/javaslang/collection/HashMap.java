/*     / \____  _    ______   _____ / \____   ____  _____
 *    /  \__  \/ \  / \__  \ /  __//  \__  \ /    \/ __  \   Javaslang
 *  _/  // _\  \  \/  / _\  \\_  \/  // _\  \  /\  \__/  /   Copyright 2014-2015 Daniel Dietrich
 * /___/ \_____/\____/\_____/____/\___\_____/_/  \_/____/    Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.Lazy;
import javaslang.Tuple2;
import javaslang.control.None;
import javaslang.control.Option;
import javaslang.control.Some;

import java.io.Serializable;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.*;

/**
 * An immutable {@code HashMap} implementation based on a
 * <a href="https://en.wikipedia.org/wiki/Hash_array_mapped_trie">Hash array mapped trie (HAMT)</a>.
 *
 * @since 2.0.0
 */
public final class HashMap<K, V> implements Map<K, V>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final HashMap<?, ?> EMPTY = new HashMap<>(HashArrayMappedTrie.empty());

    private final HashArrayMappedTrie<K, V> tree;
    private final transient Lazy<Integer> hash;
    private final transient Lazy<Seq<V>> values;

    private HashMap(HashArrayMappedTrie<K, V> tree) {
        this.tree = tree;
        this.hash = Lazy.of(() -> Traversable.hash(tree::iterator));
        this.values = Lazy.of(() -> iterator().map(entry -> entry.value).toList());
    }

    @SuppressWarnings("unchecked")
    public static <K, V> HashMap<K, V> empty() {
        return (HashMap<K, V>) EMPTY;
    }

    /**
     * Returns a singleton {@code HashMap}, i.e. a {@code HashMap} of one element.
     *
     * @param entry A map entry.
     * @param <K>   The key type
     * @param <V>   The value type
     * @return A new Map containing the given entry
     */
    public static <K, V> HashMap<K, V> of(Entry<? extends K, ? extends V> entry) {
        return HashMap.<K, V> empty().put(entry.key, entry.value);
    }

    /**
     * Creates a Map of the given entries.
     *
     * @param entries Map entries
     * @param <K>     The key type
     * @param <V>     The value type
     * @return A new Map containing the given entries
     */
    @SafeVarargs
    public static <K, V> HashMap<K, V> of(Entry<? extends K, ? extends V>... entries) {
        Objects.requireNonNull(entries, "entries is null");
        HashMap<K, V> map = HashMap.empty();
        for (Entry<? extends K, ? extends V> entry : entries) {
            map = map.put(entry.key, entry.value);
        }
        return map;
    }

    /**
     * Creates a Map of the given entries.
     *
     * @param entries Map entries
     * @param <K>     The key type
     * @param <V>     The value type
     * @return A new Map containing the given entries
     */
    @SuppressWarnings("unchecked")
    public static <K, V> HashMap<K, V> ofAll(java.lang.Iterable<? extends Entry<? extends K, ? extends V>> entries) {
        Objects.requireNonNull(entries, "entries is null");
        if (entries instanceof HashMap) {
            return (HashMap<K, V>) entries;
        } else {
            HashMap<K, V> map = HashMap.empty();
            for (Entry<? extends K, ? extends V> entry : entries) {
                map = map.put(entry.key, entry.value);
            }
            return map;
        }
    }

    @Override
    public HashMap<K, V> clear() {
        return HashMap.empty();
    }

    @Override
    public boolean contains(Entry<K, V> element) {
        return get(element.key).map(v -> Objects.equals(v, element.value)).orElse(false);
    }

    @Override
    public boolean containsKey(K key) {
        return tree.containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return values.get().contains(value);
    }

    @Override
    public HashMap<K, V> distinct() {
        return this;
    }

    @Override
    public HashMap<K, V> distinctBy(Comparator<? super Entry<K, V>> comparator) {
        Objects.requireNonNull(comparator, "comparator is null");
        return HashMap.ofAll(iterator().distinctBy(comparator));
    }

    @Override
    public <U> HashMap<K, V> distinctBy(Function<? super Entry<K, V>, ? extends U> keyExtractor) {
        Objects.requireNonNull(keyExtractor, "keyExtractor is null");
        return HashMap.ofAll(iterator().distinctBy(keyExtractor));
    }

    @Override
    public HashMap<K, V> drop(int n) {
        return HashMap.ofAll(iterator().drop(n));
    }

    @Override
    public HashMap<K, V> dropRight(int n) {
        return HashMap.ofAll(iterator().dropRight(n));
    }

    @Override
    public HashMap<K, V> dropWhile(Predicate<? super Entry<K, V>> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return HashMap.ofAll(iterator().dropWhile(predicate));
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return tree.iterator().map(Entry::of).toSet();
    }

    @Override
    public HashMap<K, V> filter(Predicate<? super Entry<K, V>> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return foldLeft(HashMap.<K, V> empty(), (acc, entry) -> {
            if (predicate.test(entry)) {
                return acc.put(entry);
            } else {
                return acc;
            }
        });
    }

    @Override
    public Option<Entry<K, V>> findLast(Predicate<? super Entry<K, V>> predicate) {
        Objects.requireNonNull(predicate, "predicate is null");
        return iterator().findLast(predicate);
    }

    @Override
    public <U> HashSet<U> flatMap(Function<? super Entry<K, V>, ? extends java.lang.Iterable<? extends U>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return foldLeft(HashSet.<U> empty(), (HashSet<U> acc, Entry<K, V> entry) -> {
            for (U u : mapper.apply(entry)) {
                acc = acc.add(u);
            }
            return acc;
        });
    }

    @Override
    public <U, W> HashMap<U, W> flatMap(BiFunction<? super K, ? super V, ? extends java.lang.Iterable<? extends Entry<? extends U, ? extends W>>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return foldLeft(HashMap.<U, W> empty(), (acc, entry) -> {
            for (Entry<? extends U, ? extends W> mappedEntry : mapper.apply(entry.key, entry.value)) {
                acc = acc.put(mappedEntry);
            }
            return acc;
        });
    }

    @Override
    public HashSet<Object> flatten() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public <U> U foldRight(U zero, BiFunction<? super Entry<K, V>, ? super U, ? extends U> f) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Option<V> get(K key) {
        return tree.get(key);
    }

    @Override
    public <C> Map<C, HashMap<K, V>> groupBy(Function<? super Entry<K, V>, ? extends C> classifier) {
        Objects.requireNonNull(classifier, "classifier is null");
        return foldLeft(HashMap.empty(), (map, entry) -> {
            final C key = classifier.apply(entry);
            final HashMap<K, V> values = map
                    .get(key)
                    .map(entries -> entries.put(entry.key, entry.value))
                    .orElse(HashMap.of(entry));
            return map.put(key, values);
        });
    }

    @Override
    public boolean hasDefiniteSize() {
        return true;
    }

    @Override
    public Entry<K, V> head() {
        if (isEmpty()) {
            throw new NoSuchElementException("head of empty HashMap");
        } else {
            return iterator().next();
        }
    }

    @Override
    public Option<Entry<K, V>> headOption() {
        return isEmpty() ? None.instance() : new Some<>(head());
    }

    @Override
    public HashMap<K, V> init() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Option<HashMap<K, V>> initOption() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean isEmpty() {
        return tree.isEmpty();
    }

    @Override
    public boolean isTraversableAgain() {
        return true;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return tree.iterator().map(Entry::of);
    }

    @Override
    public Set<K> keySet() {
        return map(entry -> entry.key);
    }

    @Override
    public int length() {
        return tree.size();
    }

    @Override
    public <U> Set<U> map(Function<? super Entry<K, V>, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return foldLeft(HashSet.empty(), (acc, entry) -> acc.add(mapper.apply(entry)));
    }

    @Override
    public <U, W> HashMap<U, W> map(BiFunction<? super K, ? super V, ? extends Entry<? extends U, ? extends W>> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return foldLeft(HashMap.empty(), (acc, entry) -> {
            final Entry<? extends U, ? extends W> e = mapper.apply(entry.key, entry.value);
            return acc.put(e.key, e.value);
        });
    }

    @Override
    public HashMap<K, V> merge(Map<K, ? extends V> that) {
        return merge(that, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U extends V> HashMap<K, V> merge(Map<K, U> that, BiFunction<? super V, ? super U, ? extends V> mergef) {
        if (isEmpty()) {
            return (HashMap<K, V>) that;
        }
        if (that.isEmpty()) {
            return this;
        }
        HashMap<K, V> result = this;
        for (Entry<K, U> e : that) {
            Option<V> old = result.get(e.key);
            if (old.isDefined() && mergef != null) {
                result = result.put(e.key, mergef.apply(old.get(), e.value));
            } else {
                result = result.put(e.key, e.value);
            }
        }
        return result;
    }

    @Override
    public Tuple2<HashMap<K, V>, HashMap<K, V>> partition(Predicate<? super Entry<K, V>> predicate) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<K, V> peek(Consumer<? super Entry<K, V>> action) {
        if (!isEmpty()) {
            action.accept(iterator().next());
        }
        return this;
    }

    @Override
    public HashMap<K, V> put(K key, V value) {
        return new HashMap<>(tree.put(key, value));
    }

    @Override
    public HashMap<K, V> put(Entry<? extends K, ? extends V> entry) {
        return put(entry.key, entry.value);
    }

    @Override
    public HashMap<K, V> put(Tuple2<? extends K, ? extends V> entry) {
        return put(entry._1, entry._2);
    }

    @Override
    public Entry<K, V> reduceRight(BiFunction<? super Entry<K, V>, ? super Entry<K, V>, ? extends Entry<K, V>> op) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<K, V> remove(K key) {
        return new HashMap<>(tree.remove(key));
    }

    @Override
    public HashMap<K, V> removeAll(java.lang.Iterable<? extends K> keys) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<K, V> replace(Entry<K, V> currentElement, Entry<K, V> newElement) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<K, V> replaceAll(Entry<K, V> currentElement, Entry<K, V> newElement) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<K, V> replaceAll(UnaryOperator<Entry<K, V>> operator) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<K, V> retainAll(java.lang.Iterable<? extends Entry<K, V>> elements) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public int size() {
        return tree.size();
    }

    @Override
    public Tuple2<HashMap<K, V>, HashMap<K, V>> span(Predicate<? super Entry<K, V>> predicate) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<K, V> tail() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Option<HashMap<K, V>> tailOption() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<K, V> take(int n) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<K, V> takeRight(int n) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<K, V> takeWhile(Predicate<? super Entry<K, V>> predicate) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public <K1, V1, K2, V2> Tuple2<HashMap<K1, V1>, HashMap<K2, V2>> unzip(Function<? super Entry<? super K, ? super V>, Tuple2<? extends Entry<? extends K1, ? extends V1>, ? extends Entry<? extends K2, ? extends V2>>> unzipper) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public <K1, V1, K2, V2> Tuple2<HashMap<K1, V1>, HashMap<K2, V2>> unzip(BiFunction<? super K, ? super V, Tuple2<? extends Entry<? extends K1, ? extends V1>, ? extends Entry<? extends K2, ? extends V2>>> unzipper) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Seq<V> values() {
        return values.get();
    }

    @Override
    public <U> HashMap<Tuple2<K, V>, U> zip(java.lang.Iterable<U> that) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public <U> HashMap<Tuple2<K, V>, U> zipAll(java.lang.Iterable<U> that, Entry<K, V> thisElem, U thatElem) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public HashMap<Tuple2<K, V>, Integer> zipWithIndex() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public int hashCode() {
        return hash.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof HashMap) {
            final HashMap<?,?> that = (HashMap<?,?>) o;
            return this.corresponds(that, Objects::equals);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return mkString(", ", "HashMap(", ")");
    }
}
