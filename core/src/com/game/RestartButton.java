package com.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by wynter on 10/8/2016.
 */

public class RestartButton {
    Texture buttonUp;
    Texture buttonDown;

    Sprite buttonSprite;

    float posX;
    float posY;

    SpriteBatch batch;
    ChessBoard chessBoard;

    public RestartButton(SpriteBatch b, ChessBoard c){
        batch = b;
        buttonUp = new Texture("restart_button_up.png");
        buttonDown = new Texture("restart_button_down.png");
        buttonSprite = new Sprite(buttonUp);
        posX = 0;
        posY = 0;

        chessBoard = c;

        buttonSprite.setPosition(posX, posY);
    }

    public void dispose(){
        buttonUp.dispose();
        buttonDown.dispose();
    }


    public void setPosition(float x, float y){
        posX = x;
        posY = y;
        buttonSprite.setPosition(posX, posY);
    }

    public void draw(){
        buttonSprite.draw(batch);
    }

    private boolean bActive = false;

    public void touchDown(float x, float y){
        Rectangle rect = buttonSprite.getBoundingRectangle();
        if(rect.contains(x, y)) {
            bActive = true;
            buttonSprite.setTexture(buttonDown);
        }
    }

    public void touchUp(float x, float y){
        Rectangle rect = buttonSprite.getBoundingRectangle();
        if(bActive){
            bActive = false;
            buttonSprite.setTexture(buttonUp);
            if(rect.contains(x, y)){
                restartGame();

            }
        }
    }

    public void restartGame() {
        chessBoard.restartGame();
    }
}
