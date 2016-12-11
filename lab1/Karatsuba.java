package crypto;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by anastasia on 06.12.16.
 */
public class Karatsuba {

    public static BigInteger multiply(BigInteger x, BigInteger y) {

        // cutoff to brute force
        int N = Math.max(x.bitLength(), y.bitLength());
        if (N <= 2000) return x.multiply(y);

        // number of bits divided by 2, rounded up
        N = (N / 2) + (N % 2);

        // x = a + 2^N b,   y = c + 2^N d
        BigInteger b = x.shiftRight(N);
        BigInteger a = x.subtract(b.shiftLeft(N));
        BigInteger d = y.shiftRight(N);
        BigInteger c = y.subtract(d.shiftLeft(N));

        // compute sub-expressions
        BigInteger ac    = multiply(a, c);
        BigInteger bd    = multiply(b, d);
        BigInteger abcd  = multiply(a.add(b), c.add(d));

        return ac.add(abcd.subtract(ac).subtract(bd).shiftLeft(N)).add(bd.shiftLeft(2*N));
    }


    public static void main(String[] args) {
        long start, stop;
        Random random = new Random(100);
        //int N = Integer.parseInt(args[0]);
        int N = 200000;
        BigInteger a = new BigInteger(N, random);
        BigInteger b = new BigInteger(N, random);
        System.out.println(a);
        System.out.println(b);

        start = System.currentTimeMillis();
        BigInteger c = multiply(a, b);
        stop = System.currentTimeMillis();
        System.out.println("Time used: " + (stop - start) + " ms");
        System.out.println("Result: " + c);

        start = System.currentTimeMillis();
        BigInteger d = a.multiply(b);
        stop = System.currentTimeMillis();
        System.out.println("Time used to multiply: " + (stop - start) + " ms");

        System.out.println((c.equals(d)));
    }
}
