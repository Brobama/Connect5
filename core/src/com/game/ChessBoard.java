package com.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by wynter on 9/17/2016.
 */
public class ChessBoard {
    public static final int boardSize = 20;

    Cell matrix[][];
    int stonesLayout[][];
    int offsetX;
    int offsetY;
    int cellLength;

    int flickX;
    int flickY;
    int flag = 0;
    int currentStoneType = 1; // 1 is white, 2 is black

    Texture whiteStone;
    Texture blackStone;

    BitmapFont font;
    boolean isGameOver;

    Vector2 winResult[];

    private ParticleEffect explosion;

    Sound stone_drop;
    Sound stone_reset;
    Sound stone_win;
    Connect5 gameMain;
    public ChessBoard(Connect5 c) {
        gameMain = c;
        matrix = new Cell[boardSize][boardSize];
        stonesLayout = new int[boardSize][boardSize];
        cellLength = 28;
        offsetX = 20;
        offsetY = 20;
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                matrix[i][j] = new Cell(offsetX + j * cellLength,
                        offsetY + i * cellLength, cellLength,
                        cellLength, 2, Color.YELLOW);

            }
        }

        flickX = 0;
        flickY = 0;
        this.setFlick(flickX, flickY);
        whiteStone = new Texture("white.png");
        blackStone = new Texture("black.png");

        explosion = new ParticleEffect();
        explosion.load(Gdx.files.internal("explosion.p"), Gdx.files.internal(""));

        font = new BitmapFont();
        font.getData().setScale(2.0f);
        font.setColor(0, 1.0f, 1.0f, 1.0f);
        isGameOver = false;

        winResult = new Vector2[5];
        for(int i=0; i<5; ++i){
            winResult[i] = new Vector2();
        }
        stone_drop = Gdx.audio.newSound(Gdx.files.internal("stone_drop.wav"));
        stone_reset = Gdx.audio.newSound(Gdx.files.internal("stone_reset.wav"));
        stone_win = Gdx.audio.newSound(Gdx.files.internal("stone_win.wav"));
    }

    public void dispose() {
        whiteStone.dispose();
        blackStone.dispose();
        explosion.dispose();
        stone_drop.dispose();
        stone_reset.dispose();
        stone_win.dispose();
    }

    public void setFlick(int x, int y) {
        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize)
            return;
        matrix[flickY][flickX].setColor(Color.YELLOW);
        matrix[flickY][flickX].setThickness(2);
        flag = 0;
        flickX = x;
        flickY = y;
        matrix[flickY][flickX].setThickness(8);
    }

    public void update() {

        if (flag < 10) {
            matrix[flickY][flickX].setColor(this.currentStoneType == 1 ? Color.WHITE : Color.BLACK);
            ++flag;
        } else {
            matrix[flickY][flickX].setColor(Color.RED);
            ++flag;
        }
        if (flag > 20) {
            flag = 0;
        }

        Vector2 vec = judgement();
        if(!isGameOver && vec.x >= 0)
        {
            isGameOver = true;
            Cell focus = this.matrix[flickY][flickX];
            this.explosion.setPosition(focus.x + focus.width/2.0f, focus.y+focus.height/2.0f);
            this.explosion.start();
            gameMain.changeBackgroundMusicVolume(0.0f, 0.0f);
            stone_win.play();
            gameMain.changeBackgroundMusicVolume(2.0f, 0.3f);
            //int v = this.stonesLayout[(int)vec.y][(int)vec.x];
        }

    }

    public Vector2 judgement(){
        for(int i=0; i<boardSize; ++i){
            for(int j=0; j<boardSize; ++j){
                if(testConnection(i, j))
                    return new Vector2(i, j);
            }
        }
        return new Vector2(-1, -1);
    }

    public boolean testConnection(int x, int y){
        int v = this.stonesLayout[y][x];
        if(v == 0)
            return false;
        return testHorizontal(x, y, v) || testVertical(x, y, v) || testDiagonal_one(x, y, v) || testDiagonal_two(x, y, v);
    }

    public boolean testHorizontal(int x, int y, int v){
        if(x > boardSize - 5)
            return false;
        for(int i = x; i<x+5; ++i){
            int e = this.stonesLayout[y][i];
            winResult[i-x].x = i;
            winResult[i-x].y = y;
            if(e != v)
                return false;
        }
        return true;
    }

    public boolean testVertical(int x, int y, int v){
        if(y > boardSize - 5)
            return false;
        for(int i = y; i< y + 5; ++i){
            int e = this.stonesLayout[i][x];
            winResult[i-y].y = i;
            winResult[i-y].x = x;
            if(e != v)
                return false;
        }
        return true;
    }
    public boolean testDiagonal_one(int x, int y, int v){
        if(x > boardSize - 5 || y > boardSize - 5)
            return false;
        for(int i = 0; i< 5; ++i){
            int e = this.stonesLayout[y+i][x+i];
            winResult[i].x = x+i;
            winResult[i].y = y+i;
            if( e != v)
                return false;
        }
        return true;
    }

    public boolean testDiagonal_two(int x, int y, int v){
        if(x > boardSize - 5 || y < 4)
            return false;
        for(int i = 0; i< 5; ++i){
            int e = this.stonesLayout[y-i][x+i];
            winResult[i].x = x+i;
            winResult[i].y = y-i;
            if( e != v)
                return false;
        }
        return true;
    }


    public void render(ShapeRenderer painter) {


        //matrix[flickY][flickX].render(painter);
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                matrix[i][j].render(painter);
            }
        }



        // draw cursor
        matrix[flickY][flickX].render(painter);

    }


    private double ratio = 0.0;


    public void drawHighlight(ShapeRenderer painter){
        // highlight
        if(isGameOver) {

            ratio += 0.123;
            if(ratio > 2.0*Math.PI)
                ratio -= 2.0*Math.PI; //sin(x) = sin(x - 2*pi)

            for (Vector2 i : winResult) {
                matrix[(int) i.y][(int) i.x].highlight(painter, (float)(0.5 + Math.sin(ratio)/2.0)*0.8f);
            }
        }

    }

    public void drawStones(SpriteBatch batch) {
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                int v = stonesLayout[i][j];
                if (v == 1) {
                    batch.draw(this.whiteStone, matrix[i][j].x, matrix[i][j].y,
                            matrix[i][j].width, matrix[i][j].height);
                } else if (v == 2) {
                    batch.draw(this.blackStone, matrix[i][j].x, matrix[i][j].y,
                            matrix[i][j].width, matrix[i][j].height);
                }
            }
        }
        //font.setColor(0,0,0,0.2f);
        font.setColor(0, 1, 1, 1);
        font.draw(batch, "Author: Dave", 725, 500);
        if(isGameOver == false) {
            if (this.currentStoneType == 1) {
                font.setColor(1, 1, 1, 1);
                font.draw(batch, "White's Turn", 725, 400);
            } else {
                font.setColor(0, 0, 0, 1);
                font.draw(batch, "Black's Turn", 725, 400);
            }
        }
        else{   //now the game is over here
            if (this.currentStoneType == 1) {
                font.setColor(0, 0, 0, 1);
                font.draw(batch, "Black wins the game", 725, 400);
            } else {
                font.setColor(1, 1, 1, 1);
                font.draw(batch, "White wins the game", 725, 400);
            }
        }
        explosion.draw(batch, Gdx.graphics.getDeltaTime());
    }

    public void restartGame(){
        stonesLayout = new int[boardSize][boardSize];
        this.setFlick(0, 0);
        isGameOver = false;
        stone_reset.play();
    }

    public void moveUp() {
        this.setFlick(this.flickX, this.flickY + 1);
    }

    public void moveDown() {
        this.setFlick(this.flickX, this.flickY - 1);
    }

    public void moveLeft() {
        this.setFlick(this.flickX - 1, this.flickY);
    }

    public void moveRight() {
        this.setFlick(this.flickX + 1, this.flickY);
    }

    public void addStone() {
        if(isGameOver)
            return;
        if (stonesLayout[flickY][flickX] > 0)    //there is already an existing stone, we do nothing here.
            return;
        stonesLayout[flickY][flickX] = currentStoneType;    //add a stone at position flickY, flickX
        currentStoneType = currentStoneType == 1 ? 2 : 1;
        stone_drop.play();
    }

}
