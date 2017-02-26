package net.pilif0.open_desert.math;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A set of unit tests for the {@code HighestCommonFactor} class
 *
 * @author Filip Smola
 * @version 1.0
 */
public class PrimesTest {

    @Test
    public void testIsPrime() throws Exception {
        //Test whether some numbers are prime
        assertTrue(Primes.isPrime(2));
        assertTrue(Primes.isPrime(13));
        assertTrue(Primes.isPrime(19));
        assertTrue(Primes.isPrime(31));
        assertTrue(Primes.isPrime(109));

        assertFalse(Primes.isPrime(4));
        assertFalse(Primes.isPrime(12));
        assertFalse(Primes.isPrime(39));
        assertFalse(Primes.isPrime(81));
        assertFalse(Primes.isPrime(135));
    }

    @Test
    public void testPrimeFactors() throws Exception {
        //Test some prime factorizations
        int[] fact16 = {2, 2, 2, 2};
        assertTrue(Arrays.equals(fact16, Primes.primeFactors(16)));
        int[] fact33 = {3, 11};
        assertTrue(Arrays.equals(fact33, Primes.primeFactors(33)));
        int[] fact106 = {2, 53};
        assertTrue(Arrays.equals(fact106, Primes.primeFactors(106)));
        int[] fact72 = {2, 2, 2, 3, 3};
        assertTrue(Arrays.equals(fact72, Primes.primeFactors(72)));
        int[] fact1537 = {29, 53};
        assertTrue(Arrays.equals(fact1537, Primes.primeFactors(1537)));
    }
}
