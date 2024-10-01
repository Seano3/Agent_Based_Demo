import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import javax.swing.*;

public class Simulation extends JPanel {
    private LinkedList<Agent> agents;
    private Timer timer;
    int frame;
    int width;
    int height;
    int upperBorderHeight;
    int rectHeight;
    String csvName = "Debug.csv";
    private JLabel timeLabel;
    private JButton pausePlayButton;
    private JButton toggleGridButton;
    private boolean isPaused;
    private boolean isGridEnabled;
    private long startTime;
    private long pausedTime;
    private long totalPausedDuration;
    private double initialKE = 0;


    public Simulation(int width, int height) {
        frame = 0;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);

        this.width = width;
        this.height = height;

        agents = new LinkedList<>();

        try (FileWriter writer = new FileWriter(csvName)) {
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }

        timeLabel = new JLabel("Time: 00:00:00");
        pausePlayButton = new JButton("Pause");
        toggleGridButton = new JButton("Enable Grid");
        isPaused = false;
        isGridEnabled = false;
        totalPausedDuration = 0;

        pausePlayButton.addActionListener(e -> {
            if (isPaused) {
                pausePlayButton.setText("Pause");
                timer.start();
                totalPausedDuration += System.currentTimeMillis() - pausedTime;
            } else {
                pausePlayButton.setText("Play");
                timer.stop();
                pausedTime = System.currentTimeMillis();
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
        setLayout(null);
        add(timeLabel);
        add(pausePlayButton);
        add(toggleGridButton);

        startTime = System.currentTimeMillis();

        timer = new Timer(16, e -> {
            update();
            repaint();
            updateTimerLabel();
        });
        timer.start();



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
    }

    public void update() {
        for (Agent i : agents) {
            frame++;
            // System.out.println(i.xAccelaration);
            i.checkCollisions(agents, frame, width, height);
            i.updateLocation();
            i.updateCollisionsStorage();

            // TODO: Write to Excel sheet of locational data of each Agent

            // System.out.println("Agent ID " + i.AgentID + ": Xvel " + i.getXVelocity() +
            // ", Yvel " + i.getYVelocity());
        }
        debugCSV();
        generateCSV(agents.size(), this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);
        for (Agent i : agents) {
            // Draw all agents
            g2d.fillOval((int) i.getLocation().getX(), (int) i.getLocation().getY(), (int) i.getSize() * 2,
                    (int) i.getSize() * 2);
            g2d.setColor(Color.RED);
        }
        g2d.setColor(Color.GRAY);
        rectHeight = 200;
        g2d.fillRect(0, height - rectHeight, width, rectHeight);

        timeLabel.setBounds(10, height - rectHeight + 10, 100, 30);
        pausePlayButton.setBounds(120, height - rectHeight + 10, 80, 30);
        toggleGridButton.setBounds(210, height - rectHeight + 10, 120, 30);
        if (isGridEnabled) {
            g2d.setColor(Color.BLACK);
            int gridHeight = height - rectHeight;
            for (int i = 0; i < width; i += 10) {
                g2d.drawLine(i, 0, i, gridHeight);
            }
            for (int i = 0; i < gridHeight; i += 10) {
                g2d.drawLine(0, i, width, i);
            }
        }
    }

    public LinkedList<Agent> getAgents() {
        return agents;
    }

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

    private void debugCSV() {
        try (FileWriter writer = new FileWriter(csvName, true)) {
            writer.write(getKE() + ","
                        );
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

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
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        }

        return totalKE;
    }
}
