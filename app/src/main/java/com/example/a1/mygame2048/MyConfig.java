package com.example.a1.mygame2048;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * 保存游戏配置
 */
public class MyConfig extends Application {
    public static SharedPreferences mSp;//SP对象
    public static int mGameGoal;//游戏目标
    public static int score = 0;//初始分
    public static int mGameLines;//行列数
    public static int mItemSize;//Item大小
    public static String SP_HIGH_SCORE = "SP_HighScore";
    public static String KEY_HIGH_SCORE = "KEY_HighScore";
    public static String KEY_GAME_LINES = "KEY_GameLines";
    public static String KEY_GAME_GOAL = "KEY_GameGoal";

    @Override
    public void onCreate()
    {
        super.onCreate();
        mSp = getSharedPreferences(SP_HIGH_SCORE,0);
        mGameGoal = mSp.getInt(KEY_GAME_GOAL,2048);
        mGameLines = mSp.getInt(KEY_GAME_LINES,4);
        mItemSize = 0;
    }
}
