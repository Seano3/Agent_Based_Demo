public class RunSimulation {
    public static void main(String[] args) throws Exception {
        Simulation sim = new Simulation(100, 100);

        //Initalize Agents
        sim.addAgent(new Agent("test1", 3, 25, 50));
        sim.addAgent(new Agent("test2", 3, 75, 50));

        while (true) {
            sim.update();
        }
    }
}
