import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

public class Simulation extends JPanel{
    private LinkedList<Agent> agents;
    private Timer timer;
    int frame;
    int width; 
    int height;



    public Simulation(int width, int height){  
        frame = 0;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);

        this.width = width;
        this.height = height;

        agents = new LinkedList<>(); 

        timer = new Timer(16, e -> {
            update();
            repaint();
        });
        timer.start();
        

    }

    public void addAgent(Agent agent) {
        agents.add(agent);
    }

    public void update(){
        int totalVelocty = 0;
        for (Agent i : agents) {
            frame++;
            //System.out.println(i.xAccelaration);
            i.checkCollisions(agents, frame, width, height);
            i.updateLocation();
            i.updateCollisionsStorage();


            totalVelocty += Math.abs(i.getXVelocity()) + Math.abs(i.getYVelocity());

            //TODO: Write to Excel sheet of locational data of each Agent
            
            //System.out.println("Agent ID " + i.AgentID + ": Xvel " + i.getXVelocity() + ", Yvel " + i.getYVelocity());
        }
        System.out.println("Total Energy: " + totalVelocty);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);
        for (Agent i : agents) {
            //Draw all agents
            g2d.fillOval((int) i.getLocation().getX(), (int) i.getLocation().getY(), (int) i.getSize() * 2, (int) i.getSize() * 2);
            g2d.setColor(Color.RED);
        }

    }

    public LinkedList<Agent> getAgents(){
        return agents;
    }
}
