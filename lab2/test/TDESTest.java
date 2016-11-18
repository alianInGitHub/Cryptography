import junit.framework.TestCase;
import java.util.Vector;

/**
 * Created by anastasia on 05.10.16.
 */

public class TDESTest extends TestCase {

    int LENGTH = 64;
    int CHAR_LENGTH = 8;
    private Vector<byte[]> breakToBlocks(String text) {
        Vector<byte[]> resultText = new Vector<>();
        byte[] block = new byte[LENGTH];
        int i = 0;      //counter for byte[]
        int j = 0;      //counter for text
        while(j < text.length()){
            int c = Character.getNumericValue(text.charAt(j));
            byte[] binC = new byte[CHAR_LENGTH];      //ASCII
            //to binary form
            int k = CHAR_LENGTH - 1;
            while((k >= 0) && (c > 0)) {
                if (k < 0)
                    break;
                binC[k] = (byte) (c % 2);
                c = c / 2;
                k--;
            }

            for(k = 0; k < CHAR_LENGTH; k++) {
                block[i] = binC[k];
                i++;
                if (i == LENGTH) {
                    resultText.add(block);
                    i = 0;
                    block = new byte[LENGTH];
                }
            }
            j++;
        }
        if(i > 0)
            resultText.add(block);
        return  resultText;
    }

    private char[] fromBinToHexadecimal(byte[] text){
        char[] result = new char[16];
        for(int i = 0; i < 64; i+= 4){
            byte n = 0;
            for(int j = 0; j < 4; j++)
                n += text[i + j] * Math.pow(2, 3 - j);
            if(n < 10){
                result[i / 4] = Character.forDigit(n, 10);
            } else {
                switch (n){
                    case 10 : result[i / 4] = 'A'; break;
                    case 11 : result[i / 4] = 'B'; break;
                    case 12 : result[i / 4] = 'C'; break;
                    case 13 : result[i / 4] = 'D'; break;
                    case 14 : result[i / 4] = 'E'; break;
                    case 15 : result[i / 4] = 'F'; break;
                }
            }
        }
        return result;
    }

    public void testEncrypt() throws Exception {
        TDES crypt = new TDES();
        crypt.generateKeys();
        String text = "apples";
        Vector<byte[]> t1 = breakToBlocks(text);
        for(int i = 0; i < LENGTH; i++)
            System.out.print(t1.firstElement()[i]);
        System.out.println();

        t1.add(crypt.encrypt(t1.firstElement()));
        for(int i = 0; i < LENGTH; i++)
        System.out.print(t1.lastElement()[i]);

    }

}