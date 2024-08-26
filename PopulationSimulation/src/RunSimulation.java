import javax.swing.*;

public class RunSimulation{
    
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Simulation sim = new Simulation(800, 600);
        frame.add(sim);


        //Initialize Agents
        for(int i = 0; i < 6; i++){
            sim.addAgent(new Agent(20, i * 50, Math.random() * 500 + 2.5, Math.random() * 5 + 2.5, Math.random() * 5 + 2.5));
        }
        

        frame.pack();
        frame.setVisible(true);      
        
    }
}
