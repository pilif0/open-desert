package net.pilif0.open_desert.math;

/**
 * This class handles calculation of the highest common factor (greatest common divisor) of various numbers.
 *
 * @author Filip Smola
 * @version 1.0
 */
public class HighestCommonFactor {

    /**
     * Calculates the highest common factor of two integers
     *
     * @param a The first integer
     * @param b The second integer
     * @return The highest common factor of the two integers
     */
    public static int hcf(int a, int b){
        //This is safe to do, because the hcf can be at most the lower of a and b
        return Math.toIntExact(hcf((long) a, (long) b));
    }

    /**
     * Calculates the highest common factor of two longs
     *
     * @param a The first long
     * @param b The second long
     * @return The highest common factor of the two longs
     */
    public static long hcf(long a, long b){
        //Check for negative numbers (hcf(a, b) = hcf(|a|, |b|))
        if(a < 0){
            a = ~a + 1;
        }
        if(b < 0){
            b = ~b + 1;
        }

        //Special case: either a or b is zero -> hcf is the other
        if(a == 0){
            return b;
        }
        if(b == 0){
            return a;
        }

        //Loop until result is found
        byte shift = 0;
        while(a != b){
            //Reduce if possible
            if((a & 1) == 0){
                //Case: a is even

                if((b & 1) == 0){
                    //Case: both are even -> 2 is a factor

                    a = a >>> 1;
                    b = b >>> 1;
                    shift++;
                    continue;
                }else{
                    //Case: only a is even -> 2 is not a factor

                    a = a >>> 1;
                    continue;
                }
            }else{
                //Case: a is odd

                if((b & 1) == 0){
                    //Case: only b is even -> 2 is not a factor

                    b = b >>> 1;
                    continue;
                }else{
                    //Case: both are odd -> the factor is the same as with the lower number and half their difference

                    if(a > b){
                        a = (a - b) >>> 1;
                    }else{
                        b = (b - a) >>> 1;
                    }
                    continue;
                }
            }
        }

        //Calculate the result
        return a << shift;
    }
}
