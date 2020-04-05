import Common.RandomIndex;

import java.util.Random;

public class Solver {
    public static void main(String[] args) {
        RandomIndex ri = new RandomIndex(5);
        for (int i=0;i<10;i++){
            System.out.println(ri.nextInt());
        }
    }
}
