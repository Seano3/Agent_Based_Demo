import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.*;

public class Simulation extends JPanel {
    private LinkedList<Agent> agents;
    private LinkedList<Exit> exits;
    private LinkedList<Obstacle> obstacle;
    private Timer timer;
    int frame;
    int width;
    int height;
    int upperBorderHeight;
    int rectHeight;
    String csvName = "Debug.csv";
    private JLabel timeLabel;
    private JLabel agentCountLabel;
    private JLabel frameLabel;
    private JButton pausePlayButton;
    private JButton toggleGridButton;
    private JButton frameStepButton;
    private boolean isPaused;
    private boolean isGridEnabled;
    private long startTime;
    private long pausedTime = 0;
    private long totalPausedDuration;
    private double initialKE = 0;
    private int totalAgents = 0;
    private int totalExits = 0;
    int[][] vectorMap; 

    public Simulation(int width, int height) {
        vectorMapGen map = new vectorMapGen();
        vectorMap = map.getResutls();
        frame = 0;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);

        this.width = width;
        this.height = height;

        agents = new LinkedList<>();
        exits = new LinkedList<>();
        obstacle = new LinkedList<>(); 

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
        totalPausedDuration = 0;
        startTime = System.currentTimeMillis();
        pausedTime = System.currentTimeMillis();

        pausePlayButton.addActionListener(e -> {
            if (isPaused) {
                pausePlayButton.setText("Pause");
                timer.start();
                totalPausedDuration += System.currentTimeMillis() - pausedTime;
                frameStepButton.setForeground(Color.gray);
            } else {
                pausePlayButton.setText("Play");
                timer.stop();
                pausedTime = System.currentTimeMillis();
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



        setLayout(null);
        add(timeLabel);
        add(pausePlayButton);
        add(toggleGridButton);
        add(agentCountLabel);
        add(frameLabel);
        add(frameStepButton);

        timer = new Timer(0, e -> {
            update();
            repaint();
            updateTimerLabel();
        });

    }

    private void updateTimerLabel() {
    long elapsedTime = System.currentTimeMillis() - startTime - totalPausedDuration;
    long hours = (elapsedTime / 3600000) % 24;
    long minutes = (elapsedTime / 60000) % 60;
    long seconds = (elapsedTime / 1000) % 60;
    String formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
    timeLabel.setText("Time: " + formattedTime);
}

    public int getRectHeight() {
        return rectHeight;
    }

    public void addAgent(Agent agent) {
        agents.add(agent);
        totalAgents++;
        agentCountLabel.setText("Agents: " + totalAgents);
    }

    public void addExit(Exit exit) { // no need to remove exits
        exits.add(exit);
        totalExits++;
    }

    public void addObjs(Obstacle obj){
        obstacle.add(obj);
    }

    public void removeAgent(Agent agent) {
        agents.remove(agent);
        totalAgents--;
        agentCountLabel.setText("Agents: " + totalAgents);
    }

    /**
     * <p>Updates the simulation each frame </p>
     */
    public void update() {
        frame++;
        frameLabel.setText("Frame: " + frame);
        for (int i = 0; i < agents.size(); i++) {
            // System.out.println(i.xAcceleration);
            agents.get(i).checkCollisions(agents, frame, width, height, exits, obstacle);
            agents.get(i).updateLocation();
            agents.get(i).updateCollisionsStorage();

            if (agents.get(i).getLocation().getX() < -agents.get(i).getSize()*2 || agents.get(i).getLocation().getY() < -agents.get(i).getSize()*2) {
               removeAgent(agents.get(i));
            }

            // TODO: Write to Excel sheet of locational data of each Agent

            // System.out.println("Agent ID " + i.AgentID + ": Xvel " + i.getXVelocity() +
            // ", Yvel " + i.getYVelocity());
        }
        debugCSV();
        generateCSV(agents.size(), this);
    }

    /**
     * <p> Updates the GUI</p>
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);
        for (Agent i : agents) {
            // Draw all agents
            g2d.setColor(i.getColor());
            g2d.fillOval((int) i.getLocation().getX(), (int) i.getLocation().getY(), (int) i.getSize() * 2, (int) i.getSize() * 2);
            g2d.setColor(Color.BLACK);
            g2d.drawString(String.valueOf(i.AgentID), (int) i.getLocation().getX(), (int) i.getLocation().getY());
        }


        for (Exit i : exits) {
            // Draw all exits
            g2d.setColor(Color.BLUE);
            if(i.getAlignment() == Exit.alignment.HORIZONTAL) {
                g2d.fillRect((int) i.getLocation().getX(), (int) i.getLocation().getY(), i.getSize(), 10);
            } else {
                g2d.fillRect((int) i.getLocation().getX(), (int) i.getLocation().getY(), 10, i.getSize());
            }

        }

        for (Obstacle i : obstacle){
            //Draw all obsticles 
            g2d.setColor(Color.BLACK);
            g2d.fillRect((int) i.getLocation().getX(), (int) i.getLocation().getY(), 200, 250); //TODO: Replace with box height/width
        }
        g2d.setColor(Color.GRAY);
        rectHeight = 200;
        g2d.fillRect(0, height - rectHeight, width, rectHeight);

        timeLabel.setBounds(10, height - rectHeight + 10, 100, 30);
        pausePlayButton.setBounds(120, height - rectHeight + 10, 80, 30);
        toggleGridButton.setBounds(210, height - rectHeight + 10, 120, 30);
        frameStepButton.setBounds(340, height - rectHeight + 10, 120, 30);
        agentCountLabel.setBounds(10, height - rectHeight + 21, 100, 30);
        frameLabel.setBounds(10, height - rectHeight + 32, 100, 30);
        if (isGridEnabled) {
            g2d.setColor(Color.BLACK);
            int gridHeight = height - rectHeight;
            for (int i = 0; i < width; i += 30) {
                g2d.drawLine(i, 0, i, gridHeight);
            }
            for (int i = 0; i < gridHeight; i += 30) {
                g2d.drawLine(0, i, width, i);
            }
        }
    }

    public LinkedList<Agent> getAgents() {
        return agents;
    }

    /**
     * <p> Generates the final positions CSV file </p>
     * @param numAgents number of agents in the sim
     * @param sim Passthrough the simulation 
     */
    public static void generateCSV(int numAgents, Simulation sim) { 
        String csvFile = "agent-output.csv"; // Name of the CSV file 
        String[][] data = new String[numAgents][6]; // Data to write to CSV file
        LinkedList<Agent> agents = sim.getAgents();
        for(int i = 0; i < numAgents; i++){
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
     * <p>Generates a CSV that has the total KE for every frame of the simulation </p>
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

        for(Agent i : agents){
            double velocity = Math.sqrt(Math.pow(i.getXVelocity(), 2) + Math.pow(i.getYVelocity(), 2));

            double KE = 0.5 * i.getSize() * Math.pow(velocity, 2);

            totalKE += KE;
        }
        if  (frame == 4) {
            initialKE = totalKE;
        }
        if (frame != 0 && initialKE != totalKE) {
            //OH GOD OH SHIT OH FUCK WE BROKE THE LAWS OF PHYSICS
            //System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        }

        return totalKE;
    }
}
