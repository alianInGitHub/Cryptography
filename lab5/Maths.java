/**
 * Created by anastasia on 25.12.16.
 */
public class Maths {
    public static float[] multiply(float[] a, float[] b, int size){
        float[] c = new float[a.length];
        float[][] newA = new float[size][size];
        float[][] newB = new float[size][size];

        for(int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                newA[i][j] = a[i + j * size];
                newB[i][j] = b[i + j * size];
            }
        }

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                float sum = 0;
                for(int k = 0; k < size; k++) {
                    sum += newA[k][i] * newB[j][k];
                }
                c[i + j * size] = sum;
            }
        }
        return c;
    }

    public static float[] transpose(float[] array, int size){
        float[] transposed = array.clone();
        float[][] newA = new float[size][size];
        for(int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                newA[i][j] = array[i + j * size];
            }
        }
        for(int i = 1; i < size; i++){
            for(int j = i - 1; j < size - 1; j++){
                transposed[i + j * size] = newA[j][i];
                transposed[i * size + j] = newA[i][j];
            }
        }
        return transposed;
    }

}
