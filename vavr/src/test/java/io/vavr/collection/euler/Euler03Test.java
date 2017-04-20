/*                        __    __  __  __    __  ___
 *                       \  \  /  /    \  \  /  /  __/
 *                        \  \/  /  /\  \  \/  /  /
 *                         \____/__/  \__\____/__/.ɪᴏ
 * ᶜᵒᵖʸʳᶦᵍʰᵗ ᵇʸ ᵛᵃᵛʳ ⁻ ˡᶦᶜᵉⁿˢᵉᵈ ᵘⁿᵈᵉʳ ᵗʰᵉ ᵃᵖᵃᶜʰᵉ ˡᶦᶜᵉⁿˢᵉ ᵛᵉʳˢᶦᵒⁿ ᵗʷᵒ ᵈᵒᵗ ᶻᵉʳᵒ
 */
package io.vavr.collection.euler;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Euler03Test {

    /**
     * <strong>Problem 3: Largest prime factor</strong>
     * <p>
     * The prime factors of 13195 are 5, 7, 13 and 29.
     * <p>
     * What is the largest prime factor of the number 600851475143?
     * <p>
     * See also <a href="https://projecteuler.net/problem=3">projecteuler.net problem 3</a>.
     */
    @Test
    public void shouldSolveProblem3() {
        assertThat(largestPrimeFactorOf(24)).isEqualTo(3);
        assertThat(largestPrimeFactorOf(29)).isEqualTo(29);
        assertThat(largestPrimeFactorOf(13195)).isEqualTo(29);
        assertThat(largestPrimeFactorOf(600_851_475_143L)).isEqualTo(6857);
    }

    private static long largestPrimeFactorOf(long val) {
        return PrimeNumbers.primeFactors(val).max().get();
    }
}
