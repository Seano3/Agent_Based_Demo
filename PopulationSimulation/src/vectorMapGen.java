
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class vectorMapGen {

    private int LENGTH;
    private int HEIGHT;
    private static final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // these set the {dx dy} for each possible jump in the BFS algo
    private int[][] results;
    int[][] map;

    public vectorMapGen(int length, int height) {
        LENGTH = length;
        HEIGHT = height;
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

        //HARD CODED LINE IN MIDDLE OF SIM FOR SHAWN
        //LINE DOWN MIDDLE
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 540; j < 560; j++) {
                map[j][i] = -1;
            }
        }

        //END OF HARD CODED 
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
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
            System.out.println("Horizonal");
            for (int i = 0; i < size; i++) {
                if (exit.buildingExit) {
                    for (int k = x; k < x + size; k++) {
                        for (int j = 0; j < 10; j++) { //this for loop gets rid of the vector map buffer for each exits. 
                            if (y == 0) {
                                map[k][0 + j] = Integer.MAX_VALUE; //Exits on top of map
                            } else {
                                map[k][y - j] = Integer.MAX_VALUE; //Exits on bottom of map
                            }
                        }
                        map[k][y] = 0;
                    }
                } else {
                    System.out.println("Exit not building exit");
                    for (int k = x; k < x + size; k++) {
                        for (int j = -5; j < 5; j++) { //this for loop is the buffer zone on each side of the line. 
                            map[k][y + j] = Integer.MAX_VALUE; //Removes barrer marker in affected zones 
                        }
                    }

                }
            }
        } else {
            System.out.println("Vertical");
            if (exit.buildingExit) {
                for (int k = y; k < y + size; k++) {
                    for (int j = 0; j < 10; j++) { //this for loop gets rid of the vector map buffer for each exits. 
                        if (x == 0) {
                            map[0 + j][k] = Integer.MAX_VALUE; //Exits on left of map
                        } else {
                            map[x - j][k] = Integer.MAX_VALUE; //Exits on right of map
                        }
                    }
                    map[x][k] = 0;
                }
            } else {
                System.out.println("Exit not building exit");
                for (int k = y; k < y + size; k++) {
                    for (int j = -5; j < 5; j++) { //this for loop is the buffer zone on each side of the line. 
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
        for (int i = 0; i < width; i++) {
            map[x + i][y] = -1;
            map[x + i][y + height] = -1;
        }
        for (int i = 0; i < height; i++) {
            map[x][y + i] = -1;
            map[x + width][y + i] = -1;
        }

    }

    public void addLineObsiticle(Line line) {

    }

    public void addObsitcle() {

    }
}
