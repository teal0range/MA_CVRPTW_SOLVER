package Algorithm;

import Algorithm.Routes;
import Common.Nodes;
import Common.Solution;

import java.util.HashSet;

public class ValidChecker {
    public static boolean check(Solution s){
        HashSet<Integer> hs = new HashSet<>();
        for (Routes r:s.routes){
            for (Nodes n:r.tour){
                if (hs.contains(n.id))
                    return false;
                else {
                    hs.add(n.id);
                }
            }
        }
        return true;
    }
}
