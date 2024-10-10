public class Collision {
    private int ID;
    int chesksum;
    private int framesRemaining; 

    public Collision (int Agent1, int Agent2, int frame){
        ID = frame + Agent1 + Agent2;
        chesksum = Agent1 + Agent2 + frame; 
        framesRemaining = 5;
    }

    /**
     * <p>Removes the a frame from the frames remaining and checks to see if there are no remaining frames left and returns true.</p>
     * @return True for no remaining frames 
     */
    public Boolean removeFrame(){
        framesRemaining--;

        if (framesRemaining == 0){
            return true;
        }

        return false;
    }

    public int GetID(){
        return ID;
    }
}
