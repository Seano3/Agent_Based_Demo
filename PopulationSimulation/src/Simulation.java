import java.util.LinkedList;
import javax.swing.*;
import java.awt.*;

public class Simulation extends JPanel{
    private LinkedList<Agent> agents;
    private Timer timer;


    public Simulation(int width, int height){  
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);

        agents = new LinkedList<Agent>(); 

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

            if(i.checkCollisions(agents)){
                i.setXVelocity(-i.getXVelocity());
                i.setYVelocity(-i.getYVelocity());
                //TODO: Other Circle and More accurate transfer of momentum (Size/Direction)
            }

            //Check Boundaries
            if(i.getLocation().getX() - i.getSize() < 0 || i.getLocation().getX() + i.getSize() > getWidth()){
                i.setXVelocity(-i.getXVelocity());
            }
            if(i.getLocation().getY() - i.getSize() < 0 || i.getLocation().getY() + i.getSize() > getWidth()){
                i.setYVelocity(-i.getYVelocity());
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);
        for (Agent i : agents) {
            g2d.fillOval((int) i.getLocation().getX(), (int) i.getLocation().getY(), i.getSize() * 2, i.getSize() * 2);
        }

    }

    public LinkedList<Agent> getAgents(){
        return agents;
    }
}
