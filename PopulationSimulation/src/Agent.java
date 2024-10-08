import java.util.*;
import java.io.FileWriter;
import java.io.IOException;

public class Agent {

    public int AgentID;
    private double size;
    public double xAccelaration;
    private double yAcceraration;
    private double xVelocity;
    private double yVelocity;
    private Location location;
    private List<Collision> collisions;
    private Simulation sim;
    private String csvName;
    //testsdffdf
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

    public void updateLocation() {
        xVelocity += xAccelaration;
        yVelocity += yAcceraration;

        xAccelaration = 0;
        yAcceraration = 0;

        double newX = location.getX() + (xVelocity/4);
        double newY = location.getY() + (yVelocity/4);

        location.changePosition(newX, newY);
        updateCSV();
    }

    public void checkCollisions(LinkedList<Agent> otherAgents, int frame, int width, int height) {
        checkWalls(frame, width, height);
        checkAgents(otherAgents, frame);
    }

    private boolean checkPreviousAgentCollisions(Collision collision, Agent otherAgent) {
        for (Collision i : collisions) {
            if (i.chesksum == collision.chesksum) {
                // System.out.println("Already Collided");
                return false;
            }
        }

        collisions.add(collision);
        otherAgent.collisions.add(collision);
        // System.out.println("Adding Collision " + collision.GetID());
        return true;
    }

    private boolean checkPreviousWallCollisions(Collision collision) {
        for (Collision i : collisions) {
            if (i.chesksum == collision.chesksum) {
                return false;
            }
        }

        collisions.add(collision);
        // System.out.println("Adding Collision " + collision.GetID());
        return true;
    }

    public void updateCollisionsStorage() {
        for (int i = 0; i < collisions.size(); i++) {
            if (collisions.get(i).removeFrame()) {
                // System.out.println("Removing Collision " + collisions.get(i).GetID());
                collisions.remove(i);
            }
        }
    }

    private void checkWalls(int frame, int width, int height) { // TODO: Set Collision cooldown on wall collisons then
                                                                // the ABS solution to clipping :)
        if (getLocation().getX() + getSize() > width - getSize()) { // Right wall

            Collision collision = new Collision(this.AgentID, -4, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.xVelocity = -Math.abs(xVelocity);
            }
        }

        if (getLocation().getY() - getSize() < 0 - getSize()) { // Top Wall

            Collision collision = new Collision(this.AgentID, -3, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.yVelocity = Math.abs(yVelocity);
            }
        }

        if (getLocation().getX() - getSize() < 0 - getSize()) { // Left Wall

            Collision collision = new Collision(this.AgentID, -2, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.xVelocity = Math.abs(xVelocity);
            }
        }

        if (getLocation().getY() + getSize() > height - this.sim.getRectHeight() - getSize()) { // Bottem wall

            Collision collision = new Collision(this.AgentID, -1, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.yVelocity = -Math.abs(yVelocity);
            }
        }
    }

    private void checkAgents(LinkedList<Agent> otherAgents, int frame) {
        for (Agent that : otherAgents) {
            if (!(that.location.equals(this.location))) { // insures that it does not check if the ball is colliding with
                // itself
                double dx = this.location.getX() - that.location.getX();
                double dy = this.location.getY() - that.location.getY();
                double distanceSquared = dx * dx + dy * dy;
                double dist = Math.sqrt(distanceSquared);
                double minDist = this.size + that.getSize();

                if (dist <= minDist) {
                    Collision collision = new Collision(this.AgentID, that.AgentID, frame);
                    if (checkPreviousAgentCollisions(collision, that)) {
                        double dvx = that.xVelocity - this.xVelocity;
                        double dvy = that.yVelocity - this.yVelocity;

                        double dvdr = dx * dvx + dy * dvy;
                        double J = 2 * this.size * that.size * dvdr / ((this.size + that.size) * minDist);
                        double Jx = J * dx / minDist;
                        double Jy = J * dy / minDist;

                        this.xVelocity += Jx / this.size;
                        this.yVelocity += Jy / this.size;

                        that.xVelocity -= Jx / that.size;
                        that.yVelocity -= Jy / that.size;

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

// Ideas:

// Subtract the force it gives to others?
