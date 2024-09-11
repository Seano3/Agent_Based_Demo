
import java.util.*;

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

    public Agent(int name, double size, double xCord, double yCord, double xVel, double yVel, Simulation sim) {
        AgentID = name;
        this.size = size;
        xVelocity = xVel;
        yVelocity = yVel;
        location = new Location(xCord, yCord);
        collisions = new LinkedList<Collision>();
        this.sim = sim;
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

        double newX = location.getX() + xVelocity;
        double newY = location.getY() + yVelocity;

        location.changePosition(newX, newY);
    }

    public void applyForce(double xForce, double yForce) {
        // f = m * a
        // a = f / m
        //Assumed to be in a collision so it subtracts the current force from the equation because it is assumed to be imparted onto the other object

        // System.out.println("Allplied " + (xForce + yForce));
        xAccelaration += xForce / this.size;
        yAcceraration += yForce / this.size;
    }

    public double getXForce() {
        // a = (v1 * v0) / t
        // f = m * a
        double acc = (0 - xVelocity) / 1;
        double force = size * acc;
        return force;
    }

    public double getYForce() {
        double acc = (0 - yVelocity) / 1;
        double force = size * acc;
        return force;
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
        System.out.println("Adding Collision " + collision.GetID());
        return true;
    }

    private boolean checkPreviousWallCollisions(Collision collision) {
        for (Collision i : collisions) {
            if (i.chesksum == collision.chesksum) {
                return false;
            }
        }

        collisions.add(collision);
        System.out.println("Adding Collision " + collision.GetID());
        return true;
    }

    public void updateCollisionsStorage() {
        for (int i = 0; i < collisions.size(); i++) {
            if (collisions.get(i).removeFrame()) {
                System.out.println("Removing Collision " + collisions.get(i).GetID());
                collisions.remove(i);
            }
        }
    }

    private void checkWalls(int frame, int width, int height) { //TODO: Set Collision cooldown on wall collisons then the ABS solution to clipping :)
        if (getLocation().getX() + getSize() > width - getSize()) { // Right wall
            double force = getXForce() * 2;
            double wallForce = force; //Negative
            Collision collision = new Collision(this.AgentID, -4, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.applyForce(wallForce, 0);
            }
        }

        if (getLocation().getY() - getSize() < 0 - getSize()) { // Top Wall
            double force = getYForce() * 2;
            double wallForce = force; //Positive
            Collision collision = new Collision(this.AgentID, -3, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.applyForce(0, wallForce);
            }
        }

        if (getLocation().getX() - getSize() < 0 - getSize()) { // Left Wall
            double force = getXForce() * 2;
            double wallForce = force; //Positive
            Collision collision = new Collision(this.AgentID, -2, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.applyForce(wallForce, 0);
            }
        }

        if (getLocation().getY() + getSize() > height - this.sim.getRectHeight() - getSize()) { // Bottem wall
            double force = getYForce() * 2;
            double wallForce = force; //Negative
            Collision collision = new Collision(this.AgentID, -1, frame);
            if (checkPreviousWallCollisions(collision)) {
                this.applyForce(0, wallForce);
            }
        }
    }

    private void checkAgents(LinkedList<Agent> otherAgents, int frame) {
        for (Agent i : otherAgents) {
            if (!(i.location.equals(this.location))) { // insures that it does not check if the ball is colliding with
                // itself
                double dx = this.location.getX() - i.location.getX();
                double dy = this.location.getY() - i.location.getY();
                double distanceSquared = dx * dx + dy * dy;
                double dist = Math.sqrt(distanceSquared);
                double minDist = this.size + i.getSize();

                if (dist <= minDist) {
                    Collision collision = new Collision(this.AgentID, i.AgentID, frame);
                    if (checkPreviousAgentCollisions(collision, i)) {
                        double xForceOnI = this.getXForce();
                        double xForceOnThis = i.getXForce();

                        double yForceOnI = this.getYForce();
                        double yForceOnThis = i.getYForce();

                        i.applyForce(xForceOnThis, yForceOnThis);
                        this.applyForce(xForceOnI, yForceOnI);

                        i.applyForce(-xForceOnI, -yForceOnI);
                        this.applyForce(-xForceOnThis, -yForceOnThis);

                        // i.applyForce(, 0);
                        // this.applyForce(, 0);

                        

                    }
                }
            }
        }
    }
}

// Ideas:

// Subtract the force it gives to others?
