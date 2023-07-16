package com.example.administrator.tetris;

import android.graphics.Canvas;

/**
 * Created by Administrator on 2017/10/8.
 */
public class DrawGameRecordThread extends Thread{
    private Boolean flag=false;
    private Canvas canvas;
    private GameRecordView grv;

    public DrawGameRecordThread(GameRecordView grv){
        this.grv=grv;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    @Override
    public void run() {
        while(flag){
            try {
                canvas=grv.sh.lockCanvas();
                if (canvas != null) {
                    grv.drawSelf(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (canvas != null)
                    grv.sh.unlockCanvasAndPost(canvas);
            }
        }
    }
}
