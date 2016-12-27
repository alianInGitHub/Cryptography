import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by anastasia on 22.12.16.
 */
public class Fridrich {
    public static float[] dzeta;
    private float[][] blocks;
    BufferedImage image;
    private int size;
    private int N;
    private int messageSize = 16;
    private int[][] message;

    public Fridrich(BufferedImage image){
        this.image = image;
        size = image.getWidth();
        N = size * size / (128 * 128);
        
        computeDzeta();
    }

    public BufferedImage parseImage(){
        if(size != 256) {
            System.out.print("Irrelevant data\n");
            return null;
        }
        
        createBlocks();
        
        generateMessage();
        
        transformBlocks();
        
        blocks = LowFrequency.parse(blocks, W);

        blocks = MiddleFrequency.parse(blocks, W);

        float[][] newBlueComponents = new float[size][size];
        for(int i = 0; i < N; i++){
            int t1 = 0;
            int t2 = (i % 2) * 128;
            if( i >= 2 )
                t1 = 128;
            for(int j = 0; j < 128; j++) {
                for (int k = 0; k < 128; k++) {
                    newBlueComponents[j + t1][k + t2] = blocks[i][k + j * 128]; //* 255.0f;
                }
            }
        }

        putBlueComponents(newBlueComponents);

        return image;
    }


    // instead of loading the signature from picture
    // I generate it randomly. If the signature is loaded,
    // it has to be black-and-white, all 0 replaced with -1
    private void generateMessage() {
        message = new int[messageSize][messageSize];
        Random random  = new Random();
        for(int i = 0; i < messageSize; i++){
            for(int j = 0; j < messageSize; j++){
                W[i][j] = random.nextInt(1);
                if(message[i][j] == 0){
                    message[i][j] = -1;
                }
            }
        }
    }

    private void createBlocks() {
        
        float[][] blueComponents = getBlueComponents(image);
        blocks = new float[N][128 * 128];
   
        for(int i = 0; i < N; i++) {
            int t = 0;
            if( i > 0 )
                t = ((i - 1) % 2) * 128;
            for(int j = 0; j < 128; j++){
                for (int k = 0; k < 128; k++)
                    blocks[i][k + j * 128] = blueComponents[j + t][k + (i % 2) * 128];
            }
        }
    }

    private void putBlueComponents(float[][] blueComponents ) {

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                image.setRGB(i, j, (int)(blueComponents[i][j]));
            }
        }
    }


    private float[][] getBlueComponents(BufferedImage image) {
        float[][] blueComponents = new float[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Color color = new Color(image.getRGB(i, j));
                blueComponents[i][j] = color.getBlue();
            }
        }
        return blueComponents;
    }

    private float mean(float[] array){
        float sum = 0;
        for(int i = 0;i < array.length; i++){
            sum += array[i];
        }
        return (float)(sum / array.length);
    }

    private float stdev(float[] array, float mean) {
        float[] temp = new float[array.length];
        for(int i = 0; i < array.length; i++){
            temp[i] = (float)Math.pow(array[i] - mean, 2);
        }
        return mean(temp);
    }

    private void transformBlocks() {
        for(int i = 0; i < N; i++) {
            float c = 1024 / size;
            float mean = mean(blocks[i]);
            float stdev = stdev(blocks[i], mean);
            for(int j = 0; j < blocks[i].length; j++) {
                blocks[i][j] = c * (blocks[i][j] - mean) / stdev;
            }
        }
    }

    public static float[] vertorise(int[][] input){
        int size = input.length;
        float[] res = new float[size * size];
        for(int i = 0; i < size; i++){
            for (int j = 0;j < size; j++){
                res[i + j] = input[i][j];
            }
        }
        return res;
    }

    private void computeDzeta(){
        int size = 128;
        dzeta = new float[size * size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == 0) {
                    dzeta[i + j * size] = (float) (1 / Math.sqrt(size));
                } else {
                    dzeta[i + j * size] = (float) (Math.sqrt(2) / Math.sqrt(size) * Math.cos(Math.PI * i * (2 * j + 1) / 8));
                }
            }
        }
    }
}
