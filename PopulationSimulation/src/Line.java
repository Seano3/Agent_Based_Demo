import java.awt.*;

public class Line extends Obstacle {

    private final Location endpoint;

    public Line(Location location, Location endpoint, double Force) {
        super(location, Force);
        this.endpoint = endpoint;
    }


    //TODO: potential problems if agent is moving parallel to line and within Tl/Te range. may not be worth it to fix
    private boolean withinRange (Agent currentAgent) {

        // find agent triangle
        double D = location.getDistance(endpoint); // length of obstacle
        double Dl = currentAgent.getLocation().getDistance(location); // distance of agent to location
        double De = currentAgent.getLocation().getDistance(endpoint); // distance of agent to endpoint

        if(D < Dl || D < De) { // agent is not close enough for obstacle to be hypotenuse
            return false;
        }

        double Tl = Math.acos(Dl / D); // theta of location w agent
        double Te = Math.acos(De / D); // theta of endpoint w agent
        if (Tl > Math.PI / 2 || Te > Math.PI / 2) { // check if agent is out of range of the obstacle. if either angle is obtuse then we are out of range
            return false;
        }

        // find distance of agent to obstacle
        double Da = Dl / Math.sin(Tl);

//        if (Da <= currentAgent.getSize() *2 )
//            System.out.println(Dl + " " + De + " " + Tl);
        return (Da <= currentAgent.getSize() * 2);
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
