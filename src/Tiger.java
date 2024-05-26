import java.awt.*;
import java.util.Random;

public class Tiger {
    public int x, y;
    private final int posOffset;
    private int boxIndex;

    public Tiger(int boxIndex) {
        this.boxIndex = boxIndex;
        this.x = GameBoard.getBoxes().get(boxIndex).x;
        this.y = GameBoard.getBoxes().get(boxIndex).y;
        this.posOffset = new Random().nextInt(-10, 10);
        GameBoard.getBoxes().get(boxIndex).setOccupant(this);
    }

    public void moveBox(Box oldBox, Box newBox) {
        oldBox.setOccupant(null);
        newBox.setOccupant(this);
        x = newBox.x;
        y = newBox.y;
        boxIndex = newBox.getIndex();
    }

    public boolean containsMouse(int mouseX, int mouseY, int boxSize) {
        return new Rectangle(x, y, boxSize, boxSize).contains(mouseX, mouseY);
    }

    public int getBoxIndex() { return boxIndex; }
    public int getPosOffset() { return posOffset; }
}
