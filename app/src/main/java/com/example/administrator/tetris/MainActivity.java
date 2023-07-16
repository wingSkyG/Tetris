package com.example.administrator.tetris;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static int screenW, screenH;
    public Resources res;
    private MenuView menuView;
    public GamingView gamingView;
    private GameAboutView gameAboutView;
    private GameRecordView gameRecordView;
    private static int initTime=0;

    private SQLiteDatabase sld;
    private Cursor result;
    private SimpleAdapter simpleAdapter=null;
    private int currentPage=1;
    private int lineSize=10;//每一页显示的记录数目
    private int numOfAllRecords=0;//数据库中存有的记录总数目
    private int lastItem=0;
    private int count=0;//数据库中的记录数
    private int numOfPage=1;//按listView显示数据库的总页数
    private ListView dataTable_body_list=null;
    private List<Map<String,Object>> all=null;

    public Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case WhatMessage.GOTO_GAMINGVIEW:
                    gotoGamingView();
                    break;
                case WhatMessage.GOTO_GAMEABOUT:
                    gotoGameAboutView();
                    break;
                case WhatMessage.GOTO_MENUVIEW:
                    gotoMenuView();
                    break;
                case WhatMessage.GOTO_GAMERECORD:
                    gotoGameRecordView();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res=this.getResources();
        screenH=this.getResources().getDisplayMetrics().heightPixels;
        screenW=this.getResources().getDisplayMetrics().widthPixels;
        //第一次游戏将常量初始化
        if(initTime==0){
            Constant.initConstant(screenW,screenH);
        }
        gotoMenuView();
    }

    private void gotoMenuView(){
        switch(Constant.currentView){
            case Constant.GAMING:
                gamingView.stopAllThreads();
                gamingView.tetris.initLocation();
                break;
            case Constant.GAME_RECORD:
                gameRecordView.dgrt.setFlag(false);
                break;
            case Constant.GAME_ABOUT:
                gameAboutView.dgat.setFlag(false);
                break;
        }
        menuView = new MenuView(this);
        this.setContentView(menuView);
        Constant.currentView=Constant.GAME_MENU;
    }

    private void gotoGamingView(){
        if(gamingView==null)
            gamingView=new GamingView(this);
        this.setContentView(gamingView);
        Constant.currentView=Constant.GAMING;
    }

    private void gotoGameAboutView(){
        this.setContentView(R.layout.gameabout);
        RelativeLayout aboutLayout=(RelativeLayout)this.findViewById(R.id.about_layout);//GameAboutView布局
        if(gameAboutView==null) {
            gameAboutView = new GameAboutView(this);
        }else{
            //当从gameAboutView退出后再次进入gameAboutView时执行以下代码
            aboutLayout.removeView(gameAboutView);
            gameAboutView = new GameAboutView(this);
        }
        aboutLayout.addView(gameAboutView);//将gameAboutView添加到layout中

        TextView tv=new TextView(this);
        tv.setText(R.string.gameAbout);
        tv.setTextSize(17);
        tv.setWidth(screenW*3/5);
        tv.setHeight(screenH/2);
        RelativeLayout.LayoutParams tv_lp=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        aboutLayout.addView(tv,tv_lp);

        Constant.currentView=Constant.GAME_ABOUT;
    }

    private void gotoGameRecordView(){
        this.setContentView(R.layout.gamerecord);
        RelativeLayout recordLayout=(RelativeLayout)this.findViewById(R.id.record_layout);//GameRecordView布局
        addBackground(recordLayout);
        addTable(recordLayout);
        Constant.currentView=Constant.GAME_RECORD;
    }

    private void addBackground(RelativeLayout recordLayout){
        if(gameRecordView==null) {
            gameRecordView = new GameRecordView(this);
        }else{
            recordLayout.removeView(gameRecordView);
            gameRecordView = new GameRecordView(this);
        }
        recordLayout.addView(gameRecordView);
    }

    private void addTable(RelativeLayout recordLayout){
        LinearLayout dataView=new LinearLayout(this);//定义用于存储表头及表格的线性布局（数据表）
        dataView.setOrientation(LinearLayout.VERTICAL);

        //添加数据表表头
        LayoutInflater dataTable_header_inflater=LayoutInflater.from(this);
        TableLayout dataTable_header=(TableLayout)dataTable_header_inflater.
                inflate(R.layout.datatable_header,dataView,false);//将tablelayout实例化
        dataTable_header.setAlpha(0.8f);
        dataTable_header.setBackgroundColor(res.getColor(R.color.tableHeaderColor));

        TextView table_header_date=dataTable_header.findViewById(R.id.table_header_date);
        TextView table_header_del2LineTimes=dataTable_header.findViewById(R.id.table_header_del2LineTimes);
        TextView table_header_delThreeLineTimes=dataTable_header.findViewById(R.id.table_header_delThreeLineTimes);
        TextView table_header_delFourLineTimes=dataTable_header.findViewById(R.id.table_header_delFourLineTimes);
        TextView table_header_score=dataTable_header.findViewById(R.id.table_header_score);
        table_header_date.setWidth(Constant.TABLE_HEADER_DATE_WIDTH);
        table_header_del2LineTimes.setWidth(Constant.TABLE_HEADER_DEL2LINETIMES_WIDTH);
        table_header_delThreeLineTimes.setWidth(Constant.TABLE_HEADER_DELTHREELINETIMES_WIDTH);
        table_header_delFourLineTimes.setWidth(Constant.TABLE_HEADER_DELFOURLINETIMES_WIDTH);
        table_header_score.setWidth(Constant.TABLE_HEADER_SCORE_WIDTH);

        dataView.addView(dataTable_header,0);

        //添加数据表表体
        /*LayoutInflater dataTable_body_inflater=LayoutInflater.from(this);
        TableLayout dataTable_body=(TableLayout)dataTable_body_inflater.inflate(R.layout.datatable_body,dataView,false);
        TextView table_body_date=dataTable_body.findViewById(R.id.table_body_date);
        TextView table_body_del2LineTimes=dataTable_body.findViewById(R.id.table_body_del2LineTimes);
        TextView table_body_delThreeLineTimes=dataTable_body.findViewById(R.id.table_body_delThreeLineTimes);
        TextView table_body_delFourLineTimes=dataTable_body.findViewById(R.id.table_body_delFourLineTimes);
        TextView table_body_score=dataTable_body.findViewById(R.id.table_body_score);
        table_body_date.setWidth(Constant.TABLE_HEADER_DATE_WIDTH);
        table_body_del2LineTimes.setWidth(Constant.TABLE_HEADER_DEL2LINETIMES_WIDTH);
        table_body_delThreeLineTimes.setWidth(Constant.TABLE_HEADER_DELTHREELINETIMES_WIDTH);
        table_body_delFourLineTimes.setWidth(Constant.TABLE_HEADER_DELFOURLINETIMES_WIDTH);
        table_body_score.setWidth(Constant.TABLE_HEADER_SCORE_WIDTH);*///该段代码不起作用

        dataTable_body_list=new ListView(this);
        dataTable_body_list.setAlpha(0.8f);
        dataTable_body_list.setBackgroundColor(res.getColor(R.color.tableBodyColor));
        RelativeLayout.LayoutParams lv_lp=new RelativeLayout.LayoutParams(Constant.SCREEN_WIDTH, Constant.SCREEN_HEIGHT/3);
        lv_lp.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        //向表体中封装数据
        numOfAllRecords=getNumOfRecords();
        numOfPage=numOfAllRecords/lineSize;
        if(numOfAllRecords%lineSize!=0){
            numOfPage++;
        }
        all=new ArrayList<Map<String,Object>>();//实例化list集合
        all=searchDataAndPack(currentPage,lineSize);//取得List集合（包装查询到的当前页的数据）
        addDataIntoListView(all,dataTable_body_list);//为listView添加适配器（集合中的数据按照tablelayout格式显示）
        dataTable_body_list.setOnScrollListener(new MyOnScrollListener());
        dataView.addView(dataTable_body_list,1);

        recordLayout.addView(dataView,lv_lp);//将数据表添加到recordLayout中
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==4){//按下返回实体键
            switch(Constant.currentView){
                case Constant.GAME_MENU:
                    System.exit(0);//退出游戏
                    break;
                case Constant.GAMING:
                case Constant.GAME_ABOUT:
                case Constant.GAME_RECORD:
                    gotoMenuView();
                    break;
            }
        }
        return false;
    }

    public void openOrCreateDatabase(){
        try {
            sld=SQLiteDatabase.openDatabase("/data/data/com.example.administrator.tetris/mydb",
                    null,
                    SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY);
            String sql="Create table if not exists records"+
                    "("+
                    "date varchar(20),"+
                    "del2LineTimes integer,"+
                    "delThreeLineTimes integer,"+
                    "delFourLineTimes integer,"+
                    "score integer"+
                    ")";
            sld.execSQL(sql);
        } catch (SQLException e) {
            Toast.makeText(this,"数据库错误："+e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    public void insertDatas(){
        //SimpleDateFormat formatter=(SimpleDateFormat)SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.FULL,SimpleDateFormat.FULL,Locale.CHINA);
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String date=formatter.format(new Date());
        try {
            String sql="insert into records values('"+ date+"',"+
                    gamingView.del2LineTimes+","+gamingView.delThreeLineTimes+"," +
                    gamingView.delFourLineTimes+","+gamingView.score+");";
            sld.execSQL(sql);
            sld.close();
        } catch (SQLException e) {
            Toast.makeText(this,"数据库错误："+e.toString(),Toast.LENGTH_SHORT).show();
        }
        //deleteAllData();
    }

    private void deleteAllData(){
        openOrCreateDatabase();
        try {
            String sql="delete from records;";
            sld.execSQL(sql);
            sld.close();
        } catch (SQLException e) {
            Toast.makeText(this,"数据库错误："+e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private int getNumOfRecords(){//得到数据库中记录条数
        openOrCreateDatabase();
        count=0;
        String sql="select * from records;";
        Cursor result=sld.rawQuery(sql,null);
        for(result.moveToFirst();!result.isAfterLast();result.moveToNext()){
            count++;
        }
        sld.close();
        return count;
    }

    private List<Map<String,Object>> searchDataAndPack(int currentPage,int lineSize){
        List<Map<String,Object>> all=new ArrayList<Map<String,Object>>();
        String sql="select date,del2LineTimes,delThreeLineTimes,delFourLineTimes,score from " +
                "records limit ?,?;";//limit ?,?从第几条记录往后查询几条记录
        String selectionArgs[]=new String[]{
                String.valueOf((currentPage-1)*lineSize),
                String.valueOf(lineSize)
        };
        openOrCreateDatabase();
        Cursor result=sld.rawQuery(sql,selectionArgs);
        for(result.moveToFirst();!result.isAfterLast();result.moveToNext()){
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("date",result.getString(0));
            map.put("del2LineTimes",result.getInt(1));
            map.put("delThreeLineTimes",result.getInt(2));
            map.put("delFourLineTimes",result.getInt(3));
            map.put("score",result.getInt(4));
            all.add(map);
        }
        result.close();
        sld.close();
        return all;
    }

    private void addDataIntoListView(List<Map<String,Object>> all,ListView listView){
        simpleAdapter=new SimpleAdapter(this,
                all,
                R.layout.datatable_body,
                new String[]{"date","del2LineTimes","delThreeLineTimes","delFourLineTimes","score"},
                new int[]{R.id.table_body_date,R.id.table_body_del2LineTimes, R.id.table_body_delThreeLineTimes,
                        R.id.table_body_delFourLineTimes,R.id.table_body_score});
        listView.setAdapter(simpleAdapter);
    }

    private void appendData(){
        List<Map<String,Object>> newData=searchDataAndPack(currentPage,lineSize);
        all.addAll(newData);
        simpleAdapter.notifyDataSetChanged();
    }

    private class MyOnScrollListener implements AbsListView.OnScrollListener{

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if(MainActivity.this.lastItem==MainActivity.this.simpleAdapter.getCount()-1
                    &&MainActivity.this.currentPage<MainActivity.this.numOfPage
                    &&i== AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                MainActivity.this.currentPage++;
                MainActivity.this.dataTable_body_list.setSelection(MainActivity.this.lastItem);
                MainActivity.this.appendData();
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            MainActivity.this.lastItem=i+i1-1;
        }
    }
}
class WhatMessage{
    public static final int GOTO_MENUVIEW=0;
    public static final int GOTO_GAMINGVIEW=1;
    public static final int GOTO_GAMEABOUT=2;
    public static final int GOTO_GAMERECORD=3;
}
