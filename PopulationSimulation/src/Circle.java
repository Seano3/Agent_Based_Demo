import java.awt.*;

//TODO: Determine whether we need to implement this or if we can just delete it
public class Circle extends Obstacle {
    private double radius;

    public Circle(Location location, double radius, double Force) {
        super(location);
        this.radius = radius;
    }

    public Circle(Location location, double radius, double xForce, double yForce) {
        super(location);
        this.radius = radius;
    }

    @Override
    public void checkCollision(Agent currentAgent, int frame) {
    }

    @Override
    public void paint(Graphics g) {

    }

}
