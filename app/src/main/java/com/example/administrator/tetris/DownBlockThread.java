package com.example.administrator.tetris;

import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/8/26.
 */
public class DownBlockThread extends Thread {
    private Boolean flag;
    private GamingView gv;
    public static int STOP_TIME=1000;
    public int stopTime=STOP_TIME;//方块停止下落的时间

    private Boolean isPause = false;

    public DownBlockThread(GamingView gv) {
        this.gv=gv;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public void setPause(Boolean isPause) {
        this.isPause = isPause;
    }

    @Override
    public void run() {
        while(flag){
            if(!isPause) {
                //没有与地图中的固定方块或围墙发生碰撞则将方块的纵坐标加1（向下移动一格）
                if (gv.isCollide(gv.tetris.x, gv.tetris.y + 1, gv.tetris.blockType, gv.tetris.turnState) == false) {
                    gv.tetris.y += 1;
                }
                //与地图中的固定方块或围墙碰撞则将该方块添加到地图中然后产生新的方块
                if (gv.isCollide(gv.tetris.x, gv.tetris.y + 1, gv.tetris.blockType, gv.tetris.turnState) == true) {
                    gv.addBlock();
                    gv.delLine();
                    gv.tetris.newBlock();
                }
                try {
                    Thread.sleep(stopTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                //触摸暂停按钮，则游戏暂停（线程执行体run什么都不做，不改变相关变量的值）
            }
        }
    }
}
