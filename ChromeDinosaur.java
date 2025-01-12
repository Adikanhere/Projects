import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener{
    int boardWidth=750;
    int boardHeight=250;

    // Images
    Image dinosaurImg;
    Image dianosaurDeadImg;
    Image dianosaurJumpImg;
    Image cactus1Img;
    Image cactus2Img;
    Image cactus3Img;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;

        Block(int x,int y,int width,int height,Image img){
            this.x=x;
            this.y=y;
            this.width=width;
            this.height=height;
            this.img=img;
        }
    }

    // dinosaur
    int dinosaurwidth=88;
    int dinosaurHeight=94;
    int dinosaurX=50;
    int dinosaurY=boardHeight-dinosaurHeight;

    Block dinosaur;

    // Cactus
    int cactus1Width=34;
    int cactus2Width=69;
    int cactus3Width=102;

    int cactusHeight=70;
    int cactusX=700;
    int cactusY=boardHeight-cactusHeight;
    ArrayList<Block> cactusArray;

    // physics
    int velocityX=-12;
    int velocityY=0; // dinosaur jump 
    int gravity=1;

    boolean gameOver=false;
    int score=0;

    // levels
    int level=1;

    Timer gameLoop;
    Timer placeCactusTimer;

    public ChromeDinosaur(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.lightGray);
        setFocusable(true);
        addKeyListener(this);

        dinosaurImg=new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
        dianosaurDeadImg=new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
        dianosaurJumpImg=new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();
        cactus1Img=new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
        cactus2Img=new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
        cactus3Img=new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();

        // dinosaur
        dinosaur=new Block(dinosaurX,dinosaurY,dinosaurwidth,dinosaurHeight,dinosaurImg);
        // cactus
        cactusArray=new ArrayList<Block>();

        // game timer
        gameLoop=new Timer(1000/60, this);
        gameLoop.start(); 

        // Place cactus timer
        placeCactusTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placeCactus();
            }
        });
        placeCactusTimer.start();
    }

    void placeCactus(){
        if(gameOver){
            return;
        }
        double placeCactusChance=Math.random(); // 0 - 0.9999
        if(placeCactusChance> .90){ // 10% get cactus3
            Block cactus=new Block(cactusX, cactusY, cactus3Width, cactusHeight, cactus3Img);
            cactusArray.add(cactus);
        } else if (placeCactusChance> .70){ // 20% get cactus2
            Block cactus=new Block(cactusX, cactusY, cactus2Width, cactusHeight, cactus2Img);
            cactusArray.add(cactus);
        } else if(placeCactusChance> .50){ // 20% get cactus1
            Block cactus=new Block(cactusX, cactusY, cactus1Width, cactusHeight, cactus1Img);
            cactusArray.add(cactus);
        }

        if(cactusArray.size()>10){
            cactusArray.remove(0); // remove 1st cactus from arraylist
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        g.drawImage(dinosaur.img, dinosaur.x, dinosaur.y, dinosaur.width,dinosaur.height,null);

        // Cactus
        for(int i=0;i<cactusArray.size();i++){
            Block cactus=cactusArray.get(i);
            g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }

        // score
        g.setColor(Color.gray);
        g.setFont(new Font("Courier", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("Game Over: "+ String.valueOf(score), 10, 35);
        } else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    public void move(){
        // dinosaur
        velocityY+=gravity;
        dinosaur.y+=velocityY;

        if(dinosaur.y>dinosaurY){
            dinosaur.y=dinosaurY;
            velocityY=0;
            dinosaur.img=dinosaurImg;
        }

        // cactus
        for(int i=0;i<cactusArray.size();i++){
            Block cactus=cactusArray.get(i);
            cactus.x+=velocityX;

            if(collision(dinosaur, cactus)){
                gameOver=true;
                dinosaur.img=dianosaurDeadImg;
                SoundPlayer.playSound("collision.wav");
            }
        }
        // score
        score++;
        if(score%100==0){
            level++;
            velocityX--;
            if(velocityX< -25) velocityX=-25;
        }
    }

    boolean collision(Block a,Block b){
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height>b.y;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            placeCactusTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            // System.out.println("JUMP!");
            if(dinosaur.y==dinosaurY){
                velocityY=-17;
                dinosaur.img=dianosaurJumpImg;
                SoundPlayer.playSound("jump.wav");
            }

            if (gameOver) {
                //restart game by resetting conditions
                dinosaur.y = dinosaurY;
                dinosaur.img = dinosaurImg;
                velocityY = 0;
                cactusArray.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placeCactusTimer.start();
            }
        }
    }
    

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}







// import java.awt.*;
// import java.awt.event.*;
// import java.util.ArrayList;
// import javax.swing.*;

// public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {
//     int boardWidth = 750;
//     int boardHeight = 250;

//     // Images
//     Image dinosaurImg;
//     Image dianosaurDeadImg;
//     Image dianosaurJumpImg;
//     Image cactus1Img;
//     Image cactus2Img;
//     Image cactus3Img;

//     class Block {
//         int x;
//         int y;
//         int width;
//         int height;
//         Image img;

//         Block(int x, int y, int width, int height, Image img) {
//             this.x = x;
//             this.y = y;
//             this.width = width;
//             this.height = height;
//             this.img = img;
//         }
//     }

//     // dinosaur
//     int dinosaurwidth = 88;
//     int dinosaurHeight = 94;
//     int dinosaurX = 50;
//     int dinosaurY = boardHeight - dinosaurHeight;

//     Block dinosaur;

//     // Cactus
//     int cactus1Width = 34;
//     int cactus2Width = 69;
//     int cactus3Width = 102;

//     int cactusHeight = 70;
//     int cactusX = 700;
//     int cactusY = boardHeight - cactusHeight;
//     ArrayList<Block> cactusArray;

//     // physics
//     int velocityX = -12;
//     int velocityY = 0; // dinosaur jump
//     int gravity = 1;

//     boolean gameOver = false;
//     int score = 0;

//     // levels
//     int level = 1;

//     Timer gameLoop;
//     Timer placeCactusTimer;

//     public ChromeDinosaur() {
//         setPreferredSize(new Dimension(boardWidth, boardHeight));
//         setBackground(Color.lightGray);
//         setFocusable(true);
//         addKeyListener(this);

//         dinosaurImg = new ImageIcon(getClass().getResource("./img/dino-run.gif")).getImage();
//         dianosaurDeadImg = new ImageIcon(getClass().getResource("./img/dino-dead.png")).getImage();
//         dianosaurJumpImg = new ImageIcon(getClass().getResource("./img/dino-jump.png")).getImage();
//         cactus1Img = new ImageIcon(getClass().getResource("./img/cactus1.png")).getImage();
//         cactus2Img = new ImageIcon(getClass().getResource("./img/cactus2.png")).getImage();
//         cactus3Img = new ImageIcon(getClass().getResource("./img/cactus3.png")).getImage();

//         // dinosaur
//         dinosaur = new Block(dinosaurX, dinosaurY, dinosaurwidth, dinosaurHeight, dinosaurImg);
//         // cactus
//         cactusArray = new ArrayList<Block>();

//         // game timer
//         gameLoop = new Timer(1000 / 60, this);
//         gameLoop.start();

//         // Place cactus timer
//         placeCactusTimer = new Timer(1500, new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e) {
//                 placeCactus();
//             }
//         });
//         placeCactusTimer.start();
//     }

//     void placeCactus() {
//         if (gameOver) {
//             return;
//         }
//         double placeCactusChance = Math.random(); // 0 - 0.9999
//         if (placeCactusChance > .90) { // 10% get cactus3
//             Block cactus = new Block(cactusX, cactusY, cactus3Width, cactusHeight, cactus3Img);
//             cactusArray.add(cactus);
//         } else if (placeCactusChance > .70) { // 20% get cactus2
//             Block cactus = new Block(cactusX, cactusY, cactus2Width, cactusHeight, cactus2Img);
//             cactusArray.add(cactus);
//         } else if (placeCactusChance > .50) { // 20% get cactus1
//             Block cactus = new Block(cactusX, cactusY, cactus1Width, cactusHeight, cactus1Img);
//             cactusArray.add(cactus);
//         }

//         if (cactusArray.size() > 10) {
//             cactusArray.remove(0); // remove 1st cactus from arraylist
//         }
//     }

//     public void paintComponent(Graphics g) {
//         super.paintComponent(g);
//         draw(g);
//     }

//     public void draw(Graphics g) {
//         g.drawImage(dinosaur.img, dinosaur.x, dinosaur.y, dinosaur.width, dinosaur.height, null);

//         // Cactus
//         for (int i = 0; i < cactusArray.size(); i++) {
//             Block cactus = cactusArray.get(i);
//             g.drawImage(cactus.img, cactus.x, cactus.y, cactus.width, cactus.height, null);
//         }

//         // score
//         g.setColor(Color.gray);
//         g.setFont(new Font("Courier", Font.PLAIN, 32));
//         if (gameOver) {
//             g.drawString("Game Over: " + String.valueOf(score), 10, 35);
//         } else {
//             g.drawString(String.valueOf(score), 10, 35);
//         }
//     }

//     public void move() {
//         // dinosaur
//         velocityY += gravity;
//         dinosaur.y += velocityY;

//         if (dinosaur.y > dinosaurY) {
//             dinosaur.y = dinosaurY;
//             velocityY = 0;
//             dinosaur.img = dinosaurImg;
//         }

//         // cactus
//         for (int i = 0; i < cactusArray.size(); i++) {
//             Block cactus = cactusArray.get(i);
//             cactus.x += velocityX;

//             if (collision(dinosaur, cactus)) {
//                 gameOver = true;
//                 dinosaur.img = dianosaurDeadImg;
//                 // SoundPlayer.playSound("C:/Users/ADITYA/Downloads/collision-83248.mp3");
//             }
//         }
//         // score
//         score++;
//         if (score % 100 == 0) {
//             level++;
//             velocityX--;
//             if (velocityX < -25) velocityX = -25;
//         }
//     }

//     boolean collision(Block a, Block b) {
//         return a.x < b.x + b.width &&
//                 a.x + a.width > b.x &&
//                 a.y < b.y + b.height &&
//                 a.y + a.height > b.y;
//     }

//     @Override
//     public void actionPerformed(ActionEvent e) {
//         move();
//         repaint();
//         if (gameOver) {
//             placeCactusTimer.stop();
//             gameLoop.stop();
//         }
//     }

//     @Override
//     public void keyPressed(KeyEvent e) {
//         if (e.getKeyCode() == KeyEvent.VK_SPACE) {
//             // System.out.println("JUMP!");
//             if (dinosaur.y == dinosaurY) {
//                 velocityY = -17;
//                 dinosaur.img = dianosaurJumpImg;
//                 // SoundPlayer.playSound("C:/Users/ADITYA/Downloads/jump-up-245782.mp3");
//             }

//             if (gameOver) {
//                 //restart game by resetting conditions
//                 dinosaur.y = dinosaurY;
//                 dinosaur.img = dinosaurImg;
//                 velocityY = 0;
//                 cactusArray.clear();
//                 score = 0;
//                 gameOver = false;
//                 gameLoop.start();
//                 placeCactusTimer.start();
//             }
//         }
//     }

//     @Override
//     public void keyTyped(KeyEvent e) {}

//     @Override
//     public void keyReleased(KeyEvent e) {}
// }
