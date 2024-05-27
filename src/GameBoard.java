//https://docs.google.com/document/d/e/2PACX-1vSOmrJYAncRtGu2-jwqASUtNJWfECHw7ZeRrmU6yoQ3eUUhz_hXlLx8arDPqSiGXgfSX2oaxKzyxLqS/pub

import javax.sound.sampled.AudioInputStream;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameBoard extends GameEngine implements MouseListener {

    // Dimensions and locations
    private static final int WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final Point BOARD_POS = new Point(WIDTH/2, HEIGHT/2 - 100);
    private static final int BOARD_SIZE = 850;
    private static final int BOX_SIZE = 70;

    // Arrays
    private static final ArrayList<Box> BOXES = new ArrayList<>(25);
    private static final ArrayList<Tiger> TIGERS = new ArrayList<>(4);
    private static final ArrayList<Goat> GOATS = new ArrayList<>(20);
    private static final ArrayList<Tiger> TRAPPED_TIGERS = new ArrayList<>(4);

    // Assets
    private static Image BoardImg, GoatImg, TigerImg, GoatBackgroundImg, TigerBackgroundImg;
    private static AudioClip ValidMove;
    private static AudioClip InvalidMove;
    private static AudioClip GameOver;

    // Dragging
    private static int mouseOffsetX, mouseOffsetY;
    private static Box startBox;
    private static Goat draggedGoat;
    private static Tiger draggedTiger;
    private static boolean draggingATile = false;
    
    
    private static int GOATS_PLACED = 0;
    private static int GOATS_KILLED = 0;
    private static final int MAX_GOATS = 20;
    private static boolean GAME_OVER = false;
    private static boolean goatTurn = true;

    @Override
    public void init() {
        setWindowSize(WIDTH, HEIGHT);

        BoardImg = loadImage("src/images/boardImg.png");
        GoatImg = loadImage("src/images/goatImg.png");
        TigerImg = loadImage("src/images/tigerImg.png");
        GoatBackgroundImg = loadImage("src/images/goatBackgroundImg.png");
        TigerBackgroundImg = loadImage("src/images/tigerBackgroundImg.png");

        ValidMove = loadAudio("src/audio/validMove.wav");
        InvalidMove = loadAudio("src/audio/invalidMove.wav");
        GameOver = loadAudio("src/audio/gameOver.wav");
        AudioClip backgroundMusic = loadAudio("src/audio/background.wav");
        startAudioLoop(backgroundMusic, -15f);

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

        TIGERS.add(new Tiger(0));
        TIGERS.add(new Tiger(4));
        TIGERS.add(new Tiger(20));
        TIGERS.add(new Tiger(24));
    }

    @Override
    public void update(double dt) {
        if (GOATS_KILLED >= 9 || TRAPPED_TIGERS.size() == 4) {
            GAME_OVER = true;
            playAudio(GameOver);
            resetGame();
        }
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

        // Draw Game Over
        if (GAME_OVER) {
            changeColor(Color.BLACK);
            drawSolidRectangle(120, 300, 520, 120);

            changeColor(Color.ORANGE);
            drawText( 200, 360, "Game Over !", "", 60);

            changeColor(Color.WHITE);
            drawText( 240, 390, "Press Enter to restart!", "", 20);
            return;
        }

        // Draw Board
        saveCurrentTransform();
        translate(BOARD_POS.getX(), BOARD_POS.getY());
        drawImage(BoardImg, -BOARD_SIZE/2.0, -BOARD_SIZE/2.0, BOARD_SIZE, BOARD_SIZE);

        // Draw Tiger Stats
        changeColor(black);
        translate(-BOARD_SIZE/2.0, -BOARD_SIZE/2.0 - 20);
        drawText(0, 0, "GOATS KILLED:   " + GOATS_KILLED, "Queensides", 25);

        // Draw Goat Stats
        changeColor(white);
        translate(BOARD_SIZE - 220, BOARD_SIZE + 70);
        drawText(0, 0,"TIGERS TRAPPED:   " + TRAPPED_TIGERS.size(), "Queensides", 25);

        changeColor(black);
        drawText(-20, -BOARD_SIZE - 70,"Remaining Goats:   " + (MAX_GOATS - GOATS_PLACED), "Queensides", 25);
        restoreLastTransform();

        // Draw Goats
        for (Goat g : GOATS) {
            drawImage(GoatImg, g.x + g.getPosOffset(), g.y + g.getPosOffset(), BOX_SIZE, BOX_SIZE);
        }

        // Draw Tigers
        for (Tiger t : TIGERS) {
            drawImage(TigerImg, t.x + t.getPosOffset(), t.y + t.getPosOffset(), BOX_SIZE, BOX_SIZE);
        }
        


    }

    public void resetGame() {
        TIGERS.clear();
        GOATS.clear();
        TRAPPED_TIGERS.clear();
        GOATS_PLACED = 0;
        GOATS_KILLED = 0;
        GAME_OVER = false;

        TIGERS.add(new Tiger(0));
        TIGERS.add(new Tiger(4));
        TIGERS.add(new Tiger(20));
        TIGERS.add(new Tiger(24));
    }

    public Integer[] getLegalMoves(int boxIndex) {
        final HashMap<Integer, Integer[]> LEGAL_MOVES = new HashMap<>();
        LEGAL_MOVES.put(0, new Integer[]{1, 5, 6});
        LEGAL_MOVES.put(1, new Integer[]{0, 2, 6});
        LEGAL_MOVES.put(2, new Integer[]{1, 3, 6, 7, 8});
        LEGAL_MOVES.put(3, new Integer[]{2, 4, 8});
        LEGAL_MOVES.put(4, new Integer[]{3, 8, 9});
        LEGAL_MOVES.put(5, new Integer[]{0, 6, 10});
        LEGAL_MOVES.put(6, new Integer[]{0, 1, 2, 5, 7, 10, 11, 12});
        LEGAL_MOVES.put(7, new Integer[]{2, 6, 8, 12});
        LEGAL_MOVES.put(8, new Integer[]{2, 3, 4, 7, 9, 12, 13, 14});
        LEGAL_MOVES.put(9, new Integer[]{4, 8, 14});
        LEGAL_MOVES.put(10, new Integer[]{5, 6, 11, 15, 16});
        LEGAL_MOVES.put(11, new Integer[]{6, 10, 12, 16});
        LEGAL_MOVES.put(12, new Integer[]{6, 7, 8, 11, 13, 16, 17, 18});
        LEGAL_MOVES.put(13, new Integer[]{8, 12, 14, 18});
        LEGAL_MOVES.put(14, new Integer[]{8, 9, 13, 18, 19});
        LEGAL_MOVES.put(15, new Integer[]{10, 11, 16, 20});
        LEGAL_MOVES.put(16, new Integer[]{10, 11, 12, 15, 17, 20, 21, 22});
        LEGAL_MOVES.put(17, new Integer[]{12, 16, 18, 22});
        LEGAL_MOVES.put(18, new Integer[]{12, 13, 14, 17, 19, 22, 23, 24});
        LEGAL_MOVES.put(19, new Integer[]{14, 18, 24});
        LEGAL_MOVES.put(20, new Integer[]{15, 16, 21});
        LEGAL_MOVES.put(21, new Integer[]{16, 20, 22});
        LEGAL_MOVES.put(22, new Integer[]{16, 17, 18, 21, 23});
        LEGAL_MOVES.put(23, new Integer[]{18, 22, 24});
        LEGAL_MOVES.put(24, new Integer[]{18, 19, 23});

        return LEGAL_MOVES.get(boxIndex);
    }

    public Integer[] getLegalJumps(int boxIndex) {
        final HashMap<Integer, Integer[]> LEGAL_JUMPS = new HashMap<>();
        LEGAL_JUMPS.put(0, new Integer[]{2, 10, 12});
        LEGAL_JUMPS.put(1, new Integer[]{3, 11});
        LEGAL_JUMPS.put(2, new Integer[]{0, 4, 10, 12, 14});
        LEGAL_JUMPS.put(3, new Integer[]{1, 13});
        LEGAL_JUMPS.put(4, new Integer[]{2, 12, 14});
        LEGAL_JUMPS.put(5, new Integer[]{7, 15});
        LEGAL_JUMPS.put(6, new Integer[]{8, 16, 18});
        LEGAL_JUMPS.put(7, new Integer[]{5, 9, 17});
        LEGAL_JUMPS.put(8, new Integer[]{6, 16, 18});
        LEGAL_JUMPS.put(9, new Integer[]{7, 19});
        LEGAL_JUMPS.put(10, new Integer[]{0, 2, 12, 20, 22});
        LEGAL_JUMPS.put(11, new Integer[]{1, 13, 21});
        LEGAL_JUMPS.put(12, new Integer[]{0, 2, 4, 10, 14, 20, 22, 24});
        LEGAL_JUMPS.put(13, new Integer[]{3, 11, 23});
        LEGAL_JUMPS.put(14, new Integer[]{2, 4, 12, 22, 24});
        LEGAL_JUMPS.put(15, new Integer[]{5, 17});
        LEGAL_JUMPS.put(16, new Integer[]{6, 8, 18});
        LEGAL_JUMPS.put(17, new Integer[]{7, 15, 19});
        LEGAL_JUMPS.put(18, new Integer[]{6, 8, 16});
        LEGAL_JUMPS.put(19, new Integer[]{9, 17});
        LEGAL_JUMPS.put(20, new Integer[]{10, 12, 22});
        LEGAL_JUMPS.put(21, new Integer[]{11, 23});
        LEGAL_JUMPS.put(22, new Integer[]{10, 12, 14, 20, 24});
        LEGAL_JUMPS.put(23, new Integer[]{13, 21});
        LEGAL_JUMPS.put(24, new Integer[]{12, 14, 22});

        return LEGAL_JUMPS.get(boxIndex);
    }

    public void checkTrappedTigers() {
        for (Tiger t : TIGERS) {
            Integer[] moveOptions = getLegalMoves(t.getBoxIndex());
            Integer[] jumpOptions = getLegalJumps(t.getBoxIndex());

            boolean moveTrapped = true;
            boolean jumpTrapped = true;

            // Checking for moves
            for (int id : moveOptions) {
                if (BOXES.get(id).getOccupant() == null) {
                    moveTrapped = false;
                    TRAPPED_TIGERS.remove(t);
                    break;
                }
            }
            // Checking for jumps
            for (int id : jumpOptions) {
                if (BOXES.get(id).getOccupant() == null) {
                    jumpTrapped = false;
                    TRAPPED_TIGERS.remove(t);
                    break;
                }
            }

            if (moveTrapped && jumpTrapped) {
                if (!TRAPPED_TIGERS.contains(t)) TRAPPED_TIGERS.add(t);
            }
        }
    }

    public boolean eatGoat(int from, int to) {
        if (BOXES.get(to).getOccupant() != null) return false;

        // The index of the jumped box has the index that's the average of the boxes on either side
        Box hoppedBox = BOXES.get((from + to) / 2);

        // If it's occupied by a goat
        if (hoppedBox.getOccupant() != null && hoppedBox.occupiedByGoat()) {
            GOATS.remove(hoppedBox.getOccupant());
            hoppedBox.setOccupant(null);
            GOATS_KILLED++;
            return true;
        }
        return false;
    }

    // Click to add a tile
    @Override
    public void mouseClicked(MouseEvent e) {
        for (Box b : BOXES) {
            if (b.containsMouse(e.getX(), e.getY(), BOX_SIZE)) {
                if (goatTurn && b.getOccupant() == null && GOATS_PLACED < MAX_GOATS) {
                    playAudio(ValidMove);
                    GOATS.add(new Goat(BOXES.indexOf(b)));
                    goatTurn = false;
                    GOATS_PLACED++;
                    checkTrappedTigers();
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
            if (draggedGoat != null && goatTurn && GOATS_PLACED == MAX_GOATS) {
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

    // Checking if valid move
    @Override
    public void mouseReleased(MouseEvent e) {
        if (startBox == null) return;
        boolean moved = false;

        for (Box newBox : BOXES) {
            if (newBox.getOccupant() != null) continue; // Occupied

            // If tile released on box, move it there
            if (newBox.containsMouse(e.getX(), e.getY(), BOX_SIZE)) {
                if (draggedGoat != null && goatTurn && GOATS_PLACED == MAX_GOATS) {
                    if (!Arrays.asList(getLegalMoves(startBox.getIndex())).contains(newBox.getIndex())) continue; // Not a valid move
                    draggedGoat.moveBox(startBox, newBox);
                    goatTurn = false;
                    moved = true;
                    playAudio(ValidMove);
                    checkTrappedTigers();
                } else if (draggedTiger != null && !goatTurn) {
                    // Tiger moved normally
                    if (Arrays.asList(getLegalMoves(startBox.getIndex())).contains(newBox.getIndex())) {
                        draggedTiger.moveBox(startBox, newBox);
                        goatTurn = true;
                        moved = true;
                        playAudio(ValidMove);
                    }
                    // Tiger jumped a goat
                    else if (Arrays.asList(getLegalJumps(startBox.getIndex())).contains(newBox.getIndex())) {
                        try {
                            if (!eatGoat(startBox.getIndex(), newBox.getIndex())) continue;
                            draggedTiger.moveBox(startBox, newBox);
                            goatTurn = true;
                            moved = true;
                            playAudio(ValidMove);
                            checkTrappedTigers();
                        } catch (IndexOutOfBoundsException ignored) {}
                    }
                }
                break;
            }
        }
        // Tile moved unsuccessfully, snap it back to where it started
        if (!moved) {
            if (draggedGoat != null) {
                draggedGoat.moveBox(startBox, startBox);
                playAudio(InvalidMove, -15);
            } else if (draggedTiger != null) {
                draggedTiger.moveBox(startBox, startBox);
                playAudio(InvalidMove, -15);
            }
        }

        draggingATile = false;
        draggedGoat = null;
        draggedTiger = null;
    }

    public static ArrayList<Goat> getGoats() { return GOATS; }
    public static ArrayList<Box> getBoxes() { return BOXES; }

    public static void main(String[] args) {
        createGame(new GameBoard(), 60);
    }
}