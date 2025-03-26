
public class Exit {

    public enum alignment {
        VERTICAL, HORIZONTAL
    }; // will need to be reworked to support angled exits

    private int size;
    private alignment alignment;
    private Location location; //topmost or leftmost point --- use negative size to extend right or down
    public boolean buildingExit;

    /**
     * This is the class used to create exit points for agents
     *
     * @param size Length of Exit in pixels
     * @param location Topmost or leftmost point of Exit
     * @param alignment Vertical or Horizontal, 0 for vertical, 1 for horizontal
     * @param buildingExit Whether the exit is a building exit or not
     */
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

    /**
     * Checks if an agent is in the exit area
     *
     * @param agent Agent to check
     * @return True if agent is in exit area and false if not
     */
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
            return agent.getLocation().getX() > leftBound - agent.getSize() + 1
                    && agent.getLocation().getX() < rightBound + agent.getSize() - 1
                    && agent.getLocation().getY() < location.getY() + agent.getSize() + 5
                    && agent.getLocation().getY() > location.getY() - agent.getSize() - 5;
        }
    }
}
