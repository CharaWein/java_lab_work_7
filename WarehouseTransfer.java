package ZSeven;

import java.util.concurrent.CountDownLatch;

public class WarehouseTransfer {
    private static final int NUM_LOADERS = 3;
    private static final int MAX_WEIGHT = 150;

    public static void main(String[] args) {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(NUM_LOADERS);

        Warehouse warehouse = new Warehouse(startSignal, doneSignal);

        for (int i = 0; i < NUM_LOADERS; i++) {
            new Thread(new Loader("Грузчик " + (i + 1), warehouse)).start();
        }

        startSignal.countDown();
        try {
            doneSignal.await();
            System.out.println("Остался один склад, нужно поднажать");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class Warehouse {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;
        private int currentWeight;

        public Warehouse(CountDownLatch startSignal, CountDownLatch doneSignal) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
            this.currentWeight = 0;
        }

        public synchronized void addWeight(int weight) {
            currentWeight += weight;
            if (currentWeight >= MAX_WEIGHT) {
                System.out.println("Этот склад заполнен, грузчики побежали нести груз на другой склад");
                currentWeight = 0;
                doneSignal.countDown();
            }
        }
    }

    static class Loader implements Runnable {
        private String name;
        private Warehouse warehouse;

        public Loader(String name, Warehouse warehouse) {
            this.name = name;
            this.warehouse = warehouse;
        }

        @Override
        public void run() {
            try {
                warehouse.startSignal.await();
                for (int i = 0; i < 13; i++) {
                    int weight = (int) (Math.random() * 30 + 1);
                    warehouse.addWeight(weight);
                    System.out.println(name + " Перенёс товар весом " + weight + " кг");
                    Thread.sleep((long) (Math.random() * 1000));
                }
                warehouse.doneSignal.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

