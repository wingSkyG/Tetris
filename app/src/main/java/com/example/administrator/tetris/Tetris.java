package com.example.administrator.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by Administrator on 2017/8/21.
 */
public class Tetris {
    public final int shapes[][][] = new int[][][] {
            // 长条形I形
            { { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 },
                    { 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0 } },
            // 倒z字形
            { { 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
                    { 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 } },
            // z字形
            { { 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
                    { 1, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 } },
            // J字形
            { { 0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 },
                    { 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 1, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
                    { 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
            // 田字形
            { { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
            // L字形
            { { 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 },
                    { 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
                    { 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 } },
            // ⊥字形
            { { 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
                    { 1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                    { 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0 } }
    };
    private int nextBlockType=-1;//下一个方块类型ype，-1为没有下一个方块（游戏刚开始）
    private int nextTurnState=-1;
    public int blockType;//当前下落方块类型
    public int turnState;//当前方块状态
    public int x, y; // 当前方块位置
    public DownBlockThread dbt;
    public GamingView gv;

    public Tetris(GamingView gv) {
        this.gv=gv;
        newBlock();
    }
    public void newBlock(){
        if(nextBlockType==-1){//没有下一个方块（游戏刚开始）
            //随机产生当前方块类型、当前方块状态；并产生下一个方块类型、下一个方块状态
            blockType=(int)(Math.random()*1000)%7;
            turnState=(int)(Math.random()*1000)%4;
            nextBlockType=(int)(Math.random()*1000)%7;
            nextTurnState=(int)(Math.random()*1000)%4;
        }
        else{//将下一个方块类型、状态赋值给当前方块，并产生下一个方块类型、下一个方块状态
            blockType=nextBlockType;
            turnState=nextTurnState;
            nextBlockType=(int)(Math.random()*1000)%7;
            nextTurnState=(int)(Math.random()*1000)%4;
        }
        //初始化tetris方块的位置为游戏区上部中间
        x=(gv.map_line_num-2)/2-2+1;
        y=0;
    }

    public void initLocation(){//游戏中点击暂停按钮，点击返回菜单后调用此方法，使方块重新从上方初始位置下落
        x=(gv.map_line_num-2)/2-2+1;
        y=0;
    }

    public void moveLeft(){
        x-=1;
    }

    public void moveRight(){
        x+=1;
    }

    // 旋转当前方块的方法
    public void turnBlock() {
        int tempturnState = turnState;
        turnState = (turnState + 1) % 4;
        if (gv.isCollide(x, y, blockType, turnState) == false) {//可以旋转（旋转后的方块没有与地图中的固定方块发生碰撞）
        }
        if (gv.isCollide(x, y, blockType, turnState) == true) {//不可以旋转（旋转后的方块与地图中的固定方块发生碰撞）
            turnState = tempturnState;//旋转状态不变
        }
    }

}
