import java.awt.*;

public class Button {
    public int x, y;
    public int index;

    public Button(int x, int y, int index){
        this.x = x;
        this.y = y;
        this.index = index;
    }

    public boolean containsMouse(int mouseX, int mouseY, int buttonWidth, int buttonHeight) {
        return new Rectangle(x, y, buttonWidth, buttonHeight).contains(mouseX, mouseY);
    }
}
