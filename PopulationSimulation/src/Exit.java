
public class Exit {

    public enum alignment {
        VERTICAL, HORIZONTAL
    }; // will need to be reworked to support angled exits

    private int size;
    private alignment alignment;
    private Location location; //topmost or leftmost point --- use negative size to extend right or down
    public boolean buildingExit;

    public Exit(int size, Location location, Exit.alignment alignment, boolean buildingExit) {
        this.buildingExit = buildingExit;
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

    public boolean inExit(Agent agent) {
        if (alignment == alignment.VERTICAL) {
            double lowerBound = location.getY();
            double upperBound = location.getY() + size;
            return agent.getLocation().getY() < upperBound - agent.getSize() + 1
                    && agent.getLocation().getY() > lowerBound + agent.getSize() - 1
                    && agent.getLocation().getX() < location.getX() + agent.getSize() + 5
                    && agent.getLocation().getX() > location.getX() - agent.getSize() - 5;
        } else { // horizontal
            double leftBound = location.getX();
            double rightBound = location.getX() + size;
            return location.getX() > leftBound - agent.getSize() + 1
                    && agent.getLocation().getX() < rightBound + agent.getSize() - 1
                    && agent.getLocation().getY() < location.getY() + agent.getSize() + 5
                    && agent.getLocation().getY() > location.getY() - agent.getSize() - 5;
        }
    }
}
