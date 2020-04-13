package Algorithm;

import Common.Instance;
import Common.Result;
import Common.Solution;
import SparseMatrixPack.Element;
import SparseMatrixPack.SparseMatrix;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Core extends Thread {
    int N_pop;
    int N_ch;
    Instance inst;
    ArrayList<Solution> sigma;
    Result res;
    Random rnd = new Random();

    public Core(int n_pop, int n_ch, Instance inst) {
        N_pop = n_pop;
        N_ch = n_ch;
        this.inst = inst;
    }

    public void run() {
        // TODO: 2020/4/5 need completion
        EAMA(N_pop, N_ch);
    }

    private void EAMA(int n_pop, int n_ch) {
        int maxNonImprove = 100;
        int nonImprove = 0;
        RoutesMinimizer rtm = new RoutesMinimizer(inst);
        rtm.determineM();
        sigma = new ArrayList<>(n_pop);
        for (int i = 0; i < n_pop; i++) {
            sigma.add(new Solution(rtm.Generate_initial_group()));
        }
        Collections.shuffle(sigma);
        for (int i = 0; i < n_pop; i++) {
            Solution p1, p2, s, s_best;
            p1 = new Solution(sigma.get(i));
            p2 = new Solution(sigma.get((i + 1) % n_pop));
            s_best = p1;
            for (int j = 0; j < n_ch; j++) {
                if (nonImprove++ > 2 * maxNonImprove) break;
                if (nonImprove < maxNonImprove) {
                    s = single_EAX(p1, p2);
                } else {
                    s = block_EAX(p1, p2);
                }
                repair(s);
                localSearch(s);
                assert s != null;
                if (s.distance < s_best.distance) {
                    nonImprove = 0;
                    s_best = s;
                }
            }
            sigma.set(i, s_best);
        }
        res.sol = findBestSol();
    }

    public double f(Solution sol) {
        return sol.distance + sol.caPenalty + sol.twPenalty;
    }

    @NotNull
    private ArrayList<int[]> findABCycles(@NotNull Solution p1, @NotNull Solution p2) {
        SparseMatrix[] mt = new SparseMatrix[3];
        mt[0] = new SparseMatrix();
        mt[2] = mt[0].Route2Gab(p1, p2);
        mt[1] = mt[2].trans();
        for (int i = 0; i < 3; i++) {
            mt[i].row = mt[i].column = inst.n;
            mt[i].initializeRpos();
        }
        int next = 1;
        SparseMatrix rt = mt[0].multiply(mt[1]);
        HashSet<String> hs = new HashSet<>();
        ArrayList<HashSet<Integer>> edgeList = new ArrayList<>();
        ArrayList<int[]> ls = new ArrayList<>();
        while (rt.size() != 0) {
            for (Element e : rt.elem) {
                if (e.row == e.column) {
                    for (int i = 0; i < e.routes.size(); i++) {
                        int[] arr = e.routes.get(i);
                        rotate(arr);
                        String str = arr2String(arr);
                        if (!hs.contains(str)) {
                            hs.add(str);
                            ls.add(arr);
                            edgeList.add(e.edges.get(i));
                        }
                    }
                }
            }
            rt = rt.multiply(mt[0]).multiply(mt[1]);
        }
//
//        Debug
//        if (ValidChecker.checkCycles(ls,mt[0],mt[1])!=-1){
//            System.out.println(ValidChecker.checkCycles(ls,mt[0],mt[1]));
//        }
        deleteSubCycle(ls, edgeList);
        return ls;
    }

    private boolean subGraphOf(@NotNull HashSet<Integer> sub, @NotNull HashSet<Integer> sup) {
        for (int edge : sub) {
            if (!sup.contains(edge)) {
                return false;
            }
        }
        return true;
    }

    private void deleteSubCycle(@NotNull ArrayList<int[]> ls, @NotNull ArrayList<HashSet<Integer>> edgeList) {
        BitSet bt = new BitSet(ls.size());
        for (int i = 0; i < edgeList.size(); i++) {
            for (int j = i + 1; j < edgeList.size(); j++) {
                if (edgeList.get(i).size() == edgeList.get(j).size()) continue;
                if (subGraphOf(edgeList.get(i), edgeList.get(j))) {
                    bt.set(i);
                }
            }
        }
        for (int i = ls.size() - 1; i >= 0; i--) {
            if (bt.get(i)) ls.remove(i);
        }
    }

    @NotNull
    private static String arr2String(@NotNull int[] arr) {
        if (arr.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            sb.append(",").append(arr[i]);
        }
        return sb.toString();
    }

    private static void rotate(@NotNull int[] rt) {
        if (rt.length == 0) return;
        int pos = 0, min = rt[0];
        for (int i = 2; i < rt.length; i += 2) {
            if (rt[i] < min) {
                pos = i;
                min = rt[i];
            }
        }
        for (int i = 0, cnt = 0; i < rt.length && cnt < rt.length; i++) {
            int last = i, next = (last + pos) % rt.length;
            int tmp = rt[last];
            while (next != i) {
                rt[last] = rt[next];
                last = next;
                next = (last + pos) % rt.length;
                cnt++;
            }
            rt[last] = tmp;
            cnt++;
        }
    }

    private Solution single_EAX(Solution p1, Solution p2) {
        ArrayList<int[]> cycles = findABCycles(p1, p2);
        int[] eSet = cycles.get(rnd.nextInt(cycles.size()));
        ArrayList<int[]> eSets = new ArrayList<>();
        eSets.add(eSet);
        SparseMatrix mt = new SparseMatrix();
        mt.convertRoute(p1, eSets);
        System.out.println(mt);
        return null;
    }

    private Solution block_EAX(Solution p1, Solution p2) {
        // TODO: 2020/4/5 finish this
        return null;
    }


    private Solution findBestSol() {
        // TODO: 2020/4/5 easy
        return null;
    }

    private void repair(Solution s) {
        // TODO: 2020/4/5 finish this
    }


    private void localSearch(Solution s) {
        // TODO: 2020/4/5 finish this
    }


    private Solution construction() {
        // TODO: 2020/4/5 complete this using cheapest insertion
        return null;
    }

    private int routeMinimization() {
        // TODO: 2020/4/5 route minimization (RM) heuristic for the VRPTW by Nagata and Br¨aysy [22]
        return 0;
    }
}
