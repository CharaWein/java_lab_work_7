package ZSeven;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadMatrixMax {

    public static final int SIZE = 100;
    public static final int THREADS = 5;

    public static void main(String[] args) {
        int[][] matrix = generateMatrix(SIZE);

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        AtomicInteger maxValue = new AtomicInteger(Integer.MIN_VALUE);

        int[][] subMatrices = splitMatrix(matrix, THREADS);

        for (int i = 0; i < THREADS; i++) {
            final int index = i;
            executor.execute(() -> {
                int max = findMax(subMatrices[index]);
                if (max > maxValue.get()) {
                    maxValue.set(max);
                }
            });
        }

        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Max value in matrix: " + maxValue);
    }

    public static int[][] generateMatrix(int size) {
        int[][] matrix = new int[size][size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                matrix[i][j] = random.nextInt(1000); // заполняем случайными числами до 1000
            }
        }
        return matrix;
    }

    public static int[][] splitMatrix(int[][] matrix, int threads) {
        int[][] subMatrices = new int[threads][];
        int size = matrix.length / threads;
        for (int i = 0; i < threads; i++) {
            int start = i * size;
            int end = (i == threads - 1) ? matrix.length : (i + 1) * size;
            subMatrices[i] = new int[end - start];
            System.arraycopy(matrix[i], start, subMatrices[i], 0, end - start);
        }
        return subMatrices;
    }

    public static int findMax(int[] array) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }
}