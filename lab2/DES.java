/**
 * Created by anastasia on 04.10.16.
 */

public class DES implements CryptInterface{

    public static final int KEY_LEN = 56;
    public static final int BLOCK_LEN = 64;

    private byte[] key;

    private static byte[][] subkey = new byte[16][48];
    private static byte[] C = new byte[KEY_LEN / 2];
    private static byte[] D = new byte[KEY_LEN / 2];



    /*..............................Public methods..............................*/

    public DES(){}

    public void setKey(byte[] ikey){
        key = ikey;
    }

    @Override
    public String encrypt(String text, boolean isDecrypt) {

        String inputHexText = stringToHex(text);

        String result = "";

        for(int count = 0; count < inputHexText.length(); count += 16) {

            String inputHex = inputHexText.substring(count, count + 16);

            byte[] bits = convert(inputHex);

            byte[] newBits = primaryPermutation(bits);

            byte L[] = new byte[32];
            byte R[] = new byte[32];

            for (int i = 0; i < 32; i++) {
                L[i] = newBits[i];
                R[i] = newBits[32 + i];
            }

            int i;

            for (i = 0; i < 28; i++) {
                C[i] = key[CD[i] - 1];
            }
            for (; i < 56; i++) {
                D[i - 28] = bits[CD[i] - 1];
            }

            for (int n = 0; n < 16; n++) {
                byte newR[];
                if (isDecrypt) {
                    newR = fiestel(R, subkey[15 - n]);
                } else {
                    newR = fiestel(R, genRoundKey(n, key));
                }
                byte[] newL = xor(L, newR);
                L = R;
                R = newL;
            }

            byte[] output = new byte[64];
            for (int j = 0; j < 32; j++) {
                output[j] = R[j];
                output[32 + j] = L[j];
            }
            byte[] finalOutput = finalPermutation(output);

            String hex = new String();
            for (i = 0; i < 16; i++) {
                String bin = new String();
                for (int j = 0; j < 4; j++) {
                    bin += finalOutput[(4 * i) + j];
                }
                int decimal = Integer.parseInt(bin, 2);
                hex += Integer.toHexString(decimal);
            }
            result += hex;
        }
        result = hexToString(result);
        return result;

    }

    public String encrypt(String ikey, String text, boolean isDecrypt) {
        byte[] temp = convert(ikey.toUpperCase());
        setKey(temp);
        return encrypt(text, isDecrypt);
    }




    /*..............................Private methods..............................*/

    private static byte[] CD = {
            57, 49,	41, 33, 25, 17, 9, 	1, 	58, 50, 42, 34,	26, 18,
            10, 2, 	59, 51, 43, 35, 27, 19,	11, 3, 	60, 52,	44, 36,
            63, 55,	47, 39, 31, 23, 15, 7, 	62, 54, 46, 38,	30, 22,
            14, 6, 	61, 53, 45, 37, 29, 21,	13, 5, 	28, 20,	12, 4
    };

    private static byte[] CD2 = {
            14, 17, 11, 24, 1,  5, 3,  28, 15, 6,  21, 10,
            23, 19, 12, 4,  26, 8, 16, 7,  27, 20, 13, 2,
            41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
    };

    private static byte[] CDShift = {
            1, 	1, 	2, 	2, 	2, 	2, 	2, 	2, 	1, 	2, 	2, 	2, 	2, 	2, 	2, 	1
    };

    private static String stringToHex(String text){
        String hex = "";
        for(int i = 0; i < text.length(); i++){
            String s = Integer.toHexString((int)text.charAt(i));
            if(s.length() < 2)
                s = "0" + s;
            hex += s;//String.format("%04x", (int)text.charAt(i));
        }
        while (hex.length() % 16 != 0)
            hex += "00";
        return hex;
    }

    private static String hexToString(String hex){
        String text = "";
        for(int i = 0; i < hex.length(); i+=2){
            String s = hex.substring(i, i + 2);
            int n = Integer.parseInt(s, 16);
            text += Character.toString((char) n);
        }
        return text;
    }

    private byte[] convert(String input){
        byte[] bits = new byte[64];
        for(int i = 0; i < 16; i++){
            String s = Integer.toBinaryString(Integer.parseInt(input.charAt(i) + "", 16));
            while(s.length() < 4) {
                s = "0" + s;
            }
            for(int j = 0 ; j < 4 ; j++) {
                bits[(4*i)+j] = Byte.parseByte(s.charAt(j) + "");
            }
        }
        return bits;
    }


    private byte[] primaryPermutation(byte[] text) {
        byte[] IP = {
                58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4,
                62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
                57, 49, 41, 33, 25, 17, 9,  1, 59, 51, 43, 35, 27, 19, 11, 3,
                61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7
        };
        byte[] permutedText = new byte[64];
        for(byte i = 0; i < 64; i++){
            permutedText[i] = text[IP[i] - 1];
        }
        return permutedText;
    }


    //fiestel function
    private byte[] fiestel(byte[] R, byte[] roundKey) {
        byte[] expandedR = E(R);
        byte[] temp = xor(expandedR, roundKey);
        byte[] output = STransformation(temp);
        return output;
    }


    private static byte[] xor(byte[] a, byte[] b) {
        byte[] answer = new byte[a.length];
        for(int i=0 ; i < a.length ; i++) {
            answer[i] = (byte) (a[i]^b[i]);
        }
        return answer;
    }


    //E function transforms R from 32 bit to 48 bit
    private byte[] E(byte[] R){
        byte[] newR = new byte[48];
        byte[] E = {
                32, 1, 	2, 	3, 	4, 	5, 4, 	5, 	6, 	7, 	8, 	9,
                8, 	9, 	10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
                16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
                24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1
        };
        for(int i = 0; i < 48; i++){
            newR[i] = R[E[i] - 1];
        }
        return newR;
    }

    /**
     * S Transformation: Sj transforms 6 bit vector Bj into 4 bit vector
     * @param B 48 version
     * @return B 32 version
     */
    private byte[] STransformation(byte[] B){
        byte[] newB = new byte[BLOCK_LEN / 2];
        byte[] S = {
                //S[0]
                14, 4, 13,  1, 	2, 	15, 11, 8, 	3, 	10, 6, 	12, 5, 	9, 	0, 	7,
                0, 	15, 7, 	4, 	14, 2, 	13, 1, 	10, 6, 	12,	11, 9, 	5, 	3, 	8,
             	4, 	1, 	14, 8, 	13, 6, 	2, 	11, 15, 12, 9, 	7, 	3, 	10, 5, 	0,
         	    15, 12, 8, 	2, 	4, 	9, 	1, 	7, 	5, 	11, 3, 	14, 10, 0, 	6, 	13,

                //S[1]
                15, 1, 	8, 	14, 6, 	11, 3, 	4, 	9, 	7, 	2, 	13, 12, 0, 	5, 	10,
                3, 	13, 4, 	7, 	15, 2, 	8, 	14, 12, 0, 	1, 	10, 6, 	9, 	11,	5,
             	0, 	14, 7, 	11, 10, 4, 	13, 1, 	5, 	8, 	12, 6, 	9, 	3, 	2, 	15,
             	13, 8, 	10, 1, 	3, 	15, 4, 	2, 	11, 6, 	7, 	12, 0, 	5, 	14, 9,

                //S[2]
                10, 0, 	9, 	14, 6, 	3, 	15, 5, 	1, 	13, 12, 7, 	11, 4, 	2, 	8,
                13, 7, 	0, 	9, 	3, 	4, 	6, 	10, 2, 	8, 	5, 	14, 12, 11, 15, 1,
         	    13, 6, 	4, 	9, 	8, 	15, 3, 	0, 	11, 1, 	2, 	12, 5, 	10, 14, 7,
         	    1, 	10, 13, 0, 	6, 	9, 	8, 	7, 	4, 	15, 14, 3, 	11, 5, 	2, 	12,

                //S[3]
                7, 	13, 14, 3, 	0, 	6, 	9, 	10, 1, 	2, 	8, 	5, 	11, 12, 4, 	15,
                13, 8, 	11, 5, 	6, 	15, 0, 	3, 	4, 	7, 	2, 	12, 1, 	10, 14, 9,
             	10, 6, 	9, 	0, 	12, 11, 7, 	13, 15, 1, 	3, 	14, 5, 	2, 	8, 	4,
             	3, 	15, 0, 	6, 	10, 1, 	13, 8, 	9, 	4, 	5, 	11, 12, 7, 	2, 	14,

                //S[4]
                2, 	12, 4, 	1, 	7, 	10, 11, 6, 	8, 	5, 	3, 	15, 13, 0, 	14, 9,
                14, 11, 2, 	12, 4, 	7, 	13, 1, 	5, 	0, 	15, 10, 3, 	9, 	8, 	6,
             	4, 	2, 	1, 	11, 10, 13, 7, 	8, 	15, 9, 	12, 5, 	6, 	3, 	0, 	14,
             	11, 8, 	12, 7, 	1, 	14, 2, 	13, 6, 	15, 0, 	9, 	10, 4, 	5, 	3,

                //S[5]
                12, 1, 	10, 15, 9, 	2, 	6, 	8, 	0, 	13, 3, 	4, 	14, 7, 	5, 	11,
                10, 15, 4, 	2, 	7, 	12, 9, 	5, 	6, 	1, 	13, 14, 0, 	11, 3, 	8,
             	9, 	14,	15, 5, 	2, 	8, 	12, 3, 	7, 	0, 	4, 	10, 1, 	13, 11, 6,
            	4, 	3, 	2, 	12, 9, 	5, 	15, 10, 11, 14, 1, 	7, 	6, 	0, 	8, 	13,

                //S[6]
                4, 	11, 2, 	14, 15, 0, 	8, 	13, 3, 	12, 9, 	7, 	5, 	10, 6, 	1,
                13, 0, 	11, 7, 	4, 	9, 	1, 	10, 14, 3, 	5, 	12, 2, 	15, 8, 	6,
            	1, 	4, 	11, 13, 12, 3, 	7, 	14, 10, 15, 6, 	8, 	0, 	5, 	9, 	2,
            	6, 	11, 13, 8, 	1, 	4, 	10, 7, 	9, 	5, 	0, 	15, 14, 2, 	3, 	12,

                //S[7]
                13,	2, 	8, 	4, 	6, 	15, 11, 1, 	10, 9, 	3, 	14, 5, 	0, 	12, 7,
                1, 	15, 13, 8, 	10, 3, 	7, 	4, 	12, 5, 	6, 	11, 0, 	14, 9, 	2,
             	7, 	11, 4, 	1, 	9, 	12, 14, 2, 	0, 	6, 	10, 13, 15, 3, 	5, 	8,
             	2, 	1, 	14, 7, 	4, 	10, 8, 	13, 15, 12, 9, 	0, 	3, 	5, 	6, 	11
        };

        for(int i = 0; i < 8; i++){
            byte[] row = new byte[2];
            byte[] col = new byte[4];
            int j = i * 6;
            row[0] = B[0 + j];
            row[1] = B[5 + j];
            String sRow = row[0] + "" + row[1];
            String sCol = "";
            for(int k = 0; k < 4; k++) {
                col[k] = B[k + j + 1];
                sCol += col[k] + "";
            }
            int iRow = Integer.parseInt(sRow, 2);
            int iColumn = Integer.parseInt(sCol, 2);
            byte x = S[j + (iRow*16) + iColumn];

            String s = Integer.toBinaryString(x);
            while(s.length() < 4) {
                s = "0" + s;
            }

            for(int k = 0; k < 4; k++){
                newB[i * 4 + k] = (byte)Integer.parseInt(s.charAt(k) + "", 2);
            }
        }

        byte[] P = {
                16, 7, 	20, 21,	29, 12, 28, 17,
                1, 	15, 23, 26,	5, 	18, 31, 10,
                2, 	8, 	24, 14,	32, 27, 3, 	9,
                19, 13, 30, 6, 	22, 11, 4, 	25
        };
        for (int i = 0; i < BLOCK_LEN / 2; i++){
            newB[i] = B[P[i] - 1];
        }

        return newB;
    }


    private byte[] leftShift(byte[] bits, int n) {
        byte[] answer = new byte[bits.length];
        for(int i = 0; i < bits.length; i++){
            answer[i] = bits[i];
        }
        for(int i=0 ; i < n ; i++) {
            byte temp = answer[0];
            for(int j=0 ; j < bits.length-1 ; j++) {
                answer[j] = answer[j+1];
            }
            answer[bits.length-1] = temp;
        }
        return answer;
    }

    private byte[] genRoundKey(int round, byte[] key) {
        byte C1[];
        byte D1[];
        int rotationTimes = (int) CDShift[round];
        C1 = leftShift(C, rotationTimes);
        D1 = leftShift(D, rotationTimes);
        byte CnDn[] = new byte[56];
        for(int i = 0; i < 28; i++){
            C1[i] = CnDn[i];
            D1[i] = CnDn[28 + i];
        }
        byte Kn[] = new byte[48];
        for(int i=0 ; i < Kn.length ; i++) {
            Kn[i] = CnDn[CD2[i]-1];
        }
        subkey[round] = Kn;
        C = C1;
        D = D1;
        return Kn;
    }


    private byte[] finalPermutation(byte[] text){
        byte[] permutedText = new byte[64];
        byte[] FP = {
                40, 8, 	48, 16, 56, 24, 64, 32,	39, 7, 47, 15, 55, 23, 63, 31,
                38, 6, 	46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
                36, 4, 	44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
                34, 2, 	42, 10, 50, 18, 58, 26, 33, 1, 41, 9,  49, 17, 57, 25
        };
        for(int i = 0; i < 64; i++){
            permutedText[i] = text[FP[i] - 1];
        }
        return permutedText;
    }



    private void displayBits(byte[] bits) {
        // display byte array bits as a hexadecimal string.
        for(int i=0 ; i < bits.length ; i+=4) {
            String output = new String();
            for(int j=0 ; j < 4 ; j++) {
                output += bits[i+j];
            }
            System.out.print(Integer.toHexString(Integer.parseInt(output, 2)));
        }
        System.out.println();
    }

}
