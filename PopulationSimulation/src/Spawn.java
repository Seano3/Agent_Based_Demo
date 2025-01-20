public class Spawn {
    public enum alignment {VERTICAL, HORIZONTAL}; // will need to be reworked to support angled exits

    private int size;
    private Spawn.alignment alignment;
    private Location location; //topmost or leftmost point --- use negative size to extend right or down
    private int spawnRateInterval;
    private int lastSpawnFrame;

    public Spawn(int size, Location location, Spawn.alignment alignment, int spawnRateInterval) {
        this.size = size;
        this.location = location;
        this.alignment = alignment;
        this.spawnRateInterval = spawnRateInterval;
        this.lastSpawnFrame = 0;
    }

    public int getSize() {
        return size;
    }

    public int getSpawnRateInterval() {
        return spawnRateInterval;
    }

    public Spawn.alignment getAlignment() {
        return alignment;
    }

    public Location getLocation() {
        return location;
    }

    public int getLastSpawnFrame() {
        return lastSpawnFrame;
    }

    public void setLastSpawnFrame(int lastSpawnFrame) {
        this.lastSpawnFrame = lastSpawnFrame;
    }

    /*public boolean inExit(Agent agent) {
        if(alignment == alignment.VERTICAL) {
            double lowerBound = location.getY();
            double upperBound = location.getY() + size;
            return agent.getLocation().getY() < upperBound + agent.getSize() &&
                    agent.getLocation().getY() > lowerBound - agent.getSize() &&
                    agent.getLocation().getX() < location.getX() + agent.getSize()+5 &&
                    agent.getLocation().getX() > location.getX() - agent.getSize()-5;
        } else { // horizontal
            double lowerBound = location.getX();
            double upperBound = location.getX() + size;
            return location.getX() < upperBound - agent.getSize() &&
                    agent.getLocation().getX() > lowerBound - agent.getSize() &&
                    agent.getLocation().getY() < location.getY() + agent.getSize()+5 &&
                    agent.getLocation().getY() > location.getY() - agent.getSize()-5;
        }
    }*/

}
