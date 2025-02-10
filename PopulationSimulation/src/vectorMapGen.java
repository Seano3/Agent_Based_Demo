
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
        for (int x = 0; x < LENGTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                map[x][y] = Integer.MAX_VALUE;
            }
        }

        for (int i = 0; i < LENGTH; i++) {
            map[i][0] = -1;
            map[i][HEIGHT - 1] = -1;
        }

        for (int i = 0; i < HEIGHT; i++) {
            map[0][i] = -1;
            map[LENGTH - 1][i] = -1;
        }

        //HARD CODED LINE IN MIDDLE OF SIM FOR SHAWN
        //LINE DOWN MIDDLE
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 540; j < 560; j++) {
                map[j][i] = -1;
            }
        }

        //DOOR IN MIDDLE OF LINE 
        for (int i = 370; i < 400; i++) {
            for (int j = 540; j < 560; j++) {
                map[j][i] = Integer.MAX_VALUE;
            }
        }

        //BUILDING EXIT
        for (int i = 50; i < 100; i++) {
            map[i][0] = 0;
        }

        //END OF HARD CODED 
        // System.out.println("Results Grid: \n");
        // for (int x = 0; x < result.length; x++) {
        //     for (int y = 0; y < result[0].length; y++) {
        //         System.out.print("[" + result[x][y] + "]");
        //     }
        //     System.out.println();
        // }
        //System.out.println("MAP: " + results[10][10]);
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
        int x = (int) exit.getLocation().getX() / 10;
        int y = (int) exit.getLocation().getY() / 10;
        int size = exit.getSize() / 10;
        if (exit.getAlignment() == Exit.alignment.VERTICAL) {
            for (int i = 0; i < size; i++) {
                if (exit.buildingExit) {
                    map[x][y + i] = 0;
                } else {
                    map[x][y + i] = Integer.MAX_VALUE;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (exit.buildingExit) {
                    map[x + i][y] = 0;
                } else {
                    map[x + i][y] = Integer.MAX_VALUE;
                }
            }
        }
    }

    public void addBoxObsticle(Box box) {
        int x = (int) box.getLocation().getX() / 10;
        int y = (int) box.getLocation().getY() / 10;
        int width = (int) box.getWidth() / 10;
        int height = (int) box.getHeight() / 10;
        for (int i = 0; i < width; i++) {
            map[x + i][y] = -1;
            map[x + i][y + height] = -1;
        }
        for (int i = 0; i < height; i++) {
            map[x][y + i] = -1;
            map[x + width][y + i] = -1;
        }

    }

    public void addObsitcle() {

    }
}
