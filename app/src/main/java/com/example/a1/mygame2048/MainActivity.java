package com.example.a1.mygame2048;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button startButton;//开始按钮
    private Button revertButton;//撤回按钮
    private Button optionsButton;//控制面板按钮
    private MyGameView myGameView;//游戏面板
    private static MainActivity mGame;//Activity引用
    private TextView mTvScore;//记录分数
    private TextView mTvHighScore;//记录历史分数
    private TextView mTvGoal;//记录目标
    private int mHighScore;//记录最高分
    private int mGoal;//记录目标

    public MainActivity()
    {
        mGame = this ;
    }

    public static MainActivity getActivity()
    {
        return mGame;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        myGameView = new MyGameView(this);
        FrameLayout frameLayout = (FrameLayout)findViewById(R.id.game_panel);
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.game_panel_rl);
        relativeLayout.addView(myGameView);
    }

    public void initView()
    {
        startButton = (Button)findViewById(R.id.btn_restart);
        revertButton = (Button)findViewById(R.id.btn_revert);
        optionsButton = (Button)findViewById(R.id.btn_option);
        mTvScore = (TextView)findViewById(R.id.score);
        mTvHighScore = (TextView)findViewById(R.id.record);
        mTvGoal = (TextView)findViewById(R.id.tv_goal);
        startButton.setOnClickListener(this);
        revertButton.setOnClickListener(this);
        optionsButton.setOnClickListener(this);

        mGoal = MyConfig.mSp.getInt(MyConfig.KEY_GAME_GOAL,2048);
        mHighScore = MyConfig.mSp.getInt(MyConfig.KEY_HIGH_SCORE,0);
        mTvGoal.setText("" + mGoal);
        mTvHighScore.setText("" + mHighScore);
        mTvScore.setText("0");

        setScre(0,0);
        setScre(mHighScore,1);
    }

    /**
     * 修改分数
     * @param score 分数
     * @param flag 0为当前得分，1为最高分
     */
    public void setScre(int score,int flag)
    {
        switch(flag)
        {
            case 1:
                //修改最高分
                mTvHighScore.setText("" + score);
                break;
            case 0:
                //修改分数
                mTvScore.setText("" + score);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.btn_restart:
                myGameView.startGame();
                setScre(0,0);
                break;
            case R.id.btn_option:
                Intent intent = new Intent(MainActivity.this,OptionActivity.class);
                startActivityForResult(intent,0);
            case R.id.btn_revert:
                myGameView.revertGame();
                break;
            default:
                break;
        }
    }

    /**
     * 获取设置界面的信息
     * @param requestCode 请求码，startActivity传递过去的值
     * @param resultCode 结果码，标注由哪个Activity传递回来
     * @param data 数据
     */
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            mGoal = MyConfig.mSp.getInt(MyConfig.KEY_GAME_GOAL,2048);
            myGameView.startGame();
            setScre(0,0);
            mTvGoal.setText("" + mGoal);
            getHighScore();
        }
    }

    /**
     * 程序异常退出时保存数据
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("不要离开我QAQ").setPositiveButton("忍痛离开",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface it,int which)
            {
                finish();
            }
        }).setNegativeButton("继续游戏", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface it, int which) {
                it.dismiss();
            }
        }).create().show();
    }

    public void getHighScore()
    {
        int highScore = MyConfig.mSp.getInt(MyConfig.KEY_HIGH_SCORE,0);
        setScre(highScore,1);
    }

    public void setGoal(int num)
    {
        mTvGoal.setText(String.valueOf(num));
    }
}
