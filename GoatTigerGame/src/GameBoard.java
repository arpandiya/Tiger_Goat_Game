
//<iframe src="https://docs.google.com/document/d/e/2PACX-1vSOmrJYAncRtGu2-jwqASUtNJWfECHw7ZeRrmU6yoQ3eUUhz_hXlLx8arDPqSiGXgfSX2oaxKzyxLqS/pub?embedded=true"></iframe>

//  https://docs.google.com/document/d/e/2PACX-1vSOmrJYAncRtGu2-jwqASUtNJWfECHw7ZeRrmU6yoQ3eUUhz_hXlLx8arDPqSiGXgfSX2oaxKzyxLqS/pub
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class GameBoard extends GameEngine implements KeyListener {
    final int BOARD_WIDTH = 800;
    final int BOARD_HEIGHT = 800;

    static boolean goatTurn = true;
    boolean tigerTurn = false;
    final int MAX_GOAT = 20;
    static int goatPlaced = 0;
    static int goatEaten = 0;
    static int totalMoves = 0;
    static boolean gameOver;
    static int trapCount;

    Image boardImg, tigerImg, goatImg;
    AudioClip winingSound, wrongMoveSound, moveSound;
    HashMap<Integer, Integer[]> validPath;

    String goatIcon = "🐐";
    String tigerIcon = "\uD83D\uDC2F";

    Tiger tiger;
    Goat goat;


    ArrayList<Box> boxes;
    ArrayList<Integer> trap;

    public Box getBoxById(int Id) {
        Box box = null;
        for(Box b: boxes) {
            if(b.id == Id) {
                box= b;
            }
        }
        return box;
    }


    public void resetGame() {
        init();
    }
    public boolean checkTrap(Box b) {
        boolean isTrapped = true;
        // ArrayList<Boolean> status = new ArrayList<>();

        for (Integer key : validPath.keySet()) {

            if(b.id == key) {
                Integer[] path = validPath.get(key);

                for(int id : path) {
                    Box box = getBoxById(id);
                    if(box.isEmpty) {
                        isTrapped = false;

                    }

                }
            }


        }

//        for(Boolean val : status) {
//            System.out.println(val + " ");
//            break;
//        }

        return isTrapped;

    }
    @Override
    public void init() {
        setWindowSize(BOARD_WIDTH, BOARD_HEIGHT);

        gameOver = false;
        totalMoves = 0;
        goatEaten = 0;
        trapCount = 0;
        goatPlaced = 0;

        validPath = new HashMap<>();
        initGameRules();
        //load images
        boardImg = loadImage("GoatTigerGame/src/images/boardImg.png");
        tigerImg = loadImage("GoatTigerGame/src/images/tigerImg.png");
        goatImg = loadImage("GoatTigerGame/src/images/goatImg.png");

        //load audio
        winingSound = loadAudio("GoatTigerGame/src/audio/wining.wav");
        wrongMoveSound = loadAudio("GoatTigerGame/src/audio/wrong_move.wav");
        moveSound = loadAudio("GoatTigerGame/src/audio/move.wav");
        boxes = new ArrayList<>();
        goat = new Goat();
        tiger = new Tiger();
        trap = new ArrayList<>();

        int id = 1;
        for(int y = 1; y < 6; y++) {
            for(int x = 1; x < 6; x++) {
                Box b = new Box();
                b.x = x*120;
                b.y = y*120;
                b.width = 120;
                b.height = 120;
                b.id = id;
                b.wayCount = validPath.get(id).length;
                boxes.add(b);
                id++;
            }

        }
        for(Box b: boxes) {
            if(b.id == 1 || b.id == 5 || b.id == 21 || b.id == 25) {
                b.isTigerBox = true;
                tiger.tigerBoxes.add(b);
                b.isEmpty = false;
            }
        }


    }

    //check if there is a valid way
    public boolean isValidPath(Integer from, Integer to) {
        boolean res = false;
        for(Integer key : validPath.keySet()) {
            boolean isIn = Arrays.stream(validPath.get(from)).toList().contains(to);
            if(isIn) {
                res = true;
            }
        }
        return res;
    }


    @Override
    public void update(double dt) {
        checkGameOver();


    }

    public void initGameRules() {
        validPath.put(1, new Integer[]{2, 6, 7});
        validPath.put(2, new Integer[]{1, 3, 7});
        validPath.put(3, new Integer[]{2, 4, 7, 8, 9});
        validPath.put(4, new Integer[]{3, 5, 9});
        validPath.put(5, new Integer[]{4, 9, 10});
        validPath.put(6, new Integer[]{1, 7, 11});
        validPath.put(7, new Integer[]{1, 2, 3, 6, 8, 11, 12, 13});
        validPath.put(8, new Integer[]{3, 7, 9, 13});
        validPath.put(9, new Integer[]{3, 4, 5, 8, 10, 13, 14, 15});
        validPath.put(10, new Integer[]{5, 9, 15});
        validPath.put(11, new Integer[]{6, 7, 12, 16, 17});
        validPath.put(12, new Integer[]{7, 11, 13, 17});
        validPath.put(13, new Integer[]{7, 8, 9, 12, 14, 17, 18, 19});
        validPath.put(14, new Integer[]{9, 13, 15, 19});
        validPath.put(15, new Integer[]{9, 10, 14, 19, 20});
        validPath.put(16, new Integer[]{11, 12,17, 21 });
        validPath.put(17, new Integer[]{11, 12, 13, 16, 18, 21, 22, 23 });
        validPath.put(18, new Integer[]{13, 17, 19, 23 });
        validPath.put(19, new Integer[]{13, 14, 15, 18, 20, 23, 24, 25});
        validPath.put(20, new Integer[]{15, 19, 25 });
        validPath.put(21, new Integer[]{16, 17, 22 });
        validPath.put(22, new Integer[]{17, 21, 23 });
        validPath.put(23, new Integer[]{17, 18, 19, 22, 24 });
        validPath.put(24, new Integer[]{19, 23, 25});
        validPath.put(25, new Integer[]{19, 20, 24 });

    }

    public void drawBoardImg() {
        drawImage(boardImg, 120, 120, 520, 520);
    }

    public void drawTigerDashboard() {
        changeColor(Color.GRAY);
        drawSolidRectangle(120, 70, 520, 30);

        changeColor(Color.WHITE);
        drawText(130, 90, "Tiger moves: " + totalMoves, "", 16);
        drawText(320, 90, "Trapped: " + trapCount, "", 16);
    }

    public void drawGoatDashboard() {
        changeColor(Color.GRAY);
        drawSolidRectangle(120, 700, 520, 30);

        changeColor(Color.WHITE);
        drawText(130, 720, "Goat placed: " + goatPlaced, "", 16);
        drawText(320, 720, "Killed: " + goatEaten, "", 16);
    }

    public void drawComponents() {
        for(Box bg : boxes) {
            if(bg.isGoatBox) {
               // drawText(bg.x, bg.y + 35, goatIcon, "", 49);
                drawImage(goatImg, bg.x, bg.y,50, 50 );
            }
            if(bg.isTigerBox && !bg.isEmpty) {
               // drawText(bg.x, bg.y + 35, tigerIcon, "", 49);
                drawImage(tigerImg, bg.x, bg.y,50, 50 );
            }
        }
    }


    public void drawGameOver() {
        if(gameOver) {
            changeColor(Color.BLACK);
            drawSolidRectangle(120, 300, 520, 120);

            changeColor(Color.ORANGE);
            drawText( 200, 360, "Game Over !", "", 60);

            changeColor(Color.WHITE);
            drawText( 240, 390, "Press Enter to restart !", "", 20);
        }
    }

    public void checkGameOver() {
        if(goatEaten > 8 || trapCount >= 4) {
            gameOver = true;
                playAudio(winingSound);
        }
    }
    @Override
    public void paintComponent() {
        saveCurrentTransform();
        changeBackgroundColor(Color.lightGray);
        clearBackground(BOARD_WIDTH, BOARD_HEIGHT);


        drawBoardImg();
        drawTigerDashboard();
        drawGoatDashboard();

        if(!gameOver) {
            drawComponents();
        } else {
            drawGameOver();
        }

//        for(Boxx b: boxes) {
//            if(b.isSelected){
//                changeColor(Color.ORANGE);
//            } else {
//                changeColor(Color.gray);
//            }
//            changeColor(Color.RED);
//            drawRectangle(b.x, b.y, 50, 50);
//            String id = String.valueOf(b.id);
//            drawText(b.x, b.y, id, "", 14);
//
//        }

        restoreLastTransform();
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        super.mouseClicked(event);

        for(Box b: boxes) {
            if( (b.x+b.width) >= event.getX() && b.x <= event.getX()) {
                if( (b.y+b.height) >= event.getY() && b.y <= event.getY()) {
                    if( goatPlaced < MAX_GOAT && b.isEmpty) {
                        goatTurn = false;
                        tigerTurn = true;
                        b.isGoatBox = true;
                        b.isEmpty = false;
                        goatPlaced+=1;
                        playAudio(moveSound);
                    }
                }
            }

        }

        for (Box box : boxes) {
            if(box.isTigerBox) {
                if(checkTrap(box)) {
                    if(!trap.contains(box.id)) {
                        trap.add(box.id);
                    }
                    System.out.println("Tiger " + box.id +" is trapped !");
                }

            }
            trapCount = trap.size();
        }
    }

    Box moveFrom;

    @Override
    public void mousePressed(MouseEvent event) {
        super.mousePressed(event);

        for(Box b: boxes) {
            if ((b.x + b.width) >= event.getX() && b.x <= event.getX()) {
                if ((b.y + b.height) >= event.getY() && b.y <= event.getY()) {
                    moveFrom = b;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        super.mouseReleased(event);
        for(Box b: boxes) {
            if ((b.x + b.width) >= event.getX() && b.x <= event.getX()) {
                if ((b.y + b.height) >= event.getY() && b.y <= event.getY()) {
                    if(isValidPath(moveFrom.id, b.id) || eatGoat(moveFrom, b)) {
                        if(moveFrom.isGoatBox && goatPlaced >= MAX_GOAT && b.isEmpty) {
                            b.isGoatBox = true;
                            moveFrom.isGoatBox = false;
                            moveFrom.isEmpty = true;
                            b.isEmpty = false;
                            goatTurn = false;
                            playAudio(moveSound);
                        }
                        if(moveFrom.isTigerBox && b.isEmpty) {
                            b.isTigerBox = true;
                            moveFrom.isTigerBox = false;

                            moveFrom.isEmpty = true;
                            b.isEmpty = false;
                            goatTurn = true;
                            eatGoat(moveFrom, b);
                            playAudio(moveSound);

                            totalMoves++;

                        }
                    } else {
                        System.out.println("Not a valid move");
                       // playAudio(wrongMoveSound);
                    }

                    System.out.println("Goat Turn: " + goatTurn);
                    System.out.println("Placed goat: " + goatPlaced);
//                    System.out.println("is "+ moveFrom.id + " empty : " + moveFrom.isEmpty );
//                    System.out.println("is "+ b.id + " empty : " + b.isEmpty );

                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.VK_ENTER && gameOver) {
            resetGame();
        }
    }

    private boolean eatGoat(Box from, Box to) {
        boolean isEating = false;

        ArrayList<Integer> checkId = new ArrayList<>();

        Integer sumId = from.id + to.id;
        Integer eatenGoatId = sumId/2;

        for(Box b: boxes) {
            if(b.id == eatenGoatId && b.isGoatBox && to.isEmpty) {
                b.isGoatBox = false;
                b.isEmpty = true;
                isEating = true;
                goatEaten++;
            }
        }

        return isEating;

    }

    public static void main(String[] args) {
        createGame(new GameBoard(), 60);

    }
}
