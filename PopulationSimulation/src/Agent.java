import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Agent {

    public int AgentID;
    private double size;
    public double xAcceleration;
    private double yAcceleration;
    private double xVelocity;
    private double yVelocity;
    private Location location;
    private List<Collision> collisions;
    private Simulation sim;
    private String csvName;
    private String folder;
    private final double TIME_STEP = 0.01;
    private Color color;
    /**
     * This is the main class we use to create agents in the simulation
     * @param name The Integer ID of the agent
     * @param size The radius of the agent, also acts as the mass
     * @param xCord The x coordinate of the agent
     * @param yCord The y coordinate of the agent
     * @param xVel The x velocity of the agent
     * @param yVel The y velocity of the agent
     * @param sim Passthrough of the simulation the agent will be added to
     */
    public Agent(int name, double size, double xCord, double yCord, double xVel, double yVel, Simulation sim) {
        AgentID = name;
        this.size = size;
        xVelocity = xVel;
        yVelocity = yVel;
        location = new Location(xCord, yCord);
        collisions = new LinkedList<Collision>();
        this.sim = sim;
        this.color = new Color((int)(Math.random() * 0x1000000));

        csvName = "src/AgentCSVs/Agent-" + AgentID + ".csv";

        try (FileWriter writer = new FileWriter(csvName)) {
            writer.write(AgentID + "," +
                    size + "," +
                    location.getX() + "," +
                    location.getY() + "," +
                    xVelocity + "," +
                    yVelocity);
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

    public Color getColor() {
        return color;
    }


    /**
     * <p>Call to update the location of the agent using its velocity and any acceleration it may have</p>
     */
    public void updateLocation() {
        xVelocity += xAcceleration;
        yVelocity += yAcceleration;

        xAcceleration = 0;
        yAcceleration = 0;

        int[][] map = sim.vectorMap;

        int xMeter = (int) location.getY() / 10; //All of the X and Ys are messed up in this section but the location.get is what to go off
        int yMeter = (int) location.getX() / 10;

        if (xMeter > 0 && xMeter < map.length - 1 && yMeter > 0 && yMeter < map[0].length - 1) {
            int center = map[xMeter][yMeter];
            int north = map[xMeter][yMeter + 1];
            int south = map[xMeter][yMeter - 1];
            int east = map[xMeter + 1][yMeter];
            int west = map[xMeter - 1][yMeter];
            int northEast = map[xMeter - 1][yMeter + 1];
            int northWest = map[xMeter + 1][yMeter + 1];
            int southEast = map[xMeter - 1][yMeter - 1];
            int southWest = map[xMeter + 1][yMeter - 1];

            System.out.println("\n[" + northWest + "][" + north + "][" + northEast + "]");
            System.out.println("[" + east + "][" + center + "][" + west + "]");
            System.out.println("[" + southWest + "][" + south + "][" + southEast + "]");
            System.out.println("Y " + xMeter + " X " + yMeter);


            int smallest = Math.min(Math.min(Math.min(northEast, northWest), Math.min(southEast, southWest)), Math.min(Math.min(north, south), Math.min(east, west)));

            /*
            if (smallest == north) {
                System.out.println("Going North");
                yVelocity = -37.5;
                xVelocity = 0;
            } else if (smallest == south) {
                System.out.println("Going South");
                yVelocity = 37.5;
                xVelocity = 0;
            } else if (smallest == west) {
                System.out.println("Going West");
                yVelocity = 0;
                xVelocity = -37.5;
            } else if (smallest == east) {
                System.out.println("Going East");
                yVelocity = 0;
                xVelocity = 37.5;
            }else if (smallest == northEast) {
                System.out.println("Going North East");
                yVelocity = -18.75;
                xVelocity = 18.75;
            } else if (smallest == northWest) {
                System.out.println("Going North West");
                yVelocity = -18.75;
                xVelocity = -18.75;
            } else if (smallest == southEast) {
                System.out.println("Going South East");
                yVelocity = 18.75;
                xVelocity = 18.75;
            } else if (smallest == southWest) {
                System.out.println("Going South West");
                yVelocity = 18.75;
                xVelocity = -18.75;
            }
            */
        }

        double newX = location.getX() + (xVelocity * TIME_STEP);
        double newY = location.getY() + (yVelocity * TIME_STEP);

        location.changePosition(newX, newY);
        updateCSV();
    }

    /**
     * <p> Checks the location of the agent to see if it is colliding with anything and updates the velocity accordingly</p>
     * @param otherAgents List of all other agents in the simulation
     * @param frame the current frame
     * @param width width of the simulation
     * @param height height of the simulation
     */
    public void checkCollisions(LinkedList<Agent> otherAgents, int frame, int width, int height, LinkedList<Exit> exits, LinkedList<Obstacle> obstacles) {
        checkWalls(frame, width, height, exits);
        checkAgents(otherAgents, frame);
        checkObstacles(obstacles, exits);
    }

    /**
     *
     * @param collision the collision that needs to be checked
     * @param otherAgent the other agent involved in the collision
     * @return Returns a boolean to see of the collision has happened in the last 5 frames, false = already collided
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
     * @return returns false if the agent had already collided with the wall withing 5 frames
     */
    private boolean checkPreviousWallCollisions(Collision collision) {
        for (Collision i : collisions) {
            if (i.checksum == collision.checksum) {
                return false;
            }
        }

        collisions.add(collision);
        // System.out.println("Adding Collision " + collision.GetID());
        return true;
    }

    /**
     * <p> Checks to see if the collision should be removed from the buffer and does so if need be </p>
     */
    public void updateCollisionsStorage() {
        for (int i = 0; i < collisions.size(); i++) {
            if (collisions.get(i).removeFrame()) {
                //System.out.println("Removing Collision " + collisions.get(i).GetID());
                collisions.remove(i);
            }
        }
    }

    private Exit inExit(LinkedList<Exit> exits) {
        for (Exit i : exits) {
            if(i.inExit(this)) {
                return i;
            }
        }
        return null;
    }

    private void checkObstacles(LinkedList<Obstacle> obstacles, LinkedList<Exit> exits) {
        //left blank temporarily
    }

    /**
     * <p>Checks the collision with each wall and changes the velocity accordingly</p>
     * @param frame The current frame
     * @param width width of the simulation
     * @param height height of the simulation
     */
    private void checkWalls(int frame, int width, int height, LinkedList<Exit> exits) {
        //If we are inside an exit, modify wall checks
        Exit currentExit = inExit(exits);
        if (currentExit != null) {
            if(currentExit.getAlignment() == Exit.alignment.VERTICAL) {

                if (getLocation().getY() - getSize() < currentExit.getLocation().getY()) { // Top Wall

                    Collision collision = new Collision(this.AgentID, -3, frame);
                    if (checkPreviousWallCollisions(collision)) {
                        this.yVelocity = Math.abs(yVelocity);
                    }
                }

                if (getLocation().getY() + getSize() > currentExit.getLocation().getY() + currentExit.getSize()) { // Bottom wall

                    Collision collision = new Collision(this.AgentID, -1, frame);
                    if (checkPreviousWallCollisions(collision)) {
                        this.yVelocity = -Math.abs(yVelocity);
                    }
                }


            } else { //alignment.horizontal
                if (getLocation().getX() + getSize() > currentExit.getLocation().getX() + currentExit.getSize()) { // Right wall

                    Collision collision = new Collision(this.AgentID, -4, frame);
                    if (checkPreviousWallCollisions(collision)) {
                        this.xVelocity = -Math.abs(xVelocity);
                    }
                }
                if (getLocation().getX() - getSize() < currentExit.getLocation().getX()) { // Left Wall

                    Collision collision = new Collision(this.AgentID, -2, frame);
                    if (checkPreviousWallCollisions(collision)) {
                        this.xVelocity = Math.abs(xVelocity);
                    }
                }
            }
            return;
        }

        //Checks the velocity for each wall and inverts the velocity accordingly
        if (getLocation().getX() > width) { // Right wall

            Collision collision = new Collision(this.AgentID, -4, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.xVelocity = -Math.abs(xVelocity);
            }
        }
        if (getLocation().getX() < 0) { // Left Wall

            Collision collision = new Collision(this.AgentID, -2, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.xVelocity = Math.abs(xVelocity);
            }
        }

        if (getLocation().getY()  < 0) { // Top Wall

            Collision collision = new Collision(this.AgentID, -3, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.yVelocity = Math.abs(yVelocity);
            }
        }

        if (getLocation().getY() > height - this.sim.getRectHeight()) { // Bottom wall

            Collision collision = new Collision(this.AgentID, -1, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.yVelocity = -Math.abs(yVelocity);
            }
        }
    }

    /**
     * <p>Checks the collision with each other agent and changes the velocity accordingly</p>
     * @param otherAgents an array of all other agents in the simulation
     * @param frame the current frame
     */
    private void checkAgents(LinkedList<Agent> otherAgents, int frame) {
        for (Agent that : otherAgents) {
            if (!(that.location.equals(this.location))) {
                double dx = this.location.getX() - that.location.getX();
                double dy = this.location.getY() - that.location.getY();
                double distanceSquared = dx * dx + dy * dy;
                double dist = Math.sqrt(distanceSquared);
                double minDist = this.size + that.getSize();

                if (dist <= minDist) {
                    Collision collision = new Collision(this.AgentID, that.AgentID, frame);
                    if (checkPreviousAgentCollisions(collision, that)) {
                        double overlap = minDist - dist;
                        double pushForce = 0.1 * overlap;

                        double pushX = (dx / dist) * pushForce;
                        double pushY = (dy / dist) * pushForce;

                        this.xVelocity += pushX / this.size;
                        this.yVelocity += pushY / this.size;

                        that.xVelocity -= pushX / that.size;
                        that.yVelocity -= pushY / that.size;
                    }
                }
            }
        }
    }


    @Override
    public String toString() {
        return "Agent{" +
                "name=" + AgentID +
                ", size=" + size +
                ", xCoord=" + location.getX() +
                ", yCoord=" + location.getY() +
                ", xVel=" + xVelocity +
                ", yVel=" + yVelocity +
                '}';
    }

    private void updateCSV() {
        try (FileWriter writer = new FileWriter(csvName, true)) {
            writer.write(AgentID + "," +
                    size + "," +
                    location.getX() + "," +
                    location.getY() + "," +
                    xVelocity + "," +
                    yVelocity);
            writer.write("\n");
        } catch (IOException e) {
            //System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}

