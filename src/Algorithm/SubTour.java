package Algorithm;

import Common.Instance;
import Common.Nodes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SubTour extends Constraints {
    public SubTour(@NotNull List<Nodes> tour, Instance inst, Nodes start) {
        super(tour, inst, start, start);
    }
}
