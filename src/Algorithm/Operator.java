package Algorithm;

import Common.Nodes;
import Common.Solution;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Operator {

    // TODO: 2020/4/5 complete the opts
    public void two_opt_star(@NotNull Solution sol, Routes r) {
        for (int i = 0; i < sol.routes.size(); i++) {
            two_opt_star(r, sol.routes.get(i));
        }
    }

    public void two_opt_star(@NotNull Routes route1, Routes route2) {
        for (int front = -1; front < route1.size(); front++) {
            for (int back = 0; back <= route2.size(); back++) {
                if (front != -1 && back != route2.size() &&
                        !route1.inst.isClose[route1.get(front).id][route2.get(back).id])
                    continue;
                int[] p = route1.cons.validConnect(front, route2, back);
                int[] q = route2.cons.validConnect(back - 1, route1, front + 1);
                // TODO: 2020/4/8 finish here
//                if (p!=null||q!=null||p[2]+q[2]>=0&&route1.isFeasible())continue;
            }
        }
    }

    public void in_relocate(Routes route) {

    }

    public void in_exchange(Routes routes) {

    }

    public void out_relocate(@NotNull Solution sol, Routes route) {
        //搜索外界能插入的点
        // 这里可能有潜在bug(bushi
        List<Routes> routes = sol.routes;
        for (Routes r : routes) {
            if (r == route) continue;
            out:
            for (int outer = 0; outer < r.size(); outer++) {
                for (int inner = 0; inner <= route.size(); inner++) {
                    if (inner != route.size() && !route.inst.isClose[r.get(outer).id][route.get(inner).id]) continue;
                    int[] p = route.cons.validInsertion(r.get(outer), inner);
                    int[] q = r.cons.validRemove(outer);
                    if ((p[0] + q[0] <= r.cons.caPenalty() + route.cons.caPenalty()
                            && p[1] + q[1] <= r.cons.twPenalty() + route.cons.twPenalty()) &&
                            (p[2] + q[2] < 0 || !r.isFeasible())) {
                        route.insert(r.get(outer), inner);
                        r.remove(outer);
                        sol.calculateCost();
                        if (outer >= r.size()) break out;
                    }
                }
            }
        }
        //搜索能插入邻近路线的点
        for (int inner = 0; inner < route.size(); inner++) {
            Nodes node = route.get(inner);
            outIn:
            for (Routes r : routes) {
                if (r == route) continue;
                for (int outer = 0; outer <= r.size(); outer++) {
                    if (outer != r.size() && !route.inst.isClose[node.id][r.get(outer).id]) continue;
                    int[] p = r.cons.validInsertion(node, outer);
                    int[] q = route.cons.validRemove(inner);
                    if ((p[0] + q[0] <= r.cons.caPenalty() + route.cons.caPenalty()
                            && p[1] + q[1] <= r.cons.twPenalty() + route.cons.twPenalty()) &&
                            (p[2] + q[2] < 0 || !route.isFeasible())) {
                        r.insert(node, outer);
                        route.remove(inner);
                        sol.calculateCost();
                        break outIn;
                    }
                }
            }
        }
    }

    public void out_relocate(Solution sol) {
        for (int i = 0; i < sol.routes.size(); i++) {
            out_relocate(sol, sol.routes.get(i));
        }
    }

    public void out_exchange(Solution sol) {

    }

}
