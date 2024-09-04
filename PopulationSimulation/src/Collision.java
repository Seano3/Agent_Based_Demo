public class Collision {
    int chesksum;
    int framesRemaining; 

    public Collision (int Agent1, int Agent2){
        chesksum = Agent1 * 10 + Agent2; 
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
