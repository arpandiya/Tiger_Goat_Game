import java.util.ArrayList;

public class Box {
    private int index;
    int x;
    int y;
    boolean top, bottom, left, right;
    boolean topLeft,topRight, bottomLeft, bottomRight;
    ArrayList<Integer> selected;
    boolean isEmpty;

    public Box(int x, int y, int index) {
//        selected = new ArrayList<>();
        this.x = x;
        this.y = y;
//        this.top = true;
//        this.bottom = true;
//        this.left = true;
//        this.right = true;
//        this.topLeft = true;
//        this.topRight = true;
//        this.bottomLeft = true;
//        this.bottomRight = true;
//        this.isEmpty = true;
        this.index = index;
    }

    public int getIndex() { return index; }
}
