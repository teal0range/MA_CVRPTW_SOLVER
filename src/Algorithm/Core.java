package Algorithm;

import Common.*;

import java.util.*;

public class Core extends Thread {
    int N_pop;
    int N_ch;
    Instance inst;
    ArrayList<Solution> sigma;
    Result res;

    public void run() {
        // TODO: 2020/4/5 need completion
        EAMA(N_pop, N_ch);
    }

    private void EAMA(int n_pop, int n_ch) {
        int maxNonImprove = 100;
        int nonImprove = 0;
        int m = routeMinimization();
        sigma = new ArrayList<>(n_pop);
        for (int i = 0; i < n_pop; i++) {
            sigma.add(construction());
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
                if (s.cost < s_best.cost) {
                    nonImprove = 0;
                    s_best = s;
                }
            }
            sigma.set(i, s_best);
        }
        res.sol = findBestSol();
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

    private Solution single_EAX(Solution p1, Solution p2) {
        // TODO: 2020/4/5 finish this
        return null;
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