package EAXOPT;

import java.util.ArrayList;

public class SparseMatrix {
    int row;
    int column;
    ArrayList<Element> elem;
    int[] rpos;

    public SparseMatrix() {
        elem = new ArrayList<>();
    }

//    public SpareMatrix Route2Matrix(Solution sol){
//        HashMap<Integer,Integer> hm = new HashMap<>();
//
//    }

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

    @Override
    public String toString() {
        StringBuilder sbr = new StringBuilder("size : " + row + " * " + column + "\n");
        for (Element e : elem) {
            sbr.append(e.toString()).append("\n");
        }
        return sbr.toString();
    }
}
