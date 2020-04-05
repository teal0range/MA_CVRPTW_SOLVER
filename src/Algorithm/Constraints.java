package Algorithm;

import Common.Instance;
import Common.Nodes;

import java.util.List;

public class Constraints {
    Instance inst;
    List<Nodes> tour;
    public int[] arrivalTimes;
    public int currentWeight;
    public int maxDelay;
    public int maxRemains;

    public Constraints(List<Nodes> tour, Instance inst) {
        this.inst = inst;
        this.tour = tour;
        currentWeight = 0;
        maxDelay = Integer.MAX_VALUE;
        arrivalTimes = new int[tour.size() + 2];
        UpdateInfo();
    }

    public boolean validInsertion(Nodes v_in, int pos) {
        Nodes pre, post;
        if (v_in.demands > maxRemains) return false;
        if (pos == 0) {
            pre = inst.nodes[0];
            post = tour.get(pos);
        } else if (pos == tour.size()) {
            pre = tour.get(pos - 1);
            post = inst.nodes[0];
        } else {
            pre = tour.get(pos - 1);
            post = tour.get(pos);
        }
        int arrivalVin = Math.max(v_in.earlyTime, arrivalTimes[pos] + pre.serviceTime + inst.dist[pre.id][v_in.id]);
        int newPostTime = Math.max(post.earlyTime, arrivalVin + v_in.serviceTime + inst.dist[v_in.id][post.id]);
        if (arrivalVin <= v_in.lateTime && newPostTime < post.lateTime) {
            int insertionDelay = newPostTime - arrivalTimes[pos + 1];
            return insertionDelay <= maxDelay;
        } else {
            return false;
        }
    }

    public void UpdateInfo(List<Nodes> tour) {
        this.tour = tour;
        UpdateInfo();
    }

    public void UpdateInfo() {
        int lastNode = 0, lastNodeServiceTime = 0;
        for (int i = 1; i < tour.size() + 1; i++) {
            Nodes node = tour.get(i - 1);
            arrivalTimes[i] = Math.max(arrivalTimes[i - 1] + lastNodeServiceTime + inst.dist[lastNode][node.id], node.earlyTime);
            lastNode = node.id;
            lastNodeServiceTime = node.serviceTime;
            if (node.lateTime - arrivalTimes[i] < maxDelay) {
                maxDelay = node.lateTime - arrivalTimes[i];
            }
            currentWeight += node.demands;
        }
        maxRemains = inst.Capacity - currentWeight;
        arrivalTimes[tour.size() + 1] = arrivalTimes[tour.size()] + lastNodeServiceTime + inst.dist[lastNode][0];
    }

    public boolean checkTimeWindowConstraint() {
        return maxDelay > 0;
    }

    public boolean checkCapacityConstraint() {
        return maxRemains > 0;
    }
}
