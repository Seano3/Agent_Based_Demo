public class Location {
    private double xCord;
    private double yCord;

    /**
     * This class saves the X and Y location of each agent
     * @param x X cord
     * @param y y cord
     */
    public Location (double x, double y){
        xCord = x;
        yCord = y;
    }

    public void changePosition(double newX, double newY){
        xCord = newX;
        yCord = newY; 
    }

    public String toString() {
        return "(" + xCord + ", " + yCord + ")";
    }

    /**
     * This method calculates the distance between two locations
     *
     * @param loc Location to compare to
     * @return Distance between the two locations
     */
    public double getDistance(Location loc){
        double xComponent = loc.getX() - xCord;
        double yComponent = loc.getY() - yCord;
        return Math.sqrt(xComponent * xComponent + yComponent * yComponent);
    }

    public double getX(){
        return xCord;
    }

    public double getY(){
        return yCord;
    }
}
