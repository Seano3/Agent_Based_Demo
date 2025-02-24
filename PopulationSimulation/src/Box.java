
import java.awt.*;

public class Box extends Obstacle {

    // Location is stored in the superclass
    private final double width;
    private final double height;

    /**
     *
     * @param location - The position of the top left corner of the box
     * @param width - The width of the box
     * @param height - The height of the box
     */
    public Box(Location location, double width, double height) {
        super(location);
        this.width = width;
        this.height = height;
    }

    /**
     * <p>
     * Checks if the x value of an agent is within the width of a box for
     * top/bottom collision checking </p>
     *
     * @param x - The X position of an agent
     * @return if the agent is within the x values of the width of the box
     */
    private boolean withinX(double x) {
        return x >= this.location.getX() && x <= this.location.getX() + this.width;
    }

    /**
     * <p>
     * Checks if the y value of an agent is within the height of a box for
     * left/right collision checking </p>
     *
     * @param y - The Y position of an agent
     * @return if the agent is within the y values of the width of the box
     */
    private boolean withinY(double y) {
        return y >= this.location.getY() && y <= this.location.getY() + this.height;
    }

    /**
     * <p>
     * Checks if an agent has collided with the box in a given frame, and
     * adjusts its velocity if it has </p>
     *
     * @param currentAgent - The agent to be checked
     * @param frame - The current frame
     */
    @Override
    public void checkCollision(Agent currentAgent, int frame) {
        Collision newCollision;
        if (currentAgent.getLocation().getX() <= location.getX() + width + currentAgent.getSize()
                && currentAgent.getLocation().getX() >= location.getX() + width - currentAgent.getSize()
                && withinY(currentAgent.getLocation().getY())) { // Right side collision
            newCollision = new Collision(currentAgent.AgentID, -4, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision)) {
                currentAgent.setXVelocity(-currentAgent.getXVelocity());
            }

            }
        if (currentAgent.getLocation().getX() >= location.getX() - currentAgent.getSize()
                && currentAgent.getLocation().getX() <= location.getX() + currentAgent.getSize()
                && withinY(currentAgent.getLocation().getY())) { // Left side collision
            newCollision = new Collision(currentAgent.AgentID, -2, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision)) {
                currentAgent.setXVelocity(-currentAgent.getXVelocity());
            }

        }

        if (currentAgent.getLocation().getY() >= location.getY() - currentAgent.getSize()
                && currentAgent.getLocation().getY() <= location.getY() + currentAgent.getSize()
                && withinX(currentAgent.getLocation().getX())) { // Top side collision

            newCollision = new Collision(currentAgent.AgentID, -3, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision)) {
                currentAgent.setYVelocity(-currentAgent.getYVelocity());
            }

        }
        if (currentAgent.getLocation().getY() <= location.getY() + height + currentAgent.getSize()
                && currentAgent.getLocation().getY() >= location.getY() + height - currentAgent.getSize()
                && // buffer zone
                withinX(currentAgent.getLocation().getX())) { // Bottom side collision

            newCollision = new Collision(currentAgent.AgentID, -1, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision)) {
                currentAgent.setYVelocity(-currentAgent.getYVelocity());
            }
        }
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /**
     * <p>
     * Draws the obstacle for a given component </p>
     *
     * @param g - Graphics to draw the obstacle in
     */
    @Override
    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.drawRect((int) location.getX(), (int) location.getY(), (int) width, (int) height);
    }
}
