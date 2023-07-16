package com.example.administrator.tetris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by Administrator on 2017/8/24.
 */
public class DrawGamingViewThread extends Thread {
    private GamingView gv;
    private Boolean flag=false;
    private Canvas canvas;
    private Paint paint;

    public DrawGamingViewThread(GamingView gv){
        this.gv=gv;
        paint=new Paint();
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @Override
    public void run() {
        while(flag){
            try {
                canvas=gv.sh.lockCanvas();
                if (canvas != null) {
                    gv.drawSelf(canvas,paint);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (canvas != null)
                    gv.sh.unlockCanvasAndPost(canvas);
            }
        }
    }
}
