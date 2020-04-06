package Algorithm;

import Common.Instance;
import Common.Nodes;
import Common.Solution;

import java.util.HashSet;
import java.util.List;

public class Operator {

    // TODO: 2020/4/5 complete the opts
    public void two_opt_star(Routes route1,Routes route2) {

    }

    public void in_relocate(Routes route) {

    }

    public void in_exchange(Routes routes){

    }

    public void out_relocate(List<Routes> routes, Routes route) {
        //搜索外界能插入的点
        for (Routes r:routes){
            if (r==route)continue;
            for (int outer=0;outer<r.size();outer++){
                for (int inner = 0;inner <= route.size();inner++){
                    if (inner!=route.size()&&!route.inst.isClose[r.get(outer).id][route.get(inner).id])continue;
                    int []p = route.cons.validInsertion(r.get(outer),inner);
                    if (p!=null)continue;
                    route.insert(r.get(outer),inner);
                    r.remove(outer);
                }
            }
        }
        //搜索能插入邻近路线的点
        for (Nodes node:route.tour){
            for (Routes r:routes){
                if (r==route)continue;
                for (int outer = 0;outer<=r.size();outer++){
                    if (outer!=r.size()&&!route.inst.isClose[node.id][r.get(outer).id])continue;
                    int []p = r.cons.validInsertion(node,outer);
                    if (p!=null)continue;
                    r.insert(node,outer);
                    route.remove(outer);
                }
            }
        }
    }
    public void out_relocate(Solution sol,Routes routes) {
        out_relocate(sol.routes,routes);
    }

    public void out_relocate(Solution sol) {

    }

    public void out_exchange(Solution sol) {

    }

}
