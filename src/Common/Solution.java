package Common;

import Algorithm.Routes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Solution {
    public int distance;
    public List<Routes> routes;
    public HashSet<Integer> infeasibleRoutes;
    public int twPenalty;
    public int caPenalty;
    public int timeCost;
    public int scheduleTime;

    public Solution() {
        this.infeasibleRoutes = new HashSet<>();
    }

    public Solution(List<Routes> routes) {
        this.routes = routes;
        this.infeasibleRoutes = new HashSet<>();
    }

    public Solution(@NotNull Solution solution) {
        this.timeCost = solution.timeCost;
        this.scheduleTime = solution.scheduleTime;
        this.distance = solution.distance;
        this.twPenalty = solution.twPenalty;
        this.caPenalty = solution.caPenalty;
        this.infeasibleRoutes = new HashSet<>();
        Iterator<Integer> iter = solution.infeasibleRoutes.iterator();
        while (iter.hasNext()) {
            this.infeasibleRoutes.add(iter.next());
        }
        this.routes = new ArrayList<>(solution.routes.size());
        for (Routes r : solution.routes) {
            this.routes.add(new Routes(r));
        }
    }

    public void addInFeasible(int i, @NotNull int[] p) {
        infeasibleRoutes.add(i);
        caPenalty += p[0];
        twPenalty += p[1];
    }

    public void removeInFeasible(int i) {
        infeasibleRoutes.remove(i);
    }

    public void calculateCost() {
        twPenalty = 0;
        caPenalty = 0;
        timeCost = 0;
        scheduleTime = 0;
        distance = 0;
        Iterator<Routes> iter = routes.iterator();
        int index = 0;
        while (iter.hasNext()) {
            Routes r = iter.next();
            if (r.isFeasible() && infeasibleRoutes.contains(index)) {
                infeasibleRoutes.remove(index);
            }
            if (!r.isFeasible() && !infeasibleRoutes.contains((index))) {
                infeasibleRoutes.add(index);
            }
            if (r.size() == 0) {
                iter.remove();
                continue;
            }
            distance += r.cons.distanceTraveled();
            twPenalty += r.cons.twPenalty();
            caPenalty += r.cons.caPenalty();
            timeCost += r.cons.timeCost();
            scheduleTime = Math.max(scheduleTime, r.cons.timeCost());
            index++;
        }
    }

    @Override
    public String toString() {
        int size = 0;
        for (Routes r:routes){
            size+=r.size();
        }
        return "size > "+size;
    }
}
