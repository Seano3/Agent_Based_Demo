
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class vectorMapGen {

    private final int LENGTH;
    private final int HEIGHT;
    private static final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // these set the {dx dy} for each possible jump in the BFS algo
    private int[][] results;
    int[][] map;
    int agentScale;

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < LENGTH && y >= 0 && y < HEIGHT;
    }

    public int getHeight() { return HEIGHT; }
    public int getLength() { return LENGTH; }


    public vectorMapGen(Simulation sim) {
        agentScale = (int) Math.ceil(sim.getAgents().getFirst().getSize() + 5); // assumes agents are present, round up to prevent bugs
        LENGTH = sim.width;
        HEIGHT = sim.height - sim.getPanelHeight();
        map = new int[LENGTH][HEIGHT];

        //THESE THREE ARE APPLIED TO EVREY MAP AND ARE NOT HARD CODED BECUASE THEY ARE NESSICARY FOR FUNCTION
        for (int x = 0; x < LENGTH; x++) { //INIT whole map
            for (int y = 0; y < HEIGHT; y++) {
                map[x][y] = Integer.MAX_VALUE;
            }
        }

        for (int i = 0; i < LENGTH; i++) { //Long Borders
            for (int j = 0; j < 10; j++) {
                map[i][0 + j] = -1;
                map[i][HEIGHT - 1 - j] = -1;
            }
        }

        for (int i = 0; i < HEIGHT; i++) { //Short Borders
            for (int j = 0; j < 10; j++) {
                map[0 + j][i] = -1;
                map[LENGTH - 1 - j][i] = -1;
            }
        }

        // public Line(Location location, Location endpoint, double Force) {
        // Line(sim.width / 2, 0), sim.width / 2, sim.height));
        for (Obstacle OBS : sim.getObstacles()) {
            if (Line.class.isAssignableFrom(OBS.getClass())) {
                Line line = (Line) OBS;
                double xSize = line.getEndpoint().getX() - line.getLocation().getX();
                double ySize = line.getEndpoint().getY() - line.getLocation().getY();
                double xIter = line.getLocation().getX();
                double yIter = line.getLocation().getY();
                int linePasses = 250;
                int i = 0;
                while (i < linePasses) {
                    for (int j = (int) xIter - agentScale; j < (int) xIter + agentScale; j++) {
                        for (int k = (int) yIter - agentScale; k < (int) yIter + agentScale; k++) {
                            if (inBounds(j, k)) {
                                map[j][k] = -1;
                            }
                        }
                    }
                    xIter += xSize / linePasses;
                    yIter += ySize / linePasses;
                    i++;
                    if (i == 250) {
                        System.out.println("Added obs to vector map");
                    }
                }
            } else if (Box.class.isAssignableFrom(OBS.getClass())) {
                addBoxObsticle((Box) OBS);
            } else { // HOLY SHIT THIS IS BAD CAPTAIN THE SHIP IS GOING DOWN
                System.exit(-1138);
            }
        }

        for (Exit exit : sim.getExits()) {
            int xPos = (int) exit.getLocation().getX();
            int yPos = (int) exit.getLocation().getY();
            if (exit.getAlignment() == Exit.alignment.VERTICAL) {
                for (int i = xPos - agentScale; i < xPos + agentScale; i++) {
                    for (int j = yPos + agentScale; j < yPos + exit.getSize() - agentScale; j++) {
                        if (inBounds(i, j)) {
                            if (exit.buildingExit) {
                                map[i][j] = 0;
                            } else {
                                map[i][j] = Integer.MAX_VALUE;
                            }
                        }
                    }
                }
            } else { // horizontal
                for (int i = yPos - agentScale; i < yPos + agentScale; i++) {
                    for (int j = xPos + agentScale; j < xPos + exit.getSize() - agentScale; j++) {
                        if (inBounds(j, i)) {
                            if (exit.buildingExit) {
                                map[j][i] = 0;
                            } else {
                                map[j][i] = Integer.MAX_VALUE;
                            }
                        }
                    }
                }
            }
        }
    }

    public int[][] calculateMap() {

        int[][] result = calculateDistances(map);
        try {
            exportToCSV(map, "JDJDJD.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        results = new int[HEIGHT][LENGTH];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                results[j][i] = result[i][j];
            }
        }

        try {
            exportToCSV(results, "debug_vector_map.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    private static int[][] calculateDistances(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int[][] distances = new int[rows][cols];

        // Initialize distances to Integer.MAX_VALUE
        for (int x = 0; x < distances.length; x++) {
            for (int y = 0; y < distances[0].length; y++) {
                distances[x][y] = Integer.MAX_VALUE;
            }
        }

        List<int[]> zeroPositions = findZeroPositions(grid);

        if (!zeroPositions.isEmpty()) {
            // Set distances to 0 for all 0 positions
            for (int[] pos : zeroPositions) {
                distances[pos[0]][pos[1]] = 0;
            }

            // BFS from all 0 positions
            Queue<int[]> queue = new LinkedList<>(zeroPositions);
            boolean[][] visited = new boolean[rows][cols];

            while (!queue.isEmpty()) {
                int[] currentPosition = queue.poll();

                // Mark as visited
                visited[currentPosition[0]][currentPosition[1]] = true;

                // Update distances for neighbors
                for (int[] dir : directions) {
                    int newRow = currentPosition[0] + dir[0], newCol = currentPosition[1] + dir[1];

                    if (isValidPosition(grid, newRow, newCol) && !visited[newRow][newCol]) {
                        distances[newRow][newCol] = distances[currentPosition[0]][currentPosition[1]] + 1;
                        queue.offer(new int[]{newRow, newCol});
                        visited[newRow][newCol] = true;
                    }
                }
            }
        }
        return distances;
    }

    private static List<int[]> findZeroPositions(int[][] grid) {
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 0) {
                    positions.add(new int[]{r, c});
                }
            }
        }
        return positions;
    }

    private static boolean isValidPosition(int[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length && grid[row][col] != 0 && grid[row][col] != -1;
    }

    public static void exportToCSV(int[][] data, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/PopulationSimulation/outputfiles/" + fileName))) {
            for (int[] row : data) {
                for (int cell : row) {
                    writer.write(String.valueOf(cell));
                    writer.write(",");
                }
                writer.newLine();
            }
        }
    }

    public void addExitVM(Exit exit) {
        int x = (int) exit.getLocation().getX();
        int y = (int) exit.getLocation().getY();
        int size = exit.getSize();
        if (exit.getAlignment() == Exit.alignment.HORIZONTAL) {
            //System.out.println("Horizonal");
            for (int i = 0; i < size; i++) {
                if (exit.buildingExit) {
                    for (int k = x + agentScale; k < x + size - agentScale; k++) {
                        for (int j = -agentScale; j < agentScale; j++) { //this for loop gets rid of the vector map buffer for each exits.
                            if (inBounds(k, j)) {
                                if (y == 0) {
                                    map[k][0 + j] = Integer.MAX_VALUE; //Exits on top of map
                                } else {
                                    map[k][y - j] = Integer.MAX_VALUE; //Exits on bottom of map
                                }
                            }
                        }
                        map[k][y] = 0;
                    }
                } else {
                    // System.out.println("Exit not building exit");
                    for (int k = x + agentScale; k < x + size - agentScale; k++) {
                        for (int j = -agentScale; j < agentScale; j++) { //this for loop is the buffer zone on each side of the line.
                            map[k][y + j] = Integer.MAX_VALUE; //Removes barrer marker in affected zones 
                        }
                    }

                }
            }
        } else {
            // System.out.println("Vertical");
            if (exit.buildingExit) {
                for (int k = y + agentScale; k < y + size - agentScale; k++) {
                    for (int j = 0; j < agentScale; j++) { //this for loop gets rid of the vector map buffer for each exits.
                        if (inBounds(k, j)) {
                            if (x == 0) {
                                map[0 + j][k] = Integer.MAX_VALUE; //Exits on left of map
                            } else {
                                map[x - j][k] = Integer.MAX_VALUE; //Exits on right of map
                            }
                        }
                    }
                    map[x][k] = 0;
                }
            } else {
                //  System.out.println("Exit not building exit");
                for (int k = y + agentScale; k < y + size - agentScale; k++) {
                    for (int j = -agentScale; j < agentScale; j++) { //this for loop is the buffer zone on each side of the line.
                        map[x + j][k] = Integer.MAX_VALUE; //Removes barrer marker in affected zones 
                    }
                }
            }

        }
    }

    public void addLineVM(Line line) {
        int x1 = (int) line.getLocation().getX();
        int y1 = (int) line.getLocation().getY();

        int x2 = (int) line.getEndpoint().getX();
        int y2 = (int) line.getEndpoint().getY();

        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                map[i][j] = -1;
            }
        }
    }

    public void addBoxObsticle(Box box) {
        int x = (int) box.getLocation().getX();
        int y = (int) box.getLocation().getY();
        int width = (int) box.getWidth();
        int height = (int) box.getHeight();
        for (int i = 0; i < width; i++) { // horizontals
            for (int j = -agentScale; j < agentScale; j++) {
                if (inBounds(x + i, y + j))
                    map[x + i][y + j] = -1;
                if (inBounds(x + i, y + height + j))
                    map[x + i][y + height + j] = -1;
            }
        }
        for (int i = 0; i < height; i++) { // verticals
            for (int j = -agentScale; j < agentScale; j++) {
                if (inBounds(x + j, y + i))
                    map[x + j][y + i] = -1;
                if (inBounds(x + width + j, y + i))
                    map[x + width + j][y + i] = -1;
            }
        }

    }

    public void addLineObsiticle(Line line) {

    }

    public void addObsitcle() {

    }
}
