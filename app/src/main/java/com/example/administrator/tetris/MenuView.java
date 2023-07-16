package com.example.administrator.tetris;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2017/8/20.
 */
public class MenuView extends SurfaceView implements SurfaceHolder.Callback{
    MainActivity ma;
    Resources res;
    private Bitmap bmpBegin,bmpBeginPressed,bmpAbout,bmpAboutPressed,
            bmpRecord,bmpRecordPressed,bmpExit,bmpExitPressed,
            bmpMenuBackground;//按下和未按下的按钮图片以及背景图片
    //按钮是否按下标识布尔量，控制按钮在不同状态下不同图片的绘制
    private Boolean begin_isPressed,record_isPressed,about_isPressed,exit_isPressed;
    private int bmpBegin_x_unPressed,bmpBegin_y_unPressed,bmpBegin_x_pressed,bmpBegin_y_pressed,
            bmpRecord_x_unPressed,bmpRecord_y_unPressed,bmpRecord_x_pressed,bmpRecord_y_pressed,
            bmpAbout_x_unPressed,bmpAbout_y_unPressed,bmpAbout_x_pressed,bmpAbout_y_pressed,
            bmpExit_x_unPressed,bmpExit_y_unPressed,bmpExit_x_pressed,bmpExit_y_pressed;//按下按钮、未按下按钮图片的坐标
    private int gap;//按钮之间的距离
    private float scale=Constant.MENU_BMP_SCALE_FACTOR;//图片缩放比例
    private int adjustValue;//调整菜单按钮群的变量
    public SurfaceHolder sh;
    private DrawMenuThread dmt;
    private Message msg;

    public MenuView(MainActivity ma){
        super(ma);
        this.ma=ma;
        res=ma.getResources();
        loadPictures();
        scaleBmp();
        gap=Constant.SCREEN_WIDTH/300;
        adjustValue=Constant.SCREEN_HEIGHT/20;//以屏幕高度为标准为adjustValue赋合适的值
        btnLocation();
        sh=this.getHolder();
        sh.addCallback(this);
        begin_isPressed=false;
        record_isPressed=false;
        about_isPressed=false;
        exit_isPressed=false;
    }
    private void loadPictures(){
        bmpBegin=BitmapFactory.decodeResource(res,R.drawable.beginbutton);
        bmpBeginPressed=BitmapFactory.decodeResource(res,R.drawable.beginbutton_pressed);
        bmpRecord=BitmapFactory.decodeResource(res,R.drawable.gamerecord);
        bmpRecordPressed=BitmapFactory.decodeResource(res,R.drawable.gamerecord_pressed);
        bmpAbout=BitmapFactory.decodeResource(res,R.drawable.gameabout);
        bmpAboutPressed=BitmapFactory.decodeResource(res,R.drawable.gameabout_pressed);
        bmpExit=BitmapFactory.decodeResource(res,R.drawable.exit);
        bmpExitPressed=BitmapFactory.decodeResource(res,R.drawable.exit_pressed);
        bmpMenuBackground=BitmapFactory.decodeResource(res,R.drawable.menu_background);
    }
    //缩放按钮图片到适当程度
    private void scaleBmp(){
        bmpBegin=bmpBegin.createScaledBitmap(bmpBegin,
                (int)(bmpBegin.getWidth()*scale),
                (int)(bmpBegin.getHeight()*scale),true);
        bmpBeginPressed=bmpBeginPressed.createScaledBitmap(bmpBeginPressed,
                (int)(bmpBeginPressed.getWidth()*scale),
                (int)(bmpBeginPressed.getHeight()*scale),true);
        bmpRecord=bmpRecord.createScaledBitmap(bmpRecord,
                (int)(bmpRecord.getWidth()*scale),
                (int)(bmpRecord.getHeight()*scale),true);
        bmpRecordPressed=bmpRecordPressed.createScaledBitmap(bmpRecordPressed,
                (int)(bmpRecordPressed.getWidth()*scale),
                (int)(bmpRecordPressed.getHeight()*scale),true);
        bmpAbout=bmpAbout.createScaledBitmap(bmpAbout,
                (int)(bmpAbout.getWidth()*scale),
                (int)(bmpAbout.getHeight()*scale),true);
        bmpAboutPressed=bmpAboutPressed.createScaledBitmap(bmpAboutPressed,
                (int)(bmpAboutPressed.getWidth()*scale),
                (int)(bmpAboutPressed.getHeight()*scale),true);
        bmpExit=bmpExit.createScaledBitmap(bmpExit,
                (int)(bmpExit.getWidth()*scale),
                (int)(bmpExit.getHeight()*scale),true);
        bmpExitPressed=bmpExitPressed.createScaledBitmap(bmpExitPressed,
                (int)(bmpExitPressed.getWidth()*scale),
                (int)(bmpExitPressed.getHeight()*scale),true);
        //缩放背景图片到全屏大小
        bmpMenuBackground=bmpMenuBackground.createScaledBitmap(bmpMenuBackground,ma.screenW,ma.screenH,true);
    }
    //设置按钮坐标，使按钮群位于屏幕中央
    private void btnLocation(){
        //以开始按钮为标准定位各个按钮未被按下时的横纵坐标
        bmpBegin_x_unPressed= ma.screenW/2-bmpBegin.getWidth()/2;
        bmpBegin_y_unPressed= ma.screenH/2-gap*3/2-2*bmpBegin.getHeight()+adjustValue;
        bmpRecord_x_unPressed=bmpBegin_x_unPressed;
        bmpRecord_y_unPressed=bmpBegin_y_unPressed+bmpBegin.getHeight()+gap;
        bmpAbout_x_unPressed=bmpBegin_x_unPressed;
        bmpAbout_y_unPressed=bmpRecord_y_unPressed+bmpBegin.getHeight()+gap;
        bmpExit_x_unPressed=bmpBegin_x_unPressed;
        bmpExit_y_unPressed=bmpAbout_y_unPressed+bmpBegin.getHeight()+gap;
        //定位各个按钮被按下后的横纵坐标，按下的横坐标（纵坐标）等于未按下的加上它们横坐标（纵坐标）之差的一半
        bmpBegin_x_pressed=bmpBegin_x_unPressed+(bmpBegin.getWidth()-bmpBeginPressed.getWidth())/2;
        bmpBegin_y_pressed=bmpBegin_y_unPressed+(bmpBegin.getHeight()-bmpBeginPressed.getHeight())/2;
        bmpRecord_x_pressed=bmpBegin_x_pressed;
        bmpRecord_y_pressed=bmpRecord_y_unPressed+(bmpRecord.getHeight()-bmpRecordPressed.getHeight())/2;
        bmpAbout_x_pressed=bmpBegin_x_pressed;
        bmpAbout_y_pressed=bmpAbout_y_unPressed+(bmpAbout.getHeight()-bmpAboutPressed.getHeight())/2;
        bmpExit_x_pressed=bmpBegin_x_pressed;
        bmpExit_y_pressed=bmpExit_y_unPressed+(bmpExit.getHeight()-bmpExitPressed.getHeight())/2;
    }
    //菜单的绘制函数
    public void drawSelf(Canvas canvas){
        canvas.drawBitmap(bmpMenuBackground,0,0,null);
        if(begin_isPressed)
            canvas.drawBitmap(bmpBeginPressed,bmpBegin_x_pressed,bmpBegin_y_pressed,null);
        else{
            canvas.drawBitmap(bmpBegin,bmpBegin_x_unPressed,bmpBegin_y_unPressed,null);
        }
        if(record_isPressed)
            canvas.drawBitmap(bmpRecordPressed,bmpRecord_x_pressed,bmpRecord_y_pressed,null);
        else{
            canvas.drawBitmap(bmpRecord,bmpRecord_x_unPressed,bmpRecord_y_unPressed,null);
        }
        if(about_isPressed)
            canvas.drawBitmap(bmpAboutPressed,bmpAbout_x_pressed,bmpAbout_y_pressed,null);
        else{
            canvas.drawBitmap(bmpAbout,bmpAbout_x_unPressed,bmpAbout_y_unPressed,null);
        }
        if(exit_isPressed)
            canvas.drawBitmap(bmpExitPressed,bmpExit_x_pressed,bmpExit_y_pressed,null);
        else{
            canvas.drawBitmap(bmpExit,bmpExit_x_unPressed,bmpExit_y_unPressed,null);
        }
    }
    public boolean onTouchEvent(MotionEvent event){
        int pointX=(int)event.getX();
        int pointY=(int)event.getY();
        int action=event.getAction();
        if(action==MotionEvent.ACTION_DOWN || action==MotionEvent.ACTION_MOVE){
            //当用户是按下动作或移动动作且位于未按下按钮图片范围之内时将isPressed置为true(按下状态),否则置为false(未按下状态)
            if(pointX>bmpBegin_x_unPressed && pointX<bmpBegin_x_unPressed+bmpBegin.getWidth() &&
                    pointY>bmpBegin_y_unPressed && pointY<bmpBegin_y_unPressed+bmpBegin.getHeight()) {
                begin_isPressed=true;
            }
            else{
                //手指移出开始按钮范围后将begin_isPressed置为false
                begin_isPressed=false;
                if(pointX>bmpRecord_x_unPressed && pointX<bmpRecord_x_unPressed+bmpRecord.getWidth() &&
                        pointY>bmpRecord_y_unPressed && pointY<bmpRecord_y_unPressed+bmpRecord.getHeight()){
                    record_isPressed=true;
                }
                else{
                    //手指移出记录按钮范围后将record_isPressed置为false
                    record_isPressed=false;
                    if(pointX>bmpAbout_x_unPressed && pointX<bmpAbout_x_unPressed+bmpAbout.getWidth() &&
                            pointY>bmpAbout_y_unPressed && pointY<bmpAbout_y_unPressed+bmpAbout.getHeight()){
                        about_isPressed=true;
                    }
                    else{
                        //手指移出关于按钮范围后将about_isPressed置为false
                        about_isPressed=false;
                        if(pointX>bmpExit_x_unPressed && pointX<bmpExit_x_unPressed+bmpExit.getWidth() &&
                                pointY>bmpExit_y_unPressed && pointY<bmpExit_y_unPressed+bmpExit.getHeight()){
                            exit_isPressed=true;
                        }
                        else{
                            //手指移出退出按钮范围后将exit_isPressed置为false
                            exit_isPressed=false;
                        }
                    }
                }
            }
        }
        if(action==MotionEvent.ACTION_UP){
            //抬起判断手指是否还是位于按钮范围之内，是则视为一次点击操作
            if(pointX>bmpBegin_x_pressed && pointX<bmpBegin_x_pressed+bmpBegin.getWidth() &&
                    pointY>bmpBegin_y_pressed && pointY<bmpBegin_y_pressed+bmpBegin.getHeight()) {
                begin_isPressed=false;
                //点击动作完成，通过myHandler将游戏状态改为GAMING
                msg=new Message();
                msg.what=WhatMessage.GOTO_GAMINGVIEW;
                ma.myHandler.sendMessage(msg);
            }
            else{
                if(pointX>bmpRecord_x_pressed && pointX<bmpRecord_x_pressed+bmpRecord.getWidth() &&
                        pointY>bmpRecord_y_pressed && pointY<bmpRecord_y_pressed+bmpRecord.getHeight()){
                    record_isPressed=false;
                    msg=new Message();
                    msg.what=WhatMessage.GOTO_GAMERECORD;
                    ma.myHandler.sendMessage(msg);
                }
                else{
                    if(pointX>bmpAbout_x_pressed && pointX<bmpAbout_x_pressed+bmpAbout.getWidth() &&
                            pointY>bmpAbout_y_pressed && pointY<bmpAbout_y_pressed+bmpAbout.getHeight()){
                        about_isPressed=false;
                        msg=new Message();
                        msg.what=WhatMessage.GOTO_GAMEABOUT;
                        ma.myHandler.sendMessage(msg);
                    }
                    else{
                        if(pointX>bmpExit_x_pressed && pointX<bmpExit_x_pressed+bmpExit.getWidth() &&
                                pointY>bmpExit_y_pressed && pointY<bmpExit_y_pressed+bmpExit.getHeight()){
                            exit_isPressed=false;
                            System.exit(0);
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        dmt=new DrawMenuThread(this);
        dmt.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        dmt.flag=false;
    }
}
