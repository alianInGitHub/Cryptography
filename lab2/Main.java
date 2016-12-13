import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by anastasia on 25.11.16.
 */
public class Main {
    private static String generate(){
        Random random = new Random();
        String key = "";
        for(int k = 0; k < 16; k++){
            String bin = "";
            for(int j = 0; j < 4; j++)
                bin += (byte)random.nextInt(2);

            int decimal = Integer.parseInt(bin , 2);
            key += Integer.toHexString(decimal);
        }
        key.toUpperCase();
        return key;
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
        TDES d = new TDES();
        d.generateKeys();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Write text : \n");
        String text = scanner.next();
        String s = d.encrypt(text, false);
        System.out.print("encrypted : " + s.toUpperCase() + "\n");
        System.out.print("decrypted : " + d.encrypt(s, true) + "\n");

    }
}
