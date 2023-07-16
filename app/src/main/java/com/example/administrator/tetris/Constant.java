package com.example.administrator.tetris;

/**
 * Created by Administrator on 2017/9/20.
 */
public class Constant {
    public static int SCREEN_WIDTH,SCREEN_HEIGHT;
    public static float MENU_BMP_SCALE_FACTOR;
    public static float ABOUT_BMP_SCALE_FACTOR;
    public static float SCORE_BMP_SCALE_FACTOR;//score与deleteline图片的缩放因子
    public static float PAUSE_BMP_SCALE_FACTOR;
    public static int BRICK_WIDTH;//每一个方块的宽度
    public static int BORDER_WIDTH;
    public static float RESUME_BMP_SCALE_FACTOR;
    //定义游戏状态常量
    public static final int GAME_MENU=0;//游戏菜单
    public static final int GAME_ABOUT=3;//游戏暂停
    public static final int GAMING=1;//游戏中
    public static final int GAME_RECORD=2;//游戏暂停
    public static int currentView;
    public static int TABLE_HEADER_DATE_WIDTH;
    public static int TABLE_HEADER_DEL2LINETIMES_WIDTH;
    public static int TABLE_HEADER_DELTHREELINETIMES_WIDTH;
    public static int TABLE_HEADER_DELFOURLINETIMES_WIDTH;
    public static int TABLE_HEADER_SCORE_WIDTH;
    public static int BMP_ADD_SCALE_FACTOR;

    public static void initConstant(int screenW,int screenH){
        SCREEN_WIDTH=screenW;
        SCREEN_HEIGHT=screenH;
        MENU_BMP_SCALE_FACTOR=0.7f;
        ABOUT_BMP_SCALE_FACTOR=0.4f;
        SCORE_BMP_SCALE_FACTOR=0.4f;
        BRICK_WIDTH=SCREEN_WIDTH/15;
        BORDER_WIDTH=BRICK_WIDTH/14;
        PAUSE_BMP_SCALE_FACTOR=0.2f;
        RESUME_BMP_SCALE_FACTOR=0.9f;
        TABLE_HEADER_DATE_WIDTH=SCREEN_WIDTH/3;
        TABLE_HEADER_DEL2LINETIMES_WIDTH=(SCREEN_WIDTH*2/3)/4;
        TABLE_HEADER_DELTHREELINETIMES_WIDTH=(SCREEN_WIDTH*2/3)/4;
        TABLE_HEADER_DELFOURLINETIMES_WIDTH=(SCREEN_WIDTH*2/3)/4;
        TABLE_HEADER_SCORE_WIDTH=(SCREEN_WIDTH*2/3)/4;
        BMP_ADD_SCALE_FACTOR=2;
    }
}
