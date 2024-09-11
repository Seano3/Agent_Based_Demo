public class Collision {
    private int ID;
    int chesksum;
    private int framesRemaining; 

    public Collision (int Agent1, int Agent2, int frame){
        ID = frame + Agent1 + Agent2;
        chesksum = Agent1 + Agent2 + frame; 
        framesRemaining = 5;
    }

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
