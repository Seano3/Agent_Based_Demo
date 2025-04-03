
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
    private JButton toggleHeatMapButton;
    private JLabel timeStepInputLabel;
    private JTextField timeStepInput;
    private boolean isPaused;
    private boolean isGridEnabled;
    private boolean isAgentNumbersEnabled;
    private double startTime;
    private double initialKE = 0;
    private int currentAgentCount = 0;
    private int totalExits = 0;
    private int totalSpawns = 0;
    private int timeStep = 1;
    int[][] vectorMap;
    vectorMapGen map;
    private boolean useVectorMap;
    private static String outputPath;
    private int lifetimeAgentCount;
    private boolean heatMapEnabled = false;


    /**
     * <p> Constructor for the Simulation class </p>
     *
     * @param width Width of the simulation panel in pixels
     * @param height Height of the simulation panel in pixels
     */
    public Simulation(int width, int height) {
        lifetimeAgentCount = 0;
        frame = 0;
        this.useVectorMap = false;
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
        toggleHeatMapButton = new JButton("Show Heat Map");
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

        toggleHeatMapButton.addActionListener(e -> {
            if (heatMapEnabled) {
                toggleHeatMapButton.setText("Show Heat Map");
                heatMapEnabled = false;
                repaint();
            } else {
                toggleHeatMapButton.setText("Hide Heat Map");
                heatMapEnabled = true;
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
        add(toggleHeatMapButton);
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

    public int getLifetimeAgentCount() { return lifetimeAgentCount;}

    public boolean vectorMapEnabled() {
        return useVectorMap;
    }

    /**
     * <p> Toggles the vector map on and off </p>
     */
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

    /**
     * <p> Adds an exit to the simulation </p>
     *
     * @param exit Exit object to add
     */
    public void addExit(Exit exit) { // no need to remove exits
        exits.add(exit);
        totalExits++;
    }

    /**
     * <p> Adds a spawn to the simulation </p>
     *
     * @param spawn Spawn object to add
     */
    public void addSpawn(Spawn spawn) {
        spawns.add(spawn);
        totalSpawns++;
    }

    /**
     * <p> Adds an agent to the simulation </p>
     *
     * @param agent Agent object to add
     * @param isSpawned boolean to determine if the agent is spawned from a spawner or not
     */
    public void addAgent(Agent agent, boolean isSpawned) {
                if (isSpawned) { // If agent is spawned from a spawner

            Spawn closestSpawn = agent.inSpawn(spawns); // run spawn calc to update bool
            double initialVelocityMagnitude = Math.sqrt(agent.getXVelocity() * agent.getXVelocity() + agent.getYVelocity() * agent.getYVelocity());

            if (closestSpawn.getAlignment() == Spawn.alignment.HORIZONTAL) { // If the spawn is horizontal
                if (closestSpawn.getDirection() == Spawn.direction.LEFT) { // If the spawn is on the top side
                    agent.setXVelocity(0);
                    agent.setYVelocity(-initialVelocityMagnitude);
                } else { // If the spawn is on the bottom side
                    agent.setXVelocity(0);
                    agent.setYVelocity(initialVelocityMagnitude);
                }
            } else { // If the spawn is vertical
                if (closestSpawn.getDirection() == Spawn.direction.LEFT) { // If the spawn is on the left side
                    agent.setXVelocity(-initialVelocityMagnitude);
                    agent.setYVelocity(0);
                } else { // If the spawn is on the right side
                    agent.setXVelocity(initialVelocityMagnitude);
                    agent.setYVelocity(0);
                }
            }
        }
        agents.add(agent);
        if (lifetimeAgentCount == 0 && isSpawned) // initialize vector map if spawned agent is first agent, since we don't have agent size until then
            toggleVectorMap();
        lifetimeAgentCount++;
        currentAgentCount++;
        agentCountLabel.setText("Agents: " + currentAgentCount);

    }

    /**
     * <p> Adds an obstacle to the simulation </p>
     *
     * @param obstacle Obstacle object to add
     */
    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }

    /**
     * <p> Removes an agent from the simulation </p>
     *
     * @param agent Agent object to remove
     */
    public void removeAgent(Agent agent) {
        agents.remove(agent);
        currentAgentCount--;
        agentCountLabel.setText("Agents: " + currentAgentCount);
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
        for (Spawn i : spawns)
                i.updateSpawner(frame, this);

        for (int i = 0; i < agents.size(); i++) {
            // System.out.println(i.xAcceleration);
            agents.get(i).updateAgent(this);
            agents.get(i).updateCollisionsStorage();

            if (agents.get(i).getLocation().getX() < -agents.get(i).getSize() * 2
                    || agents.get(i).getLocation().getY() < -agents.get(i).getSize() * 2
                    || agents.get(i).getLocation().getX() > width + agents.get(i).getSize()
                    || agents.get(i).getLocation().getY() > height + agents.get(i).getSize() - panelHeight) {
                removeAgent(agents.get(i));
            }

            // TODO: Write to Excel sheet of locational data of each Agent
        }
        debugCSV();
        generateCSV(agents.size(), this);
        generateEscapeRateCSV(agents.size(), this);
//        System.out.println("Agents Remaingng: " + agents.size());
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

        for (Spawn i : spawns) {
            // Draw all spawns
            g2d.setColor(Color.RED);
            if (i.getAlignment() == Spawn.alignment.HORIZONTAL) {
                g2d.fillRect((int) i.getLocation().getX(), (int) i.getLocation().getY() - 5, i.getSize(), 10);
            } else {
                g2d.fillRect((int) i.getLocation().getX() - 5, (int) i.getLocation().getY(), 10, i.getSize());
            }
        }

        for (Obstacle i : obstacles) {
            i.paint(g2d);
        }

        for (Agent i : agents) {
            // Draw all agents
            if (i.inExit(exits) != null) {
                g2d.setColor(Color.RED);
            } else if (i.inSpawn(spawns) != null) {
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
        toggleHeatMapButton.setBounds(760, height - panelHeight + 10, 150, 30);

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
        }

        // heat map mode
        if (heatMapEnabled) {
            for (int i = 0; i < vectorMap.length; i++) {
                for (int j = 0; j < vectorMap[i].length; j++) {
                    int red;
                    if (vectorMap[i][j] == -1) {
                        red = 0;
                    } else if (vectorMap[i][j] == 0) {
                        red = 255;
                    } else if (vectorMap[i][j] > 2275) {
                        red = 10;
                    } else {
                        red = 255 - (vectorMap[i][j] / 10);
                    }

                    Color heat = new Color(255, red,red);
                    g2d.setColor(heat);
                    g2d.drawLine(j,i,j,i);
                }
            }
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

    /**
     * <p> Clears the escape rate CSV file </p>
     */
    private void clearEscapeRateCSV() {
        String csvFile = "escape-rate.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath + csvFile))) {

        } catch (IOException e) {
            System.err.println("Error clearing Escape Rate CSV file: " + e.getMessage());
        }
    }

    /**
     * <p> Generates the escape rate CSV file </p>
     *
     * @param numAgents number of agents in the sim
     * @param sim Passthrough the simulation
     */
    public static void generateEscapeRateCSV(int numAgents, Simulation sim) {
        String csvFile = "escape-rate.csv";
        int frame = sim.frame;
        String outputPath = System.getProperty("user.dir") + "/PopulationSimulation/outputfiles/";

        if (frame % 100 == 0) {
            double elapsedTimeInSeconds = frame / 100.0; // Calculate seconds based on frame rate
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath + csvFile, true))) {
                writer.write(numAgents + "," + frame + "," + String.format("%.2f", elapsedTimeInSeconds));
                writer.newLine();
            } catch (IOException e) {
                System.err.println("Error writing to Escape Rate CSV file: " + e.getMessage());
            }
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

    /**
     * <p>Generates the vector map for the simulation</p>
     */
    public void VectorMapGeneration() {
        vectorMap = map.calculateMap();
    }

    public int getFrame() { return frame; }
}
