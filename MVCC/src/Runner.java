import java.util.LinkedList;


public class Runner<T> extends Thread {
    private final Transactionqueue<T> queue;

    /**
     * Working thread, that operates with other runners at one queue of transactions
     *
     * @param queue TransactionQueue, that the runner is working on
     */
    public Runner(Transactionqueue<T> queue) {
        this.queue = queue;
    }

    /**
     * Run method of a thread. It polls the transaction from the TransactionQueue
     * and after that it, polls all operations from the polled Transaction
     * and executes them one after the other.
     * It also copies all of the Operations of a Transaction.
     * If any runtime exception occurs, then the Operations copy will be transferred in the undoTransaction()-method
     * and the transaction will be aborted and queued again in the TransactionQueue
     */
    @Override
    public void run() {
        try {
            Operation<T> operation;
            //using Transaction queue
            while (!queue.isEmpty()) {
                Transaction<T> t = queue.poll();
                //backup
                LinkedList<Operation<T>> operations = t.getOperations();
                boolean commitable = false;

                try {
                    //using operationsqueue
                    for (int i = 0; i < t.size(); i++) {
                        operation = t.dequeue();
                        operations.add(operation);
                        if (operation.getClass() == UpdateItem.class) commitable = true;
                        operation.execute();
                    }
                    if (commitable) t.commit();
                } catch (RuntimeException e) {
                    undoTransaction(t, operations);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If any runtime exception occurs, then the Operations copy will be transferred in the undoTransaction()-method
     * and the transaction will be aborted and queued again in the TransactionQueue
     *
     * @param t          transaction, that will be aborted
     * @param operations all operations of the aborted transaction
     */
    private void undoTransaction(Transaction<T> t, LinkedList<Operation<T>> operations) {
        try {
            sleep(10);
            t.undo();
            System.err.println("Not commited transaction is in the previous undo buffer. Abort transaction.");
            queue.add(new Transaction<T>(t.storageEngine, operations));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
