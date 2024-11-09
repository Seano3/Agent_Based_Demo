
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

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height; 
    }

}
