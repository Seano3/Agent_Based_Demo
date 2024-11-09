import java.awt.*;

public abstract class Obstacle {
    protected Location location;
    protected double xForce;
    protected double yForce;
    protected boolean fill;

    public Obstacle(Location location, double Force) {
        this.location = location;
        xForce = Force;
        yForce = Force;
    }
    public Obstacle(Location location, double xForce, double yForce) {
        this.location = location;
        this.xForce = xForce;
        this.yForce = yForce;
    }

    public Location getLocation() {
        return location;
    }

    public double getXForce() {
        return xForce;
    }

    public double getYForce() {
        return yForce;
    }

    public boolean toggleFill() {
        this.fill = !this.fill;
        return this.fill;
    }

    public abstract Agent checkCollision(Agent currentAgent, int frame);
    public abstract void paint(Graphics g);

}