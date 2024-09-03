import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

public class Simulation extends JPanel{
    private LinkedList<Agent> agents;
    private Timer timer;


    public Simulation(int width, int height){  
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);

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
        for (Agent i : agents) {
            i.updateLocation();

            i.checkCollisions(agents);

            //TODO: Write to Excel sheet of locational data of each Agent
            System.out.println("Agent ID " + i.AgentID + ": Xvel " + i.getXVelocity() + ", Yvel " + i.getYVelocity());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);
        for (Agent i : agents) {
            //Draw all agents
            g2d.fillOval((int) i.getLocation().getX(), (int) i.getLocation().getY(), (int) i.getSize() * 2, (int) i.getSize() * 2);
        }

    }

    public LinkedList<Agent> getAgents(){
        return agents;
    }
}
