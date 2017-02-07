/*     / \____  _    _  ____   ______  / \ ____  __    _______
 *    /  /    \/ \  / \/    \ /  /\__\/  //    \/  \  //  /\__\   JΛVΛSLΛNG
 *  _/  /  /\  \  \/  /  /\  \\__\\  \  //  /\  \ /\\/ \ /__\ \   Copyright 2014-2017 Javaslang, http://javaslang.io
 * /___/\_/  \_/\____/\_/  \_/\__\/__/\__\_/  \_//  \__/\_____/   Licensed under the Apache License, Version 2.0
 */
package javaslang;

/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*\
   G E N E R A T O R   C R A F T E D
\*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.Objects;
import javaslang.collection.List;
import javaslang.collection.Seq;
import javaslang.collection.Stream;
import org.junit.Test;

public class Tuple10Test {

    @Test
    public void shouldCreateTuple() {
        final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> tuple = createTuple();
        assertThat(tuple).isNotNull();
    }

    @Test
    public void shouldGetArity() {
        final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> tuple = createTuple();
        assertThat(tuple.arity()).isEqualTo(10);
    }

    @Test
    public void shouldReturnElements() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        assertThat(tuple._1).isEqualTo(1);
        assertThat(tuple._2).isEqualTo(2);
        assertThat(tuple._3).isEqualTo(3);
        assertThat(tuple._4).isEqualTo(4);
        assertThat(tuple._5).isEqualTo(5);
        assertThat(tuple._6).isEqualTo(6);
        assertThat(tuple._7).isEqualTo(7);
        assertThat(tuple._8).isEqualTo(8);
        assertThat(tuple._9).isEqualTo(9);
        assertThat(tuple._10).isEqualTo(10);
    }

    @Test
    public void shouldUpdate1() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).update1(42);
      assertThat(tuple._1).isEqualTo(42);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
      assertThat(tuple._7).isEqualTo(7);
      assertThat(tuple._8).isEqualTo(8);
      assertThat(tuple._9).isEqualTo(9);
      assertThat(tuple._10).isEqualTo(10);
    }

    @Test
    public void shouldUpdate2() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).update2(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(42);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
      assertThat(tuple._7).isEqualTo(7);
      assertThat(tuple._8).isEqualTo(8);
      assertThat(tuple._9).isEqualTo(9);
      assertThat(tuple._10).isEqualTo(10);
    }

    @Test
    public void shouldUpdate3() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).update3(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(42);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
      assertThat(tuple._7).isEqualTo(7);
      assertThat(tuple._8).isEqualTo(8);
      assertThat(tuple._9).isEqualTo(9);
      assertThat(tuple._10).isEqualTo(10);
    }

    @Test
    public void shouldUpdate4() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).update4(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(42);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
      assertThat(tuple._7).isEqualTo(7);
      assertThat(tuple._8).isEqualTo(8);
      assertThat(tuple._9).isEqualTo(9);
      assertThat(tuple._10).isEqualTo(10);
    }

    @Test
    public void shouldUpdate5() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).update5(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(42);
      assertThat(tuple._6).isEqualTo(6);
      assertThat(tuple._7).isEqualTo(7);
      assertThat(tuple._8).isEqualTo(8);
      assertThat(tuple._9).isEqualTo(9);
      assertThat(tuple._10).isEqualTo(10);
    }

    @Test
    public void shouldUpdate6() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).update6(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(42);
      assertThat(tuple._7).isEqualTo(7);
      assertThat(tuple._8).isEqualTo(8);
      assertThat(tuple._9).isEqualTo(9);
      assertThat(tuple._10).isEqualTo(10);
    }

    @Test
    public void shouldUpdate7() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).update7(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
      assertThat(tuple._7).isEqualTo(42);
      assertThat(tuple._8).isEqualTo(8);
      assertThat(tuple._9).isEqualTo(9);
      assertThat(tuple._10).isEqualTo(10);
    }

    @Test
    public void shouldUpdate8() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).update8(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
      assertThat(tuple._7).isEqualTo(7);
      assertThat(tuple._8).isEqualTo(42);
      assertThat(tuple._9).isEqualTo(9);
      assertThat(tuple._10).isEqualTo(10);
    }

    @Test
    public void shouldUpdate9() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).update9(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
      assertThat(tuple._7).isEqualTo(7);
      assertThat(tuple._8).isEqualTo(8);
      assertThat(tuple._9).isEqualTo(42);
      assertThat(tuple._10).isEqualTo(10);
    }

    @Test
    public void shouldUpdate10() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> tuple = createIntTuple(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).update10(42);
      assertThat(tuple._1).isEqualTo(1);
      assertThat(tuple._2).isEqualTo(2);
      assertThat(tuple._3).isEqualTo(3);
      assertThat(tuple._4).isEqualTo(4);
      assertThat(tuple._5).isEqualTo(5);
      assertThat(tuple._6).isEqualTo(6);
      assertThat(tuple._7).isEqualTo(7);
      assertThat(tuple._8).isEqualTo(8);
      assertThat(tuple._9).isEqualTo(9);
      assertThat(tuple._10).isEqualTo(42);
    }

    @Test
    public void shouldConvertToSeq() {
        final Seq<?> actual = createIntTuple(1, 0, 0, 0, 0, 0, 0, 0, 0, 0).toSeq();
        assertThat(actual).isEqualTo(List.of(1, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    }

    @Test
    public void shouldCompareEqual() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertThat(t0.compareTo(t0)).isZero();
        assertThat(intTupleComparator.compare(t0, t0)).isZero();
    }

    @Test
    public void shouldCompare1stArg() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t1 = createIntTuple(1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertThat(t0.compareTo(t1)).isNegative();
        assertThat(t1.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t1)).isNegative();
        assertThat(intTupleComparator.compare(t1, t0)).isPositive();
    }

    @Test
    public void shouldCompare2ndArg() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t2 = createIntTuple(0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        assertThat(t0.compareTo(t2)).isNegative();
        assertThat(t2.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t2)).isNegative();
        assertThat(intTupleComparator.compare(t2, t0)).isPositive();
    }

    @Test
    public void shouldCompare3rdArg() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t3 = createIntTuple(0, 0, 1, 0, 0, 0, 0, 0, 0, 0);
        assertThat(t0.compareTo(t3)).isNegative();
        assertThat(t3.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t3)).isNegative();
        assertThat(intTupleComparator.compare(t3, t0)).isPositive();
    }

    @Test
    public void shouldCompare4thArg() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t4 = createIntTuple(0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
        assertThat(t0.compareTo(t4)).isNegative();
        assertThat(t4.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t4)).isNegative();
        assertThat(intTupleComparator.compare(t4, t0)).isPositive();
    }

    @Test
    public void shouldCompare5thArg() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t5 = createIntTuple(0, 0, 0, 0, 1, 0, 0, 0, 0, 0);
        assertThat(t0.compareTo(t5)).isNegative();
        assertThat(t5.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t5)).isNegative();
        assertThat(intTupleComparator.compare(t5, t0)).isPositive();
    }

    @Test
    public void shouldCompare6thArg() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t6 = createIntTuple(0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
        assertThat(t0.compareTo(t6)).isNegative();
        assertThat(t6.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t6)).isNegative();
        assertThat(intTupleComparator.compare(t6, t0)).isPositive();
    }

    @Test
    public void shouldCompare7thArg() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t7 = createIntTuple(0, 0, 0, 0, 0, 0, 1, 0, 0, 0);
        assertThat(t0.compareTo(t7)).isNegative();
        assertThat(t7.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t7)).isNegative();
        assertThat(intTupleComparator.compare(t7, t0)).isPositive();
    }

    @Test
    public void shouldCompare8thArg() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t8 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
        assertThat(t0.compareTo(t8)).isNegative();
        assertThat(t8.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t8)).isNegative();
        assertThat(intTupleComparator.compare(t8, t0)).isPositive();
    }

    @Test
    public void shouldCompare9thArg() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t9 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
        assertThat(t0.compareTo(t9)).isNegative();
        assertThat(t9.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t9)).isNegative();
        assertThat(intTupleComparator.compare(t9, t0)).isPositive();
    }

    @Test
    public void shouldCompare10thArg() {
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t0 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> t10 = createIntTuple(0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
        assertThat(t0.compareTo(t10)).isNegative();
        assertThat(t10.compareTo(t0)).isPositive();
        assertThat(intTupleComparator.compare(t0, t10)).isNegative();
        assertThat(intTupleComparator.compare(t10, t0)).isPositive();
    }

    @Test
    public void shouldMap() {
        final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> tuple = createTuple();
        final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> actual = tuple.map((o1, o2, o3, o4, o5, o6, o7, o8, o9, o10) -> tuple);
        assertThat(actual).isEqualTo(tuple);
    }

    @Test
    public void shouldMapComponents() {
      final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> tuple = createTuple();
      final Function1<Object, Object> f1 = Function1.identity();
      final Function1<Object, Object> f2 = Function1.identity();
      final Function1<Object, Object> f3 = Function1.identity();
      final Function1<Object, Object> f4 = Function1.identity();
      final Function1<Object, Object> f5 = Function1.identity();
      final Function1<Object, Object> f6 = Function1.identity();
      final Function1<Object, Object> f7 = Function1.identity();
      final Function1<Object, Object> f8 = Function1.identity();
      final Function1<Object, Object> f9 = Function1.identity();
      final Function1<Object, Object> f10 = Function1.identity();
      final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> actual = tuple.map(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10);
      assertThat(actual).isEqualTo(tuple);
    }

    @Test
    public void shouldReturnTuple10OfSequence10() {
      final Seq<Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> iterable = List.of(Tuple.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 11), Tuple.of(4, 5, 6, 7, 8, 9, 10, 11, 12, 13), Tuple.of(6, 7, 8, 9, 10, 11, 12, 13, 14, 15), Tuple.of(8, 9, 10, 11, 12, 13, 14, 15, 16, 17), Tuple.of(10, 11, 12, 13, 14, 15, 16, 17, 18, 19), Tuple.of(12, 13, 14, 15, 16, 17, 18, 19, 20, 21), Tuple.of(14, 15, 16, 17, 18, 19, 20, 21, 22, 23), Tuple.of(16, 17, 18, 19, 20, 21, 22, 23, 24, 25), Tuple.of(18, 19, 20, 21, 22, 23, 24, 25, 26, 27), Tuple.of(20, 21, 22, 23, 24, 25, 26, 27, 28, 29));
      final Tuple10<Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>> expected = Tuple.of(Stream.of(2, 4, 6, 8, 10, 12, 14, 16, 18, 20), Stream.of(3, 5, 7, 9, 11, 13, 15, 17, 19, 21), Stream.of(4, 6, 8, 10, 12, 14, 16, 18, 20, 22), Stream.of(5, 7, 9, 11, 13, 15, 17, 19, 21, 23), Stream.of(6, 8, 10, 12, 14, 16, 18, 20, 22, 24), Stream.of(7, 9, 11, 13, 15, 17, 19, 21, 23, 25), Stream.of(8, 10, 12, 14, 16, 18, 20, 22, 24, 26), Stream.of(9, 11, 13, 15, 17, 19, 21, 23, 25, 27), Stream.of(10, 12, 14, 16, 18, 20, 22, 24, 26, 28), Stream.of(11, 13, 15, 17, 19, 21, 23, 25, 27, 29));
      assertThat(Tuple.sequence10(iterable)).isEqualTo(expected);
    }

    @Test
    public void shouldReturnTuple10OfSequence1() {
      final Seq<Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> iterable = List.of(Tuple.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
      final Tuple10<Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>, Seq<Integer>> expected = Tuple.of(Stream.of(1), Stream.of(2), Stream.of(3), Stream.of(4), Stream.of(5), Stream.of(6), Stream.of(7), Stream.of(8), Stream.of(9), Stream.of(10));
      assertThat(Tuple.sequence10(iterable)).isEqualTo(expected);
    }

    @Test
    public void shouldMap1stComponent() {
      final Tuple10<String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1).map1(i -> "X");
      final Tuple10<String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> expected = Tuple.of("X", 1, 1, 1, 1, 1, 1, 1, 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap2ndComponent() {
      final Tuple10<Integer, String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1).map2(i -> "X");
      final Tuple10<Integer, String, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> expected = Tuple.of(1, "X", 1, 1, 1, 1, 1, 1, 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap3rdComponent() {
      final Tuple10<Integer, Integer, String, Integer, Integer, Integer, Integer, Integer, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1).map3(i -> "X");
      final Tuple10<Integer, Integer, String, Integer, Integer, Integer, Integer, Integer, Integer, Integer> expected = Tuple.of(1, 1, "X", 1, 1, 1, 1, 1, 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap4thComponent() {
      final Tuple10<Integer, Integer, Integer, String, Integer, Integer, Integer, Integer, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1).map4(i -> "X");
      final Tuple10<Integer, Integer, Integer, String, Integer, Integer, Integer, Integer, Integer, Integer> expected = Tuple.of(1, 1, 1, "X", 1, 1, 1, 1, 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap5thComponent() {
      final Tuple10<Integer, Integer, Integer, Integer, String, Integer, Integer, Integer, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1).map5(i -> "X");
      final Tuple10<Integer, Integer, Integer, Integer, String, Integer, Integer, Integer, Integer, Integer> expected = Tuple.of(1, 1, 1, 1, "X", 1, 1, 1, 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap6thComponent() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, String, Integer, Integer, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1).map6(i -> "X");
      final Tuple10<Integer, Integer, Integer, Integer, Integer, String, Integer, Integer, Integer, Integer> expected = Tuple.of(1, 1, 1, 1, 1, "X", 1, 1, 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap7thComponent() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, String, Integer, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1).map7(i -> "X");
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, String, Integer, Integer, Integer> expected = Tuple.of(1, 1, 1, 1, 1, 1, "X", 1, 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap8thComponent() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, String, Integer, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1).map8(i -> "X");
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, String, Integer, Integer> expected = Tuple.of(1, 1, 1, 1, 1, 1, 1, "X", 1, 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap9thComponent() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, String, Integer> actual = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1).map9(i -> "X");
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, String, Integer> expected = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, "X", 1);
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldMap10thComponent() {
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, String> actual = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1).map10(i -> "X");
      final Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, String> expected = Tuple.of(1, 1, 1, 1, 1, 1, 1, 1, 1, "X");
      assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldApplyTuple() {
        final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> tuple = createTuple();
        final Tuple0 actual = tuple.apply((o1, o2, o3, o4, o5, o6, o7, o8, o9, o10) -> Tuple0.instance());
        assertThat(actual).isEqualTo(Tuple0.instance());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void shouldTransformTuple() {
        final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> tuple = createTuple();
        final Tuple0 actual = tuple.transform((o1, o2, o3, o4, o5, o6, o7, o8, o9, o10) -> Tuple0.instance());
        assertThat(actual).isEqualTo(Tuple0.instance());
    }

    @Test
    public void shouldRecognizeEquality() {
        final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> tuple1 = createTuple();
        final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> tuple2 = createTuple();
        assertThat((Object) tuple1).isEqualTo(tuple2);
    }

    @Test
    public void shouldRecognizeNonEquality() {
        final Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> tuple = createTuple();
        final Object other = new Object();
        assertThat(tuple).isNotEqualTo(other);
    }

    @Test
    public void shouldRecognizeNonEqualityPerComponent() {
        final Tuple10<String, String, String, String, String, String, String, String, String, String> tuple = Tuple.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        assertThat(tuple.equals(Tuple.of("X", "2", "3", "4", "5", "6", "7", "8", "9", "10"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "X", "3", "4", "5", "6", "7", "8", "9", "10"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "X", "4", "5", "6", "7", "8", "9", "10"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "3", "X", "5", "6", "7", "8", "9", "10"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "3", "4", "X", "6", "7", "8", "9", "10"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "3", "4", "5", "X", "7", "8", "9", "10"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "3", "4", "5", "6", "X", "8", "9", "10"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "3", "4", "5", "6", "7", "X", "9", "10"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "3", "4", "5", "6", "7", "8", "X", "10"))).isFalse();
        assertThat(tuple.equals(Tuple.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "X"))).isFalse();
    }

    @Test
    public void shouldComputeCorrectHashCode() {
        final int actual = createTuple().hashCode();
        final int expected = Objects.hash(null, null, null, null, null, null, null, null, null, null);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldImplementToString() {
        final String actual = createTuple().toString();
        final String expected = "(null, null, null, null, null, null, null, null, null, null)";
        assertThat(actual).isEqualTo(expected);
    }

    private Comparator<Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> intTupleComparator = Tuple10.comparator(Integer::compare, Integer::compare, Integer::compare, Integer::compare, Integer::compare, Integer::compare, Integer::compare, Integer::compare, Integer::compare, Integer::compare);

    private Tuple10<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> createTuple() {
        return new Tuple10<>(null, null, null, null, null, null, null, null, null, null);
    }

    private Tuple10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> createIntTuple(Integer i1, Integer i2, Integer i3, Integer i4, Integer i5, Integer i6, Integer i7, Integer i8, Integer i9, Integer i10) {
        return new Tuple10<>(i1, i2, i3, i4, i5, i6, i7, i8, i9, i10);
    }
}