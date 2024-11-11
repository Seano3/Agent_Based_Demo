public class Node {
    public int x, y;
    public double gCost, hCost, fCost;
    public Node parent;
    public boolean walkable;

    public Node(int x, int y, boolean walkable) {
        this.x = x;
        this.y = y;
        this.walkable = walkable;
    }

    public void calculateCosts(Node endNode) {
        this.hCost = Math.abs(x - endNode.x) + Math.abs(y - endNode.y);
        this.fCost = gCost + hCost;
    }
}