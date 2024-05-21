//<iframe src="https://docs.google.com/document/d/e/2PACX-1vSOmrJYAncRtGu2-jwqASUtNJWfECHw7ZeRrmU6yoQ3eUUhz_hXlLx8arDPqSiGXgfSX2oaxKzyxLqS/pub?embedded=true"></iframe>

      //  https://docs.google.com/document/d/e/2PACX-1vSOmrJYAncRtGu2-jwqASUtNJWfECHw7ZeRrmU6yoQ3eUUhz_hXlLx8arDPqSiGXgfSX2oaxKzyxLqS/pub

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameBoard extends GameEngine implements KeyListener {

    // All I've done really is removed the hard coded values for sizes and positions
    // (in case we want to resize or move the game board later on). It now happens dynamically.
    // Along with some other minor improvements

    // Dimensions and locations
    private static final int HEIGHT = 1100, WIDTH = 1100;
    private static final Dimension BOARD_POS = new Dimension(WIDTH/2, HEIGHT/2);
    private static final int BOARD_SIZE = 600;
    private static final int BOX_SIZE = 40;

    // Arrays
    private static final int MAX_TIGERS = 4, MAX_GOATS = 20;
    private static final ArrayList<Box> BOXES = new ArrayList<>();
    private static final ArrayList<Tiger> TIGERS = new ArrayList<>();
    private static final ArrayList<Goat> GOATS = new ArrayList<>();

    // Image
    Image BoardImg;
//    String GoatImg, TigerImg;

    boolean goatTurn = true;
    int selectedBox = 1;
    Box start, end, chosen;


    @Override
    public void init() {
        setWindowSize(WIDTH, HEIGHT);
        BoardImg = loadImage("src/boardImg.png");
//        GoatImg = loadImage(Goat.ICON);
//        TigerImg = loadImage(Tiger.ICON);

        int index = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                BOXES.add(new Box(
                        //     Spacing     Centre the box            Relative to board
                        x * (BOARD_SIZE/4) - BOX_SIZE/2 + (int)BOARD_POS.getWidth()-BOARD_SIZE/2,
                        y * (BOARD_SIZE/4) - BOX_SIZE/2 + (int)BOARD_POS.getHeight()-BOARD_SIZE/2,
                        index++
                ));
            }
        }

        TIGERS.add(new Tiger(0));
        TIGERS.add(new Tiger(4));
        TIGERS.add(new Tiger(20));
        TIGERS.add(new Tiger(24));



//        setRules();
    }

//    public void setRules() { todo refactor
//        for (Box b : BOXES) {
//            int id = b.id - 1;
//            switch (id) {
//                case 0:
//                    BOXES.get(id).top = false;
//                    BOXES.get(id).left = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottomLeft = false;
//                    break;
//                case 1:
//                    BOXES.get(id).left = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 2:
//                    BOXES.get(id).left = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).bottomLeft = false;
//                    break;
//                case 3:
//                    BOXES.get(id).left = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 4:
//                    BOXES.get(id).left = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).bottom = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 5:
//                    BOXES.get(id).top = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 7:
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 9:
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottom = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 10:
//                    BOXES.get(id).top = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    break;
//                case 11:
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 12:
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 14:
//                    BOXES.get(id).bottom = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 15:
//                    BOXES.get(id).top = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 17:
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 19:
//                    BOXES.get(id).bottom = false;
//                    BOXES.get(id).bottomLeft = false;
//                    BOXES.get(id).bottomRight = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    break;
//                case 20:
//                    BOXES.get(id).top = false;
//                    BOXES.get(id).right = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 21:
//                    BOXES.get(id).right = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottomRight = false;
//                    BOXES.get(id).bottomLeft = false;
//                    break;
//                case 22:
//                    BOXES.get(id).right = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottomRight = false;
//                    break;
//                case 23:
//                    BOXES.get(id).right = false;
//                    BOXES.get(id).topLeft = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottomRight = false;
//                    BOXES.get(id).bottomLeft = false;
//                    break;
//                case 24:
//                    BOXES.get(id).bottom = false;
//                    BOXES.get(id).right = false;
//                    BOXES.get(id).topRight = false;
//                    BOXES.get(id).bottomRight = false;
//                    BOXES.get(id).bottomLeft = false;
//                    break;
//
//            }
//        }
//    }


    @Override
    public void update(double dt) {

    }

    @Override
    public void paintComponent() {
        changeBackgroundColor(Color.WHITE);
        clearBackground(WIDTH, HEIGHT);

        // Draw Board
        drawImage(BoardImg, BOARD_POS.getWidth() - (double)BOARD_SIZE/2,BOARD_POS.getHeight() - (double)BOARD_SIZE/2, BOARD_SIZE, BOARD_SIZE);

        // Draw Boxes
        changeColor(Color.ORANGE);
        for (Box b : BOXES) {
            drawText(b.x, b.y, ""+b.getIndex(), "", 15);
            drawRectangle(b.x, b.y, BOX_SIZE, BOX_SIZE);
        }

        // Draw Goats
        for (Goat g : GOATS) {
//            drawImage(GoatImg, g.x, g.y);
            drawText(BOXES.get(g.getIndex()).x, BOXES.get(g.getIndex()).y + 35, Goat.ICON, "", 35);
        }

        // Draw Tigers
        for (Tiger t : TIGERS) {
//            drawImage(TigerImg, t.x, t.y);
            drawText(BOXES.get(t.getIndex()).x, BOXES.get(t.getIndex()).y + 35, Tiger.ICON, "", 35);
        }



//        if (!goatTurn) {
//            //draw control
//            changeColor(Color.ORANGE);
//            drawRectangle(250, 640, 90, 50);
//            drawText(255, 680, Tiger.ICON, "", 40);
//        } else {
//            changeColor(Color.GRAY);
//            drawRectangle(250, 640, 90, 50);
//            drawText(255, 680, Tiger.ICON, "", 40);
//        }
//
//        if (goatTurn) {
//            //draw control
//            changeColor(Color.GRAY);
//            drawSolidRectangle(140, 640, 90, 50);
//            drawText(155, 680, Goat.ICON, "", 40);
//        } else {
//            //draw control
//            changeColor(Color.WHITE);
//            drawRectangle(140, 640, 90, 50);
//            drawText(155, 680, Goat.ICON, "", 40);
//        }
//
//        //draw goat
//        if (goatTurn && !GOATS.isEmpty()) {
//            for (Goat g : GOATS) {
//                drawText(g.x, g.y + 25, Goat.ICON, "", 40);
//
//            }
//        }

    }


    //key listener
    @Override
    public void mouseClicked(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();

        for (Box b : BOXES) {
            if (((b.x + BOX_SIZE) >= x && b.x <= x) && (b.y + BOX_SIZE >= y) && (b.y <= y)) {
//                if (goatTurn && b.isEmpty) {
                    GOATS.add(new Goat(b.getIndex()));
//                    b.isEmpty = false;
//                }
                break;
            }
        }
    }
    //mouse drag to move



    @Override
    public void mousePressed(MouseEvent event) {
        super.mouseDragged(event);
        // System.out.println(event.getPoint());
        for (Box b : BOXES) {
            if (((b.x + BOX_SIZE) >= event.getX() && b.x <= event.getX()) && (b.y + BOX_SIZE >= event.getY()) && (b.y <= event.getY())) {
                start = b;
                break;
            }

        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        super.mouseReleased(event);
        for (Box b : BOXES) {
            if (((b.x + BOX_SIZE) >= event.getX() && b.x <= event.getX()) && (b.y + BOX_SIZE >= event.getY()) && (b.y <= event.getY())) {
                end = b;
                break;
            }
        }
    }

    public static void main(String[] args) {
        createGame(new GameBoard(), 60);
    }
}
