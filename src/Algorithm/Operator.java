package Algorithm;

import Common.Nodes;
import Common.Solution;

import java.util.List;

public class Operator {

    public void two_opt_star(Solution sol) {
        for (Routes r : sol.routes) {
            two_opt_star(sol, r);
        }
    }

    public void two_opt_star(Solution sol, Routes r) {
        for (int i = 0; i < sol.routes.size(); i++) {
            if (sol.routes.get(i) == r) continue;
            if (two_opt_star(r, sol.routes.get(i), Integer.MAX_VALUE, 0)) {
                sol.calculateCost();
            }
        }
    }

    public boolean two_opt_star(Routes route1, Routes route2, int cnt, int threshold) {
        boolean flag = false;
        for (int front = -1; front < route1.size(); front++) {
            for (int back = 0; back <= route2.size(); back++) {
                if (front == -1 && back == route2.size() || front == route1.size() - 1 && back == 0) continue;
                if (front != -1 && back != route2.size() &&
                        !route1.inst.isClose[route1.get(front).id][route2.get(back).id])
                    continue;
                int[] p = route1.cons.validConnect(front, route2, back);
                int[] q = route2.cons.validConnect(back - 1, route1, front + 1);
                if ((p[0] + q[0] <= route1.cons.caPenalty() + route2.cons.caPenalty()
                        && p[1] + q[1] <= route1.cons.twPenalty() + route2.cons.twPenalty()) &&
                        (p[2] + q[2] < threshold || (!route1.isFeasible() || !route2.isFeasible()))) {
                    Routes tmp = new Routes(route1);
                    route1.connect(front, route2, back);
                    try {
                        route2.connect(back - 1, tmp, front + 1);
                    } catch (Exception e) {
                        System.out.println();
                    }
                    flag = true;
                    if (--cnt == 0) {
                        return true;
                    }
                    break;
                }
            }

        }
        return flag;
    }

    public void in_relocate(Solution sol, Routes route) {
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

    public void out_relocate(Solution sol, Routes routes) {
        out_relocate(sol, routes, Integer.MAX_VALUE, 0);
    }

    public void out_relocate(Solution sol, Routes route, int cnt, int threshold) {
        //搜索外界能插入的点
        List<Routes> routes = sol.routes;
        for (Routes r : routes) {
            if (r == route || r.size() == 1) continue;
            out:
            for (int outer = 0; outer < r.size(); outer++) {
                for (int inner = 0; inner <= route.size(); inner++) {
                    if (inner != route.size() && !route.inst.isClose[r.get(outer).id][route.get(inner).id]) continue;
                    int[] p = route.cons.validInsertion(r.get(outer), inner);
                    int[] q = r.cons.validRemove(outer);
                    if ((p[0] + q[0] <= r.cons.caPenalty() + route.cons.caPenalty()
                            && p[1] + q[1] <= r.cons.twPenalty() + route.cons.twPenalty()) &&
                            (p[2] + q[2] < threshold || !r.isFeasible())) {
                        route.insert(r.get(outer), inner);
                        r.remove(outer);
                        if (--cnt == 0) {
                            sol.calculateCost();
                            return;
                        }
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
                if (r == route || r.size() == 0) continue;
                for (int outer = 0; outer <= r.size(); outer++) {
                    if (outer != r.size() && !route.inst.isClose[node.id][r.get(outer).id]) continue;
                    int[] p = r.cons.validInsertion(node, outer);
                    int[] q = route.cons.validRemove(inner);
                    if ((p[0] + q[0] <= r.cons.caPenalty() + route.cons.caPenalty()
                            && p[1] + q[1] <= r.cons.twPenalty() + route.cons.twPenalty()) &&
                            (p[2] + q[2] < threshold || !route.isFeasible())) {
                        r.insert(node, outer);
                        route.remove(inner);
                        if (--cnt == 0) {
                            sol.calculateCost();
                            return;
                        }
                        if (route.size() == 1) {
                            sol.calculateCost();
                            return;
                        }
                        break outIn;
                    }
                }
            }
        }
        sol.calculateCost();
    }

    public void out_relocate(Solution sol) {
        for (int i = 0; i < sol.routes.size(); i++) {
            out_relocate(sol, sol.routes.get(i), Integer.MAX_VALUE, 0);
        }
    }

    public void out_exchange(Solution sol) {
        for (int i = 0; i < sol.routes.size(); i++) {
            for (int j = i + 1; j < sol.routes.size(); j++) {
                if (out_exchange(sol.routes.get(i), sol.routes.get(j))) {
                    out_exchange(sol.routes.get(i), sol.routes.get(j));
                }
            }
        }
    }

    public void out_exchange(Solution sol, Routes r) {
        for (int i = 0; i < sol.routes.size(); i++) {
            Routes r2 = sol.routes.get(i);
            if (r == r2) continue;
            if (out_exchange(r, r2)) sol.calculateCost();
        }
    }

    public boolean out_exchange(Routes r1, Routes r2) {
        return out_exchange(r1, r2, Integer.MAX_VALUE, 0);
    }

    public boolean out_exchange(Routes r1, Routes r2, int cnt, int threshold) {
        boolean flag = false;
        for (int i = 0; i < r1.size(); i++) {
            for (int j = 0; j < r2.size(); j++) {
                if (!r1.inst.isClose[r1.get(i).id][r2.get(j).id]) continue;
                int[] p = r1.cons.validSwap(i, r2, j);
                if ((p[0] <= r1.cons.caPenalty() + r2.cons.caPenalty()
                        && p[1] <= r1.cons.twPenalty() + r2.cons.twPenalty()) && (p[2] < threshold ||
                        !r1.isFeasible() || !r2.isFeasible())) {
                    r1.swap(i, r2, j);
                    flag = true;
                    if (--cnt == 0) {
                        return true;
                    }
                    break;
                }
            }
        }
        return flag;
    }

}
