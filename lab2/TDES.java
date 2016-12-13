import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.Vector;

/**
 * Created by anastasia on 05.10.16.
 */

public class TDES implements CryptInterface{
    private String[] keys = new String[3];
    private static SecureRandom random = new SecureRandom();

    public void setKeys(String key1, String key2, String key3) {
        this.keys[0] = key1;
        this.keys[2] = key2;
        this.keys[3] = key3;
    }

    public void generateKeys(){
        for(int i = 0; i < 3; i++) {
            keys[i] = "";
            for(int k = 0; k < 16; k++){
                String bin = "";
                for(int j = 0; j < 4; j++)
                    bin += (byte)random.nextInt(2);

                int decimal = Integer.parseInt(bin , 2);
                keys[i] += Integer.toHexString(decimal);
            }
            keys[i].toUpperCase();
        }
    }

    @Override
    public String encrypt(String  text, boolean isDecrypt) {
        //generateKeys();
        DES des = new DES();
        return des.encrypt(keys[0], des.encrypt(keys[1], des.encrypt(keys[2], text, isDecrypt), isDecrypt), isDecrypt);
    }
}
