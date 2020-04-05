package Common;

public class Nodes {
    public int id;
    public int demands;
    public int xCoordinate;
    public int yCoordinate;
    public int serviceTime;
    public int earlyTime;
    public int lateTime;

    public Nodes(int id, int demands, int xCoordinate, int yCoordinate, int serviceTime, int earlyTime, int lateTime) {
        this.id = id;
        this.demands = demands;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.serviceTime = serviceTime;
        this.earlyTime = earlyTime;
        this.lateTime = lateTime;
    }

    @Override
    public String toString() {
        return "id "+id+" : ("+xCoordinate+","+yCoordinate+")";
    }
}
