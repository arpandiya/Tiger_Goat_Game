import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameBoard extends GameEngine implements MouseListener {

    // Dimensions and locations
    private static final int WIDTH = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    private static final int HEIGHT = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private static final Point BOARD_POS = new Point(WIDTH/2, HEIGHT/2);
    private static final int BOARD_SIZE = HEIGHT/2;
    private static final int BOX_SIZE = BOARD_SIZE/10;

    // Arrays
    private static final ArrayList<Box> BOXES = new ArrayList<>(25);
    private static final ArrayList<Tiger> TIGERS = new ArrayList<>(4);
    private static final ArrayList<Goat> GOATS = new ArrayList<>(20);
    private static final ArrayList<Tiger> TRAPPED_TIGERS = new ArrayList<>(4);

    // Menu components
    private static final ArrayList<MenuButton> MENU_BUTTONS = new ArrayList<>();
    private static final int MENU_BUTTON_HEIGHT = HEIGHT/10, MENU_BUTTON_WIDTH = WIDTH/6, MENU_BUTTON_GAP = 20;
    private static final int buttonStartY = HEIGHT/3;
    private static final String[] MENU_OPTIONS = new String[] {"Play", "Rules", "Credits", "Quit"};
    private static boolean bgMuted = false;
    private static final int iconSize = 70;
    private static final Point iconPos = new Point(20, HEIGHT - iconSize - 40);

    // Assets
    private static Image BoardImg, GoatImg, TigerImg, GoatBackgroundImg, TigerBackgroundImg, ButtonImg, MutedImg, UnmutedImg, TitleImg;
    private static Image TigerWinImg, GoatWinImg;
    private static AudioClip ValidMove, InvalidMove, GameOver, BackgroundMusic;

    // Dragging
    private static int mouseOffsetX, mouseOffsetY;
    private static Box startBox;
    private static Goat draggedGoat;
    private static Tiger draggedTiger;
    private static boolean draggingATile = false;

    // Game conditions
    private static int GOATS_PLACED = 0;
    private static int GOATS_KILLED = 0;
    private static final int GOATS_TO_KILL = 6;
    private static final int MAX_GOATS = 20;
    private static boolean gameOver = true;
    private static boolean goatTurn = true;
    private static boolean menuShown = true, winSoundPlayed = true;

    @Override
    public void init() {
        setWindowSize(WIDTH, HEIGHT);

        BoardImg = loadImage("src/images/boardImg.png");
        GoatImg = loadImage("src/images/goatImg.png");
        TigerImg = loadImage("src/images/tigerImg.png");
        GoatBackgroundImg = loadImage("src/images/goatBackgroundImg.png");
        TigerBackgroundImg = loadImage("src/images/tigerBackgroundImg.png");
        ButtonImg = loadImage("src/images/buttonImg.png");
        MutedImg = loadImage("src/images/mutedImg.png");
        UnmutedImg = loadImage("src/images/unmutedImg.png");
        TigerWinImg = loadImage("src/images/tigersWinImg.png");
        GoatWinImg = loadImage("src/images/goatsWinImg.png");
        TitleImg = loadImage("src/images/titleImg.png");


        ValidMove = loadAudio("src/audio/validMove.wav");
        InvalidMove = loadAudio("src/audio/invalidMove.wav");
        GameOver = loadAudio("src/audio/gameOver.wav");
        BackgroundMusic = loadAudio("src/audio/background.wav");
        startAudioLoop(BackgroundMusic, -15f);

        // Add boxes
        boxGeneration();

        // Add tigers
        TIGERS.add(new Tiger(0));
        TIGERS.add(new Tiger(4));
        TIGERS.add(new Tiger(20));
        TIGERS.add(new Tiger(24));

        // Add menu buttons
        for (int i = 0; i < MENU_OPTIONS.length; i++) {
            MENU_BUTTONS.add(new MenuButton(WIDTH/2, buttonStartY + i*(MENU_BUTTON_GAP + MENU_BUTTON_HEIGHT), MENU_OPTIONS[i]));
        }
    }

    @Override
    public void update(double dt) {
        if (GOATS_KILLED >= GOATS_TO_KILL || TRAPPED_TIGERS.size() == 4) {
            gameOver = true;
            if (!winSoundPlayed){
                playAudio(GameOver);
                winSoundPlayed = true;
            }
        }
    }

    @Override
    public void paintComponent() {
        changeBackgroundColor(Color.WHITE);
        clearBackground(WIDTH, HEIGHT);

        // If the window gets moved around hovering will still work
        PointerInfo mouse = MouseInfo.getPointerInfo();
        Point pos = new Point(mouse.getLocation());
        SwingUtilities.convertPointFromScreen(pos, getWindow());

        // Draw Background
        saveCurrentTransform();
        translate(WIDTH/2.0, HEIGHT/2.0);
        if (goatTurn) drawImage(GoatBackgroundImg, -WIDTH/2.0, -HEIGHT/2.0, WIDTH, HEIGHT);
        else drawImage(TigerBackgroundImg, -WIDTH/2.0, -HEIGHT/2.0, WIDTH, HEIGHT);
        restoreLastTransform();

        if (!menuShown){
            // Draw Board
            saveCurrentTransform();
            translate(BOARD_POS.getX(), BOARD_POS.getY());
            drawImage(BoardImg, -BOARD_SIZE/2.0, -BOARD_SIZE/2.0, BOARD_SIZE, BOARD_SIZE);

            // Draw Tiger Stats
            changeColor(black);
            translate(-BOARD_SIZE/2.0, -BOARD_SIZE/2.0 - 20);
            drawText(0, 0, "GOATS KILLED:   " + GOATS_KILLED, "Queensides", BOARD_SIZE/30);

            // Draw Goat Stats
            changeColor(white);
            translate(BOARD_SIZE - 220, BOARD_SIZE + 70);
            drawText(0, 0, "TIGERS TRAPPED:   " + TRAPPED_TIGERS.size(), "Queensides", BOARD_SIZE/30);
            changeColor(black);
            drawText(-20, -BOARD_SIZE - 70, "Remaining Goats:   " + (MAX_GOATS - GOATS_PLACED), "Queensides", BOARD_SIZE/30);
            restoreLastTransform();

            // Draw Goats
            for (Goat g : GOATS) {
                drawImage(GoatImg, g.x + g.getPosOffset(), g.y + g.getPosOffset(), BOX_SIZE, BOX_SIZE);
            }

            // Draw Tigers
            for (Tiger t : TIGERS) {
                drawImage(TigerImg, t.x + t.getPosOffset(), t.y + t.getPosOffset(), BOX_SIZE, BOX_SIZE);
            }
        } else {
            // Draw Title
            drawImage(TitleImg, WIDTH/2.0 - WIDTH/4.0, HEIGHT/2.0 - (WIDTH*0.5)/2, WIDTH*0.5, (WIDTH*0.5)/3);

            // Draw Menu Buttons
            for (MenuButton b : MENU_BUTTONS){
                drawImage(ButtonImg, b.x - MENU_BUTTON_WIDTH /2.0, b.y, MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT);

                saveCurrentTransform();
                changeColor(b.containsMouse(pos.getX(), pos.getY(), MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT) ? white : Color.lightGray);
                drawText(b.x - 120, b.y + 10 + MENU_BUTTON_HEIGHT /2.0, b.option, "Queensides", 50);
                restoreLastTransform();
            }
        }

        // Draw Background Music Icon
        drawImage(bgMuted ? MutedImg : UnmutedImg, iconPos.getX(), iconPos.getY(), iconSize, iconSize);

        // Draw Game Over
        if (gameOver) {
            saveCurrentTransform();
            translate(BOARD_POS.getX(), BOARD_POS.getY());

            if (GOATS_KILLED >= GOATS_TO_KILL) { // Tiger wins
                drawImage(TigerWinImg, -BOARD_SIZE/2.0, -BOARD_SIZE/2.0, BOARD_SIZE, BOARD_SIZE);
            } else if (TRAPPED_TIGERS.size() == 4) { // Goat wins
                drawImage(GoatWinImg, -BOARD_SIZE/2.0, -BOARD_SIZE/2.0, BOARD_SIZE, BOARD_SIZE);
            }
            restoreLastTransform();
        }
    }

    public void boxGeneration() {
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
    }

    public void resetGame() {
        BOXES.clear();
        boxGeneration();

        TIGERS.clear();
        GOATS.clear();
        TRAPPED_TIGERS.clear();
        GOATS_PLACED = 0;
        GOATS_KILLED = 0;
        goatTurn = true;
        winSoundPlayed = false;

        MENU_BUTTONS.getFirst().option = "Play";

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
                    // Trap tiger if tiger is adjacent
                    if (BOXES.get(abs(id + t.getBoxIndex()) / 2).occupiedByGoat()) {
                        jumpTrapped = false;
                        TRAPPED_TIGERS.remove(t);
                        break;
                    }
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

        if (hoppedBox.occupiedByGoat()) {
            GOATS.remove(hoppedBox.getOccupant());
            hoppedBox.setOccupant(null);
            GOATS_KILLED++;
            return true;
        }
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver){
            gameOver = false;
            menuShown = true;
            resetGame();
        } else if (menuShown) {
            // Menu buttons
            for (MenuButton b : MENU_BUTTONS) {
                if (b.containsMouse(e.getX(), e.getY(), MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT)) {
                    switch (b.option) {
                        case "Reset":
                            resetGame();
                            menuShown = false;
                            break;
                        case "Play":
                            MENU_BUTTONS.getFirst().option = "Reset";
                            gameOver = false;
                            menuShown = false;
                            break;
                        case "Quit":
                            System.exit(0);
                    }
                }
            }
        } else {
            // Adding goats
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

        // Muting background music
        if (e.getX() > iconPos.getX() && e.getX() < iconPos.getX() + iconSize && e.getY() > iconPos.getY() && e.getY() < iconPos.getY() + iconSize) {
            bgMuted = !bgMuted;

            if (bgMuted) stopAudioLoop(BackgroundMusic);
            else startAudioLoop(BackgroundMusic);
        }
    }

    // Intialising a drag
    @Override
    public void mousePressed(MouseEvent e) {
        if (menuShown) return;

        for (Goat g : GOATS) {
            if (g.containsMouse(e.getX(), e.getY(), BOX_SIZE) && goatTurn) {
                mouseOffsetX = e.getX() - g.x;
                mouseOffsetY = e.getY() - g.y;
                draggedGoat = g;
                draggingATile = true;
            }
        }

        for (Tiger t : TIGERS) {
            if (t.containsMouse(e.getX(), e.getY(), BOX_SIZE) && !goatTurn) {
                mouseOffsetX =  e.getX() - t.x;
                mouseOffsetY = e.getY() - t.y;
                draggedTiger = t;
                draggingATile = true;
            }
        }

        for (Box b : BOXES) {
            if (b.containsMouse(e.getX(), e.getY(), BOX_SIZE)) {
                if (b.occupiedByGoat() && goatTurn || b.occupiedByTiger() && !goatTurn){
                    startBox = b;
                }
                else{
                    draggingATile = false;
                    startBox = null;
                }
            }
        }
    }

    // Drag a tile
    @Override
    public void mouseDragged(MouseEvent e) {
        if (menuShown) return;
        if (startBox == null) return;

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
            if (g.containsMouse(e.getX(), e.getY(), BOX_SIZE) && goatTurn) {
//                draggingATile = true;
                draggedGoat = g;
                draggedGoat.x = x;
                draggedGoat.y = y;
                return;
            }
        }

        for (Tiger t : TIGERS) {
            if (t.containsMouse(e.getX(), e.getY(), BOX_SIZE) && !goatTurn) {
//                draggingATile = true;
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
        if (menuShown) return;
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
                        checkTrappedTigers();
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

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE && !gameOver){
            menuShown = !menuShown;
        }
    }

    public static ArrayList<Box> getBoxes() { return BOXES; }

    public static void main(String[] args) {
        createGame(new GameBoard(), 60);
    }
}