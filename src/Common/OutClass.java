package Common;

public class OutClass {
    String instname;
    public AlgoParam param;
    int n;
    public double time;
    public String sol;

    public OutClass(String instname, AlgoParam param, int n, double time, String sol) {
        this.instname = instname;
        this.param = param;
        this.n = n;
        this.time = time;
        this.sol = sol;
    }
}
