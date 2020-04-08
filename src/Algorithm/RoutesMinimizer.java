package Algorithm;

import Common.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RoutesMinimizer {
    Instance inst;
    Solution sol;
    List<Routes> routes;
    Random rnd = new Random(AlgoParam.seed);
    Stack<Nodes> EP;
    int[] penalty;
    long startTime;
    double maxTime = 1000000;
    Operator opt;


    public RoutesMinimizer(@NotNull Instance inst) {
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
    }

    public Solution determineM(){
        while (!timeIsUp()){
            DeleteRoute();
            sol.calculateCost();
            if (routes.size()==10)break;
        }
        sol.routes = new ArrayList<>(this.routes);
        return sol;
    }

    public void DeleteRoute() {
        if (routes.size()==0)return;
        int rndIndex = rnd.nextInt(routes.size());
        Routes r = routes.get(rndIndex);
        EP.addAll(r.tour);
//        System.out.println(r.tour.toString());
        routes.remove(rndIndex);
        RandomIndex ri = new RandomIndex(routes.size());
        Arrays.fill(penalty, 1);
        while (!EP.isEmpty() && !timeIsUp()) {
            Nodes v_in = EP.pop();
            boolean flag = false;
            List<int[]> penalty_ls = new LinkedList<>();
            while (!flag&&ri.hasNext()) {
                int i = ri.nextInt();
                Routes rt = routes.get(i);
                RandomIndex rin = new RandomIndex(rt.size()+1);
                // TODO: 2020/4/5 needs optimize
                for (int pos = rin.nextInt();rin.hasNext();pos=rin.nextInt()){
                    int []p = rt.cons.validInsertion(v_in,pos);
                    if (p[0] == 0 && p[1] == 0) {
                        flag = true;
                        rt.insert(v_in, pos);
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
                // TODO: 2020/4/5 complete this
                penalty[v_in.id]++;
                // TODO: 2020/4/6 insertion-ejection
            }
            perturb();
        }
    }

    private void perturb() {
        // TODO: 2020/4/6 finish this
    }
    double alpha = 1;
    double factor = 0.99;
    public boolean squeeze(Nodes v_in, @NotNull List<int[]> penalty_ls) {
        Solution tmp = new Solution(sol);
        int []minPenalty=new int[]{Integer.MAX_VALUE>>2,Integer.MAX_VALUE>>2,Integer.MAX_VALUE>>2,-1};
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
        int non_improve = 0, threshold = 2;
        while (lastPenalty != 0) {
            localSearch(tmp, r);
            double currentPenalty = f(tmp.caPenalty, tmp.twPenalty);
            if (currentPenalty < lastPenalty) {
                lastPenalty = currentPenalty;
            } else if (non_improve < threshold) {
                non_improve++;
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
        return (a << 16) + b;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static int[] decoding(int code) {
        return new int[]{code >> 16, code % (1 << 16)};
    }

    private void localSearch(Solution tmp, Routes r) {
        opt.out_relocate(tmp, r);
    }

    public double f(@NotNull int[] Penalty) {
        return Penalty[0] + alpha * Penalty[1];
    }

    public double f(int caPenalty, int twPenalty) {
        return caPenalty + alpha * twPenalty;
    }

    public double time() {
        return (System.currentTimeMillis() - startTime) / 1000.;
    }

    public boolean timeIsUp() {
        return time() > maxTime;
    }
}
