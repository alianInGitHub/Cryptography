import java.util.Random;

/**
 * Created by anastasia on 22.12.16.
 */
public class MiddleFrequency {

    private static int size;
    private static int N;
    private static int messageSize;
    private int d = 12;


    private class BlockProcessor extends Thread{
        private float[] block;
        private float[] spr;

        public BlockProcessor(float[] block, float[] spr){
            this.block = block.clone();
            this.spr = spr;
        }

        @Override
        public void run() {
            int markL = 111;
            int markH = 147;
            float gama = 1;
            int j = 1;
            float[] omega = block;
            for (int v = 0; v < size; v++) {
                if(j >= N)
                    break;
                for(int u = 0; u < size; u++) {
                    if((markL < u + v) && (u + v < markH)) {
                        omega[v + u * size] = omega[v + u * size] + gama * spr[j];
                        j++;
                    }
                    if(j >=N)
                        break;
                }
            }
            block = Maths.multiply(Maths.multiply(Fridrich.dzeta, omega, size), Maths.transpose(Fridrich.dzeta, size), size);
        }
    }


    private float[] vrand(int s){
        int N = (int)Math.pow(2, d);
        float[] Rdec = new float[N];
        char[] Rbin = new char[d];
        byte[] m = {1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1};
        for (int i = 0; i < N - 1; i++){
            if(i == 1){
                Rdec[i] = s;
                Rbin = DecimalToBinary((int)Rdec[i], d);
            } else {
                int bit = 0;
                for(int j = 0; j < d; j++) {
                    if(m[j] == 1)
                        bit = (byte) ((Rbin[j] + bit) % 2);
                }
                char[] R = Rbin.clone();
                for (int j = 0; j < d; j++) {
                    if(j >= 1) {
                        Rbin[j] = R[j - 1];
                    } else {
                        Rbin[j] = String.valueOf(bit).charAt(0);
                    }
                }
                Rdec[i] = BinaryToDecimal(Rbin);
            }
        }
        Rdec[N - 1] = N;
        return Rdec;
    }
    
    private int BinaryToDecimal(byte[] binary) {
        int number = 0;
        for (int i = 0; i < binary.length; i++) {
            if(binary[i] == 1) {
                number += Math.pow(2, binary.length - 1 - i);
            }
        }
        return number;
    }

    private byte[] DecimalToBinary(int number, int length) {
        byte[] result = new byte[length];

        if (number <= 1) {
            result[length - 1] = (byte)number;
            return result;
        }

        int i = length - 1;
        int temp = number;
        while ((temp > 1) && (i >= 0)) {
            result[i] = (byte) (temp % 2);
            temp = temp >> 1;
            i--;
        }

        if(i >= 0) {
            result[i] = (byte) temp;
        }
        return result;
    }

    private int runif(int a, int b, int c){
        Random random = new Random();
        boolean t = random.nextBoolean();
        int res = b;
        if (t) {
            res = a + random.nextInt(b - a);
        } else {
            res = b + random.nextInt(c - b);
        }
        return res;
    }

    public float[][] gauss(){
        int N = (int)Math.pow(2, d);
        float[][] e = new float[messageSize][N];
        int start = 74;
        Random rand = new Random();
        for (int i = 0; i < messageSize; i++){
            float[] ei = vrand(start);
            for(int j = 0; j < N; j++){
                ei[j] = ei[j] / ei[N -  1];
            }
            e[i] = ei;
        }
        return e;
    }

    private float[] submatrix(float[] e, int a, int b) {
        int n = (b - a);
        float[] result = new float[n];
        int counter = 0;
        for(int i = a; i <= b; i++) {
            result[counter] = e[i];
            counter++;
        }
        return result;
    }

    public float[][] nu(float[] messageVect) {
        float[][] nu = new float[messageSize][0];
        float[][] e = gauss();
         for(int i = 0; i < messageSize; i++){
             if(messageVect[i] == 1) {
                 int m = runif(1, 1, 7);
                 nu[i] = submatrix(e[i], m, messageSize - m - 1);
             } else {
                 int m = runif(1, 10, 16);
                 nu[i] = submatrix(e[i], m, messageSize - m - 1);
             }
         }
         return nu;
    }

    private float[][] spr(float[] message){
        float[][] nu = nu(message);
        float[][] spr = new float[4][0];
        for(int b = 0; b < N; b++) {
            int to = messageSize / 4 * b;
            int from = messageSize / 4 * (b - 1) + 1;
            spr[b] = new float[from - to];
            float temp = messageSize / 8;
            for(int j = from; j < to; j++) {
                int sum = 0;
                for(int i = 0; i < nu[i].length; i++)
                    sum += nu[j][i];
                spr[b][j - from] = (float) ((sum - temp) / Math.sqrt(temp / 6));
            }
        }
        return spr;
    }

    private float[][] omega(float[][] input, float[] message) {
        float[][] omega = new float[input.length][size];
        float[][] spr = spr(message);
        BlockProcessor[] threads = new BlockProcessor[input.length];
        for (int i = 0; i < input.length; i++) {
            threads[i] = new BlockProcessor(input[i], spr[i]);
            threads[i].start();
        }

        for (int i = 0; i < input.length; i++) {
            try {
                threads[i].join();
                omega[i] = threads[i].block;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return omega;
    }

    private float[][] insertMessage(float[][] input) {
        float[][] res = input.clone();

        float min = 1000;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <  res[i].length; j++) {
                if (Math.abs(res[i][j]) < min)
                    min = res[i][j];
            }
        }

        float max = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <  res[i].length; j++) {
                res[i][j] -= min;
                if (Math.abs(res[i][j]) > max)
                    max = res[i][j];
            }
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <  res[i].length; j++) {
                res[i][j] = res[i][j] / max * 255.0f;
            }
        }
        return res;
    }


    public static float[][] parse(float[][] input, int[][] message) {
        float[] messageVect = Fridrich.vertorise(message);
        
        MiddleFrequency method = new MiddleFrequency();
        
        messageSize = messageVect.length;
        size = (int)Math.sqrt(input[0].length);
        N = input.length;
        input = method.omega(input, messageVect);
        return method.insertMessage(input);
    }
}
