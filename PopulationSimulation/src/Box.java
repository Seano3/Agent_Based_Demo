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
        Collision newCollision;
        if(width == 100 || width == 300)
        System.out.println("FRAME: " + frame + ", AGENT X + SIZE" + this.location.getX()+currentAgent.getSize() + ", AGENT LOC" + currentAgent.getLocation().getX() + ", WITHY" + withinY(currentAgent.getLocation().getY()));
        if (currentAgent.getLocation().getX() <= location.getX()+width+currentAgent.getSize() &&
                currentAgent.getLocation().getX() >= location.getX()+width-currentAgent.getSize() &&
                withinY(currentAgent.getLocation().getY())) { // Right collision
            newCollision = new Collision(currentAgent.AgentID, -4, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision))
                currentAgent.setXVelocity(-currentAgent.getXVelocity());

        }
        if (currentAgent.getLocation().getX() >= location.getX()-currentAgent.getSize() &&
                currentAgent.getLocation().getX() <= location.getX()+currentAgent.getSize() &&
                withinY(currentAgent.getLocation().getY())) { // Left collision
           newCollision = new Collision(currentAgent.AgentID, -2, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision))
                currentAgent.setXVelocity(-currentAgent.getXVelocity());

        }

        if (currentAgent.getLocation().getY() >= location.getY()-currentAgent.getSize() &&
                currentAgent.getLocation().getY() <= location.getY()+currentAgent.getSize() &&
                withinX(currentAgent.getLocation().getX())) { // Top collision

            newCollision = new Collision(currentAgent.AgentID, -3, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision))
             currentAgent.setYVelocity(-currentAgent.getYVelocity());

        }
        if (currentAgent.getLocation().getY() <= location.getY()+height+currentAgent.getSize() &&
                currentAgent.getLocation().getY() >= location.getY()+height-currentAgent.getSize() && // buffer zone
                withinX(currentAgent.getLocation().getX())) { // Bottom collision

            newCollision = new Collision(currentAgent.AgentID, -1, frame);
            if (currentAgent.checkOtherObstacleCollisions(newCollision))
                currentAgent.setYVelocity(-currentAgent.getYVelocity());
        }
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.black);
        g.drawRect((int)location.getX(), (int)location.getY(), (int)width, (int)height);
    }
}
