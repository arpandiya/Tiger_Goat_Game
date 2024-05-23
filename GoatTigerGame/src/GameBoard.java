//<iframe src="https://docs.google.com/document/d/e/2PACX-1vSOmrJYAncRtGu2-jwqASUtNJWfECHw7ZeRrmU6yoQ3eUUhz_hXlLx8arDPqSiGXgfSX2oaxKzyxLqS/pub?embedded=true"></iframe>

      //  https://docs.google.com/document/d/e/2PACX-1vSOmrJYAncRtGu2-jwqASUtNJWfECHw7ZeRrmU6yoQ3eUUhz_hXlLx8arDPqSiGXgfSX2oaxKzyxLqS/pub

import jdk.jfr.Event;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GameBoard extends GameEngine implements KeyListener {
    final int WIDTH = 800;
    final int HEIGHT = 800;
    final int MAX_GOAT = 5;
    int goatsPlaced = 0;

    Image tigerImage, goatImage;
    Image boardImage;
    boolean goatTurn = true; //set to true for testing
    int selectedBox = -1;
    int tiger1, tiger2, tiger3, tiger4;
    boolean isTiger1, isTiger2, isTiger3, isTiger4;
    Point p1;
    Box start, end;

    ArrayList<Box> boxes;
    ArrayList<Tiger> tigers;
    ArrayList<Goat> goats;

    boolean attemptMove = false;
    Box chosenBox, previousBox = new Box(0,0,-1);

    @Override
    public void init() {
        setWindowSize(WIDTH, HEIGHT);
        boardImage = loadImage("GoatTigerGame/src/images/boardImg.png");
        tigerImage = loadImage("GoatTigerGame/src/images/tigerImg.png");
        goatImage = loadImage("GoatTigerGame/src/images/goatImg.png");
        p1 = new Point();
        boxes = new ArrayList<>();
        tigers = new ArrayList<>();
        goats = new ArrayList<>();

        isTiger1 = false;
        isTiger2= false;
        isTiger3 = false;
        isTiger4 = false;

        tiger1 = 1;
        tiger2 = 5;
        tiger3 = 21;
        tiger4 = 25;

        int id = 1;
        for (int ix = 150; ix < 600; ix += 110) {
            for (int iy = 150; iy < 600; iy += 110) {
                Box t = new Box(id, ix, iy);
                boxes.add(t);
                id++;
            }
        }
        //set rules
        setRules();

        final int MAX_TIGER = 4;

        //initialize tiger
        for (int i = 0; i < 4; i++) {
            Tiger tiger = new Tiger(10, 10);
            tigers.add(tiger);
        }
        //initialise goats
//        for (int i = 0; i < 20; i++) {
//            Goat goat = new Goat(0, 0, -1);
//            goats.add(goat);
//        }
        System.out.println("Tigers: " + tigers.size());
        System.out.println("Goats: " + goats.size());

    }

    public void setRules() {
        for (Box b : boxes) {
            int id = b.id - 1;
            switch (id) {
                case 0:
                    boxes.get(id).top = false;
                    boxes.get(id).left = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottomLeft = false;
                    break;
                case 1:
                    boxes.get(id).left = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 2:
                    boxes.get(id).left = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).bottomLeft = false;
                    break;
                case 3:
                    boxes.get(id).left = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 4:
                    boxes.get(id).left = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).bottom = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 5:
                    boxes.get(id).top = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 7:
                    boxes.get(id).topRight = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 9:
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottom = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 10:
                    boxes.get(id).top = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    break;
                case 11:
                    boxes.get(id).topRight = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 12:
                    boxes.get(id).topRight = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 14:
                    boxes.get(id).bottom = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 15:
                    boxes.get(id).top = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 17:
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 19:
                    boxes.get(id).bottom = false;
                    boxes.get(id).bottomLeft = false;
                    boxes.get(id).bottomRight = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    break;
                case 20:
                    boxes.get(id).top = false;
                    boxes.get(id).right = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 21:
                    boxes.get(id).right = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottomRight = false;
                    boxes.get(id).bottomLeft = false;
                    break;
                case 22:
                    boxes.get(id).right = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottomRight = false;
                    break;
                case 23:
                    boxes.get(id).right = false;
                    boxes.get(id).topLeft = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottomRight = false;
                    boxes.get(id).bottomLeft = false;
                    break;
                case 24:
                    boxes.get(id).bottom = false;
                    boxes.get(id).right = false;
                    boxes.get(id).topRight = false;
                    boxes.get(id).bottomRight = false;
                    boxes.get(id).bottomLeft = false;
                    break;

            }
        }
    }


    @Override
    public void update(double dt) {

    }

    @Override
    public void paintComponent() {
        saveCurrentTransform();
        changeBackgroundColor(Color.darkGray);
        clearBackground(WIDTH, HEIGHT);

        changeColor(Color.WHITE);
        drawImage(boardImage, 150, 150, 480, 480);

        changeColor(Color.ORANGE);

        //draw board
        for (Box b : boxes) {
            String id = String.valueOf(b.id);
            drawText(b.x, b.y, id, "", 15);
            drawRectangle(b.x, b.y, b.width, b.height);

            if (b.id == tiger1 || b.id == tiger2 || b.id == tiger3 || b.id == tiger4) {
                drawImage(tigerImage, b.x, b.y, b.width, b.height);
               // drawText(b.x, b.y + 30, tigerIcon, "", 35);
                b.isEmpty = false;
            }
        }

        for (Box b : boxes) {
            if (selectedBox == b.id) {
                Box box = boxes.get(b.id - 1);
                changeColor(Color.GREEN);
                drawRectangle(box.x, box.y, box.width, box.height);
            }
        }


        if (!goatTurn) {
            //draw control
            changeColor(Color.ORANGE);
            drawRectangle(250, 670, 90, 50);
            drawImage(tigerImage, 255, 670, 50, 50 );
            //drawText(255, 680, tigerIcon, "", 40);
        } else {
            changeColor(Color.GRAY);
            drawRectangle(250, 670, 90, 50);
            drawImage(tigerImage, 255, 670, 50, 50 );
        }

        if (goatTurn) {
            //draw control
            changeColor(Color.GRAY);
            drawSolidRectangle(140, 670, 90, 50);
            drawImage(goatImage, 155, 670, 70, 50);
            //drawText(155, 680, goatIcon, "", 40);
        } else {
            //draw control
            changeColor(Color.WHITE);
            drawRectangle(140, 670, 90, 50);
            drawImage(goatImage, 155, 670, 70, 50);
            //drawText(155, 680, goatIcon, "", 40);
        }

        //draw goat
        if (goatTurn && !goats.isEmpty()) {
            for (Goat g : goats) {
                drawImage(goatImage, g.x, g.y, g.goatWidth+15, g.goatHeight+15);
                //drawText(g.x, g.y + 25, goatIcon, "", 40);

            }
        }

        restoreLastTransform();

    }


    //key listener
    @Override
    public void mouseClicked(MouseEvent event) {
        p1.x = event.getX();
        p1.y = event.getY();

        for (Box b : boxes) {
            if (((b.x + b.width) >= event.getX() && b.x <= event.getX()) && (b.y + b.height >= event.getY()) && (b.y <= event.getY())) {
                //System.out.println("ID: " + b.id);
                selectedBox = b.id;


                if (goatTurn){ //All goat actions in this statement
                    System.out.println(b.isEmpty);
                    if(b.isEmpty) {
                        System.out.println("Previousbox: " + previousBox.id);

                        if (goatsPlaced < MAX_GOAT) { //Add goat to empty square
                            goats.add(new Goat(b.x, b.y, b.id));
                            goatsPlaced++;
                            b.isEmpty = false;
//                            System.out.println("Placing goat");
                        } else if (attemptMove) {
                            System.out.println("Trying to move");
                            for (int i = 0; i < goats.size(); i++) {
                                if (goats.get(i).id == previousBox.id) {
                                    goats.remove(i);
                                }
                            }
                            goats.add(new Goat(b.x, b.y, b.id));
                            b.isEmpty = false;
                            previousBox.isEmpty = true;
                            boxes.set(previousBox.id, previousBox);//Makes the previous location empty
                            attemptMove = false;
                        }


                    }
                    else if(goats.size() >= MAX_GOAT) { //square not empty
                        //Need to select the square for later reference
                        previousBox = b;
                        attemptMove = true;
                        System.out.println("Selecting square");
                    }
                } else if (!goatTurn) { //All tiger turn actions in this statement...

                }

                break;
            }
        }
    }
    //mouse drag to move


    @Override
    public void mousePressed(MouseEvent event) {
        super.mouseDragged(event);

        for (Box b : boxes) {
            if (((b.x + b.width) >= event.getX() && b.x <= event.getX()) && (b.y + b.height >= event.getY()) && (b.y <= event.getY())) {
               if(!goatTurn) {

                   if(tiger1 == b.id) {
                       tiger1 = b.id;
                       isTiger1 = true;
                       b.isEmpty = true;
                   }

                   if(tiger2 == b.id) {
                       tiger2 = b.id;
                       isTiger2 = true;
                       b.isEmpty = true;
                   }

                   if(tiger3 == b.id) {
                       tiger3 = b.id;
                       isTiger3 = true;
                       b.isEmpty = true;
                   }

                   if(tiger4 == b.id) {
                       tiger4 = b.id;
                       isTiger4 = true;
                       b.isEmpty = true;
                   }
               }

                //System.out.println("Pressed: " + b.id);
                break;
            }

        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        super.mouseReleased(event);
        for (Box b : boxes) {
            if (((b.x + b.width) >= event.getX() && b.x <= event.getX()) && (b.y + b.height >= event.getY()) && (b.y <= event.getY())) {
                if(!goatTurn) {

                    if(isTiger1 && b.isEmpty) {
                        tiger1 = b.id;
                        isTiger1 = false;
                        b.isEmpty = false;
                    }
                    if(isTiger2 && b.isEmpty) {
                        tiger2 = b.id;
                        isTiger2 = false;
                        b.isEmpty = false;
                    }
                    if(isTiger3 && b.isEmpty) {
                        tiger3 = b.id;
                        isTiger3 = false;
                        b.isEmpty = false;
                    }
                    if(isTiger4 && b.isEmpty) {
                        tiger4 = b.id;
                        isTiger4 = false;
                        b.isEmpty = false;
                    }


                }
                //System.out.println("Clicked: " + b.id);
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent event){ //Troubleshooting: to see where goats are stored
        if (event.getKeyCode() == KeyEvent.VK_SPACE){
            for (int i = 0; i < goats.size(); i++){
                System.out.println(goats.get(i).id);
            }
        }
    }

    public static void main(String[] args) {
        createGame(new GameBoard(), 60);
    }
}
