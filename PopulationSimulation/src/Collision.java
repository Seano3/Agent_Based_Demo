public class Collision {
    int ID;
    int chesksum;
    int framesRemaining; 

    public Collision (int Agent1, int Agent2, int frame){
        int ID = frame;
        chesksum = Agent1 + Agent2; 
        framesRemaining = 5;
    }

    public Boolean removeFrame(){
        framesRemaining--;

        if (framesRemaining == 00){
            return true;
        }

        return false;
    }
}
