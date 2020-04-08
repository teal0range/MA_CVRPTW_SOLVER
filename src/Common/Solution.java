package Common;

import Algorithm.Routes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Solution {
    public int distance;
    public List<Routes> routes;
    public List<Integer> inFeasibleRoutes;
    public int twPenalty;
    public int caPenalty;
    public int timeCost;
    public int scheduleTime;

    public Solution() {
        this.inFeasibleRoutes = new ArrayList<>();
    }

    public Solution(List<Routes> routes) {
        this.routes = routes;
        this.inFeasibleRoutes = new ArrayList<>();
    }

    public Solution(@NotNull Solution solution) {
        this.timeCost = solution.timeCost;
        this.scheduleTime = solution.scheduleTime;
        this.distance = solution.distance;
        this.twPenalty = solution.twPenalty;
        this.caPenalty = solution.caPenalty;
        this.inFeasibleRoutes = new ArrayList<>();
        Collections.copy(inFeasibleRoutes, solution.inFeasibleRoutes);
        this.routes = new ArrayList<>(solution.routes.size());
        for (Routes r : solution.routes) {
            this.routes.add(new Routes(r));
        }
    }

    public void addInFeasible(int i, @NotNull int[] p) {
        inFeasibleRoutes.add(i);
        caPenalty += p[0];
        twPenalty += p[1];
    }

    public void removeInFeasible(int i, int[] p) {
        inFeasibleRoutes.remove(i);
    }

    public void calculateCost() {
        twPenalty = 0;
        caPenalty = 0;
        timeCost = 0;
        scheduleTime = 0;
        distance = 0;
        Iterator<Routes> iter = routes.iterator();
        while (iter.hasNext()) {
            Routes r = iter.next();
            if (r.size() == 0) {
                iter.remove();
                continue;
            }
            distance += r.cons.distanceTraveled();
            twPenalty += r.cons.twPenalty();
            caPenalty += r.cons.caPenalty();
            timeCost += r.cons.timeCost();
            scheduleTime = Math.max(scheduleTime, r.cons.timeCost());
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
