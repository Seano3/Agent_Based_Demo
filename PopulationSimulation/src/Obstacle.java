import java.awt.*;

public abstract class Obstacle {
    protected Location location;
    protected double xForce;
    protected double yForce;
    protected boolean fill;

    /**
     * Initialize an obstacle
     * @param location - The location of the top-leftmost point of the obstacle. May be outside the actual obstacle for strange shapes i.e circle
     */
    public Obstacle(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
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