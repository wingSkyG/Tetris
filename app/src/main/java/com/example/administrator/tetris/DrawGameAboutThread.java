package com.example.administrator.tetris;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Created by Administrator on 2017/9/16.
 */
public class DrawGameAboutThread extends Thread {
    private GameAboutView gav;

    public Boolean flag=false;
    private Canvas canvas;

    public DrawGameAboutThread(GameAboutView gav){
        this.gav=gav;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @Override
    public void run() {
        while(flag){
            try {
                canvas=gav.sh.lockCanvas();
                if (canvas != null) {
                    gav.drawSelf(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (canvas != null)
                    gav.sh.unlockCanvasAndPost(canvas);
            }
        }
    }
}
