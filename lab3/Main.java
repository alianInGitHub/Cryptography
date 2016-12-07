package DH;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Created by anastasia on 25.11.16.
 */
public class Main {
    private static DH user1 = new DH("Alice");
    private static DH user2 = new DH("Bob");

    public static void main(String[] args) throws IOException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        user1.setSource("data.txt");
        user2.setSource("data2.txt");
        //key exchange
        user1.generateKeys();
        user2.setG(user1.getG());
        user2.setP(user1.getP());
        user1.generateKey();
        user2.generateKey();
        user2.getPeerPublicKey(user1.getPublicKey());
        user1.getPeerPublicKey(user2.getPublicKey());

        //talking
        user2.getMessage(user1.sendMessage());
        user1.getMessage(user2.sendMessage());
    }
}
