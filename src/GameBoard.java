//https://docs.google.com/document/d/e/2PACX-1vSOmrJYAncRtGu2-jwqASUtNJWfECHw7ZeRrmU6yoQ3eUUhz_hXlLx8arDPqSiGXgfSX2oaxKzyxLqS/pub

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GameBoard extends GameEngine implements KeyListener, MouseListener {

    // Dimensions and locations
    private static final int WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final Point BOARD_POS = new Point(WIDTH/2, HEIGHT/2 - 100);
    private static final int BOARD_SIZE = 850;
    private static final int BOX_SIZE = 70;

    // Arrays
    private static final int MAX_GOATS = 20;
    private static final ArrayList<Box> BOXES = new ArrayList<>(25);
    private static final ArrayList<Tiger> TIGERS = new ArrayList<>(4);
    private static final ArrayList<Goat> GOATS = new ArrayList<>(20);

    // Images
    private static Image BoardImg, GoatImg, TigerImg, GoatBackgroundImg, TigerBackgroundImg;

    // Dragging
    private static int mouseOffsetX, mouseOffsetY;
    private static Box startBox;
    private static Goat draggedGoat;
    private static Tiger draggedTiger;
    private static boolean draggingATile = false;

    private static boolean goatTurn = true;

    @Override
    public void init() {
        setWindowSize(WIDTH, HEIGHT);

        BoardImg = loadImage("src/Images/boardImg.png");
        GoatImg = loadImage("src/Images/goatImg.png");
        TigerImg = loadImage("src/Images/tigerImg.png");
        GoatBackgroundImg = loadImage("src/Images/goatBackgroundImg.png");
        TigerBackgroundImg = loadImage("src/Images/tigerBackgroundImg.png");

        // Add boxes
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                BOXES.add(new Box(
                        //     Spacing              Centre the box            Relative to board         (the 0.82x is to account for the game boards border)
                        x * (int)(BOARD_SIZE*0.82/4) - BOX_SIZE/2 + (int)(BOARD_POS.getX()-BOARD_SIZE*0.82/2),
                        y * (int)(BOARD_SIZE*0.82/4) - BOX_SIZE/2 + (int)(BOARD_POS.getY()-BOARD_SIZE*0.82/2),
                        BOXES.size()
                ));
            }
        }

        // Add tigers
        TIGERS.add(new Tiger(0)); BOXES.get(0).setOccupied(true);
        TIGERS.add(new Tiger(4)); BOXES.get(4).setOccupied(true);
        TIGERS.add(new Tiger(20)); BOXES.get(20).setOccupied(true);
        TIGERS.add(new Tiger(24)); BOXES.get(24).setOccupied(true);
    }

    // Returns valid neighbours of any given box
    public ArrayList<Integer> getValidMoveIndices(int boxIndex) {
        final ArrayList<Integer> validMoves = new ArrayList<>();

        int top = boxIndex - 5;
        int topleft = top - 1;
        int topright = top + 1;
        int left = boxIndex - 1;
        int right = boxIndex + 1;
        int bottom = boxIndex + 5;
        int bottomleft = bottom - 1;
        int bottomright = bottom + 1;

        switch (boxIndex) {
            // Corners
            case 0:
                validMoves.add(right);
                validMoves.add(bottom);
                validMoves.add(bottomright);
                break;
            case 4:
                validMoves.add(left);
                validMoves.add(bottomleft);
                validMoves.add(bottom);
                break;
            case 20:
                validMoves.add(top);
                validMoves.add(topright);
                validMoves.add(right);
                break;
            case 24:
                validMoves.add(topleft);
                validMoves.add(top);
                validMoves.add(left);
                break;


            // Edges
            case 1, 3:
                validMoves.add(left);
                validMoves.add(right);
                validMoves.add(bottom);
                break;
            case 2:
                validMoves.add(left);
                validMoves.add(right);
                validMoves.add(bottomleft);
                validMoves.add(bottom);
                validMoves.add(bottomright);
                break;

            case 5, 15:
                validMoves.add(top);
                validMoves.add(right);
                validMoves.add(bottom);
                break;
            case 10:
                validMoves.add(top);
                validMoves.add(topright);
                validMoves.add(right);
                validMoves.add(bottom);
                validMoves.add(bottomright);
                break;

            case 9, 19:
                validMoves.add(top);
                validMoves.add(left);
                validMoves.add(bottom);
                break;
            case 14:
                validMoves.add(top);
                validMoves.add(topleft);
                validMoves.add(left);
                validMoves.add(bottom);
                validMoves.add(bottomleft);
                break;

            case 21, 23:
                validMoves.add(top);
                validMoves.add(left);
                validMoves.add(right);
                break;
            case 22:
                validMoves.add(top);
                validMoves.add(left);
                validMoves.add(topleft);
                validMoves.add(right);
                validMoves.add(topright);
                break;


            // Centers
            case 6,8,12,16,18:
                validMoves.add(topleft);
                validMoves.add(top);
                validMoves.add(topright);
                validMoves.add(left);
                validMoves.add(right);
                validMoves.add(bottomleft);
                validMoves.add(bottom);
                validMoves.add(bottomright);
                break;
            case 7,11,13,17:
                validMoves.add(top);
                validMoves.add(left);
                validMoves.add(right);
                validMoves.add(bottom);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + boxIndex);
        }
        return validMoves;
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void paintComponent() {
        changeBackgroundColor(Color.WHITE);
        clearBackground(WIDTH, HEIGHT);

        // Draw Background
        saveCurrentTransform();
        translate(WIDTH/2.0, HEIGHT/2.0);
        if (goatTurn) drawImage(GoatBackgroundImg, -WIDTH/2.0, -HEIGHT/2.0, WIDTH, HEIGHT);
        else drawImage(TigerBackgroundImg, -WIDTH/2.0, -HEIGHT/2.0, WIDTH, HEIGHT);
        restoreLastTransform();

        // Draw Board
        saveCurrentTransform();
        translate(BOARD_POS.getX(), BOARD_POS.getY());
        drawImage(BoardImg, -BOARD_SIZE/2.0, -BOARD_SIZE/2.0, BOARD_SIZE, BOARD_SIZE);
        restoreLastTransform();

        // Draw Boxes
        for (Box b : BOXES) {
            if (b.isOccupied()) changeColor(Color.red);
            else changeColor(Color.green);
            drawRectangle(b.x, b.y, BOX_SIZE, BOX_SIZE);
        }

        // Draw Goats
        for (Goat g : GOATS) {
            drawImage(GoatImg, g.x + g.getPosOffset(), g.y + g.getPosOffset(), BOX_SIZE, BOX_SIZE);
        }

        // Draw Tigers
        for (Tiger t : TIGERS) {
            drawImage(TigerImg, t.x + t.getPosOffset(), t.y + t.getPosOffset(), BOX_SIZE, BOX_SIZE);
        }
    }

    // Click to add a tile
    @Override
    public void mouseClicked(MouseEvent e) {
        for (Box b : BOXES) {
            if (b.containsMouse(e.getX(), e.getY(), BOX_SIZE)) {
                if (goatTurn && !b.isOccupied() && GOATS.size() < MAX_GOATS) {
                    GOATS.add(new Goat(BOXES.indexOf(b)));
                    b.setOccupied(true);
                    goatTurn = false;
                }
            }
        }
    }

    // Intialising a drag
    @Override
    public void mousePressed(MouseEvent e) {
        for (Goat g : GOATS) {
            if (g.containsMouse(e.getX(), e.getY(), BOX_SIZE)) {
                mouseOffsetX = e.getX() - g.x;
                mouseOffsetY = e.getY() - g.y;
                draggedGoat = g;
                draggingATile = true;
            }
        }

        for (Tiger t : TIGERS) {
            if (t.containsMouse(e.getX(), e.getY(), BOX_SIZE)) {
                mouseOffsetX =  e.getX() - t.x;
                mouseOffsetY = e.getY() - t.y;
                draggedTiger = t;
                draggingATile = true;
            }
        }

        for (Box b : BOXES) {
            if (b.containsMouse(e.getX(), e.getY(), BOX_SIZE)) {
                startBox = b;
            }
        }
    }

    // Drag a tile
    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX() - mouseOffsetX;
        int y = e.getY() - mouseOffsetY;

        if (draggingATile) {
            if (draggedGoat != null && goatTurn) {
                draggedGoat.x = x;
                draggedGoat.y = y;
            }
            if (draggedTiger != null && !goatTurn) {
                draggedTiger.x = x;
                draggedTiger.y = y;
            }
            return;
        }

        for (Goat g : GOATS) {
            if (g.containsMouse(e.getX(), e.getY(), BOX_SIZE)) {
                draggingATile = true;
                draggedGoat = g;
                draggedGoat.x = x;
                draggedGoat.y = y;
                return;
            }
        }

        for (Tiger t : TIGERS) {
            if (t.containsMouse(e.getX(), e.getY(), BOX_SIZE)) {
                draggingATile = true;
                draggedTiger = t;
                draggedTiger.x = x;
                draggedTiger.y = y;
                return;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (startBox == null) return;
        boolean moved = false;

        for (Box newBox : BOXES) {
            if (newBox.isOccupied()) continue; // Occupied
            if (!getValidMoveIndices(startBox.getIndex()).contains(newBox.getIndex())) continue; // Not a valid move

            // If tile released on box, move it there
            if (newBox.containsMouse(e.getX(), e.getY(), BOX_SIZE)) {
                if (draggedGoat != null && goatTurn) {
                    System.out.println("Goat moved: " + startBox.getIndex() + "->" + newBox.getIndex());
                    draggedGoat.x = newBox.x;
                    draggedGoat.y = newBox.y;
                    startBox.setOccupied(false);
                    newBox.setOccupied(true);
                    goatTurn = false;
                } else if (draggedTiger != null && !goatTurn) {
                    System.out.println("Tiger moved: " + startBox.getIndex() + "->" + newBox.getIndex());
                    draggedTiger.x = newBox.x;
                    draggedTiger.y = newBox.y;
                    startBox.setOccupied(false);
                    newBox.setOccupied(true);
                    goatTurn = true;
                }
                moved = true;
                break;
            }
        }
        // Tile moved unsuccessfully, snap it back to where it started
        if (!moved) {
            if (draggedGoat != null) {
                draggedGoat.x = startBox.x;
                draggedGoat.y = startBox.y;
            } else if (draggedTiger != null) {
                draggedTiger.x = startBox.x;
                draggedTiger.y = startBox.y;
            }
        }

        draggingATile = false;
        draggedGoat = null;
        draggedTiger = null;
    }

    public static ArrayList<Goat> getGoats() { return GOATS; }
    public static ArrayList<Tiger> getTigers() { return TIGERS; }
    public static ArrayList<Box> getBoxes() { return BOXES; }

    public static void main(String[] args) {
        createGame(new GameBoard(), 60);
    }
}