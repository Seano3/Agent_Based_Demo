import java.util.*;

public class Agent {
    private int size;
    private double xVelocity;
    private double yVelocity;
    private Location location;

    public Agent(int size, double xCord, double yCord, double xVel, double yVel) {
        this.size = size;
        xVelocity = xVel;
        yVelocity = yVel;
        location = new Location(xCord, yCord);
    }

    public int getSize() {
        return size;
    }

    public double getXVelocity() {
        return xVelocity;
    }

    public double getYVelocity() {
        return yVelocity;
    }

    public void setYVelocity(double velocity){
        yVelocity = velocity;
    }

    public void setXVelocity(double velocity){
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

    public boolean checkCollisions(LinkedList<Agent> otherAgents) {
        for (Agent i : otherAgents) {
            if (!i.location.equals(this.location)) {
                double dx = location.getX() - i.location.getX();
                double dy = location.getY() - i.location.getY();
                double distanceSquared = dx * dx + dy * dy;

                if (distanceSquared <= (size + i.getSize()) * (size + i.getSize())){
                    return true;
                }
            }
        }
        return false;
    }

}
