import java.awt.*;

public class Box extends Obstacle {

    private final double width;
    private final double height;

    public Box(Location location, double width, double height, double Force) {
        super(location, Force);
        this.width = width;
        this.height = height;
    }

    public Box(Location location, double width, double height, double xForce, double yForce) {
        super(location, xForce, yForce);
        this.width = width;
        this.height = height;
    }

    private boolean withinX(double x) {
        return x >= location.getX() && x <= location.getX() + width;
    }

    private boolean withinY(double y) {
        return y >= location.getY() && y <= location.getY() + height;
    }

    @Override
    public Agent checkCollision(Agent currentAgent, int frame) {
        Collision newCollision = null;
        if (currentAgent.getLocation().getX() >= width-currentAgent.getSize() && withinY(currentAgent.getLocation().getY())) { // Right collision
            newCollision = new Collision(currentAgent.AgentID, -4, frame);

        }
        if (currentAgent.getLocation().getX() <= location.getX() && withinY(currentAgent.getLocation().getY())) { // Left collision

           newCollision = new Collision(currentAgent.AgentID, -2, frame);
            //if (checkPreviousObstacleCollisions(collision))
            currentAgent.setXVelocity(Math.abs(currentAgent.getXVelocity()));

        }

        if (currentAgent.getLocation().getY() <= location.getY() && withinX(currentAgent.getLocation().getX())) { // Top collision

            newCollision = new Collision(currentAgent.AgentID, -3, frame);
            //if (checkPreviousObstacleCollisions(collision))
            currentAgent.setYVelocity(Math.abs(currentAgent.getYVelocity()));

        }

        if (currentAgent.getLocation().getY() >= height-currentAgent.getSize() && withinX(currentAgent.getLocation().getX())) { // Bottom collision

            newCollision = new Collision(currentAgent.AgentID, -1, frame);
            //if (checkPreviousObstacleCollisions(collision)) {
                currentAgent.setYVelocity(-Math.abs(currentAgent.getYVelocity()));
        }
        return currentAgent;
    }

    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.drawRect((int)location.getX(), (int)location.getY(), (int)width, (int)height);
    }
}
