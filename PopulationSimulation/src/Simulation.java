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

            //Check Boundaries
            //Possible fix for boundery glitch is to split up each wall into its own if statement and manualy tell the ball which diection to move 
            if(i.getLocation().getX() + i.getSize() > getWidth() - i.getSize()){
                i.setXVelocity(-i.getXVelocity());
            }
            if(i.getLocation().getY() - i.getSize() < 0 - i.getSize() || i.getLocation().getY() + i.getSize() > getHeight() - i.getSize()){
                i.setYVelocity(-i.getYVelocity());
            }

            if(i.getLocation().getX() - i.getSize() < 0 - i.getSize()){
                double force = i.getXForce();
                //System.out.println(force);
                i.applyForce(force, 0);
            }

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
