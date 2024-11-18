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
        return x >= this.location.getX() && x <= this.location.getX() + this.width;
    }

    private boolean withinY(double y) {
        return y >= this.location.getY() && y <= this.location.getY() + this.height;
    }

    @Override
    public void checkCollision(Agent currentAgent, int frame) {
        Collision newCollision = null;
        if (currentAgent.getLocation().getX() <= location.getX()+width &&
                currentAgent.getLocation().getX() >= location.getX()+width-20 &&
                withinY(currentAgent.getLocation().getY())) { // Right collision
            newCollision = new Collision(currentAgent.AgentID, -4, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision))
                currentAgent.setXVelocity(Math.abs(currentAgent.getXVelocity()));

        }
        if (currentAgent.getLocation().getX() >= location.getX()-currentAgent.getSize()*2 &&
                currentAgent.getLocation().getX() <= location.getX() &&
                withinY(currentAgent.getLocation().getY())) { // Left collision
           newCollision = new Collision(currentAgent.AgentID, -2, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision))
                currentAgent.setXVelocity(-Math.abs(currentAgent.getXVelocity()));

        }

        if (currentAgent.getLocation().getY() >= location.getY()-currentAgent.getSize()*2 &&
                currentAgent.getLocation().getY() <= location.getY()+20 && //buffer zone
                withinX(currentAgent.getLocation().getX())) { // Top collision

            newCollision = new Collision(currentAgent.AgentID, -3, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision))
             currentAgent.setYVelocity(-Math.abs(currentAgent.getYVelocity()));

        }
        //TODO: these aren't mutually exclusive
        if (currentAgent.getLocation().getY() <= location.getY()+height &&
                currentAgent.getLocation().getY() >= location.getY() + height - 20 && // buffer zone
                withinX(currentAgent.getLocation().getX())) { // Bottom collision

            newCollision = new Collision(currentAgent.AgentID, -1, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision))
                currentAgent.setYVelocity(Math.abs(currentAgent.getYVelocity()));
        }
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.drawRect((int)location.getX(), (int)location.getY(), (int)width, (int)height);
    }
}
