import java.awt.*;

public class Box {
    public int x, y;
    private final int index;
    private Object occupant;

    public Box(int x, int y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }

    public boolean occupiedByGoat() {
        for (Goat g: GameBoard.getGoats()) {
            if (g.getBoxIndex() == index) return true;
        }
        return false;
    }

    public boolean containsMouse(int mouseX, int mouseY, int boxSize) {
        return new Rectangle(x, y, boxSize, boxSize).contains(mouseX, mouseY);
    }

    public int getIndex() { return index; }
    public void setOccupant(Object o) { occupant = o; }
    public Object getOccupant() { return occupant; }
}
