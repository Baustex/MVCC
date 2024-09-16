import java.util.LinkedList;

public class Transaction<T> {
    StorageEngine<T> storageEngine;
    private final int timestamp;
    private LinkedList<Operation<T>> operations = new LinkedList<>();

    /**
     * Transaction is a list of operations that is performed on a storage engine
     *
     * @param storageEngine storage Engine that the Transaction is operating
     */
    public Transaction(StorageEngine<T> storageEngine) {
        timestamp = Timeslot.getTimeslotNumber();
        this.storageEngine = storageEngine;
    }

    /**
     * Transaction is a list of operations that is performed on a storage engine
     *
     * @param storageEngine storage Engine that the Transaction is operating
     * @param operations    list of all operations that this transaction should do
     */
    public Transaction(StorageEngine<T> storageEngine, LinkedList<Operation<T>> operations) {
        this.storageEngine = storageEngine;
        this.operations = operations;
        timestamp = Timeslot.getTimeslotNumber();

    }

    /**
     * adds an operation to the list
     *
     * @param op operation that will be added
     */
    public void queue(Operation<T> op) {
        operations.offer(op);
    }

    /**
     * removes the first operation from the OperationsList
     *
     * @return returns the next Operation
     */
    public Operation<T> dequeue() {
        return operations.poll();
    }

    /**
     * @return returns size of the OperationsList
     */
    public int size() {
        return operations.size();
    }

    /**
     * gets the timestamp, where the transaction was initialized
     *
     * @return returns the timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * wrapper-class for the storage engine commit()-method.
     * It takes a timestamp and calls the storage engine commitAtTimestamp()-method
     *
     * @throws InterruptedException semaphore could throw this error
     */
    public void commit() throws InterruptedException {
        int commitTime = Timeslot.getTimeslotNumber();
        storageEngine.commitAtTimestamp(timestamp, commitTime);

    }

    /**
     * wrapper-class for the storage engine undo()-method.
     * @throws InterruptedException semaphore could throw this error
     */
    public void undo() throws InterruptedException {
        storageEngine.undoAtTimestamp(timestamp);
    }

    /**
     *
     * @return a string with the transaction timestamp
     */
    @Override
    public String toString() {
        return "Transaction{" + "timestamp=" + timestamp + '}';
    }

    /**
     *
     * @returns all operations that are currently in the transaction
     */
    public LinkedList<Operation<T>> getOperations() {
        return operations;
    }

}
