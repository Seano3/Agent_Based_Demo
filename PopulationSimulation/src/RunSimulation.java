import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.*;
//test
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
        Simulation sim = new Simulation(800, 800);
        frame.add(sim);

        System.out.println("Current directory: " + System.getProperty("user.dir"));


        try (BufferedReader br = new BufferedReader(new FileReader("agent-input.csv"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");
                
                if (attributes.length != 6) {
                    System.err.println("Invalid number of attributes in line: " + line);
                    continue;
                }

                try {
                    int name = Integer.parseInt(attributes[0]);
                    double size = Double.parseDouble(attributes[1]);
                    double xCoord = Double.parseDouble(attributes[2]);
                    double yCoord = Double.parseDouble(attributes[3]);
                    double xVel = Double.parseDouble(attributes[4]);
                    double yVel = Double.parseDouble(attributes[5]);

                    Agent agent = new Agent(name, size, xCoord, yCoord, xVel, yVel, sim);
                    sim.addAgent(agent);
                    System.out.println("Created Agent: " + agent);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    


        //Initialize Agents
        // for(int i = 0; i < 3; i++){
        //     sim.addAgent(new Agent(i, 20, i * 50, Math.random() * 500 + 2.5, Math.random() * 5 + 2.5, Math.random() * 5 + 2.5));
        //     sim.addAgent(new Agent(i + 3, 20, i *2 * 50, Math.random() * 500 + 2.5, Math.random() * 5 + 2.5, Math.random() * 5 + 2.5));
            
        // } 
        // generateCSV(6, sim); 
               
        
        frame.pack();
        frame.setVisible(true);

        //TimeUnit.SECONDS.sleep(1);
        //test.applyForce(50, 0);      
        
        
    }
}
