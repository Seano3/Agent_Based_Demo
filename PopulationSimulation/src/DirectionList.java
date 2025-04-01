
import java.util.LinkedList;

public class DirectionList {

    private LinkedList<Direction> list;

    public DirectionList() {
        list = new LinkedList<Direction>();
    }

    public void addDirection(String direction, int weight) {
        list.add(new Direction(direction, weight));

    }

    public void sort() {
        list.sort((Direction d1, Direction d2) -> d1.getWeight() - d2.getWeight());
    }

    public Direction get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public boolean nearWall() {
        for (Direction d : list) {
            if (Integer.MAX_VALUE == d.getWeight()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "";

        for (Direction d : list) {
            String part = d.toString();
            result = result.concat(part);
        }

        return result;
    }
}
