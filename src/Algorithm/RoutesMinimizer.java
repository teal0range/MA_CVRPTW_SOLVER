package Algorithm;

import Common.*;
import com.sun.xml.internal.bind.v2.TODO;
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
    }

    public Solution determineM(){
        while (!timeIsUp()){
            DeleteRoute();
        }
        return sol;
    }
    public void DeleteRoute() {
        int rndIndex = rnd.nextInt(routes.size());
        Routes r = routes.get(rndIndex);
        EP.addAll(r.tour);
        System.out.println(r.tour.toString());
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
                    if (p==null) {
                        flag = true;
                        rt.insert(v_in,pos);
                        System.out.println("Insert > "+v_in.id +" > "+ i + " > pos > " +pos);
                        break;
                    }
                    else {
                        penalty_ls.add(p);
                    }
                }
            }
            if (!flag){
//                flag = squeeze(v_in,penalty_ls);
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
    public boolean squeeze(Nodes v_in,List<int[]> penalty_ls) {
        Solution tmp = new Solution(sol);
        double factor = 0.99;
        int []minPenalty=new int[]{Integer.MAX_VALUE,Integer.MAX_VALUE};
        for (int[] penalty:penalty_ls){
            if (F(minPenalty)>F(penalty)){
                minPenalty = penalty;
            }
        }

        // TODO: 2020/4/5 finish this
        return false;
    }
    public double F(int []Penalty){
        return Penalty[0] + alpha * Penalty[1];
    }

    public double time() {
        return (System.currentTimeMillis() - startTime) / 1000.;
    }

    public boolean timeIsUp() {
        return time() > maxTime;
    }
}
