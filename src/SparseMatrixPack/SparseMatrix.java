package SparseMatrixPack;

import Algorithm.Routes;
import Common.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class SparseMatrix {
    public int row;
    public int column;
    public ArrayList<Element> elem;
    public int[] rpos;

    public SparseMatrix() {
        elem = new ArrayList<>();
    }

    public void convertRoute(Solution sol) {
        HashSet<Integer> hs = getEdges(sol);
        for (int from : hs) {
            int[] edge = Element.decoding(from);
            Element e = new Element(edge[0], edge[1]);
            e.prepareRoute();
            elem.add(e);
        }
        elem.sort(Comparator.comparingInt((Element o) -> o.row).thenComparingInt(o -> o.column));
        row = column = sol.routes.get(0).inst.n;
        initializeRpos();
    }

    public void convertRoute(Solution sol, ArrayList<int[]> eSets) {
        HashSet<Integer> hs = getEdges(sol);
        for (int[] eSet : eSets) {
            for (int i = 0; i < eSet.length; i += 2) {
                hs.remove(Element.encoding(eSet[i], eSet[(i + 1) % eSet.length]));
            }
            for (int i = 1; i < eSet.length; i += 2) {
                hs.add(Element.encoding(eSet[(i + 1) % eSet.length], eSet[i]));
            }
        }
        for (int from : hs) {
            int[] edge = Element.decoding(from);
            Element e = new Element(edge[0], edge[1]);
            e.prepareRoute();
            elem.add(e);
        }
        elem.sort(Comparator.comparingInt((Element o) -> o.row).thenComparingInt(o -> o.column));
        row = column = sol.routes.get(0).inst.n;
        initializeRpos();
    }

    public SparseMatrix Route2Gab(Solution p1, Solution p2) {
        HashSet<Integer> hs1;
        HashSet<Integer> hs2;
        hs1 = getEdges(p1);
        hs2 = getEdges(p2);
        for (int fromTo : hs1) {
            int[] edge = Element.decoding(fromTo);
            if (!hs2.contains(fromTo)) {
                Element e = new Element(edge[0], edge[1]);
                e.prepareRoute();
                elem.add(e);
            }
        }
        SparseMatrix sm = new SparseMatrix();
        for (int fromTo : hs2) {
            int[] edge = Element.decoding(fromTo);
            if (!hs1.contains(fromTo)) {
                Element e = new Element(edge[0], edge[1]);
                e.prepareRoute();
                sm.elem.add(e);
            }
        }
        elem.sort(Comparator.comparingInt((Element o) -> o.row).thenComparingInt(o -> o.column));
        sm.elem.sort(Comparator.comparingInt((Element o) -> o.row).thenComparingInt(o -> o.column));
        row = column = p1.routes.get(0).inst.n;
        sm.row = sm.column = p2.routes.get(0).inst.n;
        initializeRpos();
        sm.initializeRpos();
        return sm;
    }


    public SparseMatrix trans() {
        SparseMatrix mt = this;
        int[] arr = new int[mt.column + 1];
        for (int i = 0; i < mt.elem.size(); i++) {
            arr[mt.elem.get(i).column + 1]++;
        }
        for (int i = 1; i < mt.column; i++) {
            arr[i] = arr[i] + arr[i - 1];
        }
        Element[] tmp = new Element[mt.elem.size()];
        for (int i = 0; i < mt.elem.size(); i++) {
            tmp[arr[mt.elem.get(i).column]] = new Element(mt.elem.get(i).column, mt.elem.get(i).row);
            tmp[arr[mt.elem.get(i).column]].prepareRoute();
            arr[mt.elem.get(i).column]++;
        }
        SparseMatrix newMt = new SparseMatrix();
        for (int i = 0; i < mt.elem.size(); i++) {
            newMt.add(tmp[i]);
        }
        newMt.initializeRpos();
        return newMt;
    }


    private HashSet<Integer> getEdges(Solution p1) {
        HashSet<Integer> hashSet = new HashSet<>();
        for (Routes r : p1.routes) {
            int lastNode = 0;
            for (int i = 0; i < r.size(); i++) {
                hashSet.add(Element.encoding(lastNode, r.get(i).id));
                lastNode = r.get(i).id;
            }
            hashSet.add(Element.encoding(lastNode, 0));
        }
        return hashSet;
    }

    public SparseMatrix(int row, int column) {
        this.row = row;
        this.column = column;
        elem = new ArrayList<>();
    }

    public void add(Element e) {
        if (e.row + 1 > row) row = e.row + 1;
        if (e.column + 1 > column) column = e.column + 1;
        elem.add(e);
    }

    public void put(Element e) {
        int pos = rpos[e.row];
        while (pos < elem.size() && e.column > elem.get(pos).column) {
            pos++;
        }
        if (pos < elem.size() && e.column == elem.get(pos).column) {
            return;
        }
        elem.add(pos, e);
        initializeRpos();
    }

    public void remove(int row, int column) {
        int pos = rpos[row];
        while (pos < elem.size() && elem.get(pos).row == row && elem.get(pos).column != column) pos++;
        if (pos >= elem.size() || row != elem.get(pos).row) return;
        elem.remove(pos);
        initializeRpos();
    }

    public void initializeRpos() {
        int[] arr = new int[this.row + 1];
        for (Element element : this.elem) {
            arr[element.row]++;
        }
        rpos = new int[this.row + 1];
        rpos[0] = 0;
        for (int i = 1; i < this.row + 1; i++) {
            rpos[i] = arr[i - 1] + rpos[i - 1];
        }
    }

    public SparseMatrix multiply(SparseMatrix mt2) {
        SparseMatrix reMatrix = new SparseMatrix(this.row, mt2.column);
        reMatrix.elem = new ArrayList<>(this.elem.size());
        for (int i = 0; i < this.row; i++) {
            Element[] temp = new Element[mt2.column];
            for (int j = this.rpos[i]; j < this.rpos[i + 1]; j++) {
                Element t1 = this.elem.get(j);
                for (int k = mt2.rpos[t1.column]; k < mt2.rpos[t1.column + 1]; k++) {
                    Element t2 = mt2.elem.get(k);
                    if (temp[t2.column] == null) {
                        temp[t2.column] = new Element(i, t2.column);
                    }
                    temp[t2.column].add(t1.multiply(t2));
                }
            }
            for (Element element : temp) {
                if (element != null && element.routes.size() != 0) reMatrix.add(element);
            }
        }
        reMatrix.initializeRpos();
        return reMatrix;
    }

    public boolean contains(int point) {
        return elem.get(rpos[point]).row == point;
    }

    public boolean contains(int from, int to) {
        for (Element element : elem) {
            if (element.row == from && element.column == to) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return elem.size();
    }

    @Override
    public String toString() {
        StringBuilder sbr = new StringBuilder("size : " + row + " * " + column + "\n");
        for (Element e : elem) {
            sbr.append(e.toString()).append("\n");
        }
        return sbr.toString();
    }
}
