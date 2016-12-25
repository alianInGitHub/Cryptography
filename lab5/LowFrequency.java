import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by anastasia on 22.12.16.
 */
public class LowFrequency {
    private static int size;
    private static int messageSize;

    private class BlockProcessor extends Thread {
        private float[] block;

        BlockProcessor(float[] block) {
            this.block = block;
        }

        private float[] DKP() {
            float[] f = Fridrich.dzeta;

            float[] omega = Maths.multiply(Maths.multiply(f, block, size), Maths.transpose(f, size), size);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("data" + Thread.currentThread().getName() + ".txt"));
                for(int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        writer.write(f[i + j * size] + "\t");
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return omega;
        }

        @Override
        public void run() {
            block = DKP();
        }

        public float[] getBlock() {
            return block;
        }
    }

    public List<Float> progress(float alpha) {
        List<Float> list = new ArrayList<>();
        for(int i = 0; i < 300; i++){
            float t = (float) Math.pow((1 + alpha) / (1 - alpha), i - 1);
            list.add(t);
            if(t > 256)
                break;
        }
        return list;
    }

    public int ind(float t) {
        List<Float> tau = progress(0.01f);
        for(int i = 1; i < tau.size() - 1; i++) {
            if(t < tau.get(0)){
                return  1;
            }
            if((tau.get(i) < t) && (t <= tau.get(i + 1))) {
                return (int) Math.pow(-1, i);
            }
        }
        return 1;
    }

    public float[][] DKP(float[][] array){
        int N = array.length;
        float[][] omega = new float[N][array[0].length];

        BlockProcessor[] threads = new BlockProcessor[N];
        for (int i = 0; i < N; i++){
            threads[i] = new BlockProcessor(array[i]);
            threads[i].start();
        }

        for (int i = 0; i < N; i++) {
            try {
                threads[i].join();
                omega[i] = threads[i].getBlock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return omega;
    }


    public int findPoint(int l){
        int h = l;
        float bitSum = 0;
        while (bitSum < messageSize){
            h++;
            bitSum = 0;
            for(int i = l - 1; i < h - 3; i++){
                bitSum += 1 + i;
            }
        }
        return h;
    }

    public float[] parse(float[] omega){
        int l = 40;
        float[] newOmega = omega.clone();
        int j = 1;
        for(int i = 0; i < size; i++){
            if(j > messageSize)
                break;
            for(int k = 0; k < size; k++){
                int h = findPoint(l);
                if((l < i + k) && (i + k < h)){
                    float d = Math.abs(newOmega[i + j]);
                    int temp;
                    if(newOmega[i + k] >= 0)
                        temp = 1;
                    else
                        temp = -1;
                    int ind = 1;
                    List<Float> tau = progress(0.01f);
                    for(int p = 1; p < tau.size() - 1; p++) {
                        if(d < tau.get(0)){
                            ind = 1;
                        }
                        if((tau.get(p) < d) && (d <= tau.get(p + 1))) {
                            ind = (int) Math.pow(-1, i);
                        }
                    }

                    float t1 = 0, t2 = 0;

                    if(ind == messageSize){
                        for(int p = 0; p < tau.size(); p++){
                            if(d < tau.get(p)){
                                if(p == 0){
                                    t1 = 0;
                                    t2 = tau.get(p);
                                } else {
                                    t1 = tau.get(i - 1);
                                    t2 = tau.get(i);
                                    break;
                                }
                            }
                        }
                        if((d - t1 < 0.01) || (d - t2 < 0.01)) {
                            newOmega[i + k] = temp * rnorm(1, (t1 - t2) / 2, (t1 + t2) / 13);
                        }

                    } else {
                        for(int p = 0; p < tau.size(); p++){
                            if(d < tau.get(p)){
                                if(p <= 2){
                                    t1 = tau.get(p);
                                    t2 = tau.get(p + 1);
                                } else {
                                    t1 = tau.get(p - 2);
                                    t2 = tau.get(p - 1);
                                    break;
                                }
                            }
                        }

                        newOmega[i + k] = temp * rnorm(1, (t1 - t2) / 2, (t1 + t2) / 13);
                    }
                    j++;
                }
                if(j > messageSize)
                    break;
            }
        }
        return newOmega;
    }

    private int rnorm(int x, float miu, float sigma) {
        return (int)Math.round(Math.exp(-Math.pow(x - miu, 2) / (2 * Math.pow(sigma, 2))) / (Math.pow(2 * Math.PI, 0.5) * sigma));
    }

    public static float[][]  parse(float[][] input, int[][] message) {
        LowFrequency lowFrequency = new LowFrequency();
        size = 128;
        float[] W = Fridrich.vertorise(message);
        messageSize = W.length;
        float[][] omega = lowFrequency.DKP(input);
        float[][] result = new float[input.length][0];
        for(int i = 0; i < input.length; i++) {
            result[i] = lowFrequency.parse(omega[i]);
        }
        return result;
    }
}
