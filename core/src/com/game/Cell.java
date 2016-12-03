package com.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by wynter on 9/10/2016.
 */
public class Cell {
    float x;
    float y;
    float width;
    float height;
    float thickness;
    Color color;

    public Cell(float x, float y, float width, float height, float thickness, Color color){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.thickness = thickness;
        this.color = color;
    }

    public Cell(float thickness, Color color){
        this.thickness = thickness;
        this.color = color;
    }

    public void setThickness(float thickness){
        this.thickness = thickness;
    }

    public void setCell(float leftX, float bottomY, float rightX, float topY){
        this.x = leftX;
        this.y = bottomY;
        this.width = rightX - leftX;
        this.height = topY - bottomY;
    }

    public void render(ShapeRenderer painter){
        painter.setColor(color);
        painter.rectLine(x, y, x+width, y, thickness);
        painter.rectLine(x+width, y, x+width, y+height, thickness);
        painter.rectLine(x, y, x, y+height, thickness);
        painter.rectLine(x, y+height, x+width, y+height, thickness);
    }

    public void highlight(ShapeRenderer painter, float ratio){
        //gold color
        painter.setColor(1.0f, 215.0f/255.0f, 0.0f, ratio);
        painter.rect(x,y, width, height);
    }

    public void setColor(Color c){
        this.color = c;
    }
}
