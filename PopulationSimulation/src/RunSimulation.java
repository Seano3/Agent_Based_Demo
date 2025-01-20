import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
//test
public class RunSimulation{

    private static final int PANEL_HEIGHT = 200;

    
    public static void main(String[] args) throws Exception {
        //Initialize Frame
        JFrame frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Simulation sim = new Simulation(1100, 720 + PANEL_HEIGHT);
        frame.add(sim);

        System.out.println("Current directory: " + System.getProperty("user.dir"));

        //Exit input
        try (BufferedReader br = new BufferedReader(new FileReader("exit-input.csv"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");

                if (attributes.length != 4) {
                    System.err.println("Invalid number of attributes in line: " + line);
                    continue;
                }

                try {
                    int size = Integer.parseInt(attributes[0]);
                    double xCoord = Double.parseDouble(attributes[1]);
                    double yCoord = Double.parseDouble(attributes[2]);
                    double alignmentNum = Double.parseDouble(attributes[3]);
                    Exit exit;
                    Location location = new Location(xCoord, yCoord);
                    if (alignmentNum == 0){
                        exit = new Exit(size, location, Exit.alignment.VERTICAL);
                    } else {
                        exit = new Exit(size, location, Exit.alignment.HORIZONTAL);
                    }

                    sim.addExit(exit);
                    System.out.println("Created Exit: " + exit.getLocation().toString() + " " + exit.getSize());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        //Spawn input
        try (BufferedReader br = new BufferedReader(new FileReader("spawn-input.csv"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");

                if (attributes.length != 4) {
                    System.err.println("Invalid number of attributes in line: " + line);
                    continue;
                }

                try {
                    int size = Integer.parseInt(attributes[0]);
                    double xCoord = Double.parseDouble(attributes[1]);
                    double yCoord = Double.parseDouble(attributes[2]);
                    double alignmentNum = Double.parseDouble(attributes[3]);
                    int spawnRateInterval = Integer.parseInt(attributes[4]);
                    Spawn spawn;
                    Location location = new Location(xCoord, yCoord);
                    if (alignmentNum == 0){
                        spawn = new Spawn(size, location, Spawn.alignment.VERTICAL, spawnRateInterval);
                    } else {
                        spawn = new Spawn(size, location, Spawn.alignment.HORIZONTAL, spawnRateInterval);
                    }

                    sim.addSpawn(spawn);
                    System.out.println("Created Spawn: " + spawn.getLocation().toString() + " " + spawn.getSize());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        //Agent input
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

                    Agent agent = new Agent(name, size, xCoord-size, yCoord-size, xVel, yVel, sim);
                    sim.addAgent(agent);
                    System.out.println("Created Agent: " + agent);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    
        //Exit exit1 = new Exit(100, new Location(0,100),Exit.alignment.VERTICAL);

        //Obstacle input
        try (BufferedReader br = new BufferedReader(new FileReader("obstacle-input.csv"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");
                
                if (attributes.length != 4) {
                    System.err.println("Invalid number of attributes in line: " + line);
                    continue;
                }

                try {
                    int xCoord = Integer.parseInt(attributes[0]);
                    double yCoord = Double.parseDouble(attributes[1]);
                    double height = Double.parseDouble(attributes[2]);
                    double width = Double.parseDouble(attributes[3]);

                    Obstacle obj;
                    Location location = new Location(xCoord, yCoord);

                    obj = new Box(location, width, height,0);

                    sim.addObjs(obj);
                    System.out.println("Created Obstacle: " + obj.getLocation().toString());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        // Simulation walls
        //TODO: make this a toggle in one of the files (probably obstacles)
        sim.addObstacle(new Box(new Location(0,0), sim.width, sim.height, 0));

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
