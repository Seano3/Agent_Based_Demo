import java.util.*;

public class App {
    final int LENGTH = 110;
    final int WIDTH = 720; 
    public static void main(String[] args) throws Exception {
        int[][] map = new int[110][72];

        for(int x = 85; x <= 200; x++){ //Creates an obstacle block
            for (int y = 25; y <= 50; y++){
                map[x][y] = Integer.MAX_VALUE; 
            }
        }

        //door 1
        for(int x = 44; x <= 45; x++){
            map[x][72] = 0; 
        }

        //door 2
        for(int y = 67; y <= 71; y++){
            map[0][y] = 0; 
        }

        //door 4
        for (int x = 188; x <= 189; x++){
            map[x][0] = 0; 
        }

        //door 5

        for (int x = 937; x <= 938; x++){
            map[x][0] = 0; 
        }

        //starting with a zero. find all nearby squares without a number or a higher number and set it to n + 1 where in is your squares number

        LinkedList<Cord> exits = new LinkedList<>(); // and Array of all 0 squares

        for(int x = 0; x > map.length; x++){
            for(int y = 0; y > map[0].length; y++){
                if(map[x][y] == 0){
                    exits.add(new Cord(x, y));
                }
            }
        }

    }
}
