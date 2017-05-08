/*                        __    __  __  __    __  ___
 *                       \  \  /  /    \  \  /  /  __/
 *                        \  \/  /  /\  \  \/  /  /
 *                         \____/__/  \__\____/__/.ɪᴏ
 * ᶜᵒᵖʸʳᶦᵍʰᵗ ᵇʸ ᵛᵃᵛʳ ⁻ ˡᶦᶜᵉⁿˢᵉᵈ ᵘⁿᵈᵉʳ ᵗʰᵉ ᵃᵖᵃᶜʰᵉ ˡᶦᶜᵉⁿˢᵉ ᵛᵉʳˢᶦᵒⁿ ᵗʷᵒ ᵈᵒᵗ ᶻᵉʳᵒ
 */
package io.vavr;

import io.vavr.collection.List;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.function.Predicate;

import static io.vavr.API.$;
import static io.vavr.API.*;
import static io.vavr.Predicates.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PredicatesTest {

    private static final Predicate<? super Throwable> IS_RUNTIME_EXCEPTION = instanceOf(RuntimeException.class);

    // -- instanceOf

    @Test
    public void shouldTestInstanceOf_PositiveCase() {
        assertThat(instanceOf(Number.class).test(1)).isTrue();
        assertThat(instanceOf(Number.class).test(new BigDecimal("1"))).isTrue();
        assertThat(IS_RUNTIME_EXCEPTION.test(new NullPointerException())).isTrue();
    }

    @Test
    public void shouldTestInstanceOf_NegativeCase() {
        assertThat(IS_RUNTIME_EXCEPTION.test(new Exception())).isFalse();
        assertThat(IS_RUNTIME_EXCEPTION.test(new Error("error"))).isFalse();
        assertThat(IS_RUNTIME_EXCEPTION.test(null)).isFalse();
    }

    // -- is

    @Test
    public void shouldTestIs_PositiveCase() {
        assertThat(is(1).test(1)).isTrue();
        assertThat(is((CharSequence) "1").test("1")).isTrue();
    }

    @Test
    public void shouldTestIs_NegativeCase() {
        assertThat(is(1).test(2)).isFalse();
        assertThat(is((CharSequence) "1").test(new StringBuilder("1"))).isFalse();
    }

    // -- isIn

    @Test
    public void shouldTestIsIn_PositiveCase() {
        assertThat(isIn(1, 2, 3).test(2)).isTrue();
        assertThat(isIn((CharSequence) "1", "2", "3").test("2")).isTrue();
    }

    @Test
    public void shouldTestIsIn_NegativeCase() {
        assertThat(isIn(1, 2, 3).test(4)).isFalse();
        assertThat(isIn((CharSequence) "1", "2", "3").test("4")).isFalse();
    }

    // Predicate Combinators

    // -- allOf

    private static final Predicate<Integer> P1 = i -> i > 1;
    private static final Predicate<Integer> P2 = i -> i > 2;

    @Test
    public void shouldTestAllOf_PositiveCase() {
        assertThat(allOf().test(1)).isTrue();
        assertThat(allOf(P1, P2).test(3)).isTrue();
    }

    @Test
    public void shouldTestAllOf_NegativeCase() {
        assertThat(allOf(P1, P2).test(2)).isFalse();
    }

    // -- anyOf

    @Test
    public void shouldTestAnyOf_PositiveCase() {
        assertThat(anyOf(P1, P2).test(3)).isTrue();
        assertThat(anyOf(P1, P2).test(2)).isTrue();
    }

    @Test
    public void shouldTestAnyOf_NegativeCase() {
        assertThat(anyOf().test(1)).isFalse();
        assertThat(anyOf(P1, P2).test(1)).isFalse();
    }

    // -- noneOf

    @Test
    public void shouldTestNoneOf_PositiveCase() {
        assertThat(noneOf().test(1)).isTrue();
        assertThat(noneOf(P1, P2).test(1)).isTrue();
    }

    @Test
    public void shouldTestNoneOf_NegativeCase() {
        assertThat(noneOf(P1).test(2)).isFalse();
        assertThat(noneOf(P1, P2).test(2)).isFalse();
    }

    // -- isNull

    @Test
    public void shouldTestIsNull_PositiveCase() {
        assertThat(isNull().test(null)).isTrue();
    }

    @Test
    public void shouldTestIsNull_NegativeCase() {
        assertThat(isNull().test("")).isFalse();
    }

    // -- isNotNull

    @Test
    public void shouldTestIsNotNull_PositiveCase() {
        assertThat(isNotNull().test("")).isTrue();
    }

    @Test
    public void shouldTestIsNotNull_NegativeCase() {
        assertThat(isNotNull().test(null)).isFalse();
    }

    // -- exits

    @Test
    public void shouldTestExists_PositiveCase() {
        assertThat(exists(P1).test(List.of(1, 3))).isTrue();
    }

    @Test
    public void shouldTestExists_NegativeCase() {
        assertThat(exists(P1).test(List.of(1, 0))).isFalse();
    }

    @Test
    public void shouldCheckExistsByLiftingPredicateInContravariantPositionToPredicateInCovariantPosition() {
        final List<Integer> list = List(1, 2, 3);
        final Predicate<Number> p = n -> n.intValue() % 2 == 0;
        final boolean actual = Match(list).of(
                Case($(exists(p)), true),
                Case($(), false)
        );
        assertThat(actual).isTrue();
    }

    // -- forAll

    @Test
    public void shouldTestForAll_PositiveCase() {
        assertThat(forAll(P1).test(List.of(2, 3))).isTrue();
    }

    @Test
    public void shouldTestForAll_NegativeCase() {
        assertThat(forAll(P1).test(List.of(3, 0))).isFalse();
    }

    @Test
    public void shouldCheckForAllByLiftingPredicateInContravariantPositionToPredicateInCovariantPosition() {
        final List<Integer> list = List(1, 2, 3);
        final Predicate<Number> p = n -> n.intValue() > 0;
        final boolean actual = Match(list).of(
                Case($(forAll(p)), true),
                Case($(), false)
        );
        assertThat(actual).isTrue();
    }
}
