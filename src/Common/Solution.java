package Common;

import Algorithm.Routes;

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

    public Solution(Solution solution) {
        this.timeCost = solution.timeCost;
        this.scheduleTime = solution.scheduleTime;
        this.distance = solution.distance;
        this.twPenalty = solution.twPenalty;
        this.caPenalty = solution.caPenalty;
        this.infeasibleRoutes = new HashSet<>();
        this.infeasibleRoutes.addAll(solution.infeasibleRoutes);
        this.routes = new ArrayList<>(solution.routes.size());
        for (Routes r : solution.routes) {
            this.routes.add(new Routes(r));
        }
    }

    public void addInFeasible(int i, int[] p) {
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
        HashSet<Integer> hs = new HashSet<>();
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
            index++;
        }
        index = 0;
        for (Routes r : routes) {
            if (!r.isFeasible()) {
                hs.add(index);
            }
            index++;
        }
        this.infeasibleRoutes = hs;
    }

    @Override
    public String toString() {
        int size = 0;
        for (Routes r : routes) {
            size += r.size();
        }
        return size + " " + routes.toString();
    }

    public String toPlotString(Instance inst) {
        StringBuilder sb = new StringBuilder();
        sb.append(distance).append("\r\n");
        sb.append("tour\r\n");
        for (Routes tour : routes) {
            sb.append(inst.nodes[0].id).append(" ").append(inst.nodes[0].xCoordinate).append(" ").append(inst.nodes[0].yCoordinate).append("\r\n");
            for (Nodes node : tour.tour) {
                sb.append(node.id).append(" ").append(node.yCoordinate).append(" ").append(node.yCoordinate).append("\r\n");
            }
            sb.append(inst.nodes[0].id).append(" ").append(inst.nodes[0].xCoordinate).append(" ").append(inst.nodes[0].yCoordinate).append("\r\n");
        }
        return sb.toString();
    }
}
