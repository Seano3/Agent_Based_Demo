public class Location {
    private double xCord;
    private double yCord;

    public Location (double x, double y){
        xCord = x;
        yCord = y;
    }

    public void changePosition(double newX, double newY){
        xCord = newX;
        yCord = newY; 
    }

    public double getX(){
        return xCord;
    }

    public double getY(){
        return yCord;
    }
}
