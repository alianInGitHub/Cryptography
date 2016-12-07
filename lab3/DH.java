package DH;

import javax.crypto.*;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.util.Random;

/**
 * Created by anastasia on 25.11.16.
 */

public class DH {
    private static Random random = new Random();
    private static KeyPair a;
    public static BigInteger p, g;
    //private BigInteger K;
    private byte[] K;
    private String name;
    private String filename;

    public DH(String name){
        this.name = name;
    }

    public void setSource(String filename){
        this.filename = filename;
    }

    public static void generateKeys() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        do {
            p = BigInteger.probablePrime(512, random);
            g = p.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE);
        } while (!g.isProbablePrime(10));
        System.out.print(1);

        g = BigInteger.probablePrime(512, random);
        System.out.print(p+ "\n"+ g + "\n" );
    }

    public static void generateKey() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        //a = new BigInteger(1024, random);
        DHParameterSpec dhParams = new DHParameterSpec(p, g);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
        SecureRandom s = new SecureRandom();
        keyPairGenerator.initialize(dhParams, s);
        a = keyPairGenerator.generateKeyPair();
    }


    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getG() {
        return g;
    }

    public void setG(BigInteger g) {
        this.g = g;
    }

    public PublicKey getPublicKey(){
        //return g.modPow(a, p);
        return a.getPublic();
    }

    public void getPeerPublicKey(PublicKey B) throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException, NoSuchPaddingException {
        /*K = B.modPow(a, p);
        d.setKey(K.toByteArray());*/
        KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
        keyAgreement.init(a.getPrivate());
        keyAgreement.doPhase(B, true);
        K = keyAgreement.generateSecret();
        System.out.print(K.length + "\n");
        MessageDigest hash = MessageDigest.getInstance("SHA1");
        System.out.print(new String(hash.digest(K)) + "\n");
    }

    public String sendMessage() throws IOException, NoSuchProviderException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String res = "";
        String s;
        while ((s = reader.readLine()) != null){
            res += s;
        }
        System.out.print("[" + name + "] sent message : " + res + "\n");
        Cipher ecipher = Cipher.getInstance("RC4");
        SecretKey key = new SecretKeySpec(K, 0, K.length, "RC4");
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] text = res.getBytes("UTF8");
        return new sun.misc.BASE64Encoder().encode(ecipher.doFinal(text));
        //return new String("");
        //return (new DES(K.toByteArray())).encrypt(res);
    }

    public void getMessage(String text) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException {
        Cipher decipher = Cipher.getInstance("RC4");
        SecretKey key = new SecretKeySpec(K, 0, K.length, "RC4");
        decipher.init(Cipher.DECRYPT_MODE, key);
        byte[] utf8 = new sun.misc.BASE64Decoder().decodeBuffer(text);
        utf8 = decipher.doFinal(utf8);
        System.out.print("[" + name + "] got message : " + new String(utf8, "UTF8") + "\n");
    }
}
