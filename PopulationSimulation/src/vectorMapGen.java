import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class vectorMapGen {
    private final int LENGTH = 110;
    private final int HEIGHT = 72;
    private static final int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } }; // these set the {dx dy} for each possible jump in the BFS algo
    private int[][] results;

    public vectorMapGen() {
        int[][] map = new int[LENGTH][HEIGHT];

        for (int x = 0; x < LENGTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                map[x][y] = Integer.MAX_VALUE;
            }
        }

        // door 1
        for (int x = 44; x <= 45; x++) {
            map[x][71] = 0;
        }

        // door 2
        for (int y = 67; y <= 71; y++) {
            map[0][y] = 0;
        }

        // door 4
        for (int x = 18; x <= 19; x++) {
            map[x][0] = 0;
        }

        // door 5
        for (int x = 92; x <= 93; x++) {
            map[x][0] = 0;
        }

        int[][] result = calculateDistances(map);

        results = new int[72][110]; 

        for (int i = 0; i < result.length; i++){
            for (int j = 0; j < result[0].length; j++){
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
                        queue.offer(new int[] { newRow, newCol });
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
                    positions.add(new int[] { r, c });
                }
            }
        }
        return positions;
    }

    private static boolean isValidPosition(int[][] grid, int row, int col) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[0].length && grid[row][col] != 0 && grid[row][col] != -1;
    }

    public int[][] getResults() {
        return results;
    }
}