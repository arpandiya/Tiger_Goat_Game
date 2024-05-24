import java.awt.*;
import java.util.Random;

public class Tiger {
    public int x, y;
    private int boxIndex, posOffset;

    public Tiger(int boxIndex) {
        this.boxIndex = boxIndex;
        this.x = GameBoard.getBoxes().get(boxIndex).x;
        this.y = GameBoard.getBoxes().get(boxIndex).y;
        this.posOffset = new Random().nextInt(-10, 10);
    }

    public boolean containsMouse(int mouseX, int mouseY, int boxSize) {
        return new Rectangle(x, y, boxSize, boxSize).contains(mouseX, mouseY);
    }

    public int getPosOffset() { return posOffset; }
}
