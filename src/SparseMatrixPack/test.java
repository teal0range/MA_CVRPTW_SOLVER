package SparseMatrixPack;

public class test {
    public static void main(String[] args) {
        SparseMatrix sm = new SparseMatrix();
        sm.row = 100;
        sm.column = 100;
        sm.initializeRpos();
        sm.put(new Element(1, 2));
        sm.put(new Element(2, 3));
        sm.remove(0, 4);
        System.out.println(sm);
    }
}
