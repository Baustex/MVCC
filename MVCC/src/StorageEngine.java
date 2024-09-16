import java.util.ArrayList;
import java.util.List;

public class StorageEngine<T> {
    private final ArrayList<Item<T>> items;

    /**
     * Storage engine that manages a container of Items
     */
    public StorageEngine() {
        items = new ArrayList<>();
    }

    /**
     * NOT a thread safe method.
     * It appends items to the storage engine
     * @param value value of the item, which will be added
     * @param index index of the item, which will be added
     */
    public void addItem(T value, int index) {
        Item<T> item = new Item<>(value);
        items.add(index, item);
    }
    /**
     * NOT a thread safe method.
     * It appends items to the storage engine
     * @param value value of the item, which will be added
     */
    public void addItem(T value) {
        Item<T> item = new Item<>(value);
        items.add(item);
    }

    /**
     * deletes an item
     * @param index index where the item will be deleted
     */
    public void deleteItem(int index) {
        items.remove(index);

    }

    /**
     * calls the write()-function on the selected item
     * @param index index of the selected item
     * @param value new value that should be updated
     * @param timestamp timestamp of the transaction
     * @throws InterruptedException exception is thrown, when there is a not committed transaction
     */
    // can not update the same value multiple times
    public void updateItem(int index, T value, int timestamp) throws InterruptedException {
        items.get(index).write(value, timestamp);

    }

    /**
     * gives a List of values at a given timestamp
     * @param timestamp timestamp of the storage engine state
     * @return List of all items that are now at the storage engine
     */
    public List<T> readAllItems(int timestamp) {
        ArrayList<T> result = new ArrayList<>();
        for (Item<T> item : items) {
            result.add(item.read(timestamp));
        }
        return result;
    }

    /**
     * commits all items with the same timestamp or in other words all items that are updates in one transaction.
     * @param timestamp timestamp of the transaction
     * @param commitTime new timestamp for the committed item
     * @throws InterruptedException semaphore might throw this
     */
    public void commitAtTimestamp(int timestamp, int commitTime) throws InterruptedException {
        for (Item<T> item : items) {
            if (item.getPrev() != null && !item.getPrev().isCommited() && item.getPrev().getTime() == timestamp){
                item.commit(commitTime);}
        }
    }
    /**
     * undoes all items with the same timestamp or in other words all items that are updates in one transaction.
     * @param timestamp timestamp of the transaction
     * @throws InterruptedException semaphore might throw this
     */
    public void undoAtTimestamp(int timestamp) throws InterruptedException {
        for (Item<T> item : items) {
            if(item.getPrev() != null && !item.getPrev().isCommited() && item.getPrev().getTime() == timestamp)
                item.undo();
        }
    }

    /**
     * returns the value of one specific item at a specific time
     * @param index index of the searched item
     * @param timestamp time for the state of the item
     * @return value of the item
     */
    public T readItem(int index, int timestamp) {
        return items.get(index).read(timestamp);
    }

}