/*     / \____  _    _  ____   ______  / \ ____  __    _ _____
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  / /  _  \   Javaslang
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/  \__/  /   Copyright 2014-now Daniel Dietrich
 * /___/\_/  \_/\____/\_/  \_/\__\/__/___\_/  \_//  \__/_____/    Licensed under the Apache License, Version 2.0
 */
package javaslang;

/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*\
   G E N E R A T O R   C R A F T E D
\*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import javaslang.collection.List;
import javaslang.collection.Seq;

/**
 * A tuple of two elements which can be seen as cartesian product of two components.
 *
 * @param <T1> type of the 1st element
 * @param <T2> type of the 2nd element
 * @author Daniel Dietrich
 * @since 1.1.0
 */
public final class Tuple2<T1, T2> implements Tuple, Comparable<Tuple2<T1, T2>>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The 1st element of this tuple.
     */
    public final T1 _1;

    /**
     * The 2nd element of this tuple.
     */
    public final T2 _2;

    /**
     * Constructs a tuple of two elements.
     *
     * @param t1 the 1st element
     * @param t2 the 2nd element
     */
    public Tuple2(T1 t1, T2 t2) {
        this._1 = t1;
        this._2 = t2;
    }

    public static <T1, T2> Comparator<Tuple2<T1, T2>> comparator(Comparator<? super T1> t1Comp, Comparator<? super T2> t2Comp) {
        return (Comparator<Tuple2<T1, T2>> & Serializable) (t1, t2) -> {
            final int check1 = t1Comp.compare(t1._1, t2._1);
            if (check1 != 0) {
                return check1;
            }

            final int check2 = t2Comp.compare(t1._2, t2._2);
            if (check2 != 0) {
                return check2;
            }

            // all components are equal
            return 0;
        };
    }

    @SuppressWarnings("unchecked")
    private static <U1 extends Comparable<? super U1>, U2 extends Comparable<? super U2>> int compareTo(Tuple2<?, ?> o1, Tuple2<?, ?> o2) {
        final Tuple2<U1, U2> t1 = (Tuple2<U1, U2>) o1;
        final Tuple2<U1, U2> t2 = (Tuple2<U1, U2>) o2;

        final int check1 = t1._1.compareTo(t2._1);
        if (check1 != 0) {
            return check1;
        }

        final int check2 = t1._2.compareTo(t2._2);
        if (check2 != 0) {
            return check2;
        }

        // all components are equal
        return 0;
    }

    @Override
    public int arity() {
        return 2;
    }

    @Override
    public int compareTo(Tuple2<T1, T2> that) {
        return Tuple2.compareTo(this, that);
    }

    /**
     * Getter of the 1st element of this tuple.
     *
     * @return the 1st element of this Tuple.
     */
    public T1 _1() {
        return _1;
    }

    /**
     * Getter of the 2nd element of this tuple.
     *
     * @return the 2nd element of this Tuple.
     */
    public T2 _2() {
        return _2;
    }

    @SuppressWarnings("unchecked")
    public <U1, U2> Tuple2<U1, U2> flatMap(BiFunction<? super T1, ? super T2, ? extends Tuple2<? extends U1, ? extends U2>> f) {
        return (Tuple2<U1, U2>) f.apply(_1, _2);
    }

    public <U1, U2> Tuple2<U1, U2> map(Function<? super T1, ? extends U1> f1, Function<? super T2, ? extends U2> f2) {
        return Tuple.of(f1.apply(_1), f2.apply(_2));
    }

    /**
     * Transforms this tuple to an arbitrary object (which may be also a tuple of same or different arity).
     *
     * @param f Transformation which creates a new object of type U based on this tuple's contents.
     * @param <U> New type
     * @return An object of type U
     * @throws NullPointerException if {@code f} is null
     */
    public <U> U transform(BiFunction<? super T1, ? super T2, ? extends U> f) {
        Objects.requireNonNull(f, "f is null");
        return f.apply(_1, _2);
    }

    /**
     * Transforms this tuple to an arbitrary object (which may be also a tuple of same or different arity).
     *
     * @param f Transformation which takes elements of this tuple and return an object of type U
     * @param <U> New type
     * @return An object of type U
     */
    public <U> U transform(BiFunction<? super T1, ? super T2, U> f) {
        Objects.requireNonNull(f, "f is null");
        return f.apply(_1, _2);
    }

    @Override
    public Seq<?> toSeq() {
        return List.ofAll(_1, _2);
    }

    // -- Object

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Tuple2)) {
            return false;
        } else {
            final Tuple2<?, ?> that = (Tuple2<?, ?>) o;
            return Objects.equals(this._1, that._1)
                  && Objects.equals(this._2, that._2);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", _1, _2);
    }

}