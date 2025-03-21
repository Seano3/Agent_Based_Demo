
import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Agent {

    public int AgentID;
    private double size;
    private double xVelocity;
    private double yVelocity;
    private Location location;
    private List<Collision> collisions;
    private Simulation sim;
    private String csvName;
    private final double TIME_STEP = 0.01;
    private Color color;
    private int timeSinceLastWallCollision = 50;
    private boolean inSpawn;
    private boolean firstSpawnBoundCheck;
    private int choiceMove;
    private double targetVelocity;
    private int Divisor = 1;
    private int xDirection;
    private int yDirection;
    public boolean inExit;
    public boolean inAnyExit;
    private int scanningAgent = -1;
    private int blockedTimer;

    /**
     * This is the main class we use to create agents in the simulation
     *
     * @param name The Integer ID of the agent
     * @param size The radius of the agent, also acts as the mass
     * @param xCord The x coordinate of the agent
     * @param yCord The y coordinate of the agent
     * @param xVel The x velocity of the agent
     * @param yVel The y velocity of the agent
     * @param sim Pass through of the simulation the agent will be added to
     */
    public Agent(int name, double size, double xCord, double yCord, double xVel, double yVel, Simulation sim) {
        blockedTimer = 0;
        AgentID = name;
        inExit = false;
        choiceMove = 0;
        this.size = size;
        targetVelocity = size * 1.25 / 0.255; // replicate human walking speed based on average person
        xVelocity = xVel;
        yVelocity = yVel;
        location = new Location(xCord, yCord);
        collisions = new LinkedList<Collision>();
        this.sim = sim;
        this.color = new Color((int) (Math.random() * 0x1000000));

        csvName = "src/AgentCSVs/Agent-" + AgentID + ".csv";

        try (FileWriter writer = new FileWriter(csvName)) {
            writer.write(AgentID + ","
                    + size + ","
                    + location.getX() + ","
                    + location.getY() + ","
                    + xVelocity + ","
                    + yVelocity);
            writer.write("\n");
        } catch (IOException e) {
            //System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    public double getSize() {
        return size;
    }

    public double getXVelocity() {
        return xVelocity;
    }

    public double getYVelocity() {
        return yVelocity;
    }

    public void setYVelocity(double velocity) {
        yVelocity = velocity;
    }

    public void setXVelocity(double velocity) {
        xVelocity = velocity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Color getColor() {
        return color;
    }

    public void setInSpawn(boolean inSpawn) {
        this.inSpawn = inSpawn;
    }

    public boolean getInSpawn() {
        return inSpawn;
    }

    public void setFirstSpawnBoundCheck(boolean firstSpawnBoundCheck) {
        this.firstSpawnBoundCheck = firstSpawnBoundCheck;
    }



    /**
     * <p> Updates the velocity of the agent based on the vector map </p>
     *
     * @param scaleOveride boolean to override the scaling of the velocity
     */
    private void updateVelocity(boolean scaleOveride) {
        int choice = choiceMove;
        //Change to find the choice lowest that is both unoccupied
        int yMeter = (int) location.getY();
        int xMeter = (int) location.getX();
        int[][] map = sim.vectorMap;

        if (!inSpawn){
        if (yMeter > 0 && yMeter < map.length - 1 && xMeter > 0 && xMeter < map[0].length - 1) {
            int center = map[yMeter][xMeter];
            int north = map[yMeter - 1][xMeter];
            int south = map[yMeter + 1][xMeter];
            int east = map[yMeter][xMeter + 1];
            int west = map[yMeter][xMeter - 1];
            int northEast = map[yMeter - 1][xMeter + 1];
            int northWest = map[yMeter - 1][xMeter - 1];
            int southEast = map[yMeter + 1][xMeter + 1];
            int southWest = map[yMeter + 1][xMeter - 1];

            // System.out.println("\nAgent ID: " + AgentID);
            // System.out.println("[" + northWest + "][" + north + "][" + northEast + "]");
            // System.out.println("[" + east + "][" + center + "][" + west + "]");
            // System.out.println("[" + southWest + "][" + south + "][" + southEast + "]");
            // System.out.println("X " + xMeter + " Y " + yMeter);
            //final int Divisor = 4;
            List<Integer> values = Arrays.asList(center, north, south, east, west, northEast, northWest, southEast, southWest);
            Collections.sort(values);

            int smallest = values.get(choice);

            if (smallest == 0) {
                //updateLocation();
                //System.out.println("Agent " + AgentID + " is at door");
                return;
            }

            if (smallest == Integer.MAX_VALUE) {
                choiceMove = 8;
                return;
            }

            if (values.contains(Integer.MAX_VALUE)) {
                Divisor = 1;
            }

            if (Math.abs(xVelocity) + Math.abs(yVelocity) < targetVelocity / 5) {
                Divisor = 1;
            }

            double transferedVelx = ((Math.abs(xVelocity) / Divisor));
            double transferedVely = ((Math.abs(yVelocity) / Divisor));

            double transferedVel = (transferedVelx + transferedVely);

            xVelocity = reduceMagnitude(xVelocity, transferedVelx);
            //System.out.println("X: " + xVelocity + " change by " + xVelocity / DIVISOR);
            yVelocity = reduceMagnitude(yVelocity, transferedVely);
            //System.out.println("Y: " + yVelocity + " change by " + yVelocity / DIVISOR);

            if (timeSinceLastWallCollision > 5) {
                if (smallest == north) {
                    if (AgentID == scanningAgent) {
                        System.out.println("Going North");
                    }
                    yVelocity -= transferedVel;
                    xDirection = -1;
                    yDirection = 0;
                } else if (smallest == south) {
                    if (AgentID == scanningAgent) {
                        System.out.println("Going South");
                    }
                    yVelocity += transferedVel;
                    yDirection = 1;
                    xDirection = 0;
                } else if (smallest == west) {
                    if (AgentID == scanningAgent) {
                        System.out.println("Going West");
                    }
                    xVelocity -= transferedVel;
                    xDirection = -1;
                    yDirection = 0;
                } else if (smallest == east) {
                    if (AgentID == scanningAgent) {
                        System.out.println("Going East");
                    }
                    xVelocity += transferedVel;
                    xDirection = 1;
                    yDirection = 0;
                } else if (smallest == northEast) {
                    if (AgentID == scanningAgent) {
                        System.out.println("Going North East");
                    }
                    yVelocity -= transferedVel / 2;
                    xVelocity += transferedVel / 2;
                    yDirection = -1;
                    xDirection = 1;
                } else if (smallest == northWest) {
                    if (AgentID == scanningAgent) {
                        System.out.println("Going North West");
                    }
                    yVelocity -= transferedVel / 2;
                    xVelocity -= transferedVel / 2;
                    xDirection = -1;
                    yDirection = -1;
                } else if (smallest == southEast) {
                    if (AgentID == scanningAgent) {
                        System.out.println("Going South East");
                    }
                    yVelocity += transferedVel / 2;
                    xVelocity += transferedVel / 2;
                    xDirection = 1;
                    yDirection = 1;
                } else if (smallest == southWest) {
                    if (AgentID == scanningAgent) {
                        System.out.println("Going South West");
                    }
                    yVelocity += transferedVel / 2;
                    xVelocity -= transferedVel / 2;
                    xDirection = -1;
                    yDirection = 1;
                }
            }
        }

            double currentMagnitude = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity); // Set your desired magnitude
            double currentDirection = Math.atan2(yVelocity, xVelocity);
            double scaleFactor = targetVelocity / currentMagnitude;

            scaleXVelocity(scaleFactor, scaleOveride);
            scaleYVelocity(scaleFactor, scaleOveride);

            if (AgentID == scanningAgent) {
                System.out.println("X: " + xVelocity + " Y: " + yVelocity);
                System.out.println("Scale Factor " + scaleFactor + " Target Velocity " + targetVelocity + " Current Magnitude " + currentMagnitude);
            }

            if (xVelocity + yVelocity < targetVelocity / Divisor) {
                Divisor = 1;
            }
        }

    }

    /**
     * <p> Scales the X velocity of the agent based on the scale factor and the current velocity</p>
     *
     * @param scaleFactor The scale factor to multiply the velocity by
     * @param scaleOveride boolean to override the scaling of the velocity
     */
    private void scaleXVelocity(double scaleFactor, boolean scaleOveride) {
        if (scaleOveride || isSameSine(xDirection, xVelocity) || (xVelocity < 1 && xVelocity > -1) || inExit || inSpawn) {
            if (AgentID == scanningAgent) {
                System.out.println("Scaling X");
            }
            xVelocity *= scaleFactor;
            return;
        }
    }

    /**
     * <p> Scales the Y velocity of the agent based on the scale factor and the current velocity</p>
     *
     * @param scaleFactor The scale factor to multiply the velocity by
     * @param scaleOveride boolean to override the scaling of the velocity
     */
    private void scaleYVelocity(double scaleFactor, boolean scaleOveride) {
        if (scaleOveride || isSameSine(yDirection, yVelocity) || (yVelocity < 0.1 && yVelocity > -0.1) || inExit || inSpawn) {
            if (AgentID == scanningAgent) {
                System.out.println("Scaling Y");
            }
            yVelocity *= scaleFactor;
            return;
        }
    }

    /**
     * <p>
     * Call to update the location of the agent using its velocity and any
     * acceleration it may have</p>
     */
    public void updateLocation() {
        timeSinceLastWallCollision++;
        double newX = location.getX() + (xVelocity * TIME_STEP);
        double newY = location.getY() + (yVelocity * TIME_STEP);
        location.changePosition(newX, newY);
    }

    /**
     * <p>
     * Checks the location of the agent to see if it is colliding with anything
     * and updates the velocity accordingly</p>
     *
     * @param otherAgents List of all other agents in the simulation
     * @param frame the current frame
     * @param exits list of all exits in the simulation
     * @param obstacles list of all obstacles in the simulation
     */
    public void updateAgent(LinkedList< Agent> otherAgents, int frame, LinkedList<
        Exit> exits, LinkedList< Obstacle> obstacles) {
        if (collisions.isEmpty()) {
            Divisor = 8;
        }
        if (!this.firstSpawnBoundCheck) {
            checkObstacles(obstacles, frame, exits);
            choiceMove = 0;
        }
        checkAgents(otherAgents);
        updateCSV();
    }

    /**
     *
     * @param collision the collision that needs to be checked
     * @param otherAgent the other agent involved in the collision
     * @return Returns a boolean to see of the collision has happened in the
     * last 5 frames, false = already collided
     */
    private boolean checkPreviousAgentCollisions(Collision collision, Agent otherAgent) {
        for (Collision i : collisions) {
            if (i.GetID() == otherAgent.AgentID + AgentID) {
                //System.out.println("Already Collided");
                return false;
            }
        }

        collisions.add(collision);
        otherAgent.collisions.add(collision);
        // System.out.println("Adding Collision " + collision.GetID());
        return true;
    }

    /**
     *
     * @param collision the collision with the wall
     * @return returns false if the agent had already collided with the wall
     * withing 5 frames
     */
    public boolean checkOtherObstacleCollisions(Collision collision) {
        for (Collision i : collisions) {
            if (i.checksum == collision.checksum) {
                return false;
            }
        }
        Divisor = 1;
        collisions.add(collision);
        // System.out.println("Adding Collision " + collision.GetID());
        return true;
    }

    /**
     * <p>
     * Checks to see if the collision should be removed from the buffer and does
     * so if need be </p>
     */
    public void updateCollisionsStorage() {
        for (int i = 0; i < collisions.size(); i++) {
            if (collisions.get(i).removeFrame()) {
                //System.out.println("Removing Collision " + collisions.get(i).GetID());
                collisions.remove(i);
                Divisor = 1;
            }
        }
    }

    /**
     * <p> Checks if the agent is in an exit </p>
     *
     * @param exits list of all exits in the simulation
     * @return the exit the agent is in
     */
    public Exit inExit(LinkedList< Exit> exits) {
        for (Exit i : exits) {
            if (i.inExit(this)) {
                return i;
            }
        }
        return null;
    }

    /**
     * <p> Checks if the agent is in a spawn </p>
     *
     * @param spawns list of all exits in the simulation
     * @return the spawn the agent is in
     */
    private Spawn inSpawn(LinkedList< Spawn> spawns) {
        for (Spawn i : spawns) {
            if (i.inSpawn(this)) {
                return i;
            }
        }
        return null;
    }

    /**
     * <p>
     * Checks the collision with each Obstacle and changes the velocity
     * accordingly</p>
     *
     * @param obstacles list of all obstacles in the simulation
     * @param frame The current frame
     * @param exits list of all exits in the simulation
     */
    private void checkObstacles(LinkedList< Obstacle> obstacles, int frame, LinkedList<Exit> exits) {
        //If we are inside an exit, modify collision checks
        Exit currentExit = inExit(exits);
        if (currentExit != null) {
            if (currentExit.getAlignment() == Exit.alignment.VERTICAL) {

                if (getLocation().getY() - getSize() < currentExit.getLocation().getY()) { // Top of exit

                    Collision collision = new Collision(this.AgentID, -3, frame);
                    if (checkOtherObstacleCollisions(collision)) {
                        this.yVelocity = Math.abs(yVelocity);
                    }
                }

                if (getLocation().getY() + getSize() > currentExit.getLocation().getY() + currentExit.getSize()) { // Bottom of exit

                    Collision collision = new Collision(this.AgentID, -1, frame);
                    if (checkOtherObstacleCollisions(collision)) {
                        this.yVelocity = -Math.abs(yVelocity);
                    }
                }

                if (getLocation().getY() < currentExit.getLocation().getY() + size - 0.5) { // can't fit through upper part of exit
                    Collision collision = new Collision(this.AgentID, -5, frame);
                    if (checkOtherObstacleCollisions(collision)) {
                        this.xVelocity = -Math.abs(xVelocity);
                    }
                }

                if (getLocation().getY() > currentExit.getLocation().getY() + currentExit.getSize() - size + 0.5) { // can't fit through lower part of exit
                    Collision collision = new Collision(this.AgentID, -6, frame);
                    if (checkOtherObstacleCollisions(collision)) {
                        this.xVelocity = -Math.abs(xVelocity);
                    }
                }

            } else { //alignment.horizontal
                if (getLocation().getX() + getSize() > currentExit.getLocation().getX() + currentExit.getSize()) { // Right of exit

                    Collision collision = new Collision(this.AgentID, -4, frame);
                    if (checkOtherObstacleCollisions(collision)) {
                        this.xVelocity = -Math.abs(xVelocity);
                    }
                }
                if (getLocation().getX() - getSize() < currentExit.getLocation().getX()) { // Left of exit

                    Collision collision = new Collision(this.AgentID, -2, frame);
                    if (checkOtherObstacleCollisions(collision)) {
                        this.xVelocity = Math.abs(xVelocity);
                    }
                }

                if (getLocation().getX() < currentExit.getLocation().getX() + size - 0.5) { // can't fit through left part of exit
                    Collision collision = new Collision(this.AgentID, -7, frame);
                    if (checkOtherObstacleCollisions(collision)) {
                        this.yVelocity = -Math.abs(yVelocity);
                    }
                }

                if (getLocation().getX() > currentExit.getLocation().getX() + currentExit.getSize() - size + 0.5) { // can't fit through right part of exit
                    Collision collision = new Collision(this.AgentID, -8, frame);
                    if (checkOtherObstacleCollisions(collision)) {
                        this.yVelocity = -Math.abs(yVelocity);
                    }
                }
            }
            return;
        }

        for (Obstacle i : obstacles) {
            i.checkCollision(this, frame);
        }

    }

    public void deactivateVectorMap() {
        System.out.println("BONK!");
        timeSinceLastWallCollision = 0;
    }

    /**
     * <p>
     * Checks the collision with each other agent and changes the velocity
     * accordingly</p>
     *
     * @param otherAgents an array of all other agents in the simulation
     */
    private void checkAgents(LinkedList< Agent> otherAgents) {
        //Divisor = 1;
        updateVelocity(false);
        double newX = location.getX() + (xVelocity * TIME_STEP);
        double newY = location.getY() + (yVelocity * TIME_STEP);
        //Divisor = 8;
        //updateVelocity(false);

        for (Agent i : otherAgents) {
            // System.out.println(i.AgentID != this.AgentID);
            if (i.AgentID != this.AgentID) {
                double dx = i.getLocation().getX() - newX;
                double dy = i.getLocation().getY() - newY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < ((i.getSize() + this.getSize()))) {
                    if (choiceMove <= 7) {
                        Divisor = 1;
                        choiceMove++;
                        if (AgentID == scanningAgent) {
                            System.out.println(this.AgentID + " chose move " + choiceMove + " blocked with agent" + i.AgentID);
                        }
                        checkAgents(otherAgents);
                        return;
                    } else {
                        if (AgentID == scanningAgent) {
                            System.out.println(AgentID + " is blocked");
                        }
                        blockedTimer++;
                        if (blockedTimer > 10) {
                            choiceMove = 0;
                            blockedTimer = 0;
                            //scaleBuffer = (int) this.size / 4;
                        } else {
                            choiceMove = 0;
                        }
                        return;
                    }
                }
            }
        }
        //System.out.println("Moving " + AgentID);

        updateLocation();
    }

    @Override
    public String toString() {
        return "Agent{"
                + "name=" + AgentID
                + ", size=" + size
                + ", xCoord=" + location.getX()
                + ", yCoord=" + location.getY()
                + ", xVel=" + xVelocity
                + ", yVel=" + yVelocity
                + '}';
    }

    /**
     * <p> Writes the agent data to a CSV file </p>
     */
    private void updateCSV() {
        try (FileWriter writer = new FileWriter(csvName, true)) {
            writer.write(AgentID + ","
                    + size + ","
                    + location.getX() + ","
                    + location.getY() + ","
                    + xVelocity + ","
                    + yVelocity);
            writer.write("\n");
        } catch (IOException e) {
            //System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    /**
     * <p> Reduces the magnitude of a value by a certain amount </p>
     *
     * @param value Value to be reduced
     * @param reduction Amount to reduce the value by
     * @return The value after the reduction
     */
    public double reduceMagnitude(double value, double reduction) {
        double valueModified = value;
        if (value > 0) {
            valueModified -= reduction;
            // System.out.println("Reducing " + value + " by " + reduction + " to " + valueModified);
        } else if (value < 0) {
            valueModified += reduction;
            // System.out.println("Increasing " + value + " by " + reduction + " to " + valueModified);
        }
        return valueModified;
    }

    /**
     * <p> Checks if two values have the same sign </p>
     *
     * @param value1 First value to compare
     * @param value2 Second value to compare
     * @return True if the values have the same sign, false if not
     */
    private boolean isSameSine(double value1, double value2) {
        return (value1 > 0) == (value2 > 0);
    }
}
