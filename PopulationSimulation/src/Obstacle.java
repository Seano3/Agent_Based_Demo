import java.awt.*;

public abstract class Obstacle {
    protected Location location;
    protected double xForce;
    protected double yForce;
    protected boolean fill;

    /**
     * Initialize an obstacle
     * @param location - The location of the top-leftmost point of the obstacle. May be outside the actual obstacle for strange shapes i.e circle
     * @param Force - The repulsive force exerted on agents by the obstacle. Not implemented
     */
    public Obstacle(Location location, double Force) {
        this.location = location;
        xForce = Force;
        yForce = Force;
    }

    /**
     * Initialize an Obstacle with seperate forces for each axis
     * @param location - The location of the top-leftmost point of the obstacle. May be outside the actual obstacle for strange shapes i.e circle
     * @param xForce - The repulsive horizontal force exerted on agents by the obstacle. Not implemented
     * @param yForce - The repulsive vertical force exerted on agents by the obstacle. Not implemented
     */
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

    /**
     * <p> Toggle whether or not to draw a "filled" obstacle when drawing the obstacle</p>
     * @return whether to fill the obstacle or not
     */
    public boolean toggleFill() {
        this.fill = !this.fill;
        return this.fill;
    }

    public abstract void checkCollision(Agent currentAgent, int frame);
    public abstract void paint(Graphics g);

}