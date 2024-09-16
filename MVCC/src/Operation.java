

public abstract class Operation<T> {
    protected StorageEngine<T> storageEngine;
    protected int timestamp;

    Operation(StorageEngine<T> storageEngine, int timestamp) {
        this.storageEngine = storageEngine;
        this.timestamp = timestamp;
    }

    public abstract void execute() throws InterruptedException;

}

class UpdateItem<T> extends Operation<T> {
    private final T update;
    private final int index;

    /**
     * Operation that the storage engine supports.
     * It is a wrapper class for the storage engine that allows to do basic database actions.
     *
     * @param storageEngine storage engine that class is assigned
     * @param timestamp     timestamp of the transaction
     * @param index         index of the item the action should be executed
     * @param value         new value of the selected item
     */
    public UpdateItem(StorageEngine<T> storageEngine, int timestamp, int index, T value) {
        super(storageEngine, timestamp);
        this.update = value;
        this.index = index;
    }

    /**
     * starts the action
     *
     * @throws InterruptedException semaphore might throw these exception
     */
    @Override
    public void execute() throws InterruptedException {
        storageEngine.updateItem(index, update, timestamp);
    }
}

class ReadItem<T> extends Operation<T> {
    private final int index;
    private T result;

    /**
     * Operation that the storage engine supports.
     * It is a wrapper class for the storage engine that allows to do basic database actions.
     *
     * @param storageEngine storage engine that class is assigned
     * @param timestamp     timestamp of the transaction
     * @param index         index of the item the action should be executed
     */
    public ReadItem(StorageEngine<T> storageEngine, int timestamp, int index) {
        super(storageEngine, timestamp);
        this.index = index;
    }

    /**
     * Returns the result of the operation
     * @return result
     */
    public T getResult() {
        return result;
    }

    /**
     * starts the action
     *
     * @throws InterruptedException semaphore might throw these exception
     */
    @Override
    public void execute() throws InterruptedException {
        result = storageEngine.readItem(index, timestamp);

    }

}








