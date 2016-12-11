package crypto;

import java.math.BigInteger;
import java.util.Scanner;

/**
 * Created by anastasia on 06.12.16.
 */
public class ModPow {
    private static final BigInteger TWO = BigInteger.valueOf(2);

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        while (true) {

            System.out.println("Number: ");
            BigInteger numb = scan.nextBigInteger();

            System.out.println("Degree: ");
            BigInteger deg = scan.nextBigInteger();

            System.out.println("Mod: ");
            BigInteger mod = scan.nextBigInteger();

            System.out.println(binpow(numb, deg).mod(mod) + "\n");

            System.out.print("Continue?(Y/N)");
            String ans = scan.next();
            if(ans.equals("N") || scan.equals("n"))
                break;
        }
    }

    private static BigInteger binpow(BigInteger a, BigInteger n) {
        if (n.equals(BigInteger.ZERO)) {
            return BigInteger.ONE;
        }
        if (n.mod(TWO).equals(BigInteger.ONE)) {
            return a.multiply(binpow(a, n.subtract(BigInteger.ONE)));
        } else {
            BigInteger b = binpow(a, n.divide(TWO));
            return b.multiply(b);
        }
    }
}
