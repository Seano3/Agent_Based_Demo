public class Location {
    private int xCord;
    private int yCord;

    public Location (int x, int y){
        xCord = x;
        yCord = y;
    }

    public void changePosition(int xVelocity, int yVelocity){
        xCord += xVelocity;
        yCord += yVelocity; 
    }

    public int getX(){
        return xCord;
    }

    public int getY(){
        return yCord;
    }
}
