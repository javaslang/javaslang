/*     / \____  _    ______   _____ / \____   ____  _____
 *    /  \__  \/ \  / \__  \ /  __//  \__  \ /    \/ __  \   Javaslang
 *  _/  // _\  \  \/  / _\  \\_  \/  // _\  \  /\  \__/  /   Copyright 2014-2015 Daniel Dietrich
 * /___/ \_____/\____/\_____/____/\___\_____/_/  \_/____/    Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.Lazy;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.HashArrayMappedTrieModule.*;
import javaslang.control.None;
import javaslang.control.Option;
import javaslang.control.Some;

import java.io.Serializable;
import java.util.Objects;

/**
 * An immutable <a href="https://en.wikipedia.org/wiki/Hash_array_mapped_trie">Hash array mapped trie (HAMT)</a>.
 *
 * @author Ruslan Sennov
 * @since 2.0.0
 */
public interface HashArrayMappedTrie<K, V> extends java.lang.Iterable<Tuple2<K, V>> {

    static <K, V> HashArrayMappedTrie<K, V> empty() {
        return EmptyNode.instance();
    }

    boolean isEmpty();

    int size();

    Option<V> get(K key);

    boolean containsKey(K key);

    HashArrayMappedTrie<K, V> put(K key, V value);

    HashArrayMappedTrie<K, V> remove(K key);

    // this is a javaslang.collection.Iterator!
    @Override
    Iterator<Tuple2<K, V>> iterator();

}

interface HashArrayMappedTrieModule {

    /**
     * TODO: javadoc
     *
     * @param <K> Key type
     * @param <V> Value type
     */
    abstract class AbstractNode<K, V> implements HashArrayMappedTrie<K, V> {

        static final int SIZE = 5;
        static final int BUCKET_SIZE = 1 << SIZE;
        static final int MAX_INDEX_NODE = BUCKET_SIZE / 2;
        static final int MIN_ARRAY_NODE = BUCKET_SIZE / 4;

        private static final int M1 = 0x55555555;
        private static final int M2 = 0x33333333;
        private static final int M4 = 0x0f0f0f0f;

        private final transient Lazy<Integer> hashCode = Lazy.of(() -> Traversable.hash(this));

        int bitCount(int x) {
            x = x - ((x >> 1) & M1);
            x = (x & M2) + ((x >> 2) & M2);
            x = (x + (x >> 4)) & M4;
            x = x + (x >> 8);
            x = x + (x >> 16);
            return x & 0x7f;
        }

        int hashFragment(int shift, int hash) {
            return (hash >>> shift) & (BUCKET_SIZE - 1);
        }

        int toBitmap(int hash) {
            return 1 << hash;
        }

        int fromBitmap(int bitmap, int bit) {
            return bitCount(bitmap & (bit - 1));
        }

        abstract boolean isLeaf();

        abstract Option<V> lookup(int shift, K key);

        abstract AbstractNode<K, V> modify(int shift, K key, Option<V> value);

        @Override
        public Option<V> get(K key) {
            return lookup(0, key);
        }

        @Override
        public boolean containsKey(K key) {
            return get(key).isDefined();
        }

        @Override
        public HashArrayMappedTrie<K, V> put(K key, V value) {
            return modify(0, key, new Some<>(value));
        }

        @Override
        public HashArrayMappedTrie<K, V> remove(K key) {
            return modify(0, key, None.instance());
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof HashArrayMappedTrie) {
                final Iterator<?> iter1 = this.iterator();
                final Iterator<?> iter2 = ((HashArrayMappedTrie<?, ?>) o).iterator();
                while (iter1.hasNext() && iter2.hasNext()) {
                    if (!Objects.equals(iter1.next(), iter2.next())) {
                        return false;
                    }
                }
                return !iter1.hasNext() && !iter2.hasNext();
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return hashCode.get();
        }

        @Override
        public String toString() {
            return iterator().map(t -> t._1 + " -> " + t._2).mkString(", ", "HashArrayMappedTrie(", ")");
        }
    }

    /**
     * TODO: javadoc
     *
     * @param <K> Key type
     * @param <V> Value type
     */
    final class EmptyNode<K, V> extends AbstractNode<K, V> implements Serializable {

        private static final long serialVersionUID = 1L;

        private static final EmptyNode<?, ?> INSTANCE = new EmptyNode<>();

        private EmptyNode() {
        }

        @SuppressWarnings("unchecked")
        static <K, V> EmptyNode<K, V> instance() {
            return (EmptyNode<K, V>) INSTANCE;
        }

        @Override
        Option<V> lookup(int shift, K key) {
            return None.instance();
        }

        @Override
        AbstractNode<K, V> modify(int shift, K key, Option<V> value) {
            return value.isEmpty() ? this : new LeafNode<>(Objects.hashCode(key), key, value.get());
        }

        @Override
        boolean isLeaf() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return Iterator.empty();
        }

        /**
         * Instance control for object serialization.
         *
         * @return The singleton instance of EmptyNode.
         * @see java.io.Serializable
         */
        private Object readResolve() {
            return INSTANCE;
        }
    }

    /**
     * TODO: javadoc
     *
     * @param <K> Key type
     * @param <V> Value type
     */
    final class LeafNode<K, V> extends AbstractNode<K, V> implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int hash;
        private final int size;
        private final List<Tuple2<K, V>> entries;

        LeafNode(int hash, K key, V value) {
            this(hash, 1, List.of(Tuple.of(key, value)));
        }

        private LeafNode(int hash, int size, List<Tuple2<K, V>> entries) {
            this.hash = hash;
            this.size = size;
            this.entries = entries;
        }

        private AbstractNode<K, V> update(K key, Option<V> value) {
            List<Tuple2<K, V>> filtered = entries.removeFirst(t -> t._1.equals(key));
            if (value.isEmpty()) {
                return filtered.isEmpty() ? EmptyNode.instance() : new LeafNode<>(hash, filtered.length(), filtered);
            } else {
                return new LeafNode<>(hash, filtered.length() + 1, filtered.prepend(Tuple.of(key, value.get())));
            }
        }

        @Override
        Option<V> lookup(int shift, K key) {
            if (hash != key.hashCode()) {
                return None.instance();
            }
            return entries.findFirst(t -> t._1.equals(key)).map(t -> t._2);
        }

        @Override
        AbstractNode<K, V> modify(int shift, K key, Option<V> value) {
            if (key.hashCode() == hash) {
                return update(key, value);
            } else {
                return value.isEmpty() ? this : mergeLeaves(shift, new LeafNode<>(key.hashCode(), key, value.get()));
            }
        }

        private AbstractNode<K, V> mergeLeaves(int shift, LeafNode<K, V> other) {
            final int h1 = this.hash;
            final int h2 = other.hash;
            if (h1 == h2) {
                return new LeafNode<>(h1, size + other.size, other.entries.foldLeft(entries, List::prepend));
            }
            final int subH1 = hashFragment(shift, h1);
            final int subH2 = hashFragment(shift, h2);
            final int newBitmap = toBitmap(subH1) | toBitmap(subH2);
            if (subH1 == subH2) {
                AbstractNode<K, V> newLeaves = mergeLeaves(shift + SIZE, other);
                return new IndexedNode<>(newBitmap, newLeaves.size(), List.of(newLeaves));
            } else {
                return new IndexedNode<>(newBitmap, size + other.size, subH1 < subH2 ? List.of(this, other) : List.of(other, this));
            }
        }

        @Override
        boolean isLeaf() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return entries.iterator();
        }
    }

    /**
     * TODO: javadoc
     *
     * @param <K> Key type
     * @param <V> Value type
     */
    final class IndexedNode<K, V> extends AbstractNode<K, V> implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int bitmap;
        private final int size;
        private final List<AbstractNode<K, V>> subNodes;

        IndexedNode(int bitmap, int size, List<AbstractNode<K, V>> subNodes) {
            this.bitmap = bitmap;
            this.size = size;
            this.subNodes = subNodes;
        }

        @Override
        Option<V> lookup(int shift, K key) {
            int h = key.hashCode();
            int frag = hashFragment(shift, h);
            int bit = toBitmap(frag);
            return ((bitmap & bit) != 0) ? subNodes.get(fromBitmap(bitmap, bit)).lookup(shift + SIZE, key) : None.instance();
        }

        @Override
        AbstractNode<K, V> modify(int shift, K key, Option<V> value) {
            final int frag = hashFragment(shift, key.hashCode());
            final int bit = toBitmap(frag);
            final int index = fromBitmap(bitmap, bit);
            final int mask = bitmap;
            final boolean exists = (mask & bit) != 0;
            final AbstractNode<K, V> atIndx = exists ? subNodes.get(index) : null;
            AbstractNode<K, V> child = exists ? atIndx.modify(shift + SIZE, key, value)
                    : EmptyNode.<K, V> instance().modify(shift + SIZE, key, value);
            boolean removed = exists && child.isEmpty();
            boolean added = !exists && !child.isEmpty();
            int newBitmap = removed ? mask & ~bit : added ? mask | bit : mask;
            if (newBitmap == 0) {
                return EmptyNode.instance();
            } else if (removed) {
                if (subNodes.length() <= 2 && subNodes.get(index ^ 1).isLeaf()) {
                    return subNodes.get(index ^ 1); // collapse
                } else {
                    return new IndexedNode<>(newBitmap, size - atIndx.size(), subNodes.removeAt(index));
                }
            } else if (added) {
                if (subNodes.length() >= MAX_INDEX_NODE) {
                    return expand(frag, child, mask, subNodes);
                } else {
                    return new IndexedNode<>(newBitmap, size + child.size(), subNodes.insert(index, child));
                }
            } else {
                if (!exists) {
                    return this;
                } else {
                    return new IndexedNode<>(newBitmap, size - atIndx.size() + child.size(), subNodes.update(index, child));
                }
            }
        }

        private ArrayNode<K, V> expand(int frag, AbstractNode<K, V> child, int mask, List<AbstractNode<K, V>> subNodes) {
            int bit = mask;
            int count = 0;
            List<AbstractNode<K, V>> sub = subNodes;
            final Object[] arr = new Object[BUCKET_SIZE];
            for (int i = 0; i < BUCKET_SIZE; i++) {
                if ((bit & 1) != 0) {
                    arr[i] = sub.head();
                    sub = sub.tail();
                    count++;
                } else if (i == frag) {
                    arr[i] = child;
                    count++;
                } else {
                    arr[i] = EmptyNode.instance();
                }
                bit = bit >>> 1;
            }
            return new ArrayNode<>(count, size + child.size(), Array.wrap(arr));
        }

        @Override
        boolean isLeaf() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return Iterator.concat(subNodes);
        }
    }

    /**
     * TODO: javadoc
     *
     * @param <K> Key type
     * @param <V> Value type
     */
    final class ArrayNode<K, V> extends AbstractNode<K, V> implements Serializable {

        private static final long serialVersionUID = 1L;

        private final Array<AbstractNode<K, V>> subNodes;
        private final int count;
        private final int size;

        ArrayNode(int count, int size, Array<AbstractNode<K, V>> subNodes) {
            this.subNodes = subNodes;
            this.count = count;
            this.size = size;
        }

        @Override
        Option<V> lookup(int shift, K key) {
            int frag = hashFragment(shift, key.hashCode());
            AbstractNode<K, V> child = subNodes.get(frag);
            return child.lookup(shift + SIZE, key);
        }

        @Override
        AbstractNode<K, V> modify(int shift, K key, Option<V> value) {
            int frag = hashFragment(shift, key.hashCode());
            AbstractNode<K, V> child = subNodes.get(frag);
            AbstractNode<K, V> newChild = child.modify(shift + SIZE, key, value);
            if (child.isEmpty() && !newChild.isEmpty()) {
                return new ArrayNode<>(count + 1, size + newChild.size(), subNodes.update(frag, newChild));
            } else if (!child.isEmpty() && newChild.isEmpty()) {
                if (count - 1 <= MIN_ARRAY_NODE) {
                    return pack(frag, subNodes);
                } else {
                    return new ArrayNode<>(count - 1, size - child.size(), subNodes.update(frag, EmptyNode.instance()));
                }
            } else {
                return new ArrayNode<>(count, size - child.size() + newChild.size(), subNodes.update(frag, newChild));
            }
        }

        private IndexedNode<K, V> pack(int idx, Array<AbstractNode<K, V>> elements) {
            List<AbstractNode<K, V>> arr = List.empty();
            int bitmap = 0;
            int size = 0;
            for (int i = BUCKET_SIZE - 1; i >= 0; i--) {
                AbstractNode<K, V> elem = elements.get(i);
                if (i != idx && !elem.isEmpty()) {
                    size += elem.size();
                    arr = arr.prepend(elem);
                    bitmap = bitmap | (1 << i);
                }
            }
            return new IndexedNode<>(bitmap, size, arr);
        }

        @Override
        boolean isLeaf() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return Iterator.concat(subNodes);
        }
    }
}