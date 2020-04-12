package EAXOPT;

import java.util.ArrayList;
import java.util.HashMap;

public class Element {
    public Element(int row, int column) {
        this.routes = new ArrayList<>();
        this.row = row;
        this.column = column;
    }

    public void prepareRoute() {
        routes.add(new int[]{row});
        edges.add(new HashMap<>());
    }

    ArrayList<int[]> routes;
    ArrayList<HashMap<Integer, Integer>> edges = new ArrayList<>();

    int row;
    int column;

    public ArrayList<int[]> multiply(Element e) {
        ArrayList<int[]> r = new ArrayList<>(this.routes.size() * e.routes.size());
        for (int cnt = 0; cnt < routes.size(); cnt++) {
            int[] front = routes.get(cnt);
            HashMap<Integer, Integer> hm1 = edges.get(cnt);

            out:
            for (int ct = 0; ct < e.routes.size(); ct++) {
                int[] tail = e.routes.get(ct);
                int[] rt = new int[front.length + tail.length];
                int lastFrom = front[front.length - 1];
                for (int value : tail) {
                    if (hm1.containsKey(lastFrom) && hm1.get(lastFrom) == value) {
                        break out;
                    }
                    lastFrom = value;
                }
                System.arraycopy(front, 0, rt, 0, front.length);
                System.arraycopy(tail, 0, rt, front.length, tail.length);
                r.add(rt);
            }
        }
        return r;
    }

    public void add(Element e) {
        routes.addAll(e.routes);
        edges.addAll(e.edges);
    }

    public void add(ArrayList<int[]> e) {
        routes.addAll(e);
        for (int[] arr : e) {
            HashMap<Integer, Integer> hm = new HashMap<>();
            for (int i = 0; i < arr.length - 1; i++) {
                hm.put(arr[i], arr[i + 1]);
            }

            edges.add(hm);
        }
    }

    @Override
    public String toString() {
        StringBuilder sbr = new StringBuilder();
        sbr.append("{");
        for (int[] arr : routes) {
            sbr.append("[");
            for (int i = 0; i < arr.length - 1; i++) {
                sbr.append(arr[i]).append(",");
            }
            if (arr.length != 0) sbr.append(arr[arr.length - 1]).append("]");
        }
        sbr.append("}");
        return row + " " + column + " > " + sbr.toString();
    }
}
