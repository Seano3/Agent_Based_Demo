import javax.swing.*;

public class RunSimulation{
    
    public static void main(String[] args) throws Exception {
        //Initalize Frame
        JFrame frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Simulation sim = new Simulation(800, 600);
        frame.add(sim);


        //Initialize Agents
        // for(int i = 0; i < 3; i++){
        //     sim.addAgent(new Agent(i, 20, i * 50, Math.random() * 500 + 2.5, Math.random() * 5 + 2.5, Math.random() * 5 + 2.5));
        //     sim.addAgent(new Agent(i + 3, 20, i *2 * 50, Math.random() * 500 + 2.5, Math.random() * 5 + 2.5, Math.random() * 5 + 2.5));

        // }

        Agent test = new Agent(1, 20, 200, 200, 0, 0);
        sim.addAgent(test);       
        
        
        frame.pack();
        frame.setVisible(true);

        test.applyForce(-50, 0);
        //TimeUnit.SECONDS.sleep(1);
        //test.applyForce(50, 0);      
        
    }
}
