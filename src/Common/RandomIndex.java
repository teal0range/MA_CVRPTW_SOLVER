package Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomIndex {
    List<Integer> index;
    private int next;
    public RandomIndex(int bound){
       index =  new ArrayList<>(bound);
       for (int i=0;i<bound;i++){
           index.add(i);
       }
       reset();
    }
    private void shuffle(){
        Collections.shuffle(index);
    }
    public void reset(){
//        shuffle();
        next = 0;
    }
    public boolean hasNext(){
        return next<index.size();
    }
    public int nextInt(){
        if (!hasNext())return -1;
        return index.get(next++);
    }
}
