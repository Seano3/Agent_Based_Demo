
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
    private JButton toggleAgentNumbersButton;
    private boolean isPaused;
    private boolean isGridEnabled;
    private boolean isAgentNumbersEnabled;
    private double startTime;
    private double initialKE = 0;
    private int totalAgents = 0;
    private int totalExits = 0;
    private int totalSpawns = 0;
    int[][] vectorMap;
    vectorMapGen map;
    private boolean useVectorMap;

    public Simulation(int width, int height, boolean vectorMapEnabled) {
        map = new vectorMapGen(width / 10, (height - 200) / 10);
        frame = 0;
        this.useVectorMap = vectorMapEnabled;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);

        this.width = width;
        this.height = height;

        agents = new LinkedList<>();
        exits = new LinkedList<>();
        obstacles = new LinkedList<>();
        spawns = new LinkedList<>();

        try (FileWriter writer = new FileWriter(csvName)) {
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }

        timeLabel = new JLabel("Time: 00:00:00");
        agentCountLabel = new JLabel("Agents: 0");
        frameLabel = new JLabel("Frame: 0");

        pausePlayButton = new JButton("Play");
        toggleGridButton = new JButton("Enable Grid");
        frameStepButton = new JButton("Frame Step");

        isPaused = true;
        isGridEnabled = false;
        startTime = 0.0;
        elapsedTime = 0.0;

        pausePlayButton.addActionListener(e -> {
            if (isPaused) {
                pausePlayButton.setText("Pause");
                timer.start();
                frameStepButton.setForeground(Color.gray);
            } else {
                pausePlayButton.setText("Play");
                timer.stop();
                frameStepButton.setForeground(Color.black);
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
        add(toggleAgentNumbersButton);

        timer = new Timer(0, e -> {
            update();
            repaint();
            updateTimerLabel();
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
    }

    public void addExit(Exit exit) { // no need to remove exits
        if (exit.buildingExit) {
            map.addExitVM(exit);
        }
        exits.add(exit);
        totalExits++;
    }

    public void addBoxVM(Box box) {
        obstacles.add(box);
    }

    public void addSpawn(Spawn spawn) {
        spawns.add(spawn);
        totalSpawns++;
    }

    public void addAgent(Agent agent, boolean isSpawned) {
        Exit closestExit = findClosestExit(agent.getLocation());
        Spawn closestSpawn = findClosestSpawn(agent.getLocation());


        // Determine if agent is in the spawn so agent class will disable vector map for that agent if it is inside
        if (closestSpawn.inSpawn(agent)) {
            agent.setInSpawn(true);
        } else {
            agent.setInSpawn(false);
        }

        if (isSpawned && closestSpawn != null) {
            double centerX = width / 2.0;
            double centerY = height / 2.0;

            double dx = centerX - closestSpawn.getLocation().getX();
            double dy = centerY - closestSpawn.getLocation().getY();
            double magnitude = Math.sqrt(dx * dx + dy * dy);
            double directionX = dx / magnitude;
            double directionY = dy / magnitude;

            double initialVelocityMagnitude = Math.sqrt(agent.getXVelocity() * agent.getXVelocity() + agent.getYVelocity() * agent.getYVelocity());
            agent.setXVelocity(directionX * initialVelocityMagnitude);
            agent.setYVelocity(directionY * initialVelocityMagnitude);
        } else if (closestExit != null) {
            double[] directionVector = calculateDirectionVector(agent.getLocation(), closestExit.getLocation());
            double xMagnitude = agent.getXVelocity() * agent.getXVelocity();
            double yMagnitude = agent.getYVelocity() * agent.getYVelocity();
            double magnitude = Math.sqrt(xMagnitude + yMagnitude);
            agent.setXVelocity(directionVector[0] * magnitude);
            agent.setYVelocity(directionVector[1] * magnitude);
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
        if (frame == 0) { //create the vector map once the first time the simulation is updated
            VectorMapGeneration();
        }
        frame++;
        elapsedTime += 0.01;
        frameLabel.setText("Frame: " + frame);

        for (Spawn spawn : spawns) {
            if (frame - spawn.getLastSpawnFrame() >= spawn.getSpawnRateInterval() && spawn.getIsActivelySpawning()) {
                // Spawn a new agent
                Agent newAgent = new Agent(totalAgents, spawn.getSpawnAgentSize(), spawn.getLocation().getX(), spawn.getLocation().getY(), spawn.getSpawnAgentXVelocity(), spawn.getSpawnAgentYVelocity(), this);
                addAgent(newAgent, true);
                spawn.setLastSpawnFrame(frame);
            }
        }


        for (int i = 0; i < agents.size(); i++) {
            // System.out.println(i.xAcceleration);
            agents.get(i).checkCollisions(agents, frame, exits, obstacles);
            agents.get(i).updateLocation();
            agents.get(i).updateCollisionsStorage();

            if(agents.get(i).getInSpawn()){
                spawns.get(0).setIsActivelySpawning(false);
            }
            else{
                spawns.get(0).setIsActivelySpawning(true);
            }



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
        for (Agent i : agents) {
            // Draw all agents
            g2d.setColor(i.getColor());
            g2d.fillOval((int) (i.getLocation().getX() - i.getSize()), (int) (i.getLocation().getY()-i.getSize()), (int) i.getSize() * 2, (int) i.getSize() * 2);
            if (isAgentNumbersEnabled) {
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(i.AgentID), (int) i.getLocation().getX(), (int) i.getLocation().getY());
            }
            g2d.drawLine((int)i.getLocation().getX(), (int)i.getLocation().getY(), (int)i.getLocation().getX(), (int)i.getLocation().getY());
        }

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

        for (Spawn i : spawns) {
            // Draw all spawns
            g2d.setColor(Color.RED);
            if (i.getAlignment() == Spawn.alignment.HORIZONTAL) {
                g2d.fillRect((int) i.getLocation().getX(), (int) i.getLocation().getY() - 5, i.getSize(), 10);
            } else {
                g2d.fillRect((int) i.getLocation().getX() - 5, (int) i.getLocation().getY(), 10, i.getSize());
            }
            for(Agent j : agents){
                if(i.inSpawn(j)){
                    j.setInSpawn(true);
                }
                else {
                    j.setInSpawn(false);
                }
            }

        }

        for (Obstacle i : obstacles) {
            i.paint(g2d);
        }

        g2d.setColor(Color.GRAY);
        panelHeight = 200;
        g2d.fillRect(0, height - panelHeight, width, panelHeight);

        timeLabel.setBounds(10, height - panelHeight + 10, 100, 30);
        pausePlayButton.setBounds(120, height - panelHeight + 10, 80, 30);
        toggleGridButton.setBounds(210, height - panelHeight + 10, 120, 30);
        frameStepButton.setBounds(340, height - panelHeight + 10, 120, 30);
        agentCountLabel.setBounds(10, height - panelHeight + 21, 100, 30);
        frameLabel.setBounds(10, height - panelHeight + 32, 100, 30);
        toggleAgentNumbersButton.setBounds(470, height - panelHeight + 10, 150, 30);

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

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
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
     * <p>
     * Generates a CSV that has the total KE for every frame of the simulation
     * </p>
     */
    private void debugCSV() {
        try (FileWriter writer = new FileWriter(csvName, true)) {
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
            System.out.println("Exit works");
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
