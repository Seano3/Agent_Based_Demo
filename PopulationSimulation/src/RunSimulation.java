
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.*;




public class RunSimulation{

     
    public static void generateCSV(int numAgents, Simulation sim) { 
        String csvFile = "agent-output.csv"; // Name of the CSV file 
        String[] headers = {"Agent ID", "Size", "X-Velocity", "Y-Velocity", "X-Location", "Y-Location"}; // CSV headers 
        String[][] data = new String[numAgents + 1][6]; // Data to write to CSV file
        LinkedList<Agent> agents = sim.getAgents();
        data[0] = headers;
        for(int i = 0; i < numAgents; i++){
            data[i + 1][0] = Integer.toString(agents.get(i).AgentID);
            data[i + 1][1] = Double.toString(agents.get(i).getSize());
            data[i + 1][2] = Double.toString(agents.get(i).getXVelocity());
            data[i + 1][3] = Double.toString(agents.get(i).getYVelocity());
            data[i + 1][4] = Double.toString(agents.get(i).getLocation().getX());
            data[i + 1][5] = Double.toString(agents.get(i).getLocation().getY());
        }
 
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) { 
            writer.newLine(); 
  
            for (String[] row : data) { 
                writer.write(String.join(",", row)); 
                writer.newLine(); 
            } 
 
            System.out.println("CSV file created successfully."); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    } 
    
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
        // generateCSV(6, sim); 
        
        // sim.addAgent(new Agent(1, 20, 400, 200, 5, 5)); //Change y to 200 to see bad corner behavior

        sim.addAgent(new Agent(1, 20, 550, 300, -5, 0));
        sim.addAgent(new Agent(2, 20, 100, 300, 10, 0));


        // sim.addAgent(new Agent(1, 20, 300, 300, 0, 5));
        // sim.addAgent(new Agent(2, 20, 300, 100, 0, -5));
        
        
        frame.pack();
        frame.setVisible(true);

        //TimeUnit.SECONDS.sleep(1);
        //test.applyForce(50, 0);      
        
        
    }
}
