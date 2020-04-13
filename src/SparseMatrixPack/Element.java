package SparseMatrixPack;

import java.util.ArrayList;
import java.util.HashSet;

public class Element {
    public Element(int row, int column) {
        this.routes = new ArrayList<>();
        this.row = row;
        this.column = column;
    }

    public void prepareRoute() {
        routes.add(new int[]{row});
        edges.add(new HashSet<>());
    }

    public ArrayList<int[]> routes;
    public ArrayList<HashSet<Integer>> edges = new ArrayList<>();

    public int row;
    public int column;

    public ArrayList<int[]> multiply(Element e) {
        ArrayList<int[]> r = new ArrayList<>(this.routes.size() * e.routes.size());
        for (int cnt = 0; cnt < routes.size(); cnt++) {
            int[] front = routes.get(cnt);
            HashSet<Integer> hs = edges.get(cnt);

            out:
            for (int ct = 0; ct < e.routes.size(); ct++) {
                int[] tail = e.routes.get(ct);
                int[] rt = new int[front.length + tail.length];
                int lastEdge = encoding(front[front.length - 1], tail[0]);
                for (int i = 0; i < tail.length - 1; i++) {
                    if (hs.contains(lastEdge)) {
                        break out;
                    }
                    lastEdge = encoding(tail[i], tail[i + 1]);
                }
                lastEdge = encoding(tail[tail.length - 1], e.column);
                if (hs.contains(lastEdge)) {
                    break;
                }
                System.arraycopy(front, 0, rt, 0, front.length);
                System.arraycopy(tail, 0, rt, front.length, tail.length);
                r.add(rt);
            }
        }
        return r;
    }

    public static int encoding(int a, int b) {
        return (a << 16) + b;
    }

    public static int[] decoding(int code) {
        return new int[]{code >> 16, code % (1 << 16)};
    }

    public void add(Element e) {
        routes.addAll(e.routes);
        edges.addAll(e.edges);
    }

    public void add(ArrayList<int[]> e) {
        routes.addAll(e);
        for (int[] arr : e) {
            HashSet<Integer> hs = new HashSet<>();
            for (int i = 0; i < arr.length - 1; i++) {
                hs.add(encoding(arr[i], arr[i + 1]));
            }
            edges.add(hs);
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
