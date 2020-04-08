package Algorithm;

import Algorithm.Constraints;
import Common.Instance;
import Common.Nodes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class Routes {
    Instance inst;
    public ArrayList<Nodes> tour;
    private boolean isFeasible;
    public Constraints cons;

    public Routes(ArrayList<Nodes> tour, Instance inst) {
        this.inst = inst;
        this.tour = tour;
        cons = new Constraints(tour, inst);
        checkFeasibility();
    }

    public Routes(@NotNull Routes r) {
        this.inst = r.inst;
        this.tour = new ArrayList<>(r.tour.size());
        for (int i=0;i<r.tour.size();i++)this.tour.add(null);
        Collections.copy(this.tour,r.tour);
        this.cons = new Constraints(tour,inst);
        checkFeasibility();
    }

    public Nodes get(int index){
        return tour.get(index);
    }

    public int size(){
        return tour.size();
    }

    public boolean isFeasible() {
        return isFeasible;
    }

    public void checkFeasibility() {
        isFeasible = cons.checkCapacityConstraint() & cons.checkTimeWindowConstraint();
    }

    public void insert(Nodes v_in,int pos){
        tour.add(pos,v_in);
        // TODO: 2020/4/6 need optimize
        cons = new Constraints(tour,inst);
        checkFeasibility();
    }
    public void remove(Nodes v_out){
        tour.remove(v_out);
        cons = new Constraints(tour,inst);
        checkFeasibility();
    }
    public void remove(int index){
        tour.remove(index);
        cons = new Constraints(tour,inst);
        checkFeasibility();
    }

    @Override
    public String toString() {
        return "size > " + tour.size() + " > " +isFeasible;
    }
}
