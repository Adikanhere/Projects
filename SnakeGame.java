import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.random.*;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener{
    private class Tile {
        int x;
        int y;

        Tile(int x,int y){
            this.x=x;
            this.y=y;
        }
    }

    int boardWidth;
    int boardHeight;
    int tileSize=25;

    // Snake
    Tile snakHead;
    ArrayList<Tile> snakeBody;

    // Food
    Tile food;
    Random random;

    // Game Logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver=false;

    SnakeGame(int boardWidth,int boardHeight){
        this.boardWidth=boardWidth;
        this.boardHeight=boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakHead=new Tile(5, 5);
        snakeBody=new ArrayList<Tile>();

        food=new Tile(10, 10);
        random=new Random();
        placeFood();

        velocityX=1;
        velocityY=0;

        // Game timer
        gameLoop=new Timer(100, this);
        gameLoop.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        // Grid
        // for(int i=0;i<boardWidth/tileSize;i++){
        //     g.drawLine(i*tileSize, 0, i*tileSize, boardHeight);
        //     g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
        // }

        // Food
        g.setColor(Color.red);
        // g.fillRect(food.x*tileSize, food.y*tileSize, tileSize, tileSize);
        g.fill3DRect(food.x*tileSize, food.y*tileSize, tileSize, tileSize, true);

        // Snake Head
        g.setColor(Color.green);
        // g.fillRect(snakHead.x * tileSize, snakHead.y*tileSize, tileSize, tileSize);
        g.fill3DRect(snakHead.x * tileSize, snakHead.y*tileSize, tileSize, tileSize, true);

        // Snake Body
        for(int i=0;i<snakeBody.size();i++){
            Tile snakePart=snakeBody.get(i);
            // g.fillRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize);
            g.fill3DRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize, true);
        }

        // Score
        g.setFont(new Font("Arial",Font.PLAIN, 16));
        if(gameOver){
            g.setColor(Color.red);
            g.drawString("Game Over: "+ String.valueOf(snakeBody.size()), tileSize-16, tileSize);
        } else {
            g.drawString("Score: "+ String.valueOf(snakeBody.size()), tileSize-16, tileSize);
        }
    }

    public void placeFood(){
        food.x=random.nextInt(boardWidth/tileSize);
        food.y=random.nextInt(boardHeight/tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2){
        return tile1.x==tile2.x && tile1.y==tile2.y;
    }
    public void move(){
        // Eat food
        if(collision(snakHead, food)){
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        // Snake Body
        for(int i=snakeBody.size()-1;i>=0;i--){
            Tile snakePart=snakeBody.get(i);
            if(i==0){
                snakePart.x=snakHead.x;
                snakePart.y=snakHead.y;
            } else {
                Tile prevSnakePart=snakeBody.get(i-1);
                snakePart.x=prevSnakePart.x;
                snakePart.y=prevSnakePart.y;
            }
        }
        snakHead.x+=velocityX;
        snakHead.y+=velocityY;

        // Game over Conditions
        for(int i=0;i<snakeBody.size();i++){
            Tile snakePart=snakeBody.get(i);

            // Collide with the snake head
            if(collision(snakHead, snakePart)){
                gameOver=true;
            }
        }
        if(snakHead.x*tileSize<0 || snakHead.x*tileSize>boardWidth ||
            snakHead.y*tileSize<0 || snakHead.y*tileSize>boardHeight){
            gameOver=true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_UP && velocityY!=1){
            velocityX=0;
            velocityY=-1;
        } else if(e.getKeyCode()==KeyEvent.VK_DOWN && velocityY!=-1){
            velocityX=0;
            velocityY=1;
        } else if(e.getKeyCode()==KeyEvent.VK_LEFT && velocityX!=1){
            velocityX=-1;
            velocityY=0;
        } else if(e.getKeyCode()==KeyEvent.VK_RIGHT && velocityX!=-1){
            velocityX=1;
            velocityY=0;
        }
    }

    // Do Not need
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
} 
