
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.*;

public class Simulation extends JPanel {

    private LinkedList<Agent> agents;
    private LinkedList<Exit> exits;
    private LinkedList<Spawn> spawns;
    private LinkedList<Obstacle> obstacles;
    private Timer timer;
    int frame;
    int width;
    int height;
    int upperBorderHeight;
    int panelHeight;
    String csvName = "Debug.csv";
    private double elapsedTime;
    private JLabel timeLabel;
    private JLabel agentCountLabel;
    private JLabel frameLabel;
    private JButton pausePlayButton;
    private JButton toggleGridButton;
    private JButton frameStepButton;
    private JButton timeStepButton;
    private JButton toggleAgentNumbersButton;
    private JLabel timeStepInputLabel;
    private JTextField timeStepInput;
    private boolean isPaused;
    private boolean isGridEnabled;
    private boolean isAgentNumbersEnabled;
    private double startTime;
    private double initialKE = 0;
    private int totalAgents = 0;
    private int totalExits = 0;
    private int totalSpawns = 0;
    private int timeStep = 1;
    int[][] vectorMap;
    vectorMapGen map;
    private boolean useVectorMap;
    private static String outputPath;
    private int NumberOfAgents;

    public Simulation(int width, int height, boolean vectorMapEnabled) {
        NumberOfAgents = 0;
        map = new vectorMapGen(width, (height - 200));
        frame = 0;
        this.useVectorMap = vectorMapEnabled;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);
        panelHeight = 200;

        outputPath = System.getProperty("user.dir") + "/PopulationSimulation/outputfiles/";

        this.width = width;
        this.height = height;

        agents = new LinkedList<>();
        exits = new LinkedList<>();
        obstacles = new LinkedList<>();
        spawns = new LinkedList<>();
        clearEscapeRateCSV();

        try (FileWriter writer = new FileWriter(outputPath + csvName)) {
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }

        timeLabel = new JLabel("Time: 00:00:00");
        agentCountLabel = new JLabel("Agents: 0");
        frameLabel = new JLabel("Frame: 0");
        timeStepInputLabel = new JLabel("TimeStep amount: ");

        pausePlayButton = new JButton("Play");
        toggleGridButton = new JButton("Enable Grid");
        frameStepButton = new JButton("Frame Step");
        timeStepButton = new JButton("Time Step");
        timeStepInput = new JTextField("1", 1);
        timeStepInput.setEditable(true);

        isPaused = true;
        isGridEnabled = false;
        startTime = 0.0;
        elapsedTime = 0.0;

        pausePlayButton.addActionListener(e -> {
            if (isPaused) {
                pausePlayButton.setText("Pause");
                timer.start();
                frameStepButton.setForeground(Color.gray);
                timeStepButton.setForeground(Color.gray);
            } else {
                pausePlayButton.setText("Play");
                timer.stop();
                frameStepButton.setForeground(Color.black);
                timeStepButton.setForeground(Color.black);
            }
            isPaused = !isPaused;
        });

        toggleGridButton.addActionListener(e -> {
            if (isGridEnabled) {
                toggleGridButton.setText("Enable Grid");
                isGridEnabled = false;
                repaint();
            } else {
                toggleGridButton.setText("Disable Grid");
                isGridEnabled = true;
                repaint();
            }
        });

        frameStepButton.addActionListener(e -> {
            if (isPaused) {
                update();
                repaint();
            }
        });

        timeStepButton.addActionListener(e -> {
            int timeStepFrames = timeStep * 100;
            if (timeStepFrames == 0) {
                System.out.println("Time step 0 - no time skipped");
            }
            if (isPaused) {
                for (int i = 0; i < timeStepFrames; i++) {
                    update();
                }
            }
            repaint();
        });

        timeStepButton.addActionListener(e -> {
            String timeStepStr = timeStepInput.getText();
            timeStep = Integer.parseInt(timeStepStr);
            if (timeStep < 0) { // prevent negative time step
                timeStep = 1;
                timeStepInput.setText("1");
            }
        });

        isAgentNumbersEnabled = true;

        toggleAgentNumbersButton = new JButton("Hide Agent Numbers");
        toggleAgentNumbersButton.addActionListener(e -> {
            isAgentNumbersEnabled = !isAgentNumbersEnabled;
            if (isAgentNumbersEnabled) {
                toggleAgentNumbersButton.setText("Hide Agent Numbers");
            } else {
                toggleAgentNumbersButton.setText("Show Agent Numbers");
            }
            repaint();
        });

        add(toggleAgentNumbersButton);

        setLayout(null);
        add(timeLabel);
        add(pausePlayButton);
        add(toggleGridButton);
        add(agentCountLabel);
        add(frameLabel);
        add(frameStepButton);
        add(timeStepButton);
        add(toggleAgentNumbersButton);
        add(timeStepInput);
        add(timeStepInputLabel);

        timer = new Timer(0, e -> {
            update();
            repaint();
        });

    }

    //TODO: fix this
    private void updateTimerLabel() {
        double hours = (elapsedTime / 3600000) % 24;
        double minutes = (elapsedTime / 60000) % 60;
        double seconds = (elapsedTime / 1000) % 60;
        String formattedTime = String.format("%02f", elapsedTime);
        timeLabel.setText("Time: " + formattedTime);
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public boolean vectorMapEnabled() {
        return useVectorMap;
    }

    public void toggleVectorMap() {
        useVectorMap = !useVectorMap;
        if (useVectorMap) {
            map = new vectorMapGen(this);
            for (Exit i : exits) {
                map.addExitVM(i);
            }
            VectorMapGeneration();
        }
    }

    public void addExit(Exit exit) { // no need to remove exits
        exits.add(exit);
        totalExits++;
    }

    public void addSpawn(Spawn spawn) {
        spawns.add(spawn);
        totalSpawns++;
    }

    public void addAgent(Agent agent, boolean isSpawned) {
        NumberOfAgents++;
        Exit closestExit = findClosestExit(agent.getLocation());
        Spawn closestSpawn = findClosestSpawn(agent.getLocation());

        if(vectorMapEnabled()) {
            // Determine if agent is in the spawn so agent class will disable vector map for that agent if it is inside
            if (closestSpawn != null) {
                agent.setInSpawn(closestSpawn.inSpawn(agent));
            }

            if (isSpawned && closestSpawn != null) {
            /*double centerX = closestSpawn.getLocation().getX() + closestSpawn.getSize() / 2.0;
            double centerY = closestSpawn.getLocation().getY() + closestSpawn.getSize() / 2.0;

            agent.setLocation(new Location(centerX, centerY));*/
                double initialVelocityMagnitude = Math.sqrt(agent.getXVelocity() * agent.getXVelocity() + agent.getYVelocity() * agent.getYVelocity());

                if (closestSpawn.getAlignment() == Spawn.alignment.HORIZONTAL) {
                    if (closestSpawn.getDirection() == Spawn.direction.LEFT) {
                        agent.setXVelocity(0);
                        agent.setYVelocity(-initialVelocityMagnitude);
                    } else {
                        agent.setXVelocity(0);
                        agent.setYVelocity(initialVelocityMagnitude);
                    }
                } else {
                    if (closestSpawn.getDirection() == Spawn.direction.LEFT) {
                        agent.setXVelocity(-initialVelocityMagnitude);
                        agent.setYVelocity(0);
                        System.out.println("Left");
                    } else {
                        agent.setXVelocity(initialVelocityMagnitude);
                        agent.setYVelocity(0);
                        System.out.println("Right");
                    }
                }
            } else if (closestExit != null) {
                System.out.println("Normal");
                double[] directionVector = calculateDirectionVector(agent.getLocation(), closestExit.getLocation());
                double xMagnitude = agent.getXVelocity() * agent.getXVelocity();
                double yMagnitude = agent.getYVelocity() * agent.getYVelocity();
                double magnitude = Math.sqrt(xMagnitude + yMagnitude);
                agent.setXVelocity(directionVector[0] * magnitude);
                agent.setYVelocity(directionVector[1] * magnitude);
            }
        }

        agents.add(agent);
        totalAgents++;
        agentCountLabel.setText("Agents: " + totalAgents);
    }

    public void addObjs(Obstacle obj) {
        obstacles.add(obj);
    }

    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    public void removeAgent(Agent agent) {
        agents.remove(agent);
        totalAgents--;
        agentCountLabel.setText("Agents: " + totalAgents);
    }

    /**
     * <p>
     * Updates the simulation each frame </p>
     */
    public void update() {
        frame++;
        elapsedTime += 0.01;
        frameLabel.setText("Frame: " + frame);
        updateTimerLabel();

        for (Spawn spawn : spawns) {
            if (frame - spawn.getLastSpawnFrame() >= spawn.getSpawnRateInterval() && spawn.getIsActivelySpawning() && (spawn.getSpawnNumber() != 0) && (spawn.getSpawnDelay() < frame)) {
                // Spawn a new agent
                Agent newAgent = new Agent(totalAgents, spawn.getSpawnAgentSize(), spawn.getCenterLocation().getX(), spawn.getCenterLocation().getY(), spawn.getSpawnAgentXVelocity(), spawn.getSpawnAgentYVelocity(), this);
                addAgent(newAgent, true);
                spawn.setLastSpawnFrame(frame);
                spawn.setSpawnNumber(spawn.getSpawnNumber() - 1);
            }
        }

        for (int i = 0; i < agents.size(); i++) {
            // System.out.println(i.xAcceleration);
            agents.get(i).updateAgent(agents, frame, exits, obstacles);
            agents.get(i).updateCollisionsStorage();
            if (!spawns.isEmpty()) {
                spawns.getFirst().setIsActivelySpawning(!agents.get(i).getInSpawn());
            }

            if (agents.get(i).getLocation().getX() < -agents.get(i).getSize() * 2
                    || agents.get(i).getLocation().getY() < -agents.get(i).getSize() * 2
                    || agents.get(i).getLocation().getX() > width + agents.get(i).getSize()
                    || agents.get(i).getLocation().getY() > height + agents.get(i).getSize() - panelHeight) {
                removeAgent(agents.get(i));
            }

            // if (agents.get(i).inExit(exits) != null) {
            //     //removeAgent(agents.get(i));
            //     NumberOfAgents--;
            // }
            // TODO: Write to Excel sheet of locational data of each Agent
        }
        debugCSV();
        generateCSV(agents.size(), this);
        generateEscapeRateCSV(agents.size(), this);
        System.out.println("Agents Remaingng: " + agents.size());
    }

    /**
     * <p>
     * Updates the GUI</p>
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);

        for (Exit i : exits) {

            // Draw all exits
            g2d.setColor(Color.BLUE);
            if (i.getAlignment() == Exit.alignment.HORIZONTAL) {
                g2d.fillRect((int) i.getLocation().getX(), (int) i.getLocation().getY() - 5, i.getSize(), 10);
            } else {
                g2d.fillRect((int) i.getLocation().getX() - 5, (int) i.getLocation().getY(), 10, i.getSize());
            }
        }

        // for (Spawn i : spawns) {
        //     // Draw all spawns
        //     g2d.setColor(Color.RED);
        //     if (i.getAlignment() == Spawn.alignment.HORIZONTAL) {
        //         g2d.fillRect((int) i.getLocation().getX(), (int) i.getLocation().getY() - 5, i.getSize(), 10);
        //     } else {
        //         g2d.fillRect((int) i.getLocation().getX() - 5, (int) i.getLocation().getY(), 10, i.getSize());
        //     }
        // }
        for (Spawn i : spawns) {
            // Draw all spawns
            g2d.setColor(Color.RED);
            if (i.getAlignment() == Spawn.alignment.HORIZONTAL) {
                g2d.fillRect((int) i.getLocation().getX(), (int) i.getLocation().getY() - 5, i.getSize(), 10);
            } else {
                g2d.fillRect((int) i.getLocation().getX() - 5, (int) i.getLocation().getY(), 10, i.getSize());
            }
            for (Agent j : agents) {
                if (i.inSpawn(j)) {
                    j.setInSpawn(true);
                } else {
                    j.setInSpawn(false);
                    System.out.println("Agent " + j.AgentID + " is not in spawn");
                }
            }

        }

        for (Obstacle i : obstacles) {
            i.paint(g2d);
        }

        for (Agent i : agents) {
            // Draw all agents
            if (i.inExit(exits) != null) {
                g2d.setColor(Color.RED);
            } else if (i.getInSpawn()) {
                g2d.setColor(Color.BLUE);
            } else {
                g2d.setColor(i.getColor());
            }
            g2d.fillOval((int) (i.getLocation().getX() - i.getSize()), (int) (i.getLocation().getY() - i.getSize()), (int) i.getSize() * 2, (int) i.getSize() * 2);
            if (isAgentNumbersEnabled) {
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(i.AgentID), (int) i.getLocation().getX(), (int) i.getLocation().getY());
            }
            g2d.drawLine((int) i.getLocation().getX(), (int) i.getLocation().getY(), (int) i.getLocation().getX(), (int) i.getLocation().getY());
        }

        g2d.setColor(Color.GRAY);
        g2d.fillRect(0, height - panelHeight, width, panelHeight);

        timeLabel.setBounds(10, height - panelHeight + 10, 100, 30);
        pausePlayButton.setBounds(120, height - panelHeight + 10, 80, 30);
        toggleGridButton.setBounds(210, height - panelHeight + 10, 120, 30);
        frameStepButton.setBounds(340, height - panelHeight + 10, 120, 30);
        timeStepButton.setBounds(470, height - panelHeight + 10, 120, 30);
        agentCountLabel.setBounds(10, height - panelHeight + 21, 100, 30);
        frameLabel.setBounds(10, height - panelHeight + 32, 100, 30);
        toggleAgentNumbersButton.setBounds(600, height - panelHeight + 10, 150, 30);

        timeStepInputLabel.setBounds(10, height - panelHeight + 50, 130, 30);
        timeStepInput.setBounds(130, height - panelHeight + 50, 40, 30);

        if (isGridEnabled) {
            g2d.setColor(Color.BLACK);
            int gridHeight = height - panelHeight;
            for (int i = 0; i < width; i += 10) {
                g2d.drawLine(i, 0, i, gridHeight);
            }
            for (int i = 0; i < gridHeight; i += 10) {
                g2d.drawLine(0, i, width, i);
            }

            // for(int i = 0; i < 110; i++){
            //     for (int j = 0; j < 72; j++){/
            //         g2d.drawString(String.valueOf(vectorMap[j][i]), i * 10, j * 10);
            //     }
            // }
        }
    }

    public LinkedList<Agent> getAgents() {
        return agents;
    }

    public LinkedList<Exit> getExits() {
        return exits;
    }

    public LinkedList<Spawn> getSpawns() {
        return spawns;
    }

    public LinkedList<Obstacle> getObstacles() {
        return obstacles;
    }

    /**
     * <p>
     * Generates the final positions CSV file </p>
     *
     * @param numAgents number of agents in the sim
     * @param sim Passthrough the simulation
     */
    public static void generateCSV(int numAgents, Simulation sim) {
        String csvFile = "agent-output.csv"; // Name of the CSV file
        outputPath = System.getProperty("user.dir") + "/PopulationSimulation/outputfiles/";

        String[][] data = new String[numAgents][6]; // Data to write to CSV file
        LinkedList<Agent> agents = sim.getAgents();
        for (int i = 0; i < numAgents; i++) {
            data[i][0] = Integer.toString(agents.get(i).AgentID);
            data[i][1] = Double.toString(agents.get(i).getSize());
            data[i][2] = Double.toString(agents.get(i).getLocation().getX());
            data[i][3] = Double.toString(agents.get(i).getLocation().getY());
            data[i][4] = Double.toString(agents.get(i).getXVelocity());
            data[i][5] = Double.toString(agents.get(i).getYVelocity());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath + csvFile))) {
            writer.newLine();

            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    private void clearEscapeRateCSV() {
        String csvFile = "escape-rate.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath + csvFile))) {

        } catch (IOException e) {
            System.err.println("Error clearing Escape Rate CSV file: " + e.getMessage());
        }
    }

    public static void generateEscapeRateCSV(int numAgents, Simulation sim) {
        String csvFile = "escape-rate.csv";
        int frame = sim.frame;
        outputPath = System.getProperty("user.dir") + "/PopulationSimulation/outputfiles/";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath + csvFile, true))) {
            writer.write(numAgents + "," + frame);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to Escape Rate CSV file: " + e.getMessage());
        }
    }

    /**
     * <p>
     * Generates a CSV that has the total KE for every frame of the simulation
     * </p>
     */
    private void debugCSV() {
        try (FileWriter writer = new FileWriter(outputPath + csvName, true)) {
            writer.write(getKE() + ","
            );
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    /**
     *
     * @return The total KE of every agent in the Simulation
     */
    private double getKE() {
        double totalKE = 0;

        for (Agent i : agents) {
            double velocity = Math.sqrt(Math.pow(i.getXVelocity(), 2) + Math.pow(i.getYVelocity(), 2));

            double KE = 0.5 * i.getSize() * Math.pow(velocity, 2);

            totalKE += KE;
        }
        if (frame == 4) {
            initialKE = totalKE;
        }
        if (frame != 0 && initialKE != totalKE) {
            //OH GOD OH SHIT OH FUCK WE BROKE THE LAWS OF PHYSICS
            //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        }

        return totalKE;
    }

    private Exit findClosestExit(Location location) {
        Exit closestExit = null;
        double minDistance = Double.MAX_VALUE;

        for (Exit exit : exits) {
            //System.out.println("Exit works");
            double distance = distanceTo(location, exit.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                closestExit = exit;
            }
        }

        return closestExit;
    }

    private Spawn findClosestSpawn(Location location) {
        Spawn closestSpawn = null;
        double minDistance = Double.MAX_VALUE;

        for (Spawn spawn : spawns) {
            System.out.println("Spawn works");
            double distance = distanceTo(location, spawn.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                closestSpawn = spawn;
            }
        }

        return closestSpawn;
    }

    private double distanceTo(Location start, Location finish) {
        double dx = start.getX() - finish.getX();
        double dy = start.getY() - finish.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double[] calculateDirectionVector(Location agentLocation, Location exitLocation) {
        double dx = exitLocation.getX() - agentLocation.getX();
        double dy = exitLocation.getY() - agentLocation.getY();
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        return new double[]{dx / magnitude, dy / magnitude};
    }

    public void VectorMapGeneration() {
        vectorMap = map.calculateMap();
    }
}
