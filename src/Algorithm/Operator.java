package Algorithm;

import Common.Nodes;
import Common.Solution;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Operator {

    // TODO: 2020/4/5 complete the opts
    public void two_opt_star(@NotNull Solution sol, @NotNull Routes r) {
        for (int i = 0; i < sol.routes.size(); i++) {
            if (sol.routes.get(i) == r) continue;
            if (two_opt_star(r, sol.routes.get(i))) {
                sol.calculateCost();
            }
        }
    }

    public boolean two_opt_star(@NotNull Routes route1, @NotNull Routes route2) {
        boolean flag = false;
        for (int front = -1; front < route1.size(); front++) {
            for (int back = 0; back <= route2.size(); back++) {
                if (front != -1 && back != route2.size() &&
                        !route1.inst.isClose[route1.get(front).id][route2.get(back).id])
                    continue;
                int[] p = route1.cons.validConnect(front, route2, back);
                int[] q = route2.cons.validConnect(back - 1, route1, front + 1);
                if ((p[0] + q[0] <= route1.cons.caPenalty() + route2.cons.caPenalty()
                        && p[1] + q[1] <= route1.cons.twPenalty() + route2.cons.twPenalty()) &&
                        (p[2] + q[2] < 0 || !route1.isFeasible() || !route2.isFeasible())) {
                    Routes tmp = new Routes(route1);
                    route1.connect(front, route2, back);
                    route2.connect(back - 1, tmp, front + 1);
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public void in_relocate(@NotNull Solution sol, @NotNull Routes route) {
        // TODO: 2020/4/10 待测试
        for (int in = 0; in < route.size(); in++) {
            Nodes v_in = route.get(in);
            boolean flag = false;
            for (int pos = 0; pos < in && !flag; pos++) {
                if (!route.inst.isClose[route.get(pos).id][v_in.id]) continue;
                int[] p = route.cons.validInsertion(v_in, pos);
                int[] q = route.cons.validRemove(in);
                if ((p[0] + q[0] <= route.cons.caPenalty()
                        && p[1] + q[1] <= route.cons.twPenalty() &&
                        (p[2] + q[2] < 0 || !route.isFeasible()))) {
                    route.remove(in);
                    route.insert(v_in, pos);
                    flag = true;
                }
            }
        }
        sol.calculateCost();
    }

    public void in_exchange(Routes routes) {

    }

    public void out_relocate(@NotNull Solution sol, @NotNull Routes route) {
        //搜索外界能插入的点
        //（求求别出bug了
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
                        break outIn;
                    }
                }
            }
        }
        sol.calculateCost();
    }

    public void out_relocate(@NotNull Solution sol) {
        for (int i = 0; i < sol.routes.size(); i++) {
            out_relocate(sol, sol.routes.get(i));
        }
    }

    public void out_exchange(Solution sol) {

    }

}
