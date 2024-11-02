
public class Circle extends Obstacle {
    private double radius;

    public Circle(Location location, double radius, double Force) {
        super(location, Force);
        this.radius = radius;
    }

    public Circle(Location location, double radius, double xForce, double yForce) {
        super(location, xForce, yForce);
        this.radius = radius;
    }
}
