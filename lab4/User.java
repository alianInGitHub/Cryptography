import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by anastasia on 11.12.16.
 */
public class User {
    private RSA crypto = new RSA(1024);
    private ArrayList<BigInteger> messages;
    private int count = 0;
    private BigInteger gotMessage;

    public void readMessages(String filepath) throws IOException {
        messages = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String num;
        while ((num = reader.readLine()) != null)
            messages.add(new BigInteger(num));
        reader.close();
    }

    public BigInteger sendMessage() {
        count++;
        return crypto.encrypt(messages.get(count - 1));
    }

    public int getMessagesAmount(){
        return messages.size();
    }

    public void getMessage(BigInteger m){
        gotMessage = crypto.decrypt(m);
    }

    public void setPublicKeys(BigInteger... k){
        crypto.setE(k[0]);
        crypto.setN(k[1]);
    }

    public BigInteger[] getPublicKeys(){
        BigInteger[] k = new BigInteger[2];
        k[0] = crypto.getE();
        k[1] = crypto.getN();
        return k;
    }

    public boolean assertEqualMessages(BigInteger... m){
        boolean[] used = new boolean[messages.size()];
        for(int i = 0; i < m.length; i++) {
            boolean t = false;
            for(int j = 0; j < messages.size(); j++) {
                if(!used[j] && m[i].equals(messages.get(i))) {
                    t = true;
                    break;
                }
            }
            if(!t)
                return false;
        }
        return true;
    }
}
