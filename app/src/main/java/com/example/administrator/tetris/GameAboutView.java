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
import android.widget.TextView;

/**
 * Created by Administrator on 2017/9/16.
 */
public class GameAboutView extends SurfaceView implements SurfaceHolder.Callback {
    private MainActivity ma;
    public SurfaceHolder sh;
    public DrawGameAboutThread dgat;
    private Resources res= this.getResources();
    private Bitmap bmpGameaboutBackground, bmpBackUnpressed, bmpBackPressed;
    private int btn_x_unPressed, btn_y_unPressed,btn_x_pressed,btn_y_pressed;//返回按钮的横纵坐标
    private float scale = Constant.ABOUT_BMP_SCALE_FACTOR;//图片缩放比例
    private int distance=Constant.SCREEN_WIDTH / 12;;//返回按钮与屏幕下边界、右边界的距离
    private Boolean isPressed = false;
    private Message msg;

    public GameAboutView(MainActivity ma) {
        super(ma);
        this.ma = ma;
        sh = this.getHolder();
        sh.addCallback(this);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
        loadAndScaleBmp();
        locateBackButton();
    }

    //加载用于GameAboutView的图片资源
    private void loadAndScaleBmp() {
        //加载背景图片并缩放到屏幕大小
        bmpGameaboutBackground = BitmapFactory.decodeResource(res, R.drawable.gameabout_background);
        bmpGameaboutBackground = bmpGameaboutBackground.createScaledBitmap(bmpGameaboutBackground,
                Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT, true);
        bmpBackUnpressed = BitmapFactory.decodeResource(res, R.drawable.back);
        bmpBackUnpressed = bmpBackUnpressed.createScaledBitmap(bmpBackUnpressed,
                (int) (bmpBackUnpressed.getWidth() * scale), (int) (bmpBackUnpressed.getHeight() * scale), true);
        bmpBackPressed = BitmapFactory.decodeResource(res, R.drawable.back_pressed);
        bmpBackPressed = bmpBackPressed.createScaledBitmap(bmpBackPressed,
                (int) (bmpBackPressed.getWidth() * scale), (int) (bmpBackPressed.getHeight() * scale), true);
    }

    private void locateBackButton() {
        btn_x_unPressed = Constant.SCREEN_WIDTH - (bmpBackUnpressed.getWidth() + distance);
        btn_y_unPressed = Constant.SCREEN_HEIGHT - (bmpBackUnpressed.getHeight() + distance);
        btn_x_pressed=btn_x_unPressed+(bmpBackUnpressed.getWidth()-bmpBackPressed.getWidth())/2;
        btn_y_pressed=btn_y_unPressed+(bmpBackUnpressed.getHeight()-bmpBackPressed.getHeight())/2;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        dgat = new DrawGameAboutThread(this);
        dgat.setFlag(true);
        dgat.start();
    }

    public void drawSelf(Canvas canvas) {
        canvas.drawBitmap(bmpGameaboutBackground, 0, 0, null);
        if(isPressed)
            canvas.drawBitmap(bmpBackPressed,btn_x_pressed,btn_y_pressed,null);
        else{
            canvas.drawBitmap(bmpBackUnpressed, btn_x_unPressed, btn_y_unPressed, null);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int pointX = (int) event.getX();
        int pointY = (int) event.getY();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            if (pointX > btn_x_unPressed && pointX < btn_x_unPressed + bmpBackUnpressed.getWidth() &&
                    pointY > btn_y_unPressed && pointY < btn_y_unPressed + bmpBackUnpressed.getHeight()) {
                isPressed = true;
            }
        }
        if (action == MotionEvent.ACTION_UP) {
            if (pointX > btn_x_unPressed && pointX < btn_x_unPressed + bmpBackUnpressed.getWidth() &&
                    pointY > btn_y_unPressed && pointY < btn_y_unPressed + bmpBackUnpressed.getHeight()) {
                isPressed = false;
                msg = new Message();
                msg.what = WhatMessage.GOTO_MENUVIEW;
                ma.myHandler.sendMessage(msg);
            }
        }
        return true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        dgat.flag = false;
    }
}
