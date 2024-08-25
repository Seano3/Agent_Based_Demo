import javax.swing.*;

public class RunSimulation{
    
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Simulation sim = new Simulation(800, 600);
        frame.add(sim);


        //Initialize Agents
        sim.addAgent(new Agent(20, 200, 300, 5, 0));
        sim.addAgent(new Agent(20, 400, 300, -5, 0));

        frame.pack();
        frame.setVisible(true);      
        
    }
}
