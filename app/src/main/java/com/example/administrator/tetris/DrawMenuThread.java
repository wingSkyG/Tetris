package com.example.administrator.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

/**
 * Created by Administrator on 2017/8/22.
 */
public class DrawMenuThread extends Thread {
    private MenuView mv;
    public Boolean flag=false;
    private Canvas canvas;

    public DrawMenuThread(MenuView mv) {
        this.mv=mv;
        flag=true;
    }

    @Override
    public void run() {
        while(flag) {
            try {
                canvas=mv.sh.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.CYAN);
                    mv.drawSelf(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (canvas != null)
                    mv.sh.unlockCanvasAndPost(canvas);
            }
        }
    }
}
