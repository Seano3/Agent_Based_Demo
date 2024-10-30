import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

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
    private final double TIME_STEP = 0.01; 
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

        csvName = "Agent-" + AgentID + ".csv";

        try (FileWriter writer = new FileWriter(csvName)) {
            writer.write(AgentID + "," +
                    size + "," +
                    location.getX() + "," +
                    location.getY() + "," +
                    xVelocity + "," +
                    yVelocity);
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
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

    /**
     * <p>Call to update the location of the agent using its velocity and any acceleration it may have</p>
     */
    public void updateLocation() {
        xVelocity += xAcceleration;
        yVelocity += yAcceleration;

        xAcceleration = 0;
        yAcceleration = 0;

        double newX = location.getX() + (xVelocity*TIME_STEP);
        double newY = location.getY() + (yVelocity*TIME_STEP);

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
    public void checkCollisions(LinkedList<Agent> otherAgents, int frame, int width, int height, LinkedList<Exit> exits) {
        checkWalls(frame, width, height, exits);
        checkAgents(otherAgents, frame);
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



    private boolean inExit(LinkedList<Exit> exits) {
        for (Exit i : exits) {
            if(i.getAlignment() == Exit.alignment.VERTICAL) {
                double lowerBound = i.getLocation().getY();
                double upperBound = i.getLocation().getY() + i.getSize();
                if (location.getY() < upperBound - size / 2 &&
                        location.getY() > lowerBound - size / 2 &&
                        location.getX() < i.getLocation().getX() + size * 2 &&
                        location.getX() > i.getLocation().getX() - size * 2){
                    return true;
                }
            } else { // horizontal
                double lowerBound = i.getLocation().getX();
                double upperBound = i.getLocation().getX() + i.getSize();
                if (location.getX() < upperBound - size / 2 &&
                        location.getX() > lowerBound - size / 2 &&
                        location.getY() < i.getLocation().getY() + size * 2 &&
                        location.getY() > i.getLocation().getY() - size * 2){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <p>Checks the collision with each wall and changes the velocity accordingly</p>
     * @param frame The current frame
     * @param width width of the simulation
     * @param height height of the simulation
     */

    private void checkWalls(int frame, int width, int height, LinkedList<Exit> exits) {
        //If we are inside an exit, ignore the wall check
        if(inExit(exits)) {
            return;
        }
        //Checks the velocity for each wall and inverts the velocty accordingly 
        if (getLocation().getX() + getSize() > width - getSize()) { // Right wall

            Collision collision = new Collision(this.AgentID, -4, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.xVelocity = -Math.abs(xVelocity);
            }
        }
        if (getLocation().getX() - getSize() < 0 - getSize()) { // Left Wall

            Collision collision = new Collision(this.AgentID, -2, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.xVelocity = Math.abs(xVelocity);
            }
        }

        if (getLocation().getY() - getSize() < 0 - getSize()) { // Top Wall

            Collision collision = new Collision(this.AgentID, -3, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.yVelocity = Math.abs(yVelocity);
            }
        }

        if (getLocation().getY() + getSize() > height - this.sim.getRectHeight() - getSize()) { // Bottom wall

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
            if (!(that.location.equals(this.location))) { // insures that it does not check if the ball is colliding with
                // itself
                double dx = this.location.getX() - that.location.getX();
                double dy = this.location.getY() - that.location.getY();
                double distanceSquared = dx * dx + dy * dy;
                double dist = Math.sqrt(distanceSquared);
                //dist is calculated using pythagorean theorem
                double minDist = this.size + that.getSize();
            
                if (dist <= minDist) {
                    Collision collision = new Collision(this.AgentID, that.AgentID, frame);
                    if (checkPreviousAgentCollisions(collision, that)) { //Adds the collision to the previous ones if not already there
                        double dvx = that.xVelocity - this.xVelocity;
                        double dvy = that.yVelocity - this.yVelocity;

                        double dvdr = dx * dvx + dy * dvy;
                        double J = 2 * this.size * that.size * dvdr / ((this.size + that.size) * dist);
                        double Jx = J * dx / dist;
                        double Jy = J * dy / dist;

                        this.xVelocity += (Jx / this.size);
                        this.yVelocity += (Jy / this.size);

                        that.xVelocity -= (Jx / that.size);
                        that.yVelocity -= (Jy / that.size);

                        //Equations used here from equations.pdf in the teams 

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
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}

