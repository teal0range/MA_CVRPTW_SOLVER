package Common;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.PriorityQueue;

class DoubleTuple implements Comparable<DoubleTuple> {
    public int key;
    public int value;
    public DoubleTuple(int key, int value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(DoubleTuple o) {
        return Integer.compare(this.value,o.value);
    }
}

public class Instance {
    public String instName;
    public Nodes[] nodes;
    public int n;
    public int maxVehicles;
    public int Capacity;
    public int[][] dist;
    public int[][] closestPoints;
    public boolean[][] isClose;
    double closeRatio = 0.5;

    public Instance(String instName, @NotNull Nodes[] nodes, int Capacity) {
        this.instName = instName;
        this.nodes = nodes;
        this.Capacity = Capacity;
        this.n = nodes.length;
        computeDistance();
    }

    public Instance() {
    }

    public void computeDistance(){
        dist = new int[n][n];
        closestPoints = new int[n][n-1];
        isClose = new boolean[n][n];
        PriorityQueue<DoubleTuple> que = new PriorityQueue<>();
        for (int i=0;i<n;i++){
            for (int j=i+1;j<n;j++){
                dist[i][j] = dist [j][i] = (int)(0.5 +Math.sqrt((nodes[i].xCoordinate-nodes[j].xCoordinate)*(nodes[i].xCoordinate-nodes[j].xCoordinate)
                +(nodes[i].yCoordinate-nodes[j].yCoordinate)*(nodes[i].yCoordinate-nodes[j].yCoordinate)));
            }
            for (int j=0;j<i;j++){
                que.add(new DoubleTuple(j,dist[i][j]));
            }
            for (int j=i+1;j<n;j++){
                que.add(new DoubleTuple(j,dist[i][j]));
            }
            for (int j=0;j<n-1;j++){
                closestPoints[i][j] = Objects.requireNonNull(que.poll()).key;
                if (j < closeRatio * n){
                    isClose[i][closestPoints[i][j]] = true;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "size > "+this.nodes.length;
    }
}
