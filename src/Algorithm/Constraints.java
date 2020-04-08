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
    public int currentWeight;

    public Constraints(@NotNull List<Nodes> tour, Instance inst) {
        this.inst = inst;
        this.tour = tour;
        currentWeight = 0;
        int red = 5;
        arrivalTimesExtended = new ArrayList<>(tour.size()+2+red);
        arrivalTimes = new ArrayList<>(tour.size()+2+red);
        frontTw = new ArrayList<>(tour.size()+2+red);
        zrrivalTimesExtended = new ArrayList<>(tour.size()+2+red);
        zrrivalTimes = new ArrayList<>(tour.size()+2+red);
        backTw = new ArrayList<>(tour.size()+2+red);
        for (int i=0;i<tour.size()+2;i++){
            arrivalTimesExtended.add(0);
        }
        arrivalTimes.addAll(arrivalTimesExtended);
        frontTw.addAll(arrivalTimesExtended);
        zrrivalTimes.addAll(arrivalTimes);
        zrrivalTimesExtended.addAll(arrivalTimes);
        backTw.addAll(arrivalTimes);
        UpdateInfo();
    }

    public int maxRemains(){
        return inst.Capacity - currentWeight;
    }

    public int[] validSwap() {
        return null;
    }

    public int[] validRemove(int pos){
        Nodes target = tour.get(pos);
        Nodes pre, post;
        int capacity_penalty=0;
        if (maxRemains()<0){
            capacity_penalty -= Math.max(tour.get(pos).demands,-maxRemains());
        }
        if (pos == 0 && pos==tour.size()-1){
            pre = inst.nodes[0];
            post = inst.nodes[0];
        }
        else if (pos == 0) {
            pre = inst.nodes[0];
            post = tour.get(pos + 1);
        } else if (pos == tour.size()-1) {
            pre = tour.get(pos-1);
            post = inst.nodes[0];
        } else {
            pre = tour.get(pos - 1);
            post = tour.get(pos + 1);
        }
        int tw_penalty = - Math.max(Math.max(arrivalTimesExtended.get(pos)+
                pre.serviceTime+inst.dist[pre.id][target.id],target.earlyTime)-Math.min(target.lateTime,zrrivalTimesExtended.get(pos+2)-
                inst.dist[target.id][post.id]-target.serviceTime),0);
        int dis_penalty = inst.dist[pre.id][post.id] - inst.dist[pre.id][target.id] - inst.dist[target.id][post.id];
        return new int[]{capacity_penalty,tw_penalty,dis_penalty,pos};
    }

    public int[] validInsertion(@NotNull Nodes v_in, int pos) {
        // TODO: 2020/4/6 if its feasible return null,if not return penalty
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

        if (capacity_penalty>0||tw_penalty>0){
            return new int[]{capacity_penalty,dis_penalty,tw_penalty,pos};
        }

        else {
            return null;
        }
    }

    public void UpdateInfo(List<Nodes> tour) {
        this.tour = tour;
        UpdateInfo();
    }

    public void UpdateInfo() {
        int lastNode = 0, lastNodeServiceTime = inst.nodes[0].serviceTime;
        currentWeight = 0;
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
            currentWeight += node.demands;
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
        currentWeight += node.demands;

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
            }
            else {
                backTw.set(i,backTw.get(i+1));
                zrrivalTimesExtended.set(i,Math.min(node.lateTime,zrrivalTimes.get(i)));
            }
            lastNode = node.id;
        }
        node=inst.nodes[0];
        zrrivalTimes.set(0, zrrivalTimesExtended.get(1) - node.serviceTime - inst.dist[lastNode][node.id]);
        delta_penalty = node.earlyTime - zrrivalTimes.get(0);
        if (delta_penalty > 0){
            backTw.set(0,delta_penalty+ backTw.get(1));
            zrrivalTimesExtended.set(0,node.earlyTime);
        }
        else {
            backTw.set(0,backTw.get(1));
            zrrivalTimesExtended.set(0,Math.min(node.lateTime,zrrivalTimes.get(0)));
        }
    }

    public int twPenalty(){
        return frontTw.get(frontTw.size()-1);
    }

    public int caPenalty(){
        return Math.max(0,-maxRemains());
    }

    public int timeCost(){
        return arrivalTimesExtended.get(arrivalTimesExtended.size()-1);
    }

    public boolean checkTimeWindowConstraint() {
        return frontTw.get(frontTw.size()-1)==0;
    }

    public boolean checkCapacityConstraint() {
        return maxRemains() > 0;
    }
}
