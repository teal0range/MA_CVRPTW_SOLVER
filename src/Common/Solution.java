package Common;

import Algorithm.Routes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solution {
    public int cost;
    public List<Routes> routes;
    public List<Integer> inFeasibleRoutes;
    public int twPenalty;
    public int caPenalty;

    public Solution(){
        this.inFeasibleRoutes = new ArrayList<>();
    }
    public Solution(List<Routes> routes){
        this.routes = routes;
        this.inFeasibleRoutes = new ArrayList<>();
    }
    public Solution(Solution solution) {
        this.cost = solution.cost;
        this.twPenalty = solution.twPenalty;
        this.caPenalty = solution.caPenalty;
        this.inFeasibleRoutes = new ArrayList<>();
        Collections.copy(inFeasibleRoutes,solution.inFeasibleRoutes);
        for (Routes r:solution.routes){
            this.routes.add(new Routes(r));
        }
    }
    public void addInFeasible(int i,int []p){
        inFeasibleRoutes.add(i);
        caPenalty += p[0];
        twPenalty += p[1];
    }
    public void removeInFeasible(int i,int []p){
        inFeasibleRoutes.remove(i);
    }

    public void calculateCost(){

    }
}
