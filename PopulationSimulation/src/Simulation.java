import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

        timer = new Timer(16, e -> {
            update();
            repaint();
        });
        timer.start();

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
        g2d.setColor(Color.BLACK);
        rectHeight = 200;
        g2d.fillRect(0, height - rectHeight, width, rectHeight);

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

            totalKE =+ KE; 
        }

        return totalKE;
    }
}
