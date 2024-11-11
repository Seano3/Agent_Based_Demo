import java.util.*;

public class Pathfinding {
    Node[][] grid;
    private int gridWidth, gridHeight;

    public Pathfinding(int width, int height) {
        this.gridWidth = width;
        this.gridHeight = height;
        this.grid = new Node[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = new Node(x, y, true);
            }
        }
    }

    private void markObstacles(List<Obstacle> obstacles) {
        for (Obstacle obstacle : obstacles) {
            int x = (int) obstacle.getLocation().getX();
            int y = (int) obstacle.getLocation().getY();
            if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
                grid[x][y].walkable = false;
            }
        }
    }

    public List<Node> findPath(Node startNode, Node endNode) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        HashSet<Node> closedList = new HashSet<>();
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();
            closedList.add(currentNode);

            if (currentNode == endNode) {
                return retracePath(startNode, endNode);
            }

            for (Node neighbor : getNeighbors(currentNode)) {
                if (!neighbor.walkable || closedList.contains(neighbor)) continue;

                double newMovementCostToNeighbor = currentNode.gCost + getDistance(currentNode, neighbor);
                if (newMovementCostToNeighbor < neighbor.gCost || !openList.contains(neighbor)) {
                    neighbor.gCost = newMovementCostToNeighbor;
                    neighbor.calculateCosts(endNode);
                    neighbor.parent = currentNode;

                    if (!openList.contains(neighbor)) openList.add(neighbor);
                }
            }
        }
        return null;
    }

    private List<Node> retracePath(Node startNode, Node endNode) {
        List<Node> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != startNode) {
            path.add(currentNode);
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int newX = node.x + dx[i];
            int newY = node.y + dy[i];

            if (newX >= 0 && newX < gridWidth && newY >= 0 && newY < gridHeight) {
                neighbors.add(grid[newX][newY]);
            }
        }
        return neighbors;
    }

    private double getDistance(Node a, Node b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}