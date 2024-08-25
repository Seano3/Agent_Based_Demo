public class RunSimulation {
    public static void main(String[] args) throws Exception {
        Simulation sim = new Simulation(100, 100);

        //Initalize Agents
        sim.addAgent(new Agent("test1", 3, 25, 50, 5, 0));
        sim.addAgent(new Agent("test2", 3, 75, 50, -5, 0));

        while (true) {
            sim.update();
            System.out.println("Test1 at " +  sim.getAgents().get(0).getLocation().getX() + ", " + sim.getAgents().get(0).getLocation().getY());
            System.out.println("Test2 at " +  sim.getAgents().get(1).getLocation().getX() + ", " + sim.getAgents().get(1).getLocation().getY());

            try {
                Thread.sleep(1000); //Sleep for 1 Second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
