package Algorithm;

import Common.Instance;
import Common.Nodes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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

    public void swap(int src, @NotNull Routes rt, int det) {
        // TODO: 2020/4/10 待测试
        Nodes tmp = tour.get(src);
        tour.set(src, rt.get(det));
        rt.tour.set(det, tmp);
        this.cons = new Constraints(tour, inst);
        rt.cons = new Constraints(rt.tour, inst);
        checkFeasibility();
    }

    public void connect(int front, @NotNull Routes routes, int back) {
        ArrayList<Nodes> result = new ArrayList<>(front + 1 + routes.size() - back);
        for (int i = 0; i <= front; i++) {
            result.add(tour.get(i));
        }
        for (int i = back; i < routes.size(); i++) {
            result.add(routes.get(i));
        }
        this.tour = result;
        this.cons = new Constraints(tour, inst);
        checkFeasibility();
    }

    public void insert(Nodes v_in, int pos) {
        tour.add(pos, v_in);
        // TODO: 2020/4/6 need optimize
        cons = new Constraints(tour, inst);
        checkFeasibility();
    }

    public void remove(Nodes v_out) {
        tour.remove(v_out);
        cons = new Constraints(tour, inst);
        checkFeasibility();
    }

    public void remove(int index){
        tour.remove(index);
        cons = new Constraints(tour,inst);
        checkFeasibility();
    }

    public void removeIter(@NotNull Iterator<Nodes> iter){
        iter.remove();
        cons = new Constraints(tour,inst);
        checkFeasibility();
    }
    @Override
    public String toString() {
        return "size > " + tour.size() + " > " +isFeasible;
    }
}
