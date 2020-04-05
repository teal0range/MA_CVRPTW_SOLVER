package Algorithm;

import Common.AlgoParam;
import Common.Instance;
import Common.Nodes;
import Common.RandomIndex;

import java.util.*;

public class RoutesMinimizer {
    Instance inst;
    List<Routes> routes;
    Random rnd = new Random(AlgoParam.seed);
    Stack<Nodes> EP;
    int[] penalty;
    long startTime;
    double maxTime = 1;


    public RoutesMinimizer(Instance inst) {
        this.inst = inst;
        routes = new LinkedList<>();
        for (int i = 0; i < inst.n; i++) {
            ArrayList<Nodes> route = new ArrayList<>();
            route.add(inst.nodes[i]);
            routes.add(new Routes(route, inst));
        }
        EP = new Stack<>();
        penalty = new int[inst.n];
        startTime = System.currentTimeMillis();
    }

    public void DeleteRoute() {
        int rndIndex = rnd.nextInt(routes.size());
        Routes r = routes.get(rndIndex);
        EP.addAll(r.tour);
        routes.remove(rndIndex);
        RandomIndex ri = new RandomIndex(routes.size());
        Arrays.fill(penalty, 1);
        while (!EP.isEmpty() && !timeIsUp()) {
            Nodes v_in = EP.pop();
            boolean flag = false;
            while (!flag&&ri.hasNext()) {
                int i = ri.nextInt();
                Routes rt = routes.get(i);
                ArrayList<Integer> insertions = new ArrayList<>();
                // TODO: 2020/4/5 needs optimize
                for (int pos=0;pos<=rt.tour.size();pos++){
                    if (rt.cons.validInsertion(v_in,pos))insertions.add(pos);
                }
                if (insertions.size()!=0){
                    flag = true;
                    rt.insert(v_in,insertions.get(rnd.nextInt(insertions.size())));
                }
            }
            if (!flag){
                flag = squeeze(v_in);
            }
            if (!flag){
                // TODO: 2020/4/5 complete this
            }
        }
    }

    public boolean squeeze(Nodes v_in) {
        // TODO: 2020/4/5 finish this
        return false;
    }

    public double time() {
        return (System.currentTimeMillis() - startTime) / 1000.;
    }

    public boolean timeIsUp() {
        return time() > maxTime;
    }
}
