import java.awt.*;

public class Box {
    public int x, y;
    private final int index;
    public boolean occupied = false;

    public Box(int x, int y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }

    public boolean containsMouse(int mouseX, int mouseY, int boxSize) {
        return new Rectangle(x, y, boxSize, boxSize).contains(mouseX, mouseY);
    }

    public int getIndex() { return index; }
    public void setOccupied(boolean o) { occupied = o; }
    public boolean isOccupied() { return occupied; }
}
