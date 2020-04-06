package Common;

import Algorithm.Routes;

import java.util.List;

public class Solution {
    public int cost;
    List<Routes> routes;

    public Solution(){

    }
    public Solution(List<Routes> routes){
        this.routes = routes;
    }
    public Solution(Solution solution) {
        this.cost = solution.cost;
    }

    public void calculateCost(){

    }
}
