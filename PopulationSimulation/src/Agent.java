
import java.util.*;

public class Agent {

    public int AgentID;
    private double size;
    public double xAccelaration;
    private double yAcceraration;
    private double xVelocity;
    private double yVelocity;
    private Location location;

    public Agent(int name, double size, double xCord, double yCord, double xVel, double yVel) {
        AgentID = name;
        this.size = size;
        xVelocity = xVel;
        yVelocity = yVel;
        location = new Location(xCord, yCord);
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
        //f = m * a
        //a = f / m

        System.out.println("Allplied " + xForce);

        xAccelaration = xForce / this.size;
        yAcceraration = yForce / this.size;
    }

    public double getXForce() {
        // a = (v1 * v0) / t
        //f = m * a
        double acc = (0 - xVelocity) / 1;
        double force = size * acc;
        return force;
    }

    public double getYForce() {
        double force = size / (0 - yVelocity);
        return force;
    }

    public void checkCollisions(LinkedList<Agent> otherAgents) {
        //Check Boundaries
        //Possible fix for boundery glitch is to split up each wall into its own if statement and manualy tell the ball which diection to move 
        if (getLocation().getX() + getSize() > 800 - getSize()) {
            double force = getXForce() * 2;
            System.out.println(force);
            this.applyForce(force, 0);
        }
        if (getLocation().getY() - getSize() < 0 - getSize() || getLocation().getY() + getSize() > 600 - getSize()) {
            setYVelocity(-getYVelocity());
        }

        if (getLocation().getX() - getSize() < 0 - getSize()) {
            double force = getXForce() * 2;
            System.out.println(force);
            this.applyForce(force, 0);
            //this.applyForce(force, 0);
        }

        //check Other Agents
        for (Agent i : otherAgents) {
            if (!i.location.equals(this.location)) { //insures that it does not check if the ball is colliding with itself
                double dx = location.getX() - i.location.getX();
                double dy = location.getY() - i.location.getY();
                double distanceSquared = dx * dx + dy * dy;

                if (distanceSquared <= (size + i.getSize()) * (size + i.getSize())) {
                    //2D Elastic & Inelastic Collisions For physics info

                    double tempX = this.xVelocity;
                    double tempY = this.yVelocity;

                    this.xVelocity = (this.xVelocity * (this.size - i.size) + 2 * i.size * i.xVelocity) / (this.size + i.size);
                    this.yVelocity = (this.yVelocity * (this.size - i.size) + 2 * i.size * i.yVelocity) / (this.size + i.size);

                    i.setXVelocity((i.xVelocity * (i.size - this.size) + 2 * this.size * tempX) / (i.size + this.size));
                    i.setYVelocity((i.yVelocity * (i.size - this.size) + 2 * this.size * tempY) / (i.size + this.size));

                }
            }
        }

    }

}
