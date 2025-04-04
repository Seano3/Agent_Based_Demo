
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
//test

public class RunSimulation {

    private static final int PANEL_HEIGHT = 200;

    public static void main(String[] args) throws Exception {
        //Initialize Frame
        JFrame frame = new JFrame("Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Simulation sim = new Simulation(1100, 720 + PANEL_HEIGHT);
        frame.add(sim);

        System.out.println("Current directory: " + System.getProperty("user.dir"));
        String inputDir = System.getProperty("user.dir") + "/PopulationSimulation/inputfiles/";

        //Exit input
        try (BufferedReader br = new BufferedReader(new FileReader(inputDir + "exit-input.csv"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");

                if (attributes.length != 5) {
                    System.err.println("Invalid number of attributes in line: " + line);
                    continue;
                }

                try {
                    int size = Integer.parseInt(attributes[0]);
                    double xCoord = Double.parseDouble(attributes[1]);
                    double yCoord = Double.parseDouble(attributes[2]);
                    double alignmentNum = Double.parseDouble(attributes[3]);
                    int buildingExit = (int) Double.parseDouble(attributes[4]);
                    Exit exit;
                    Location location = new Location(xCoord, yCoord);
                    if (alignmentNum == 0) {
                        if (buildingExit == 0) {
                            exit = new Exit(size, location, Exit.alignment.VERTICAL, false);
                        } else {
                            exit = new Exit(size, location, Exit.alignment.VERTICAL, true);
                        }
                    } else {
                        if (buildingExit == 0) {
                            exit = new Exit(size, location, Exit.alignment.HORIZONTAL, false);
                        } else {
                            exit = new Exit(size, location, Exit.alignment.HORIZONTAL, true);
                        }
                    }

                    sim.addExit(exit);

                    //System.out.println("Created Exit: " + exit.getLocation().toString() + " " + exit.getSize());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        //Spawn input
        try (BufferedReader br = new BufferedReader(new FileReader(inputDir + "spawn-input.csv"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");

                if (attributes.length != 9) {
                    System.err.println("Invalid number of attributes in line: " + line);
                    continue;
                }

                try {
                    int size = Integer.parseInt(attributes[0]);
                    double xCoord = Double.parseDouble(attributes[1]);
                    double yCoord = Double.parseDouble(attributes[2]);
                    double alignmentNum = Double.parseDouble(attributes[3]);
                    int spawnRateInterval = Integer.parseInt(attributes[4].replace(" ",""));
                    double spawnAgentSize = Double.parseDouble(attributes[5]);
                    double direction = Double.parseDouble(attributes[6]);
                    int spawnDelay = Integer.parseInt(attributes[7].replace(" ",""));
                    int spawnNumber = Integer.parseInt(attributes[8].replace(" ","")); //If spawnNumber is -1, then it will spawn indefinitely
                    Spawn spawn;
                    Location location = new Location(xCoord, yCoord);
                    Spawn.direction directionEnum = Spawn.direction.LEFT;
                    if (direction == 1) {
                        directionEnum = Spawn.direction.RIGHT;
                    }
                    if (alignmentNum == 0) {
                        spawn = new Spawn(size, location, Spawn.alignment.VERTICAL, spawnRateInterval, spawnAgentSize, directionEnum, spawnDelay, spawnNumber);
                    } else {
                        spawn = new Spawn(size, location, Spawn.alignment.HORIZONTAL, spawnRateInterval, spawnAgentSize, directionEnum, spawnDelay, spawnNumber);
                    }

                    sim.addSpawn(spawn);
                    //System.out.println("Created Spawn: " + spawn.getLocation().toString() + " " + spawn.getSize());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        //Agent input
        try (BufferedReader br = new BufferedReader(new FileReader(inputDir + "agent-input.csv"))) {

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
                    sim.addAgent(agent, false);
                    //System.out.println("Created Agent: " + agent);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        //Exit exit1 = new Exit(100, new Location(0,100),Exit.alignment.VERTICAL);
        //Obstacle input
        try (BufferedReader br = new BufferedReader(new FileReader(inputDir + "obstacle-input.csv"))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");

                if (attributes.length != 5 && attributes.length != 1) {
                    System.err.println("Invalid number of attributes in line: " + line);
                    continue;
                }

                try {
                    String type = attributes[0];
                    Obstacle obj;
                    if (type.equalsIgnoreCase("walls")) {
                        obj = new Box(new Location(0, 0), sim.width, sim.height - PANEL_HEIGHT);
                    } else if (type.equalsIgnoreCase("box")) {
                        double xCoord = Double.parseDouble(attributes[1]);
                        double yCoord = Double.parseDouble(attributes[2]);
                        double height = Double.parseDouble(attributes[3]);
                        double width = Double.parseDouble(attributes[4]);
                        obj = new Box(new Location(xCoord, yCoord), width, height);
                    } else if (type.equalsIgnoreCase("line")) {
                        double x1 = Double.parseDouble(attributes[1]);
                        double y1 = Double.parseDouble(attributes[2]);
                        double x2 = Double.parseDouble(attributes[3]);
                        double y2 = Double.parseDouble(attributes[4]);
                        obj = new Line(x1, y1, x2, y2);
                    } else { // Unknown Type
                        System.err.println("Invalid number of attributes in line: " + line);
                        continue;
                    }
                    sim.addObstacle(obj);
                    //System.out.println("Created Obstacle: " + obj.getLocation().toString());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        if(!sim.getAgents().isEmpty())
            sim.toggleVectorMap(); // enable and regen vector map
        //Initialize Agents
        // for(int i = 0; i < 3; i++){
        //     sim.addAgent(new Agent(i, 20, i * 50, Math.random() * 500 + 2.5, Math.random() * 5 + 2.5, Math.random() * 5 + 2.5));
        //     sim.addAgent(new Agent(i + 3, 20, i *2 * 50, Math.random() * 500 + 2.5, Math.random() * 5 + 2.5, Math.random() * 5 + 2.5));
        // } 
        // generateCSV(6, sim);
        if (!sim.vectorMapEnabled()) {
            System.out.println("******Vector map is not enabled********");
        }
        frame.pack();
        frame.setVisible(true);

        //TimeUnit.SECONDS.sleep(1);
        //test.applyForce(50, 0);
    }
}
