public class Agent {
    private String name;
    private int size;
    private int xVelocity;
    private int yVelocity;
    private Location location;

    public Agent (String name, int size, int xCord, int yCord){
        this.name = name;
        this.size = size;
        xVelocity = 0;
        yVelocity = 0;
        location = new Location(xCord, yCord);
    }

    public String getName(){
        return name;
    }

    public int getSize(){
        return size;
    }

    public int getXVelocity(){
        return xVelocity;
    }

    public int getYVelocity(){
        return yVelocity;
    }

    public Location getLocation(){
        return location; 
    }

    public void updateLocation(){
        location.changePosition(xVelocity, yVelocity);
    }
}
