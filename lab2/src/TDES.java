import java.util.Random;
import java.util.Vector;

/**
 * Created by anastasia on 05.10.16.
 */

public class TDES implements CryptInterface{
    private byte[] key1, key2, key3;

    public void setKeys(byte[] key1, byte[] key2, byte[] key3) {
        this.key1 = key1;
        this.key2 = key2;
        this.key2 = key3;
    }

    public void generateKeys(){
        key1 = new byte[56];
        key2 = new byte[56];
        key3 = new byte[56];
        Random r = new Random(47);
        for(int i = 0; i < 56; i++)
        {
            key1[i] = (byte)r.nextInt(2);
            key2[i] = (byte)r.nextInt(2);
            key3[i] = (byte)r.nextInt(2);
        }
    }

    @Override
    public byte[] encrypt(byte[] text){
        //generateKeys();
        DES des = new DES();
        return des.encryptWithSetKey(key3, des.encryptWithSetKey(key2, des.encryptWithSetKey(key1, text)));
    }
}
