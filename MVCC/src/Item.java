import java.util.concurrent.Semaphore;

public class Item<T> {
    private final Semaphore semaphore;
    private UndoBuffer<T> prev;
    private T value;

    /**
     * Thread safe container class for MVCC
     *
     * @param value value of the container item
     */
    public Item(T value) {
        semaphore = new Semaphore(1);
        prev = null;
        this.value = value;
    }

    /**
     * Returns the current value of the item.
     *
     * @return current value
     */
    public T getValue() {
        return value;
    }

    /**
     * Returns the previous UndoBuffer, that holds the previous values and is linked to previous UndoBuffers of the item.
     * Keep in mind that the UndoBuffer is not committed yet.
     *
     * @return UndoBuffer with previous value of the item
     */
    public UndoBuffer<T> getPrev() {
        return prev;
    }

    /**
     * Write is a thread safe method that overwrites the current value and create a new UndoBuffer with the current value.
     *
     * @param newValue  value of the new item
     * @param timestamp timestamp that the transaction has, that initiated the update
     * @throws InterruptedException Exception is thrown, when there is a not committed transaction
     */
    public void write(T newValue, int timestamp) throws InterruptedException {
        semaphore.acquire();
        try {
            //Not Committed transaction
            if (prev != null && !prev.isCommited()) {
                throw new RuntimeException("Not commited transaction is in the previous undo buffer. Abort transaction.");
            }
            UndoBuffer<T> newBuffer = new UndoBuffer<>(timestamp, this.value, prev);
            this.value = newValue;
            prev = newBuffer;
        } finally {
            semaphore.release();
        }
    }

    /**
     * Read is a thread safe method that gives the stable (committed) value at a specific @param timestamp
     * @param timestamp timestamp for the specific state of the value
     * @return value of the item at the given time
     */
    public T read(int timestamp) {
        UndoBuffer<T> last = null;
        UndoBuffer<T> current = prev;
        //go to next UndoBuffer if prev is not committed
        if (prev != null && !prev.isCommited()) {
            current = prev.getPrev();
            //if current == null return previous value
            if (current == null) {
                return prev.getPrevValue();
            }
        }
        //Case 1: no committed UndoBuffer
        if (current == null) {
            return value;
        }
        while (current != null) {
            if (current.getTime() < timestamp) {
                //edge case last == null
                if (last == null) {
                    return value;
                }
                //Case 2: current UndoBuffer time is larger than timestamp
                return last.getPrevValue();
            }
            if (current.getPrev() == null)
                return current.getPrevValue();
            last = current;
            current = current.getPrev();

        }
        //Case 3: last UndoBuffer time is smaller than timestamp
        return last.getPrevValue();
    }

    /**
     * Undoes the current UndoBuffer if it is not committed.
     */
    public void undo() throws InterruptedException {
        semaphore.acquire();
        try {
            //only if prev is not committed
            if (!prev.isCommited()) {
                value = this.prev.getPrevValue();
                prev = prev.getPrev();
            }
        } finally {
            semaphore.release();

        }

    }

    /**
     * Commits the previous UndoBuffer. It sets committed-flag to true
     * @param timestamp timestamp where the transaction is finalized and the value should commit
     * @throws InterruptedException semaphore could throw this error
     */
    public void commit(int timestamp) throws InterruptedException {
        semaphore.acquire();
        try {
            prev.setCommited();
            prev.setTime(timestamp);
        } finally {
            semaphore.release();
        }
    }
}
