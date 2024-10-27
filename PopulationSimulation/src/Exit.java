public class Exit {
    public enum alignment {VERTICAL, HORIZONTAL}; // will need to be reworked to support angled exits

    private int size;
    private alignment alignment;
    private Location location; //topmost or leftmost point --- use negative size to extend right or down

    public Exit(int size, Location location, alignment alignment) {
        this.size = size;
        this.location = location;
        this.alignment = alignment;
    }

    public int getSize() {
        return size;
    }

    public alignment getAlignment() {
        return alignment;
    }

    public Location getLocation() {
        return location;
    }
}
