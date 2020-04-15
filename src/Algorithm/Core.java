package Algorithm;

import Common.*;
import SparseMatrixPack.Element;
import SparseMatrixPack.SparseMatrix;

import java.util.*;

public class Core {
    int N_pop;
    int N_ch;
    Instance inst;
    ArrayList<Solution> sigma;
    Result res;
    Random rnd = new Random();
    Operator opt = new Operator();
    long startTime = System.currentTimeMillis();
    int TimeLimit;
    RoutesMinimizer rtm;

    public Core(int n_pop, int n_ch, Instance inst, int TimeLimit, int genTime, AlgoParam param) {
        N_pop = n_pop;
        N_ch = n_ch;
        rtm = new RoutesMinimizer(inst, genTime);
        res = new Result(inst, param);
        this.inst = inst;
        this.TimeLimit = TimeLimit;
        rtm.determineM();
        System.out.println(time() + "s > number of Routes > " + rtm.sol.routes.size());
    }

    public double time() {
        return (System.currentTimeMillis() - startTime) / 1000.;
    }

    public boolean timeIsUp() {
        return time() > TimeLimit;
    }

    public void run() {
        int gen = 1;
        while (!timeIsUp()) {
            EAMA(N_pop, N_ch);
            System.out.println("\t MA > Gen " + gen + " > dis > " + res.sol.distance + " > " + res.time + "s");
            gen++;
        }
        res.output(inst);
    }

    private void EAMA(int n_pop, int n_ch) {
        int maxNonImprove = 100;
        int nonImprove = 0;
        if (sigma == null) {
            sigma = new ArrayList<>(n_pop);
            for (int i = 0; i < n_pop; i++) {
                sigma.add(new Solution(rtm.Generate_initial_group()));
            }
        } else {
            sigma = new ArrayList<>(n_pop);
            sigma.add(res.sol);
            rtm.sol = new Solution(res.sol);
            rtm.routes = rtm.sol.routes;
            for (int i = 1; i < n_pop; i++) {
                sigma.add(new Solution(rtm.Generate_initial_group()));
            }
        }
        res.sol = sigma.get(0);
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
                if (f(s) < f(s_best)) {
                    nonImprove = 0;
                    s_best = s;
                }
            }
            sigma.set(i, s_best);
        }
        int dis = res.sol.distance;
        for (Solution s : sigma) {
            if (s.infeasibleRoutes.size() == 0 && s.distance < dis) {
                res.sol = s;
                res.time = time();
                dis = s.distance;
            }
        }
    }


    public double f(Solution sol) {
        return sol.distance + sol.caPenalty + sol.twPenalty;
    }


    private ArrayList<int[]> findABCycles(Solution p1, Solution p2) {
        SparseMatrix[] mt = new SparseMatrix[3];
        mt[0] = new SparseMatrix();
        mt[2] = mt[0].Route2Gab(p1, p2);
        mt[1] = mt[2].trans();
        for (int i = 0; i < 3; i++) {
            mt[i].row = mt[i].column = inst.n;
            mt[i].initializeRpos();
        }
        SparseMatrix rt = mt[0].multiply(mt[1]);
        HashSet<String> hs = new HashSet<>();
        ArrayList<HashSet<Integer>> edgeList = new ArrayList<>();
        ArrayList<int[]> ls = new ArrayList<>();
        int size = 0;
        while (rt.size() != 0 && size++ < 5) {
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
        deleteSubCycle(ls, edgeList);
        return ls;
    }

    private boolean subGraphOf(HashSet<Integer> sub, HashSet<Integer> sup) {
        for (int edge : sub) {
            if (!sup.contains(edge)) {
                return false;
            }
        }
        return true;
    }

    private void deleteSubCycle(ArrayList<int[]> ls, ArrayList<HashSet<Integer>> edgeList) {
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


    private static String arr2String(int[] arr) {
        if (arr.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(arr[0]);
        for (int i = 1; i < arr.length; i++) {
            sb.append(",").append(arr[i]);
        }
        return sb.toString();
    }

    private static void rotate(int[] rt) {
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

    //debug
    public static boolean checkMt(SparseMatrix mt) {
        for (int i = 1; i < mt.rpos.length - 1; i++) {
            if (mt.rpos[i + 1] - mt.rpos[i] > 1) return false;
        }
        return true;
    }

    private Solution single_EAX(Solution p1, Solution p2) {
        ArrayList<int[]> cycles = findABCycles(p1, p2);
        ArrayList<int[]> eSets = new ArrayList<>();
        if (cycles.size() > 0) {
            int[] eSet = cycles.get(rnd.nextInt(cycles.size()));
            eSets.add(eSet);
        }
        SparseMatrix mt = new SparseMatrix();
        mt.convertRoute(p1, eSets);
        return matrix2Route(mt);
    }

    private Solution block_EAX(Solution p1, Solution p2) {
        ArrayList<int[]> cycles = findABCycles(p1, p2);
        ArrayList<int[]> eSets = new ArrayList<>();
        if (cycles.size() > 0) {
            int[] c1 = cycles.get(rnd.nextInt(cycles.size()));
            eSets.add(c1);
            out:
            for (int[] cycle : cycles) {
                if (cycle.length < c1.length) continue;
                HashSet<Integer> c2 = new HashSet<>();
                for (int k = 0; k < cycle.length; k++) {
                    c2.add(c1[k]);
                }
                for (int value : c1) {
                    if (c2.contains(value)) {
                        eSets.add(c1);
                        break out;
                    }
                }
            }
        }
        SparseMatrix mt = new SparseMatrix();
        mt.convertRoute(p1, eSets);
        return matrix2Route(mt);
    }

    public Solution matrix2Route(SparseMatrix mt) {
        List<Routes> route = new ArrayList<>();
        HashSet<Integer> unexplored = new HashSet<>(inst.n);
        for (int i = 0; i < inst.n; i++) {
            unexplored.add(i);
        }
        unexplored.remove(0);
        for (int i = mt.rpos[0]; i < mt.rpos[1]; i++) {
            Element e = mt.elem.get(i);
            ArrayList<Nodes> tour = new ArrayList<>();
            while (e.column != 0) {
                int next = mt.rpos[e.column];
                tour.add(inst.nodes[e.column]);
                e = mt.elem.get(next);
                unexplored.remove(e.row);
            }
            route.add(new Routes(tour, inst));
        }
        while (!unexplored.isEmpty()) {
            ArrayList<Nodes> subTour = new ArrayList<>();
            Iterator<Integer> iter = unexplored.iterator();
            int first = iter.next();
            unexplored.remove(first);
            Element e = mt.elem.get(mt.rpos[first]);
            while (e.column != first) {
                subTour.add(inst.nodes[e.column]);
                e = mt.elem.get(mt.rpos[e.column]);
                unexplored.remove(e.row);
            }
            Constraints sub = new Constraints(subTour, inst, inst.nodes[first], inst.nodes[first]);
            double minPenalty = Double.MAX_VALUE;
            Routes bestRoute = null;
            int[] best = null;
            out:
            for (int i = -1; i < sub.tour.size(); i++) {
                for (Routes rt : route) {
                    for (int j = -1; j < rt.size(); j++) {
                        int[] p = rt.cons.validSubTourInsertion(sub, i, j);
                        if (f(p[0], p[1]) < minPenalty) {
                            minPenalty = f(p[0], p[1]);
                            best = p;
                            bestRoute = rt;
                        }
                        if (minPenalty == 0) break out;
                    }
                }
            }
            assert best != null;
            int[] op = RoutesMinimizer.decoding(best[3]);
            bestRoute.insertSubTour(sub, op[0], op[1]);
        }
        Solution sol = new Solution(route);
        sol.calculateCost();
        return sol;
    }

    private double f(int a, int b) {
        return a + b;
    }

    private void repair(Solution s) {
        double p = f(s.caPenalty, s.twPenalty);
        for (int index : s.infeasibleRoutes) {
            Routes r = s.routes.get(index);
            while (true) {
                opt.out_exchange(s, r);
                opt.two_opt_star(s, r);
                opt.out_relocate(s, r);
                if (f(s.caPenalty, s.twPenalty) < p) {
                    p = f(s.caPenalty, s.twPenalty);
                } else {
                    break;
                }
            }
        }
    }


    private void localSearch(Solution s) {
        opt.out_exchange(s);
        opt.out_relocate(s);
        opt.two_opt_star(s);
    }
}
