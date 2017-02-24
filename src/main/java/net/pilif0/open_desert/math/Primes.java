package net.pilif0.open_desert.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class handles everything around prime numbers.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class Primes {
    //Definition of prime: n >= 2 such that the only positive factors of n are 1 and n

    /** The primes already calculated */
    private static List<Integer> primes;
    /** All the prime numbers up to this number have been generated */
    private static int generatedTo;

    static {
        //Initialise the primes list with primes to 13
        Integer[] initial = {2, 3, 5, 7, 11, 13};
        primes = new ArrayList<>(Arrays.asList(initial));
        generatedTo = 13;
    }

    /**
     * Verifies whether the integer is a prime number
     *
     * @param n The integer to test
     * @return Whether the integer is a prime number
     */
    public static synchronized boolean isPrime(int n){
        //Verify n >= 2
        if(n < 2){
            return false;
        }

        //Generate if needed
        generatePrimes(n);

        //Not divisible by anything, therefore prime
        return primes.contains(n);
    }

    /**
     * Returns an array of prime factors of an integer
     *
     * @param n The integer
     * @return The prime factors
     */
    public static synchronized int[] primeFactors(int n){
        //Verify n >= 2
        if(n < 2){
            return new int[0];
        }

        //Skip if n is prime
        if(isPrime(n)){
            int[] result = {n};
            return result;
        }

        //Prepare result
        List<Integer> factors = new ArrayList<>();

        //Divide n by primes
        int l = n;
        int[] ds = primes.stream()
                .filter(x -> x <= l)
                .mapToInt(x -> x)
                .toArray();
        while(n != 1){
            //Find a divisor
            for(int d : ds){
                if(n % d == 0){
                    factors.add(d);
                    n /= d;
                    break;
                }
            }
        }

        //Return the list as a sorted array
        int[] result = factors.stream()
                .mapToInt(x -> x)
                .toArray();
        Arrays.sort(result);
        return result;
    }

    /**
     * Generates primes up to the limit and add them to the list
     *
     * @param limit The limit to which generate
     */
    private static synchronized void generatePrimes(int limit){
        //Skip if already generated
        if(limit <= generatedTo){
            return;
        }

        //Find the start (odd integer greater than or equal to generatedTo)
        int start;
        if((generatedTo & 1) == 0){
            start = generatedTo + 1;
        }else{
            start = generatedTo;
        }

        //Verify every odd number up to the limit
        for(int n = start; n <= limit; n += 2){
            //Test whether the number is prime
            boolean prime = true;
            int l = (int) Math.sqrt((double) n);
            int[] ds = primes.stream()
                    .filter(x -> x <= l)
                    .mapToInt(x -> x)
                    .toArray();
            for(int d : ds){
                //Test whether n is divisible by d
                if(n % d == 0){
                    prime = false;
                }
            }

            if(prime){
                primes.add(n);
            }
        }

        //Update generatedTo
        generatedTo = limit;
    }
}
