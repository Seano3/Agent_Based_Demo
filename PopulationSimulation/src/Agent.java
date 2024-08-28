import java.util.*;

public class Agent {
    private double size;
    private double xVelocity;
    private double yVelocity;
    private Location location;

    public Agent(double size, double xCord, double yCord, double xVel, double yVel) {
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
        double newX = location.getX() + xVelocity;
        double newY = location.getY() + yVelocity;

        location.changePosition(newX, newY);
    }

    public void checkCollisions(LinkedList<Agent> otherAgents) {
        for (Agent i : otherAgents) {
            if (!i.location.equals(this.location)) {
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
