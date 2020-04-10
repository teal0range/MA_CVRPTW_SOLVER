package Algorithm;

import Common.Instance;
import Common.Nodes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Constraints {
    Instance inst;
    List<Nodes> tour;
    public ArrayList<Integer> arrivalTimesExtended;
    public ArrayList<Integer> arrivalTimes;
    public ArrayList<Integer> frontTw;
    public ArrayList<Integer> zrrivalTimesExtended;
    public ArrayList<Integer> zrrivalTimes;
    public ArrayList<Integer> backTw;
    public ArrayList<Integer> distanceTraveled;
    public ArrayList<Integer> currentWeight;

    public Constraints(@NotNull List<Nodes> tour, Instance inst) {
        this.inst = inst;
        this.tour = tour;
        int red = 0;
        int initialCapacity = tour.size() + 2 + red;
        arrivalTimesExtended = new ArrayList<>(initialCapacity);
        arrivalTimes = new ArrayList<>(initialCapacity);
        frontTw = new ArrayList<>(initialCapacity);
        zrrivalTimesExtended = new ArrayList<>(initialCapacity);
        zrrivalTimes = new ArrayList<>(initialCapacity);
        backTw = new ArrayList<>(initialCapacity);
        distanceTraveled = new ArrayList<>(initialCapacity);
        currentWeight = new ArrayList<>(initialCapacity);
        for (int i = 0; i < tour.size() + 2; i++) {
            arrivalTimesExtended.add(0);
        }
        arrivalTimes.addAll(arrivalTimesExtended);
        frontTw.addAll(arrivalTimesExtended);
        zrrivalTimes.addAll(arrivalTimes);
        zrrivalTimesExtended.addAll(arrivalTimes);
        backTw.addAll(arrivalTimes);
        distanceTraveled.addAll(arrivalTimes);
        currentWeight.addAll(arrivalTimes);
        if (tour.size() != 0) {
            UpdateInfo();
        }
    }

    public int maxRemains() {
        return inst.Capacity - currentWeight.get(currentWeight.size() - 1);
    }

    public int[] validSwap(int src, @NotNull Routes rt, int det) {
        // TODO: 2020/4/9 待测试
        int[] p = validSubstitute(src, rt.get(det));
        int[] q = rt.cons.validSubstitute(det, tour.get(src));
        return new int[]{p[0] + q[0], p[1] + q[1], p[2] + q[2], src};
    }

    public int[] validConnect(int front, @NotNull Routes tail, int back) {
        //front 从-1取到tour.size()-1
        //back 从0取到tail.size()
        int totalDemands = this.currentWeight.get(front + 1) +
                tail.cons.currentWeight.get(tail.cons.currentWeight.size() - 1)
                - tail.cons.currentWeight.get(back);
        int capacity_penalty = Math.min(inst.Capacity - totalDemands, 0);
        int totalDis = this.distanceTraveled.get(front + 1) +
                tail.cons.distanceTraveled.get(tail.cons.distanceTraveled.size() - 1)
                - tail.cons.distanceTraveled.get(back);
        int dis_penalty = totalDis - this.distanceTraveled();
        Nodes frontNode = front == -1 ? inst.nodes[0] : tour.get(front);
        Nodes backNode = back == tail.size() ? inst.nodes[0] : tail.get(back);
        int tw_Penalty = this.frontTw.get(front + 1) + tail.cons.backTw.get(back + 1) +
                Math.max(this.arrivalTimesExtended.get(front + 1) + frontNode.serviceTime +
                        inst.dist[frontNode.id][backNode.id] - backNode.lateTime, 0);
        return new int[]{capacity_penalty, tw_Penalty, dis_penalty, front};
    }

    public int[] validSubstitute(int src, @NotNull Nodes v_in) {
        // TODO: 2020/4/9 待测试
        int totalDemands = this.currentWeight.get(currentWeight.size() - 1)
                - tour.get(src).demands + v_in.demands;
        int ca_penalty = Math.max(totalDemands - inst.Capacity, 0);
        Nodes srcPre = src == 0 ? inst.nodes[0] : tour.get(src - 1);
        Nodes srcNode = tour.get(src);
        Nodes srcPost = src == tour.size() - 1 ? inst.nodes[0] : tour.get(src + 1);
        int dis_penalty = -inst.dist[srcPre.id][srcNode.id] -
                inst.dist[srcPost.id][srcNode.id] + inst.dist[srcPre.id][v_in.id] + inst.dist[srcPost.id][v_in.id];
        int tw_penalty = frontTw.get(src) + backTw.get(src + 2) +
                Math.max(Math.max(arrivalTimesExtended.get(src) + srcPre.serviceTime + inst.dist[srcPre.id][v_in.id], v_in.earlyTime),
                        Math.min(srcPost.lateTime, zrrivalTimesExtended.get(src + 2) - v_in.serviceTime - inst.dist[srcPost.id][v_in.id]));
        return new int[]{ca_penalty, tw_penalty, dis_penalty, src};
    }

    public int[] validRemove(int pos) {
        Nodes target = tour.get(pos);
        Nodes pre, post;
        int capacity_penalty = 0;
        if (maxRemains() < 0) {
            capacity_penalty = Math.max(-maxRemains() - tour.get(pos).demands, 0);
        }
        if (pos == 0 && pos == tour.size() - 1) {
            pre = inst.nodes[0];
            post = inst.nodes[0];
        } else if (pos == 0) {
            pre = inst.nodes[0];
            post = tour.get(pos + 1);
        } else if (pos == tour.size() - 1) {
            pre = tour.get(pos - 1);
            post = inst.nodes[0];
        } else {
            pre = tour.get(pos - 1);
            post = tour.get(pos + 1);
        }
        int tw_penalty = this.twPenalty() - Math.max(Math.max(arrivalTimesExtended.get(pos) +
                pre.serviceTime + inst.dist[pre.id][target.id], target.earlyTime) - Math.min(target.lateTime, zrrivalTimesExtended.get(pos + 2) -
                inst.dist[target.id][post.id] - target.serviceTime), 0);
        int dis_penalty = inst.dist[pre.id][post.id] - inst.dist[pre.id][target.id] - inst.dist[target.id][post.id];
        return new int[]{capacity_penalty, tw_penalty, dis_penalty, pos};
    }

    public int[] validInsertion(@NotNull Nodes v_in, int pos) {
        Nodes pre, post;
        int capacity_penalty=0;
        if (v_in.demands > maxRemains()){
            capacity_penalty += v_in.demands - maxRemains();
        }
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

        int tw_penalty = frontTw.get(pos) + backTw.get(pos + 1) + Math.max(Math.max(arrivalTimesExtended.get(pos)+
                pre.serviceTime+inst.dist[pre.id][v_in.id],v_in.earlyTime)-Math.min(v_in.lateTime,zrrivalTimesExtended.get(pos+1)-
                inst.dist[v_in.id][post.id]-v_in.serviceTime),0);

        int dis_penalty = inst.dist[pre.id][v_in.id] + inst.dist[v_in.id][post.id] - inst.dist[pre.id][post.id];

        return new int[]{capacity_penalty, tw_penalty, dis_penalty, pos};
    }

    public void UpdateInfo(List<Nodes> tour) {
        this.tour = tour;
        UpdateInfo();
    }

    public void UpdateInfo() {
        // TODO: 2020/4/9 这里需要优化
        int lastNode = 0, lastNodeServiceTime = inst.nodes[0].serviceTime;
        arrivalTimes.set(0,inst.nodes[0].earlyTime);
        arrivalTimesExtended.set(0,inst.nodes[0].earlyTime);
        for (int i = 1; i < tour.size() + 1; i++) {
            Nodes node = tour.get(i - 1);
            arrivalTimes.set(i, arrivalTimesExtended.get(i - 1) + lastNodeServiceTime + inst.dist[lastNode][node.id]);
            int delta_penalty = arrivalTimes.get(i) - node.lateTime;
            if (delta_penalty > 0){
                frontTw.set(i, delta_penalty + frontTw.get(i-1));
                arrivalTimesExtended.set(i,node.lateTime);
            }
            else {
                frontTw.set(i, frontTw.get(i-1));
                arrivalTimesExtended.set(i, Math.max(arrivalTimes.get(i), node.earlyTime));
            }
            lastNode = node.id;
            lastNodeServiceTime = node.serviceTime;
        }
        Nodes node = inst.nodes[0];
        arrivalTimes.set(tour.size()+1, arrivalTimesExtended.get(tour.size()) + lastNodeServiceTime + inst.dist[lastNode][node.id]);
        int delta_penalty = arrivalTimes.get(tour.size()+1) - node.lateTime;
        if (delta_penalty > 0){
            frontTw.set(tour.size()+1, delta_penalty + frontTw.get(tour.size()));
            arrivalTimesExtended.set(tour.size()+1,node.lateTime);
        }
        else {
            frontTw.set(tour.size()+1, frontTw.get(tour.size()));
            arrivalTimesExtended.set(tour.size()+1, Math.max(arrivalTimes.get(tour.size()+1), node.earlyTime));
        }

        lastNode = 0;
        zrrivalTimesExtended.set(tour.size()+1,inst.nodes[0].lateTime);
        zrrivalTimes.set(tour.size()+1,zrrivalTimesExtended.get(tour.size()+1));
        for (int i = tour.size();i>0;i--){
            node = tour.get(i-1);
            zrrivalTimes.set(i, zrrivalTimesExtended.get(i + 1) - node.serviceTime - inst.dist[lastNode][node.id]);
            delta_penalty = node.earlyTime - zrrivalTimes.get(i);
            if (delta_penalty > 0){
                backTw.set(i,delta_penalty + backTw.get(i+1));
                zrrivalTimesExtended.set(i,node.earlyTime);
            } else {
                backTw.set(i, backTw.get(i + 1));
                zrrivalTimesExtended.set(i, Math.min(node.lateTime, zrrivalTimes.get(i)));
            }
            lastNode = node.id;
        }
        node = inst.nodes[0];
        zrrivalTimes.set(0, zrrivalTimesExtended.get(1) - node.serviceTime - inst.dist[lastNode][node.id]);
        delta_penalty = node.earlyTime - zrrivalTimes.get(0);
        if (delta_penalty > 0) {
            backTw.set(0, delta_penalty + backTw.get(1));
            zrrivalTimesExtended.set(0, node.earlyTime);
        } else {
            backTw.set(0, backTw.get(1));
            zrrivalTimesExtended.set(0, Math.min(node.lateTime, zrrivalTimes.get(0)));
        }
        currentWeight.set(1, currentWeight.get(0) + tour.get(0).demands);
        for (int i = 1; i < tour.size(); i++) {
            currentWeight.set(i + 1, currentWeight.get(i) + tour.get(i).demands);
        }
        currentWeight.set(currentWeight.size() - 1, currentWeight.get(currentWeight.size() - 2) + inst.nodes[0].demands);
        distanceTraveled.set(1, distanceTraveled.get(0) + inst.dist[inst.nodes[0].id][tour.get(0).id]);
        for (int i = 1; i < tour.size(); i++) {
            distanceTraveled.set(i + 1, distanceTraveled.get(i) + inst.dist[tour.get(i - 1).id][tour.get(i).id]);
        }
        distanceTraveled.set(distanceTraveled.size() - 1, distanceTraveled.get(distanceTraveled.size() - 2) + inst.dist[inst.nodes[0].id][tour.get(tour.size() - 1).id]);
    }

    public int distanceTraveled() {
        return distanceTraveled.get(distanceTraveled.size() - 1);
    }

    public int twPenalty() {
        return frontTw.get(frontTw.size() - 1);
    }

    public int caPenalty() {
        return Math.max(0, -maxRemains());
    }

    public int timeCost() {
        return arrivalTimesExtended.get(arrivalTimesExtended.size() - 1);
    }

    public boolean checkTimeWindowConstraint() {
        return frontTw.get(frontTw.size() - 1) == 0;
    }

    public boolean checkCapacityConstraint() {
        return maxRemains() > 0;
    }
}
