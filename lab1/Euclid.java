package crypto;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Created by anastasia on 06.12.16.
 */
public class Euclid {
    private static BigInteger x = BigInteger.ZERO;
    private static BigInteger y = BigInteger.ZERO;

    public static BigInteger euclidExtended(BigInteger a, BigInteger b) {
        if(a == BigInteger.ZERO) {
            x = BigInteger.ZERO;
            y = BigInteger.ONE;
            return b;
        }

        BigInteger d = euclidExtended(b.remainder(a), a);
        BigInteger x1 = x;
        x = y.add(x1.multiply(b.divide(a)).negate());
        y = x1;
        return d;
    }

    public static void main(String[] args){
        Euclid e = new Euclid();
        Random random = new Random();
        System.out.println("GCD Extended");
        int length = random.nextInt(20000) + 1;
        BigInteger a = new BigInteger(length, random);
        a = new BigInteger("3");
        BigInteger b = new BigInteger("-7");
        //length = random.nextInt(2000) + 1;
        //BigInteger b = new BigInteger(length, random);
        System.out.print(euclidExtended(a, b) + "\n");
        System.out.print(a.gcd(b) + "\n");
        //assertEquals(euclidExtended(a, b), a.gcd(b));
        System.out.println("x = " + x + "\n");
        System.out.println("y = " + y + "\n");
        System.out.println();
    }
}
