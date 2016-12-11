package crypto;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by anastasia on 06.12.16.
 */
public class Ferma {
    private final static Random rand = new Random();

    private static BigInteger getRandom(BigInteger n) {
        while (true) {
            final BigInteger a = new BigInteger (n.bitLength(), rand);
            // must have 1 <= a < n
            if (BigInteger.ONE.compareTo(a) <= 0 && a.compareTo(n) < 0) {
                return a;
            }
        }
    }

    public static boolean test(BigInteger n, int maxIterations)
    {
        if (n.equals(BigInteger.ONE))
            return false;

        for (int i = 0; i < maxIterations; i++) {
            BigInteger a = getRandom(n);
            a = a.modPow(n.subtract(BigInteger.ONE), n);

            if (!a.equals(BigInteger.ONE))
                return false;
        }

        return true;
    }

    public static void main(String[] args){
        System.out.println("Ferma test");
        for (int i = 0; i < 20; i++){
            BigInteger a = BigInteger.probablePrime(i * 2 + 2, rand);
            System.out.print(a.toString() + " is prime? " + test(a, 20) + "\n");
        }
        BigInteger a = BigInteger.valueOf(25);
        System.out.print(a.toString() + " is prime? " + test(a, 20) + "\n");
    }
}
