
public class Direction {

    private String direction;
    private int weight;

    public Direction(String direction, int weight) {
        this.direction = direction;
        this.weight = weight;
    }

    public String getDirection() {
        return direction;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "(" + direction + ", " + weight + ") ";
    }
}
