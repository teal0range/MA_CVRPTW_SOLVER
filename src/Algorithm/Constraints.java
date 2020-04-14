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
    public ArrayList<Integer> frontTw;
    public ArrayList<Integer> zrrivalTimesExtended;
    public ArrayList<Integer> backTw;
    public ArrayList<Integer> distanceTraveled;
    public ArrayList<Integer> currentWeight;
    Nodes firstVertex, lastVertex;

    public Constraints(@NotNull List<Nodes> tour, Instance inst) {
        this(tour, inst, inst.nodes[0], inst.nodes[0]);
    }

    public Constraints(@NotNull List<Nodes> tour, Instance inst, Nodes first, Nodes last) {
        this.inst = inst;
        this.tour = tour;
        int red = 0;
        int initialCapacity = tour.size() + 2 + red;
        arrivalTimesExtended = new ArrayList<>(initialCapacity);
        frontTw = new ArrayList<>(initialCapacity);
        zrrivalTimesExtended = new ArrayList<>(initialCapacity);
        backTw = new ArrayList<>(initialCapacity);
        distanceTraveled = new ArrayList<>(initialCapacity);
        currentWeight = new ArrayList<>(initialCapacity);
        for (int i = 0; i < tour.size() + 2; i++) {
            arrivalTimesExtended.add(0);
        }
        frontTw.addAll(arrivalTimesExtended);
        zrrivalTimesExtended.addAll(arrivalTimesExtended);
        backTw.addAll(arrivalTimesExtended);
        distanceTraveled.addAll(arrivalTimesExtended);
        currentWeight.addAll(arrivalTimesExtended);
        firstVertex = inst.nodes[0];
        lastVertex = inst.nodes[0];

        firstVertex = first;
        lastVertex = last;
        if (tour.size() != 0) {
            UpdateInfo();
        }
    }

    public int[] validSubTourInsertion(@NotNull Constraints v_in, int breakPoint, int insertPos) {
        //breakPoint 由-1到v_in.tour.size()-1
        //insertPos 由-1到tour.size()-1
        int totalDemands = currentWeight.get(currentWeight.size() - 1) +
                v_in.currentWeight.get(v_in.currentWeight.size() - 1);
        Nodes Pre = insertPos == -1 ? firstVertex : tour.get(insertPos);
        Nodes Next = insertPos == tour.size() - 1 ? lastVertex : tour.get(insertPos + 1);
        Nodes breakNode = breakPoint == -1 ? v_in.firstVertex : v_in.tour.get(breakPoint);
        Nodes breakNext = breakPoint == v_in.tour.size() - 1 ? v_in.lastVertex : v_in.tour.get(breakPoint + 1);
        int ca_Penalty = Math.max(totalDemands - inst.Capacity, 0);
        int dis_Penalty = v_in.distanceTraveled() - inst.dist[Pre.id][Next.id]
                - inst.dist[breakNode.id][breakNext.id] +
                inst.dist[Pre.id][breakNext.id] + inst.dist[breakNode.id][Next.id];
        int tw_Penalty = 0;
        Nodes lastNode = Pre;
        int lastArrivalTime = arrivalTimesExtended.get(insertPos + 1);
        Nodes thisNode;
        int arrival;
        for (int i = breakPoint + 1; i < v_in.tour.size(); i++) {
            thisNode = v_in.tour.get(i);
            arrival = lastArrivalTime + inst.dist[lastNode.id][thisNode.id] + thisNode.serviceTime;
            lastArrivalTime = Math.min(thisNode.lateTime, Math.max(thisNode.earlyTime, arrival));
            tw_Penalty += Math.max(arrival - thisNode.lateTime, 0);
            lastNode = thisNode;
        }
        thisNode = v_in.lastVertex;
        arrival = lastArrivalTime + inst.dist[lastNode.id][thisNode.id] + thisNode.serviceTime;
        lastArrivalTime = Math.min(thisNode.lateTime, Math.max(thisNode.earlyTime, arrival));
        tw_Penalty += Math.max(arrival - thisNode.lateTime, 0);
        lastNode = thisNode;
        for (int i = 0; i <= breakPoint; i++) {
            thisNode = v_in.tour.get(i);
            arrival = lastArrivalTime + inst.dist[lastNode.id][thisNode.id] + thisNode.serviceTime;
            lastArrivalTime = Math.min(thisNode.lateTime, Math.max(thisNode.earlyTime, arrival));
            tw_Penalty += Math.max(arrival - thisNode.lateTime, 0);
            lastNode = thisNode;
        }
        tw_Penalty += backTw.get(insertPos + 2) + Math.max(lastArrivalTime + lastNode.serviceTime
                + inst.dist[lastNode.id][Next.id] - zrrivalTimesExtended.get(insertPos + 2), 0);
        return new int[]{ca_Penalty, tw_Penalty, dis_Penalty, RoutesMinimizer.encoding(breakPoint, insertPos)};
    }


    public int maxRemains() {
        return inst.Capacity - currentWeight.get(currentWeight.size() - 1);
    }

    public int[] validSwap(int src, @NotNull Routes rt, int det) {
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
        int capacity_penalty = Math.max(-inst.Capacity + totalDemands, 0);
        int totalDis = this.distanceTraveled.get(front + 1) +
                tail.cons.distanceTraveled.get(tail.cons.distanceTraveled.size() - 1)
                - tail.cons.distanceTraveled.get(back);
        int dis_penalty = totalDis - this.distanceTraveled();
        Nodes frontNode = front == -1 ? inst.nodes[0] : tour.get(front);
        Nodes backNode = back == tail.size() ? inst.nodes[0] : tail.get(back);
        int tw_Penalty = this.frontTw.get(front + 1) + tail.cons.backTw.get(back + 1) +
                Math.max(this.arrivalTimesExtended.get(front + 1) + frontNode.serviceTime +
                        inst.dist[frontNode.id][backNode.id] - tail.cons.zrrivalTimesExtended.get(back + 1), 0);
        return new int[]{capacity_penalty, tw_Penalty, dis_penalty, front};
    }

    public int[] validSubstitute(int src, @NotNull Nodes v_in) {
        int totalDemands = this.currentWeight.get(currentWeight.size() - 1)
                - tour.get(src).demands + v_in.demands;
        int ca_penalty = Math.max(totalDemands - inst.Capacity, 0);
        Nodes srcPre = src == 0 ? inst.nodes[0] : tour.get(src - 1);
        Nodes srcNode = tour.get(src);
        Nodes srcPost = src == tour.size() - 1 ? inst.nodes[0] : tour.get(src + 1);
        int dis_penalty = -inst.dist[srcPre.id][srcNode.id] -
                inst.dist[srcPost.id][srcNode.id] + inst.dist[srcPre.id][v_in.id] + inst.dist[srcPost.id][v_in.id];
        int tw_penalty = frontTw.get(src) + backTw.get(src + 2) +
                Math.max(Math.max(arrivalTimesExtended.get(src) + srcPre.serviceTime + inst.dist[srcPre.id][v_in.id],
                        v_in.earlyTime) - Math.min(v_in.lateTime, zrrivalTimesExtended.get(src + 2)
                        - v_in.serviceTime - inst.dist[srcPost.id][v_in.id]), 0);
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
        int lastNode = firstVertex.id, lastNodeServiceTime = firstVertex.serviceTime;
        int arrivalTimes;
        arrivalTimesExtended.set(0, firstVertex.earlyTime);
        for (int i = 1; i < tour.size() + 1; i++) {
            Nodes node = tour.get(i - 1);
            arrivalTimes = arrivalTimesExtended.get(i - 1) + lastNodeServiceTime + inst.dist[lastNode][node.id];
            int delta_penalty = arrivalTimes - node.lateTime;
            if (delta_penalty > 0) {
                frontTw.set(i, delta_penalty + frontTw.get(i - 1));
                arrivalTimesExtended.set(i, node.lateTime);
            } else {
                frontTw.set(i, frontTw.get(i - 1));
                arrivalTimesExtended.set(i, Math.max(arrivalTimes, node.earlyTime));
            }
            lastNode = node.id;
            lastNodeServiceTime = node.serviceTime;
        }
        Nodes node = lastVertex;
        arrivalTimes = arrivalTimesExtended.get(tour.size()) + lastNodeServiceTime + inst.dist[lastNode][node.id];
        int delta_penalty = arrivalTimes - node.lateTime;
        if (delta_penalty > 0) {
            frontTw.set(tour.size() + 1, delta_penalty + frontTw.get(tour.size()));
            arrivalTimesExtended.set(tour.size() + 1, node.lateTime);
        } else {
            frontTw.set(tour.size() + 1, frontTw.get(tour.size()));
            arrivalTimesExtended.set(tour.size() + 1, Math.max(arrivalTimes, node.earlyTime));
        }

        lastNode = lastVertex.id;
        zrrivalTimesExtended.set(tour.size() + 1, lastVertex.lateTime);
        int zrrivalTimes;
        for (int i = tour.size(); i > 0; i--) {
            node = tour.get(i - 1);
            zrrivalTimes = zrrivalTimesExtended.get(i + 1) - node.serviceTime - inst.dist[lastNode][node.id];
            delta_penalty = node.earlyTime - zrrivalTimes;
            if (delta_penalty > 0) {
                backTw.set(i, delta_penalty + backTw.get(i + 1));
                zrrivalTimesExtended.set(i, node.earlyTime);
            } else {
                backTw.set(i, backTw.get(i + 1));
                zrrivalTimesExtended.set(i, Math.min(node.lateTime, zrrivalTimes));
            }
            lastNode = node.id;
        }
        node = firstVertex;
        zrrivalTimes = zrrivalTimesExtended.get(1) - node.serviceTime - inst.dist[lastNode][node.id];
        delta_penalty = node.earlyTime - zrrivalTimes;
        if (delta_penalty > 0) {
            backTw.set(0, delta_penalty + backTw.get(1));
            zrrivalTimesExtended.set(0, node.earlyTime);
        } else {
            backTw.set(0, backTw.get(1));
            zrrivalTimesExtended.set(0, Math.min(node.lateTime, zrrivalTimes));
        }
        currentWeight.set(1, currentWeight.get(0) + tour.get(0).demands);
        for (int i = 1; i < tour.size(); i++) {
            currentWeight.set(i + 1, currentWeight.get(i) + tour.get(i).demands);
        }
        currentWeight.set(currentWeight.size() - 1, currentWeight.get(currentWeight.size() - 2) + lastVertex.demands);
        distanceTraveled.set(1, distanceTraveled.get(0) + inst.dist[firstVertex.id][tour.get(0).id]);
        for (int i = 1; i < tour.size(); i++) {
            distanceTraveled.set(i + 1, distanceTraveled.get(i) + inst.dist[tour.get(i - 1).id][tour.get(i).id]);
        }
        distanceTraveled.set(distanceTraveled.size() - 1, distanceTraveled.get(distanceTraveled.size() - 2) + inst.dist[lastVertex.id][tour.get(tour.size() - 1).id]);
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
        return maxRemains() >= 0;
    }
}
