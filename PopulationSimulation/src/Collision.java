public class Collision {
    private int ID;
    int checksum;
    private int framesRemaining;
    private int Agent1;
    private int Agent2;
    private double height;
    private double width;

    /**
     * This class stores information about each collision 
     * @param Agent1 An agent in the collision 
     * @param Agent2 The other agent in the collision
     * @param frame the frame of the collision
     */
    public Collision (int Agent1, int Agent2, int frame){
        ID = Agent1 + Agent2;
        checksum = Agent1 + Agent2 + frame;
        framesRemaining = 2;
    }

    /**
     * <p>Removes the a frame from the frames remaining and checks to see if there are no remaining frames left and returns true.</p>
     * @return True for no remaining frames 
     */
    public Boolean removeFrame(){
        --framesRemaining;

        if (framesRemaining <= 0){
            return true;
        }

        return false;
    }

    public int getAgent1 () {
        return Agent1;
    }

    public int getAgent2 () {
        return Agent2;
    }

    public int GetID(){
        return ID;
    }

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height; 
    }
}
