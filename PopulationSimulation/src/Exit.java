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

    public boolean inExit(Agent agent) {
        if(alignment == alignment.VERTICAL) {
            double lowerBound = location.getY();
            double upperBound = location.getY() + size;
            return agent.getLocation().getY() < upperBound + agent.getSize() &&
                    agent.getLocation().getY() > lowerBound - agent.getSize() &&
                    agent.getLocation().getX() < location.getX() + agent.getSize() &&
                    agent.getLocation().getX() > location.getX() - agent.getSize() * 2;
        } else { // horizontal
            double lowerBound = location.getX();
            double upperBound = location.getX() + size;
            return location.getX() < upperBound - agent.getSize() &&
                    agent.getLocation().getX() > lowerBound - agent.getSize() &&
                    agent.getLocation().getY() < location.getY() + agent.getSize() &&
                    agent.getLocation().getY() > location.getY() - agent.getSize() * 2;
        }
    }
}