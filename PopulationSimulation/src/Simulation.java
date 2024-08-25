import java.util.LinkedList;
import java.util.List;

public class Simulation {
    private int width;
    private int height;
    private List<Agent> agents;

    public Simulation(int width, int height){
        this.width = width;
        this.height = height; 
        agents = new LinkedList<Agent>(); 
    }

    public void addAgent(Agent agent) {
        agents.add(agent);
    }

    public void update(){
        for (Agent i : agents) {
            i.updateLocation();
        }
    }

    public List<Agent> getAgents(){
        return agents;
    }
}
