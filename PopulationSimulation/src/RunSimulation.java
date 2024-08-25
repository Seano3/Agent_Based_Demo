import javax.swing.*;

public class RunSimulation{
    
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Simulation sim = new Simulation(800, 600);
        frame.add(sim);


        //Initialize Agents
        sim.addAgent(new Agent(20, 200, 300, Math.random() * 5 - 2.5, Math.random() * 5 - 2.5));
        sim.addAgent(new Agent(20, 400, 300, Math.random() * 5 - 2.5, Math.random() * 5 - 2.5));

        frame.pack();
        frame.setVisible(true);      
        
    }
}
