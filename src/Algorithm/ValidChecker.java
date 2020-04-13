package Algorithm;

import Common.Nodes;
import Common.Solution;
import SparseMatrixPack.SparseMatrix;

import java.util.ArrayList;
import java.util.HashSet;

public class ValidChecker {
    public static boolean check(Solution s){
        HashSet<Integer> hs = new HashSet<>();
        for (Routes r:s.routes){
            for (Nodes n : r.tour) {
                if (hs.contains(n.id))
                    return false;
                else {
                    hs.add(n.id);
                }
            }
        }
        return true;
    }

    public static int checkCycles(ArrayList<int[]> ls, SparseMatrix hm1, SparseMatrix hm2) {
        for (int i = 0; i < ls.size(); i++) {
            int[] rt = ls.get(i);
            for (int j = 0; j < rt.length - 1; j++) {
                if (j % 2 == 0 && !hm1.contains(rt[j], rt[j + 1])) {
                    return i * 1000 + j;
                } else if (j % 2 == 1 && !hm2.contains(rt[j], rt[j + 1])) {
                    return i * 100 + j;
                }
            }
        }
        return -1;
    }
}
