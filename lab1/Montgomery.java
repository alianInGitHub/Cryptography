package crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

/**
 * Created by anastasia on 06.12.16.
 */
public class Montgomery {
    public static void main(String[] args) throws IOException {
        // Prompt user on standard output, parse standard input
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "US-ASCII"));
        System.out.print("Number x: ");
        BigInteger x = new BigInteger(in.readLine());
        System.out.print("Operation (\"times\" or \"pow\"): ");
        String oper = in.readLine();
        System.out.print("н =  ");
        BigInteger y = new BigInteger(in.readLine());
        System.out.print("Modulus: ");
        BigInteger mod = new BigInteger(in.readLine());
        System.out.println();

        // Do computation
        MontgomeryReducer red = new MontgomeryReducer(mod);
        BigInteger xm = red.convertIn(x);
        BigInteger zm;
        BigInteger z;
        if (oper.equals("times")) {
            zm = red.multiply(xm, red.convertIn(y));
            z = x.multiply(y).mod(mod);
        } else if (oper.equals("pow")) {
            zm = red.pow(xm, y);
            z = x.modPow(y, mod);
        } else
            throw new IllegalArgumentException("Invalid operation: " + oper);
        if (!red.convertOut(zm).equals(z))
            throw new AssertionError("Self-check failed");
        System.out.printf("%d%s%d mod %d%n", x, oper.equals("times") ? " * " : "^", y, mod);
        System.out.println("= " + z);
    }
}

class MontgomeryReducer {

    // Input parameter
    private BigInteger modulus;  // Must be an odd number at least 3

    // Computed numbers
    private BigInteger reducer;       // Is a power of 2
    private int reducerBits;          // Equal to log2(reducer)
    private BigInteger reciprocal;    // Equal to reducer^-1 mod modulus
    private BigInteger mask;          // Because x mod reducer = x & (reducer - 1)
    private BigInteger factor;        // Equal to (reducer * reducer^-1 - 1) / n
    private BigInteger convertedOne;  // Equal to convertIn(BigInteger.ONE)


    // The modulus must be an odd number at least 3
    public MontgomeryReducer(BigInteger modulus) {
        // Modulus
        if (modulus == null)
            throw new NullPointerException();
        if (!modulus.testBit(0) || modulus.compareTo(BigInteger.ONE) <= 0)
            throw new IllegalArgumentException("Modulus must be an odd number at least 3");
        this.modulus = modulus;

        // Reducer
        reducerBits = (modulus.bitLength() / 8 + 1) * 8;  // This is a multiple of 8
        reducer = BigInteger.ONE.shiftLeft(reducerBits);  // This is a power of 256
        mask = reducer.subtract(BigInteger.ONE);
        assert reducer.compareTo(modulus) > 0 && reducer.gcd(modulus).equals(BigInteger.ONE);

        // Other computed numbers
        reciprocal = reducer.modInverse(modulus);
        factor = reducer.multiply(reciprocal).subtract(BigInteger.ONE).divide(modulus);
        convertedOne = reducer.mod(modulus);
    }


    // The range of x is unlimited
    public BigInteger convertIn(BigInteger x) {
        return x.shiftLeft(reducerBits).mod(modulus);
    }


    // The range of x is unlimited
    public BigInteger convertOut(BigInteger x) {
        return x.multiply(reciprocal).mod(modulus);
    }


    // Inputs and output are in Montgomery form and in the range [0, modulus)
    public BigInteger multiply(BigInteger x, BigInteger y) {
        assert x.signum() >= 0 && x.compareTo(modulus) < 0;
        assert y.signum() >= 0 && y.compareTo(modulus) < 0;
        BigInteger product = x.multiply(y);
        BigInteger temp = product.and(mask).multiply(factor).and(mask);
        BigInteger reduced = product.add(temp.multiply(modulus)).shiftRight(reducerBits);
        BigInteger result = reduced.compareTo(modulus) < 0 ? reduced : reduced.subtract(modulus);
        assert result.signum() >= 0 && result.compareTo(modulus) < 0;
        return result;
    }


    // Input x (base) and output (power) are in Montgomery form and in the range [0, modulus); input y (exponent) is in standard form
    public BigInteger pow(BigInteger x, BigInteger y) {
        assert x.signum() >= 0 && x.compareTo(modulus) < 0;
        if (y.signum() == -1)
            throw new IllegalArgumentException("Negative exponent");

        BigInteger z = convertedOne;
        for (int i = 0, len = y.bitLength(); i < len; i++) {
            if (y.testBit(i))
                z = multiply(z, x);
            x = multiply(x, x);
        }
        return z;
    }
}
