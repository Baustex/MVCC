
public class Parallelisationtest {
    public static void main(String[] args) {
        try {

            // int sizeEngine = 10000;
            int sizeEngine = 10000;
            int transactionCounter = 10000;


            test(sizeEngine,transactionCounter, 1);
            test(sizeEngine,transactionCounter, 2);
            test(sizeEngine,transactionCounter, 3);
            test(sizeEngine,transactionCounter, 4);
            test(sizeEngine,transactionCounter, 5);
            test(sizeEngine,transactionCounter, 6);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test(int engineSize, int transactionCounter, int numberThreads) throws InterruptedException {
        Transactionqueue<Integer> queue=createQueue(engineSize, transactionCounter);
        Runner[] runners = new Runner[numberThreads];
        for (int i = 0; i < numberThreads; i++) {
            runners[i] = new Runner<>(queue);
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < numberThreads; i++) {
            runners[i].start();
        }
        for (int i = 0; i < numberThreads; i++) {
            runners[i].join();
        }
        long end = System.currentTimeMillis();
        System.out.printf("%d Thread(s) needed %d ms to run the test\n", numberThreads, end - start);
    }

    public static Transactionqueue<Integer> createQueue(int engineSize, int transactionCounter) {
        StorageEngine<Integer> engine = new StorageEngine<>();
        for (int i = 0; i < engineSize; i++) {
            engine.addItem(i + 1, i);
        }

        //init Transactionqueue
        Transactionqueue<Integer> queue = new Transactionqueue<>();
        Transaction<Integer> transaction;


        //init Transactions
        for (int i = 0; i < transactionCounter; i++) {
            transaction = new Transaction<>(engine);

            Operation<Integer> op;
            if (i % 100 == 0) {
                op = new UpdateItem<>(engine, transaction.getTimestamp(), i, i + 100);
                transaction.queue(op);

            } else {
                for (int j = 0; j < engineSize; j++) {
                    op = new ReadItem<>(engine, transaction.getTimestamp(), j);
                    transaction.queue(op);
                }

            }
            queue.add(transaction);

        }
        return queue;
    }
}