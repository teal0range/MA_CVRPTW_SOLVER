package Algorithm;

import Common.Nodes;
import Common.Solution;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Operator {

    // TODO: 2020/4/5 complete the opts
    public void two_opt_star(Routes route1,Routes route2) {

    }

    public void in_relocate(Routes route) {

    }

    public void in_exchange(Routes routes){

    }

    private void out_relocate(@NotNull List<Routes> routes, Routes route) {
        //搜索外界能插入的点这里可能有潜在bug
        for (Routes r:routes){
            if (r==route)continue;
            out:
            for (int outer=0;outer < r.size();outer++){
                for (int inner = 0;inner <= route.size();inner++){
                    if (inner!=route.size()&&!route.inst.isClose[r.get(outer).id][route.get(inner).id])continue;
                    int []p = route.cons.validInsertion(r.get(outer),inner);
                    int []q = r.cons.validRemove(outer);
                    if (!(p==null||p[2]+q[2]>=0&& !r.isFeasible()))continue;
                    route.insert(r.get(outer),inner);
                    r.remove(outer);
                    if (outer >= r.size())break out;
                }
            }
        }
        //搜索能插入邻近路线的点
        for (int inner=0;inner<route.size();inner++){
            Nodes node = route.get(inner);
            outIn:
            for (Routes r:routes){
                if (r==route)continue;
                for (int outer = 0;outer<=r.size();outer++){
                    if (outer!=r.size()&&!route.inst.isClose[node.id][r.get(outer).id])continue;
                    int []p = r.cons.validInsertion(node,outer);
                    int []q = route.cons.validRemove(inner);
                    if (!(p==null||p[2]+q[2]>=0&&!route.isFeasible()))continue;
                    r.insert(node,outer);
                    route.remove(inner);
                    break outIn;
                }
            }
        }
    }
    public void out_relocate(@NotNull Solution sol, Routes routes) {
        out_relocate(sol.routes,routes);
        sol.calculateCost();
    }

    public void out_relocate(Solution sol) {
        for (int i = 0; i < sol.routes.size(); i++) {
            out_relocate(sol, sol.routes.get(i));
        }
    }

    public void out_exchange(Solution sol) {

    }

}
