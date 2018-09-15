package com.example.a1.mygame2048;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OptionActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mBtnGameLines;
    private Button backBtn;//返回按钮
    private Button doneBtn;//确定按钮
    private TextView mTvGoal;//目标分数
    private String [] mGameLinesList;
    private String [] mGameGoaList;
    private String [] mGameStranded;//游戏难度
    private AlertDialog.Builder dialog;
    private String mStandard;//当前难度
    private String mLines;//当前行数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        initView();
    }

    public void initView()
    {
        mTvGoal = (TextView)findViewById(R.id.tv_goal);
        backBtn = (Button)findViewById(R.id.btn_back);
        doneBtn = (Button)findViewById(R.id.btn_done);
        mBtnGameLines = (Button)findViewById(R.id.btn_gamelines);
        backBtn.setOnClickListener(this);
        doneBtn.setOnClickListener(this);
        mBtnGameLines.setOnClickListener(this);

        mTvGoal.setText(" " + MyConfig.mSp.getInt(MyConfig.KEY_GAME_GOAL,2048));
        mGameLinesList = new String[]{"4","5","6"};
        mGameStranded = new String[]{"简单","一般","困难"};
        mGameGoaList = new String[]{"2048","4096","8192"};
    }

    /**
     * 保存设置
     */
    public void saveOption()
    {
        SharedPreferences.Editor editor = MyConfig.mSp.edit();
        try
        {
            editor.putInt(MyConfig.KEY_GAME_LINES,Integer.parseInt(mLines));
            editor.putInt(MyConfig.KEY_GAME_GOAL,Integer.parseInt(mTvGoal.getText().toString()));
        }
        catch (Exception e)
        {
            Toast.makeText(OptionActivity.this,"你没有改变游戏难度哦~",Toast.LENGTH_SHORT).show();
        }
        finally
        {
            editor.commit();
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId()){
            case R.id.btn_gamelines:
                dialog=new AlertDialog.Builder(this);
                dialog.setTitle("选择难度");
                dialog.setItems(mGameStranded, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mBtnGameLines.setText(mGameStranded[i]);
                        mStandard=mGameStranded[i];
                        mLines=mGameLinesList[i];
                        mTvGoal.setText(mGameGoaList[i]);
                    }
                }).create().show();
                break;
            case R.id.tv_goal:
                dialog = new AlertDialog.Builder(this);
                dialog.setTitle("设置完成游戏目标分数");
                dialog.setItems(mGameGoaList,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTvGoal.setText(mGameGoaList[which]);
                            }
                        });
                dialog.create().show();
                break;
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_done:
                saveOption();
                setResult(RESULT_OK);
                this.finish();
                break;
            default:
                break;
        }
    }
}
