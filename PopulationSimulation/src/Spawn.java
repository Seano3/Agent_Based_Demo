public class Spawn {
    public enum alignment {VERTICAL, HORIZONTAL}; // will need to be reworked to support angled exits
    public enum direction {LEFT, RIGHT};

    private int size;
    private Spawn.alignment alignment;
    private Spawn.direction direction;
    private Location location; //topmost or leftmost point --- use negative size to extend right or down
    private Location centerLocation;
    private int spawnNumber;
    private int spawnRateInterval;
    private int spawnDelay;
    private int lastSpawnFrame;
    private double spawnAgentSize;
    private double spawnAgentXVelocity;
    private double spawnAgentYVelocity;
    private boolean isActivelySpawning;

    /**
     * This is the class used to create spawn points for agents
     *
     * @param size Length of Spawn in pixels
     * @param location Topmost or leftmost point of Spawn
     * @param alignment Vertical or Horizontal, 0 for vertical, 1 for horizontal
     * @param spawnRateInterval Number of frames between spawns
     * @param spawnAgentSize Size of spawned agents
     * @param spawnAgentXVelocity X velocity of spawned agents
     * @param spawnAgentYVelocity Y velocity of spawned agents
     * @param direction Direction spawning takes place relative to center of spawn, 0 for left or up, 1 for right or down
     * @param spawnDelay Number of frames before first spawn
     * @param spawnNumber Number of agents to spawn, -1 for infinite
     */

    public Spawn(int size, Location location, Spawn.alignment alignment, int spawnRateInterval, double spawnAgentSize, double spawnAgentXVelocity, double spawnAgentYVelocity, Spawn.direction direction, int spawnDelay ,int spawnNumber) {
        this.size = size;
        this.location = location;
        this.alignment = alignment;
        this.spawnRateInterval = spawnRateInterval;
        this.lastSpawnFrame = 0;
        this.spawnAgentSize = spawnAgentSize;
        this.spawnAgentXVelocity = spawnAgentXVelocity;
        this.spawnAgentYVelocity = spawnAgentYVelocity;
        this.isActivelySpawning = true;
        this.direction = direction;
        this.spawnDelay = spawnDelay;
        this.spawnNumber = spawnNumber;
        if (alignment == alignment.VERTICAL) {
            this.centerLocation = new Location(location.getX(), location.getY() + (double) size /2);
        } else {
            this.centerLocation = new Location(location.getX() + (double) size /2, location.getY());
        }
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

    public Spawn.direction getDirection() { return direction; }

    public Location getLocation() {
        return location;
    }

    public Location getCenterLocation() { return centerLocation; }

    public int getLastSpawnFrame() {
        return lastSpawnFrame;
    }

    public double getSpawnAgentSize() {
        return spawnAgentSize;
    }

    public double getSpawnAgentXVelocity() {
        return spawnAgentXVelocity;
    }

    public double getSpawnAgentYVelocity() {
        return spawnAgentYVelocity;
    }

    public int getSpawnNumber() { return spawnNumber; }

    public int getSpawnDelay() { return spawnDelay; }

    public void setLastSpawnFrame(int lastSpawnFrame) {
        this.lastSpawnFrame = lastSpawnFrame;
    }

    public void setSpawnNumber(int spawnNumber) { this.spawnNumber = spawnNumber; }

    public boolean getIsActivelySpawning() {
        return isActivelySpawning;
    }

    public void setIsActivelySpawning(boolean isActivelySpawning) {
        this.isActivelySpawning = isActivelySpawning;
    }

    /**
     * Checks if an agent is in the spawn area
     *
     * @param agent Agent to check
     * @return True if agent is in spawn area and false if not
     */
    public boolean inSpawn(Agent agent) {
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
    }

}
