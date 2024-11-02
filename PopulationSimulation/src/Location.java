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

    public double getX(){
        return xCord;
    }

    public double getY(){
        return yCord;
    }
}
