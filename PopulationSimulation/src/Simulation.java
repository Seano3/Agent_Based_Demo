import java.util.List;

public class Simulation {
    private int width;
    private int height;
    private List<Agent> agents;

    public Simulation(int width, int height){
        this.width = width;
        this.height = height; 
    }

    public void addAgent(Agent agent) {
        agents.add(agent);
    }

    public void update(){
        for (Agent i : agents) {
            i.updateLocation();
        }
    }
}
