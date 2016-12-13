import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Created by anastasia on 11.12.16.
 */
public class Main {
    private static BigInteger[] keys;
    private static BigInteger a = BigInteger.valueOf(3), b = BigInteger.valueOf(5);

    
    // find the greatest common devider of 2 polinoms
    private static BigDecimal[] gcd(BigDecimal[] f, BigDecimal[] g){
        if(g.length > f.length)
            return null;
        BigDecimal[] q, r;
        q = new BigDecimal[-g.length + f.length + 1];
        r = new BigDecimal[f.length - 1];
        BigDecimal[] temp = new BigDecimal[g.length];
        int n = (int)Math.log(g[g.length - 1].toBigInteger().abs().bitLength()) + 1;
        for(int i = 0; i < temp.length; i++) {
            temp[i] = g[i].divide(g[g.length - 1], n + 2, RoundingMode.HALF_UP);
        }
        q[q.length - 1] = f[f.length - 1].divide(g[g.length - 1], n + 2, RoundingMode.HALF_UP);
        for(int i = q.length - 2; i >= 0; i--)
            q[i] = BigDecimal.ZERO;
        for(int i = r.length - 1; i >= 0; i--)
            if(i < temp.length)
                r[i] = f[i].subtract(temp[i]);
            else r[i] = f[i];
        boolean zero = true;
        for(int i = 0; i < r.length; i++)
            if(r[i] != BigDecimal.ZERO){
                zero = false;
                break;
            }
        if(zero)
            return g;
        int i = r.length - 1;
        while (r[i].equals(BigDecimal.ZERO))
            i--;
        if(i != r.length - 1){
            temp = new BigDecimal[i + 1];
            for(int j = i; j >= 0; j--)
                temp[j] = r[j];
            return gcd(g, temp);
        }
        return gcd(g, r);
    }

    private static BigInteger f(BigInteger x){
        return x.multiply(a).add(b);
    }


    //write examples into the file
    private static void write(String file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        String[] vals = new String[5];
        vals[0] = "13223598643439867049386735";
        vals[1] = "8463047480763459387463058";
        vals[2] = "765494674936";
        vals[3] = "5";
        vals[4] = "4";

        for(int i = 4; i >= 0; i--){
            writer.write(vals[i] + "\n");
            writer.write(f(new BigInteger(vals[i])).mod(keys[1]).toString() + "\n");
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {

	//generate public keys
        User Alice;
        do {
            Alice = new User();
            keys = Alice.getPublicKeys();
            System.out.print(keys[0] + "\t" + keys[1] + "\n");
        } while (!keys[0].equals(BigInteger.valueOf(3)));

        //write("data.txt");

        User Bob = new User();
        Bob.readMessages("data.txt");
        Bob.setPublicKeys(keys);
        for(int count = 0; count < Bob.getMessagesAmount(); count += 2) {

            BigInteger C1 = Bob.sendMessage();
            BigInteger C2 = Bob.sendMessage();

            System.out.print("C1 = " + C1 + "\nC2 = " + C2 +"\n\n");

	    //M2 = f(M1), there M1, M2 - original messages we have to find
	    //g1 = x^e - C1
	    //g2 = f(x)^e - C2

            BigDecimal[] g1 = new BigDecimal[4];
            g1[3] = BigDecimal.ONE;
            g1[2] = BigDecimal.ZERO;
            g1[1] = BigDecimal.ZERO;
            g1[0] = new BigDecimal(C1.negate());

            BigDecimal[] g2 = new BigDecimal[4];
            g2[3] = new BigDecimal(a.pow(3));
            g2[2] = new BigDecimal(a.pow(2).multiply(BigInteger.valueOf(3)).multiply(b));
            g2[1] = new BigDecimal(a.multiply(BigInteger.valueOf(3)).multiply(b.pow(2)));
            g2[0] = new BigDecimal(b.pow(3).subtract(C2));

            BigDecimal[] func = gcd(g2, g1);
	    // if the gdc of polinoms g1 and g2 is a linear function
            if ((func != null) && (func.length > 1)) {
		// then it has to be presented like v * (x - M1), 
		// there v - is random constant
                BigInteger m1 = func[0].divide(func[1]).toBigInteger();
                m1.negate();
		// calculate M2
                BigInteger m2 = f(m1);
		//print the answer
                System.out.print(m1 + "\n" + m2 + "\n" + Bob.assertEqualMessages(m1, m2));
            } else
                System.out.print("Cannot attack using this algorithm.\n\n");
        }
    }
}
