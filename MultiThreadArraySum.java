package ZSeven;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadArraySum {

    private static final int NUM_THREADS = 2; // Количество потоков
    private static final int ARRAY_SIZE = 1000000; // Размер массива

    private static int[] array = new int[ARRAY_SIZE];

    public static void main(String[] args) {
        // Заполнение массива случайными числами
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = (int) (Math.random() * 100);
        }

        // Создание ExecutorService с NUM_THREADS потоками
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // Разделение массива на равные части и отправка каждой части в отдельный поток
        int chunkSize = ARRAY_SIZE / NUM_THREADS;
        AtomicInteger sum = new AtomicInteger();
        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * chunkSize;
            int end = (i == NUM_THREADS - 1) ? ARRAY_SIZE : start + chunkSize;

            final int threadIndex = i;
            executor.execute(() -> {
                int localSum = 0;
                for (int j = start; j < end; j++) {
                    localSum += array[j];
                }
                System.out.println("Thread " + threadIndex + " sum: " + localSum);
                synchronized (MultiThreadArraySum.class) {
                    sum.addAndGet(localSum);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Получение общей суммы от всех потоков
        System.out.println("\nFinal sum: " + sum);
    }
}

