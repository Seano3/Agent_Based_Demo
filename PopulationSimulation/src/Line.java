import java.awt.*;

public class Line extends Obstacle {

    private final Location endpoint;
    private final double length;

    public Line(Location location, Location endpoint) {
        super(location);
        this.endpoint = endpoint;
        length = location.getDistance(endpoint);
    }

    public Line(double x, double y, double endx, double endy) {
        super(new Location(x, y));
        endpoint = new Location(endx, endy);
        length = location.getDistance(endpoint);
    }

    public Location getEndpoint() {
        return endpoint;
    }

    public double getLength() {
        return length;
    }

    //TODO: potential problems if agent is moving parallel to line and within Tl/Te range. may not be worth it to fix
    private boolean withinRange (Agent currentAgent) {

        // find agent triangle
        double Dl = currentAgent.getLocation().getDistance(location); // distance of agent to location
        double De = currentAgent.getLocation().getDistance(endpoint); // distance of agent to endpoint

        //TODO: will not work with length < currentAgent.size() since dl will be longer than length when a collision is supposed to happen. fix with an extra if() if necessary
        if (length < Dl || length < De) { // agent is not close enough for obstacle to be hypotenuse
            return false;
        }

        double Tl = Math.acos( (length * length + De * De - Dl * Dl) / (2 * length * De)); // theta of location w agent using law of cosines
        double Te = Math.acos( (length * length + Dl * Dl - De * De) / (2 * length * Dl)); // theta of endpoint w agent using law of cosines
        if (Tl > Math.PI / 2 || Te > Math.PI / 2) { // check if agent is out of range of the obstacle. if either angle is obtuse then we are out of range
            return false;
        }

        // find distance of agent to obstacle
        double Da = Math.sin(Te) * Dl;

        //if (Da <= currentAgent.getSize() *2 ) System.out.println(Dl + " " + De + " " + Tl);

        return (Da <= currentAgent.getSize());
    }


    @Override
    public void checkCollision(Agent currentAgent, int frame) {
        // double theta = Math.atan2(endpoint.getX()-location.getX(), endpoint.getY()-location.getY()); // angle of obstacle based on X axis
        if (withinRange(currentAgent)) {
            currentAgent.setYVelocity(-currentAgent.getYVelocity());
            currentAgent.setXVelocity(-currentAgent.getXVelocity());
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawLine((int) location.getX(), (int) location.getY(), (int) endpoint.getX(), (int) endpoint.getY());
    }
}
