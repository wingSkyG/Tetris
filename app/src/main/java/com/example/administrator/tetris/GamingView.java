package com.example.administrator.tetris;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/8/23.
 */
public class GamingView extends SurfaceView implements SurfaceHolder.Callback {
    private MainActivity ma;
    public SurfaceHolder sh;
    private DrawGamingViewThread dgvt;
    public DownBlockThread dbt;
    private static int brickWidth;//每一个方块的宽度
    private static int borderWidth;//方块外部的边框宽度
    private static int gap_pause;//pause图片与屏幕的距离
    private static int gameAreaWidth, gameAreaHeight;//游戏区域的宽度、高度
    private static int bmpScore_x, bmpScore_y, bmpDelLine_x, bmpDelLine_y, bmpPause_x, bmpPause_y, bmpPausePressed_x, bmpPausePressed_y;
    private static int gameAreaL_x, gameAreaL_y;//游戏区域的坐标
    public static int map_row_num = 21, map_line_num = 12;//游戏地图的行列数
    private int[][] map;//定义二维数组map
    public Tetris tetris;
    private float downX, downY, upX, upY;//定义手指触摸屏幕时的坐标和手指离开屏幕时的坐标
    private float x_standard, y_standard, y_min, y_max, x_min, x_max;
    private Boolean isDown = false;
    private Timer mTimer;
    private JudgeFingerState jfs;
    private Boolean isLongPressed = false;
    private Long downTime, upTime;//手指按下和抬起的时间
    private static int LONG_PRESS_THRESHOLD_TIME = 80;//长按模式的判断阈值，若手指按下时间大于此值，则为长按模式
    public int score = 0;
    private static int strScore_x;//分数值x坐标
    private static int textSize;//分数值字体大小
    private int delLineNum = 0;
    private static int strDelLineNum_x;
    private Resources res;
    private Bitmap bmpBackground, bmpScore, bmpDelLine, bmpPause, bmpPausePressed;
    private Bitmap bmpAdd3, bmpAdd6, bmpAdd9;//加分图片
    private Boolean isDelThreeLines = false;
    private Boolean isDelFourLines = false;
    private Boolean isDelTwoLines = false;
    private int bmpAddScores_x, bmpAddScores_y;
    //消除多行时出现的加分信息相关变量（消息由不透明转换为透明）
    private int alpha = 255;
    private Paint paint;
    private Boolean isPause = false;
    private int pauseAlpha = 100;
    private Bitmap bmpResume, bmpReturnToMenu, bmpResumePressed, bmpReturnToMenuPressed;
    private int bmpResume_x, bmpResume_y, bmpReturnToMenu_x, bmpReturnToMenu_y,
            bmpResume_x_pressed, bmpResume_y_pressed, bmpReturnToMenu_x_pressed, bmpReturnToMenu_y_pressed;
    private int gap = Constant.SCREEN_WIDTH / 40;//继续按钮与返回菜单按钮之间的距离
    private boolean resume_isPressed = false, returnToMenu_isPressed = false;
    public int del2LineTimes, delThreeLineTimes, delFourLineTimes;

    public GamingView(MainActivity ma) {
        super(ma);
        this.ma = ma;
        sh = this.getHolder();
        sh.addCallback(this);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
        loadAndScaleBmp();
        initSizeAndLocation();
        createAndInitMap();
    }

    private void initSizeAndLocation() {
        brickWidth = Constant.BRICK_WIDTH;
        borderWidth = Constant.BORDER_WIDTH;
        gameAreaWidth = Constant.BRICK_WIDTH * map_line_num;
        gameAreaHeight = Constant.BRICK_WIDTH * map_row_num;
        gameAreaL_x = Constant.SCREEN_WIDTH - gameAreaWidth;
        gameAreaL_y = Constant.SCREEN_HEIGHT - gameAreaHeight;
        bmpScore_x = 0;
        bmpScore_y = (Constant.SCREEN_HEIGHT - gameAreaHeight - bmpScore.getHeight()) / 2;
        bmpDelLine_x = Constant.SCREEN_WIDTH / 2;
        bmpDelLine_y = bmpScore_y;
        gap_pause = Constant.SCREEN_WIDTH / 100;
        bmpPause_x = Constant.SCREEN_WIDTH - (bmpPause.getWidth() + gap_pause);
        bmpPause_y = gap_pause;
        strScore_x = bmpScore_x + bmpScore.getWidth();
        textSize = (int) (bmpScore.getHeight() * 0.8);
        strDelLineNum_x = bmpDelLine_x + bmpDelLine.getWidth();
        bmpAddScores_x = Constant.SCREEN_WIDTH / 2 - bmpAdd3.getWidth() / 2;
        bmpAddScores_y = Constant.SCREEN_HEIGHT / 3 - bmpAdd3.getHeight() / 2;
        bmpPausePressed_x = bmpPause_x + (bmpPause.getWidth() - bmpPausePressed.getWidth()) / 2;
        bmpPausePressed_y = bmpPause_y + (bmpPause.getHeight() - bmpPausePressed.getHeight()) / 2;
        bmpResume_x = Constant.SCREEN_WIDTH / 2 - bmpResume.getWidth() / 2;
        bmpResume_y = Constant.SCREEN_HEIGHT / 3 - (bmpResume.getHeight() + gap / 2);
        bmpReturnToMenu_x = bmpResume_x;
        bmpReturnToMenu_y = Constant.SCREEN_HEIGHT / 3 + gap / 2;
        bmpResume_x_pressed = bmpResume_x + (bmpResume.getWidth() - bmpResumePressed.getWidth()) / 2;
        bmpResume_y_pressed = bmpResume_y + (bmpResume.getHeight() - bmpResumePressed.getHeight()) / 2;
        bmpReturnToMenu_x_pressed = bmpResume_x_pressed;
        bmpReturnToMenu_y_pressed = bmpReturnToMenu_y + (bmpReturnToMenu.getHeight() - bmpReturnToMenuPressed.getHeight()) / 2;
    }

    private void loadAndScaleBmp() {
        res = getResources();
        bmpBackground = BitmapFactory.decodeResource(res, R.drawable.gaming_background);
        bmpBackground = Bitmap.createScaledBitmap(bmpBackground, Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT, true);
        bmpScore = BitmapFactory.decodeResource(res, R.drawable.score);
        bmpScore = Bitmap.createScaledBitmap(bmpScore,
                (int) (bmpScore.getWidth() * Constant.SCORE_BMP_SCALE_FACTOR),
                (int) (bmpScore.getHeight() * Constant.SCORE_BMP_SCALE_FACTOR), true);
        bmpDelLine = BitmapFactory.decodeResource(res, R.drawable.deleteline);
        bmpDelLine = Bitmap.createScaledBitmap(bmpDelLine,
                (int) (bmpDelLine.getWidth() * Constant.SCORE_BMP_SCALE_FACTOR),
                (int) (bmpDelLine.getHeight() * Constant.SCORE_BMP_SCALE_FACTOR), true);
        bmpPause = BitmapFactory.decodeResource(res, R.drawable.pause);
        bmpPause = Bitmap.createScaledBitmap(bmpPause,
                (int) (bmpPause.getWidth() * Constant.PAUSE_BMP_SCALE_FACTOR),
                (int) (bmpPause.getHeight() * Constant.PAUSE_BMP_SCALE_FACTOR), true);
        bmpAdd3 = BitmapFactory.decodeResource(res, R.drawable.add3);
        bmpAdd6 = BitmapFactory.decodeResource(res, R.drawable.add6);
        bmpAdd9 = BitmapFactory.decodeResource(res, R.drawable.add9);
        bmpAdd3 = Bitmap.createScaledBitmap(bmpAdd3,
                (int) (bmpAdd3.getWidth() * Constant.BMP_ADD_SCALE_FACTOR),
                (int) (bmpAdd3.getHeight() * Constant.BMP_ADD_SCALE_FACTOR), true);
        bmpAdd6 = Bitmap.createScaledBitmap(bmpAdd6,
                (int) (bmpAdd6.getWidth() * Constant.BMP_ADD_SCALE_FACTOR),
                (int) (bmpAdd6.getHeight() * Constant.BMP_ADD_SCALE_FACTOR), true);
        bmpAdd9 = Bitmap.createScaledBitmap(bmpAdd9,
                (int) (bmpAdd9.getWidth() * Constant.BMP_ADD_SCALE_FACTOR),
                (int) (bmpAdd9.getHeight() * Constant.BMP_ADD_SCALE_FACTOR), true);
        bmpPausePressed = BitmapFactory.decodeResource(res, R.drawable.pause_pressed);
        bmpPausePressed = Bitmap.createScaledBitmap(bmpPausePressed,
                (int) (bmpPausePressed.getWidth() * Constant.PAUSE_BMP_SCALE_FACTOR),
                (int) (bmpPausePressed.getHeight() * Constant.PAUSE_BMP_SCALE_FACTOR), true);
        bmpResume = BitmapFactory.decodeResource(res, R.drawable.resume);
        bmpReturnToMenu = BitmapFactory.decodeResource(res, R.drawable.return_menu);
        bmpResume = bmpResume.createScaledBitmap(bmpResume,
                (int) (bmpResume.getWidth() * Constant.RESUME_BMP_SCALE_FACTOR),
                (int) (bmpResume.getHeight() * Constant.RESUME_BMP_SCALE_FACTOR), true);
        bmpReturnToMenu = bmpReturnToMenu.createScaledBitmap(bmpReturnToMenu,
                (int) (bmpReturnToMenu.getWidth() * Constant.RESUME_BMP_SCALE_FACTOR),
                (int) (bmpReturnToMenu.getHeight() * Constant.RESUME_BMP_SCALE_FACTOR), true);
        bmpResumePressed = BitmapFactory.decodeResource(res, R.drawable.resume_clicked);
        bmpReturnToMenuPressed = BitmapFactory.decodeResource(res, R.drawable.return_menu_clicked);
        bmpResumePressed = bmpResumePressed.createScaledBitmap(bmpResumePressed,
                (int) (bmpResumePressed.getWidth() * Constant.RESUME_BMP_SCALE_FACTOR),
                (int) (bmpResumePressed.getHeight() * Constant.RESUME_BMP_SCALE_FACTOR), true);
        bmpReturnToMenuPressed = bmpReturnToMenuPressed.createScaledBitmap(bmpReturnToMenuPressed,
                (int) (bmpReturnToMenuPressed.getWidth() * Constant.RESUME_BMP_SCALE_FACTOR),
                (int) (bmpReturnToMenuPressed.getHeight() * Constant.RESUME_BMP_SCALE_FACTOR), true);
    }

    private void createAndInitMap() {
        map = new int[map_line_num][map_row_num];//动态初始化地图数组（为数组指明元素个数）
        int i, j;
        for (i = 0; i < map_line_num; i++) {
            for (j = 0; j < map_row_num; j++) {
                if (i == 0)
                    map[i][j] = 0;//存储围墙
                else if (i == map_line_num - 1)
                    map[i][j] = 0;
                else if (j == map_row_num - 1)
                    map[i][j] = 0;
                else
                    map[i][j] = 9;//存储方块的地方（已固定的方块和正在下落的方块）
            }
        }
    }

    //绘制地图（围墙和已经固定的方块）和正在下落的方块
    public void drawSelf(Canvas canvas, Paint paint) {
        canvas.drawColor(Color.WHITE);
        if (isPause) {
            paint.setAlpha(pauseAlpha);
        } else {
            paint.setAlpha(255);
        }
        drawBackground(canvas, paint);
        drawMap(canvas, paint);
        drawBlock(canvas, paint);//绘制下落的方块
        drawBmpScoreAndDelLine(canvas, paint);
        drawBmpAddScores(canvas);
        drawBmpPause(canvas, paint);
        if (isPause) {
            if (!resume_isPressed) {
                canvas.drawBitmap(bmpResume, bmpResume_x, bmpResume_y, paint);
            } else {
                canvas.drawBitmap(bmpResumePressed, bmpResume_x_pressed, bmpResume_y_pressed, paint);
            }
            if (!returnToMenu_isPressed) {
                canvas.drawBitmap(bmpReturnToMenu, bmpReturnToMenu_x, bmpReturnToMenu_y, paint);
            } else {
                canvas.drawBitmap(bmpReturnToMenuPressed, bmpReturnToMenu_x_pressed, bmpReturnToMenu_y_pressed, paint);
            }
        }
    }

    private void drawBackground(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bmpBackground, 0, 0, paint);
    }

    private void drawMap(Canvas canvas, Paint paint) {
        for (int i = 0; i < map_line_num; i++) {
            for (int j = 0; j < map_row_num; j++) {
                if (map[i][j] == 0 || map[i][j] == 1 || map[i][j] == 2 || map[i][j] == 3 ||
                        map[i][j] == 4 || map[i][j] == 5 || map[i][j] == 6 || map[i][j] == 7) {//地图中有数据
                    //先绘制白色方块，然后绘制中间的小方块，四周为整个方块的边框
                    paint.setColor(Color.parseColor("#FFFFFF"));
                    if (isPause) {
                        paint.setAlpha(pauseAlpha);
                    } else {
                        paint.setAlpha(255);
                    }
                    canvas.drawRect(gameAreaL_x + i * brickWidth,
                            gameAreaL_y + j * brickWidth,
                            gameAreaL_x + (i * brickWidth + brickWidth),
                            gameAreaL_y + (j * brickWidth + brickWidth),
                            paint);
                    if (map[i][j] == 0) {
                        paint.setColor(ma.res.getColor(R.color.wallColor));
                    }
                    if (map[i][j] == 1) {
                        paint.setColor(ma.res.getColor(R.color.block1Color));
                    }
                    if (map[i][j] == 2) {
                        paint.setColor(ma.res.getColor(R.color.block2Color));
                    }
                    if (map[i][j] == 3) {
                        paint.setColor(ma.res.getColor(R.color.block2Color));
                    }
                    if (map[i][j] == 4) {
                        paint.setColor(ma.res.getColor(R.color.block3Color));
                    }
                    if (map[i][j] == 5) {
                        paint.setColor(ma.res.getColor(R.color.block4Color));
                    }
                    if (map[i][j] == 6) {
                        paint.setColor(ma.res.getColor(R.color.block5Color));
                    }
                    if (map[i][j] == 7) {
                        paint.setColor(ma.res.getColor(R.color.block6Color));
                    }
                    if (isPause) {
                        paint.setAlpha(pauseAlpha);
                    } else {
                        paint.setAlpha(255);
                    }
                    canvas.drawRect(gameAreaL_x + (i * brickWidth) + borderWidth,
                            gameAreaL_y + (j * brickWidth) + borderWidth,
                            gameAreaL_x + (i * brickWidth + brickWidth) - borderWidth,
                            gameAreaL_y + (j * brickWidth + brickWidth) - borderWidth,
                            paint);
                }
            }
        }
    }

    private void drawBlock(Canvas canvas, Paint paint) {
        for (int i = 0; i < 16; i++) {
            if (tetris.shapes[tetris.blockType][tetris.turnState][i] == 1) {
                //先绘制白色方块，然后绘制中间的小方块，四周为整个方块的边框
                paint.setColor(Color.parseColor("#FFFFFF"));
                if (isPause) {
                    paint.setAlpha(pauseAlpha);
                } else {
                    paint.setAlpha(255);
                }
                canvas.drawRect(gameAreaL_x + (i % 4 + tetris.x) * brickWidth,
                        gameAreaL_y + (i / 4 + tetris.y) * brickWidth,
                        gameAreaL_x + (i % 4 + tetris.x) * brickWidth + brickWidth,
                        gameAreaL_y + (i / 4 + tetris.y) * brickWidth + brickWidth,
                        paint);
                if (tetris.blockType == 0) {
                    paint.setColor(ma.res.getColor(R.color.block1Color));
                } else if (tetris.blockType == 1 || tetris.blockType == 2) {
                    paint.setColor(ma.res.getColor(R.color.block2Color));
                } else if (tetris.blockType == 3) {
                    paint.setColor(ma.res.getColor(R.color.block3Color));
                } else if (tetris.blockType == 4) {
                    paint.setColor(ma.res.getColor(R.color.block4Color));
                } else if (tetris.blockType == 5) {
                    paint.setColor(ma.res.getColor(R.color.block5Color));
                } else if (tetris.blockType == 6) {
                    paint.setColor(ma.res.getColor(R.color.block6Color));
                }
                if (isPause) {
                    paint.setAlpha(pauseAlpha);
                } else {
                    paint.setAlpha(255);
                }
                canvas.drawRect(gameAreaL_x + (i % 4 + tetris.x) * brickWidth + borderWidth,
                        gameAreaL_y + (i / 4 + tetris.y) * brickWidth + borderWidth,
                        gameAreaL_x + ((i % 4 + tetris.x) * brickWidth + brickWidth) - borderWidth,
                        gameAreaL_y + ((i / 4 + tetris.y) * brickWidth + brickWidth) - borderWidth,
                        paint);
            }
        }
    }

    private void drawBmpScoreAndDelLine(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bmpScore, bmpScore_x, bmpScore_y, paint);
        canvas.drawBitmap(bmpDelLine, bmpDelLine_x, bmpDelLine_y, paint);
        Paint.FontMetrics fm = paint.getFontMetrics();
        float fontHeight = fm.descent - fm.ascent;
        float baseY = bmpScore_y + bmpScore.getHeight() / 2 + fontHeight / 2 - fm.bottom;
        paint.setColor(Color.WHITE);
        paint.setTextSize(textSize);
        canvas.drawText(" " + score, strScore_x, baseY, paint);
        canvas.drawText(" " + delLineNum, strDelLineNum_x, baseY, paint);
    }

    private void drawBmpPause(Canvas canvas, Paint paint) {
        if (!isPause) {
            canvas.drawBitmap(bmpPause, bmpPause_x, bmpPause_y, paint);//绘制暂停按钮图片
        } else {
            canvas.drawBitmap(bmpPausePressed, bmpPausePressed_x, bmpPausePressed_y, paint);
        }
    }

    private void drawBmpAddScores(Canvas canvas) {
        paint = new Paint();
        paint.setAlpha(alpha);
        if (isDelTwoLines) {
            canvas.drawBitmap(bmpAdd3, bmpAddScores_x, bmpAddScores_y, paint);//在屏幕中上部绘制加分图片
        } else if (isDelThreeLines) {
            canvas.drawBitmap(bmpAdd6, bmpAddScores_x, bmpAddScores_y, paint);
        } else if (isDelFourLines) {
            canvas.drawBitmap(bmpAdd9, bmpAddScores_x, bmpAddScores_y, paint);
        }
        alpha -= 2;
        if (alpha <= 0) {
            alpha = 255;
            isDelTwoLines = false;
            isDelThreeLines = false;
            isDelFourLines = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        tetris = new Tetris(this);
        mTimer = new Timer();
        createAllThreads();
        startAllThreads();
    }

    private void createAllThreads() {
        dgvt = new DrawGamingViewThread(this);
        dbt = new DownBlockThread(this);
    }

    private void startAllThreads() {
        dgvt.setFlag(true);
        dgvt.start();
        dbt.setFlag(true);
        dbt.start();
    }

    public void stopAllThreads() {
        dgvt.setFlag(false);
        dbt.setFlag(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        dgvt.setFlag(false);
        dbt.setFlag(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (!isPause) {
            if (action == MotionEvent.ACTION_DOWN) {
                downX = event.getX();
                downY = event.getY();
                //若触摸区域位于暂停按钮图片区域之内则将isPuse置为true
                if (downX > bmpPause_x && (downX < bmpPause_x + bmpPause.getWidth())
                        && downY > bmpPause_y && (downY < bmpPause_y + bmpPause.getHeight())) {
                    isPause = true;
                    dbt.setPause(true);//使方块停止下落
                } else {
                    isDown = true;
                    jfs = new JudgeFingerState();
                    mTimer.schedule(jfs, LONG_PRESS_THRESHOLD_TIME);//启动任务，LONG_PRESS_THRESHOLD_TIME后执行
                    downTime = System.currentTimeMillis();
                }
            }
            if (action == MotionEvent.ACTION_UP) {
                if (isPause) {

                } else {
                    isPause = false;
                    isDown = false;
                    dbt.stopTime = dbt.STOP_TIME;//长按操作结束，将方块停止下落时间恢复
                    //若手指按下时间小于LONG_PRESS_THRESHOLD_TIME，则视此操作为点击，否则视为经过一次长按操作，此操作不是点击操作
                    upTime = System.currentTimeMillis();
                    if ((upTime - downTime) < LONG_PRESS_THRESHOLD_TIME) {
                        isLongPressed = false;
                    } else {
                        isLongPressed = true;
                    }
                    //返回相对于手机屏幕左上角的横纵坐标
                    upX = event.getRawX();
                    upY = event.getRawY();
                    if ((Math.abs(upX - downX)) < 3 && (Math.abs(upY - downY)) < 3 && !isLongPressed) {//手指上下左右移动范围在3像素内且不是长按模式则视为点击操作，
                        // 改变方块的形状；否则视为滑动操作，移动方块
                        tetris.turnBlock();
                    } else {
                        //为了区别左、右、下方向界线，
                        //分别以x方向和y方向的长度通过三角函数tan()计算y方向和x方向的长度，以此长度为衡量手指移动方向的标准
                        x_standard = (float) Math.tan(1.414 / 2) * (Math.abs(downX - upX));
                        y_standard = (float) Math.tan(1.414 / 2) * (upY - downY);
                        y_min = downY - x_standard;
                        y_max = downY + x_standard;
                        x_min = downX - y_standard;
                        x_max = downX + y_standard;
                        if (upX - downX < 0) {
                            //手指向左移动且upY位于y_max和y_min范围之内
                            if (upY < y_max && upY > y_min) {
                                if (isCollide(tetris.x - 1, tetris.y, tetris.blockType, tetris.turnState) == false)//没有发生碰撞则移动
                                    tetris.moveLeft();
                            }
                        } else {
                            //手指向右移动且upY位于y_max和y_min范围之内
                            if (upY < y_max && upY > y_min) {
                                if (isCollide(tetris.x + 1, tetris.y, tetris.blockType, tetris.turnState) == false)
                                    tetris.moveRight();
                            }
                        }
                    }
                }
            }
        } else {
            downX = event.getX();
            downY = event.getY();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                if (downX > bmpResume_x && downX < bmpResume_x + bmpResume.getWidth() &&
                        downY > bmpResume_y && downY < bmpResume_y + bmpResume.getHeight()) {
                    resume_isPressed = true;
                } else {
                    resume_isPressed = false;
                    if (downX > bmpReturnToMenu_x && downX < bmpReturnToMenu_x + bmpReturnToMenu.getWidth() &&
                            downY > bmpReturnToMenu_y && downY < bmpReturnToMenu_y + bmpReturnToMenu.getHeight()) {
                        returnToMenu_isPressed = true;
                    } else {
                        returnToMenu_isPressed = false;
                    }
                }
            }
            if (action == MotionEvent.ACTION_UP) {
                if (downX > bmpResume_x_pressed && downX < bmpResume_x_pressed + bmpResumePressed.getWidth() &&
                        downY > bmpResume_y_pressed && downY < bmpResume_y_pressed + bmpResumePressed.getHeight()) {
                    resume_isPressed = false;
                    isPause = false;//将绘图透明度置为不透明（返回游戏）且不再绘制bmpResume
                    dbt.setPause(false);//使方块恢复下落
                } else {
                    if (downX > bmpReturnToMenu_x && downX < bmpReturnToMenu_x + bmpReturnToMenuPressed.getWidth() &&
                            downY > bmpReturnToMenu_y && downY < bmpReturnToMenu_y + bmpReturnToMenuPressed.getHeight()) {
                        returnToMenu_isPressed = false;
                        isPause = false;//将绘图透明度置为不透明（返回游戏）且不再绘制bmpReturnToMenu

                        ma.openOrCreateDatabase();
                        ma.insertDatas();

                        Message msg = new Message();
                        msg.what = WhatMessage.GOTO_MENUVIEW;
                        ma.myHandler.sendMessage(msg);
                    }
                }
            }
        }
        return true;
    }

    //检测方块是否与地图中的固定方块或围墙发生碰撞，是则返回true，否则返回false
    public boolean isCollide(int x, int y, int blockType, int turnState) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ((tetris.shapes[blockType][turnState][i * 4 + j] == 1 && (//与已经固定的方块重合
                        (map[x + j][y + i] == 1) || (map[x + j][y + i] == 2) ||
                                (map[x + j][y + i] == 3) || (map[x + j][y + i] == 4) ||
                                (map[x + j][y + i] == 5) || (map[x + j][y + i] == 6) || (map[x + j][y + i] == 7)
                )) || (tetris.shapes[blockType][turnState][i * 4 + j] == 1 && map[x + j][y + i] == 0)) {//与围墙重合
                    return true;
                }
            }
        }
        return false;
    }

    //将方块添加到地图中
    public void addBlock() {
        int a = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (tetris.shapes[tetris.blockType][tetris.turnState][a] == 1) {
                    map[tetris.x + j][tetris.y + i] = tetris.blockType + 1;//将方块类型(1~7)添加到地图中
                }
                a++;
            }
        }
    }

    //消行方法
    public void delLine() {
        int c = 0;//计数一行砖块数的变量
        int delLineNumForOnce = 0;//一次消的行数
        for (int i = 0; i < map_row_num-1; i++) {//从第一行到倒数第二行
            for (int j = 1; j < map_line_num-1; j++) {//从第二列到倒数第二列
                if (map[j][i] != 9) {
                    c += 1;
                    if (c == map_line_num - 2) {//满一行，则消行（整体下移一行）
                        delLineNum += 1;
                        delLineNumForOnce += 1;
                        for (int k = i; k > 0; k--) {
                            for (int l = 0; l < map_line_num; l++) {
                                map[l][k] = map[l][k - 1];
                            }
                        }
                    }
                }
            }
            c = 0;
        }
        //一次一行加一分、俩行加3分、三行加6分、四行加9分
        if (delLineNumForOnce == 1) {
            score += 1;
        } else if (delLineNumForOnce == 2) {
            score += 3;
            del2LineTimes += 1;
            isDelTwoLines = true;
        } else if (delLineNumForOnce == 3) {
            score += 6;
            delThreeLineTimes += 1;
            isDelThreeLines = true;
        } else if (delLineNumForOnce == 4) {
            score += 9;
            delFourLineTimes += 1;
            isDelFourLines = true;
        }
    }

    private class JudgeFingerState extends TimerTask {
        @Override
        public void run() {
            //长按指定时间后如手指仍为按下状态，则将方块停止下落时间改为100ms
            if (isDown) {
                dbt.stopTime = 100;
            }
        }
    }

}
