package Algorithm;

import Common.*;

import java.util.*;

public class RoutesMinimizer {
    Instance inst;
    Solution sol;
    List<Routes> routes;
    Random rnd = new Random(AlgoParam.seed);
    Stack<Nodes> EP;
    int[] penalty;
    long startTime;
    double maxTime = 10;
    Operator opt;


    public RoutesMinimizer(Instance inst, int maxTime) {
        this.inst = inst;
        routes = new LinkedList<>();
        for (int i = 1; i < inst.n; i++) {
            ArrayList<Nodes> route = new ArrayList<>();
            route.add(inst.nodes[i]);
            routes.add(new Routes(route, inst));
        }
        EP = new Stack<>();
        sol = new Solution(routes);
        penalty = new int[inst.n];
        startTime = System.currentTimeMillis();
        opt = new Operator();
        this.maxTime = maxTime;
    }

    @Deprecated
    public Solution Generate_initial_group(int m) {
        while (!timeIsUp()) {
            Solution tp = new Solution(sol);
            DeleteRoute();
            if (!EP.isEmpty()) {
                sol = tp;
                routes = tp.routes;
                EP.empty();
            }
            if (m == sol.routes.size()) break;
        }
        sol.routes = new ArrayList<>(this.routes);
//        System.out.println(sol.routes.size() + " > " + ValidChecker.check(sol));
        return sol;
    }

    public boolean SolutionChecker(Solution p1) {
        HashSet<Integer> hs = new HashSet<>();
        for (Routes r : p1.routes) {
            for (Nodes node : r.tour) {
                if (hs.contains(node.id)) return false;
                hs.add(node.id);
            }
        }
        return true;
    }

    public Solution Generate_initial_group() {
        Solution s = new Solution(sol);
        perturb(inst.n * inst.n / 20);
        while (sol.routes.size() != s.routes.size()) {
            sol = s;
            routes = s.routes;
            perturb(inst.n * inst.n / 20);
        }
        sol.routes = new ArrayList<>(this.routes);
        return sol;
    }

    public void determineM() {
        while (!timeIsUp()) {
            Solution tp = new Solution(sol);
            DeleteRoute();
            if (!EP.isEmpty()) {
                sol = tp;
                routes = tp.routes;
                EP.clear();
            }
        }
        sol.routes = new ArrayList<>(this.routes);
    }

    public void DeleteRoute() {
        if (routes.size() == 0) return;
        int rndIndex = rnd.nextInt(routes.size());
        Routes r = routes.get(rndIndex);
        EP.addAll(r.tour);
//        System.out.println(r.tour.toString());
        routes.remove(rndIndex);
        sol.calculateCost();
        Arrays.fill(penalty, 1);
        long st = System.currentTimeMillis();
        while (!EP.isEmpty() && !timeIsUp(st, 1)) {
            RandomIndex ri = new RandomIndex(routes.size());
            Nodes v_in = EP.pop();
            boolean flag = false;
            List<int[]> penalty_ls = new LinkedList<>();
            while (!flag && ri.hasNext()) {
                int i = ri.nextInt();
                Routes rt = routes.get(i);
                RandomIndex rin = new RandomIndex(rt.size() + 1);
                for (int pos = rin.nextInt(); rin.hasNext(); pos = rin.nextInt()) {
                    int[] p = rt.cons.validInsertion(v_in, pos);
                    if (p[0] == 0 && p[1] == 0) {
                        flag = true;
                        rt.insert(v_in, pos);
                        sol.calculateCost();
//                        System.out.println("Insert > "+v_in.id +" > "+ i + " > pos > " +pos);
                        break;
                    } else {
                        p[3] = encoding(p[3], i);
                        penalty_ls.add(p);
                    }
                }
            }
            if (!flag&&penalty_ls.size()>0){
                flag = squeeze(v_in,penalty_ls);
            }
            if (!flag){
                penalty[v_in.id]++;
                insertion_ejection(v_in, 5);
            }
            perturb(1000);
        }
    }

    private void perturb(int I_max) {
        Collections.shuffle(routes);
        for (int i = 0; i < I_max; i += 10) {
            int c = rnd.nextInt(3);
            switch (c) {
                case 0:
                    opt.out_relocate(sol, sol.routes.get(rnd.nextInt(sol.routes.size())),
                            10, sol.distance / 50);
                case 1: {
                    Routes r1 = sol.routes.get(rnd.nextInt(sol.routes.size()));
                    Routes r2 = sol.routes.get(rnd.nextInt(sol.routes.size()));
                    while (r1 == r2) {
                        r2 = sol.routes.get(rnd.nextInt(sol.routes.size()));
                    }
                    if (opt.two_opt_star(r1, r2, 10, sol.distance / 50)) {
                        sol.calculateCost();
                    }
                }
                case 2: {
                    Routes r1 = sol.routes.get(rnd.nextInt(sol.routes.size()));
                    Routes r2 = sol.routes.get(rnd.nextInt(sol.routes.size()));
                    while (r1 == r2) {
                        r2 = sol.routes.get(rnd.nextInt(sol.routes.size()));
                    }
                    if (opt.out_exchange(r1, r2, 10, sol.distance / 50)) {
                        sol.calculateCost();
                    }
                }
            }
        }
    }


    public void insertion_ejection(Nodes v_in, int kMax) {
        //此处做了一定的简化
        int pSumMin = Integer.MAX_VALUE;
        Routes bestRoute = routes.get(0);
        int bestInsertionPos = -1;
        int bestRemoveLeft = 0;
        int bestDisPenalty = 0;
        //找到最优插入剔除组合
        outer:
        for (Routes r : sol.routes) {
            out:
            for (int insertion_pos = 1; insertion_pos <= r.size(); insertion_pos++) {
                int pSum = 0;
                r.insert(v_in, insertion_pos);
                for (int removeLeft = insertion_pos - 2; removeLeft >= -1 && removeLeft >= insertion_pos - kMax - 1; removeLeft--) {
                    int[] p = r.cons.validConnect(removeLeft, r, insertion_pos);
                    pSum += penalty[r.get(removeLeft + 1).id];
                    if (pSum > pSumMin) {
                        r.remove(insertion_pos);
                        continue out;
                    }
                    if (p[0] == 0 && p[1] == 0) {
                        if (pSum < pSumMin || bestDisPenalty > p[2]) {
                            pSumMin = pSum;
                            bestDisPenalty = p[2];
                            bestInsertionPos = insertion_pos;
                            bestRemoveLeft = removeLeft;
                            bestRoute = r;
                        }
                        r.remove(insertion_pos);
                        if (pSumMin == 1) {
                            break outer;
                        }
                        continue out;
                    }
                }
                r.remove(insertion_pos);
            }
        }
        bestRoute.insert(v_in, bestInsertionPos);
        for (int i = bestRemoveLeft + 1; i < bestInsertionPos; i++) {
            EP.push(bestRoute.get(i));
        }
        bestRoute.connect(bestRemoveLeft, bestRoute, bestInsertionPos);
    }


    double alpha = 1;
    double factor = 0.99;

    public boolean squeeze(Nodes v_in, List<int[]> penalty_ls) {
        Solution tmp = new Solution(sol);
        int[] minPenalty = new int[]{Integer.MAX_VALUE >> 2, Integer.MAX_VALUE >> 2, Integer.MAX_VALUE >> 2, -1};
        for (int[] penalty : penalty_ls) {
            if (f(minPenalty) > f(penalty)) {
                minPenalty = penalty;
            }
        }
        int route_id = decoding(minPenalty[3])[1];
        int pos = decoding(minPenalty[3])[0];
        Routes r = tmp.routes.get(route_id);
        r.insert(v_in, pos);
        tmp.calculateCost();
        double lastPenalty = f(tmp.caPenalty, tmp.twPenalty);
//        int non_improve = 0, threshold = 2;
        while (lastPenalty != 0) {
            localSearch(tmp, r);
            Iterator<Integer> iter = tmp.infeasibleRoutes.iterator();
            if (!tmp.infeasibleRoutes.isEmpty()) {
                int rndIndex = rnd.nextInt(tmp.infeasibleRoutes.size());
                while (iter.hasNext()) {
                    int next = iter.next();
                    if (rndIndex > 0) {
                        rndIndex--;
                        continue;
                    }
                    r = tmp.routes.get(next);
                }
            }
            double currentPenalty = f(tmp.caPenalty, tmp.twPenalty);
            if (currentPenalty < lastPenalty) {
                lastPenalty = currentPenalty;
            } else {
                break;
            }
        }
        if (lastPenalty == 0) {
            sol = tmp;
            this.routes = tmp.routes;
            return true;
        } else {
            alpha *= factor;
            return false;
        }
    }

    public static int encoding(int a, int b) {
        return (a + 1 << 16) + b + 1;
    }

    public static int[] decoding(int code) {
        return new int[]{(code >> 16) - 1, code % (1 << 16) - 1};
    }

    private void localSearch(Solution tmp, Routes r) {
        opt.out_relocate(tmp, r);
        opt.two_opt_star(tmp, r);
//        opt.in_relocate(tmp, r);
    }

    public double f(int[] Penalty) {
        return Penalty[0] + alpha * Penalty[1];
    }

    public double f(int caPenalty, int twPenalty) {
        return caPenalty + alpha * twPenalty;
    }

    public double time() {
        return (System.currentTimeMillis() - startTime) / 1000.;
    }

    public double time(long startTime) {
        return (System.currentTimeMillis() - startTime) / 1000.;
    }

    public boolean timeIsUp() {
        return time() > maxTime;
    }

    public boolean timeIsUp(long startTime, int maxTime) {
        return time(startTime) > maxTime;
    }
}
