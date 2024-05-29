import java.awt.*;

public class MenuButton {
    public int x, y;
    public String option;

    public MenuButton(int x, int y, String option){
        this.x = x;
        this.y = y;
        this.option = option;
    }

    public boolean containsMouse(double mouseX, double mouseY, int buttonWidth, int buttonHeight) {
        return new Rectangle(x - buttonWidth/2, y, buttonWidth, buttonHeight).contains(mouseX, mouseY);
    }
}
