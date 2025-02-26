
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class App {

    final int LENGTH = 100;
    final int HEIGHT = 50;
    private static final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; //these set the {dx dy} for each possible jump in the BFS algo

    public static void main(String[] args) throws Exception {
        int[][] map = new int[100][50];

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 50; y++) {
                map[x][y] = Integer.MAX_VALUE;
            }
        }

        // for (int x = 85; x <= 105; x++) { // Creates an obstacle block
        //     for (int y = 25; y <= 50; y++) {
        //         map[x][y] = -1;
        //     }
        // }
        for (int i = 45; i < 55; i++) {
            map[i][0] = 0;
        }

        for (int x = 40; x < 60; x++) {
            for (int y = 20; y < 30; y++) {
                map[x][y] = -1;
            }
        }

        // int[][] inputGrid = {
        // { 0, 1, 1, 1 },
        // { 1, 1, 1, 1 },
        // { 1, 1, 1, 1 },
        // { 0, 1, 1, 0 }
        // };
        int[][] result = calculateDistances(map);

        int[][] results = new int[50][100];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                results[j][i] = result[i][j];
            }
        }

        // System.out.println("Results Grid: \n");
        // for (int x = 0; x < result.length; x++) {
        //     for (int y = 0; y < result[0].length; y++) {
        //         System.out.print("[" + result[x][y] + "]");
        //     }
        //     System.out.println();
        // }
        try {
            exportToCSV(results, "vector_map.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // starting with a zero. find all nearby squares without a number or a higher
        // number and set it to n + 1 where in is your squares number
    }

    public static int[][] calculateDistances(int[][] grid) {
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

    private static boolean isValidPosition(int grid[][], int row, int col) {
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

}
