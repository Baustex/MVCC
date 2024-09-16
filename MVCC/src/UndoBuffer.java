public class UndoBuffer <T>{
    private final T prevValue;
    private final UndoBuffer<T> prev;
    private Boolean commited;
    private int time;

    public UndoBuffer(int time, T prevValue, UndoBuffer<T> prev) {
        this.prevValue = prevValue;
        this.prev = prev;
        commited = false;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public T getPrevValue() {
        return prevValue;
    }

    public UndoBuffer<T> getPrev() {
        return prev;
    }

    public void setCommited() {
        commited = true;
    }

    public boolean isCommited() {
        return commited;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
