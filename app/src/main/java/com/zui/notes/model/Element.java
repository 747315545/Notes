package com.zui.notes.model;

/**
 * Created by huangfei on 2017/2/15.
 */

public class Element {
    public int color;
    public Double direction;
    public float speed;
    public float x = 0;
    public float y = 0;

    public Element(int color, Double direction, float speed){
        this.color = color;
        this.direction = direction;
        this.speed = speed;
    }
}