package crypto;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by anastasia on 06.12.16.
 */
public class MillerRyabin {

    public static boolean test(BigInteger n, int certainty){
        //return n.isProbablePrime(certainty);
        final int[] primes = {
                2,   3,   5,   7,  11,  13,  17,  19,  23,  29,  31,  37,  41,  43,
                47,  53,  59,  61,  67,  71,  73,  79,  83,  89,  97, 101, 103, 107,
                109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181,
                191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251
        };

        if(certainty < 1)
            return true;
        // n > 2
        BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
        if(n.compareTo(two) <= 0)
            return true;
        // check for small primes
        if(n.compareTo(new BigInteger("252")) < 0)
        {
            for(int i = 0; i < primes.length; i++){
                if(n.equals(new BigInteger(Integer.toString(primes[i]))))
                    return true;
            }
            return false;
        }

        BigInteger p = n.subtract(BigInteger.ONE);
        BigInteger t = p;
        BigInteger s = BigInteger.ZERO;
        while (t.remainder(two) == BigInteger.ZERO){
            s = s.add(BigInteger.ONE);
            t = t.divide(two);
        }
        for(int i = 0; i < certainty; i++){
            int len = p.bitLength();
            BigInteger a = new BigInteger(Math.min(2, p.bitLength()), p.bitLength(), new Random());
            BigInteger x = a.modPow(t, n);
            if((x.equals(BigInteger.ONE)) || (x.equals(p)))
                continue;
            s = s.subtract(BigInteger.ONE);
            BigInteger j;
            for(j = BigInteger.ZERO; j.compareTo(s) < 0; j = j.add(BigInteger.ONE)){
                x = x.modPow(two, n);
                if(x == BigInteger.ONE)
                    return false;
                if(x.equals(p))
                    break;
            }
            if(j.compareTo(s) >= 0)
                return false;
            else
            if((j.compareTo(s) < 0) && !x.equals(p))
                return false;
            s = s.add(BigInteger.ONE);
        }
        return true;
    }

    public static void main(String[] args){
        Random random = new Random();
        System.out.println("Miller-Ryabin Test");
        BigInteger errorCount = BigInteger.ZERO;
        for(BigInteger i = new BigInteger("30000");
            !i.equals(new BigInteger("200000")); i = i.add(BigInteger.ONE)){
            int certanity = random.nextInt(2000);
            boolean myRes = test(i, certanity);
            boolean officialRes = i.isProbablePrime(certanity);
            if( myRes != officialRes)
            {
                errorCount.add(BigInteger.ONE);
                System.out.println(myRes + " " + officialRes + " " + i);
            }
        }
        System.out.println("Errors : " + errorCount);
        System.out.println();
    }
}
