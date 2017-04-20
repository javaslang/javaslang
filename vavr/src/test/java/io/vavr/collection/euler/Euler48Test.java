/*                        __    __  __  __    __  ___
 *                       \  \  /  /    \  \  /  /  __/
 *                        \  \/  /  /\  \  \/  /  /
 *                         \____/__/  \__\____/__/.ɪᴏ
 * ᶜᵒᵖʸʳᶦᵍʰᵗ ᵇʸ ᵛᵃᵛʳ ⁻ ˡᶦᶜᵉⁿˢᵉᵈ ᵘⁿᵈᵉʳ ᵗʰᵉ ᵃᵖᵃᶜʰᵉ ˡᶦᶜᵉⁿˢᵉ ᵛᵉʳˢᶦᵒⁿ ᵗʷᵒ ᵈᵒᵗ ᶻᵉʳᵒ
 */
package io.vavr.collection.euler;

import io.vavr.collection.Stream;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Euler48Test {

    private static final long MOD = 10_000_000_000L;

    /**
     * <strong>Problem 48: Self powers</strong>
     * <p>
     * The series, 1<sup>1</sup> + 2<sup>2</sup> + 3<sup>3</sup> + ... + 10<sup>10</sup> = 10405071317.
     * <p>
     * Find the last ten digits of the series, 1<sup>1</sup> + 2<sup>2</sup> + 3<sup>3</sup> + ... + 1000<sup>1000</sup>.
     * <p>
     * See also <a href="https://projecteuler.net/problem=48">projecteuler.net problem 48</a>.
     */
    @Test
    public void shouldSolveProblem48() {
        assertThat(sumPowers(10)).isEqualTo(405_071_317L);
        assertThat(sumPowers(1000)).isEqualTo(9_110_846_700L);
    }

    private static long sumPowers(int max) {
        return Stream.range(1, max)
                .map(Euler48Test::selfPower)
                .reduce(Euler48Test::sumMod);
    }

    private static long selfPower(long v) {
        final Stream<Long> powers = Stream.iterate(v, el -> multMod(el, el));
        return bits(v)
                .map(powers::get)
                .prepend(1L)
                .reduce(Euler48Test::multMod);
    }

    private static long multMod(long v1, long v2) {
        final Stream<Long> shifts = Stream.iterate(v1, el -> sumMod(el, el));
        return bits(v2)
                .map(shifts::get)
                .prepend(0L)
                .reduce(Euler48Test::sumMod);
    }

    private static long sumMod(long v1, long v2) {
        return (v1 + v2) % MOD;
    }

    private static Stream<Integer> bits(long v) {
        return Stream.from(0)
                .takeWhile(b -> (v >> b) > 0)
                .filter(b -> ((v >> b) & 1) != 0);
    }

}
