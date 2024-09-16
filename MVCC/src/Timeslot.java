public class Timeslot {
    private static int timeslotNumber = 0;
    public synchronized static int getTimeslotNumber() {
        return timeslotNumber++;
    }
}
