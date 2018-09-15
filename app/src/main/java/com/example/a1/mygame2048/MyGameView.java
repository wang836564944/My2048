package com.example.a1.mygame2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.GridLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MyGameView extends GridLayout implements View.OnTouchListener{

    private int mScoreHistory;//历史分数
    private int mGameLines;//行列数
    private int mHighScore;//最高分
    private MyGameItem[][] mGameMatrix;//游戏矩阵
    private int[][] mGameMatrixHistroy;//矩阵历史
    private ArrayList<Integer> mCalList;//Item集合
    private ArrayList<Point> mBlankList;//空白集合
    private int mKeyNum = -1;//item合并标志位
    private int mTarget = 2048;//目标分数

    private int startX;//初始横坐标
    private int startY;//初始纵坐标
    private int endX;//截止横坐标
    private int endY;//截止纵坐标
    public MyGameView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        initMarix();
    }
    public MyGameView(Context context)
    {
        super(context);
        initMarix();
    }
    /**
     * 初始化矩阵
     */
    private void initMarix()
    {
        Resources resources = getContext().getResources();
        Drawable drawable = resources.getDrawable(R.drawable.lalala);
        setBackgroundDrawable(drawable);
        removeAllViews();
        mScoreHistory = 0;
        MyConfig.score = 0;
        MyConfig.mGameLines = MyConfig.mSp.getInt(MyConfig.KEY_GAME_LINES,4);
        MyConfig.mGameGoal = MyConfig.mSp.getInt(MyConfig.KEY_GAME_GOAL,2048);
        mGameLines = MyConfig.mGameLines;
        mGameMatrix = new MyGameItem[mGameLines][mGameLines];
        mGameMatrixHistroy = new int[mGameLines][mGameLines];

        mCalList = new ArrayList<>();
        mBlankList = new ArrayList<>();
        mHighScore = MyConfig.mSp.getInt(MyConfig.KEY_HIGH_SCORE,0);
        setColumnCount(mGameLines);//设置行数
        setRowCount(mGameLines);//设置列数
        setOnTouchListener(this);

        //设置Item大小，获取屏幕分辨率
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        display.getMetrics(metrics);
        MyConfig.mItemSize = metrics.widthPixels/MyConfig.mGameLines;

        initGameView(MyConfig.mItemSize);
    }

    /**
     * 处理滑动事件
     * @param v view
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                //触摸屏幕时
                saveHistroyMatrix();
                startX = (int) event.getX();
                startY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                //离开屏幕时
                endX = (int) event.getX();
                endY = (int) event.getY();
                judgeMove(endX - startX,endY - startY);
                if(isMoved())
                {
                    addRandomNum();
                    MainActivity.getActivity().setScre(MyConfig.score,0);
                }
                checkCompleted();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 判断滑动方向
     * @param offX X偏移量
     * @param offY Y偏移量
     */
    public void judgeMove(int offX,int offY)
    {
        int density = getDeviceDensity();
        int slideDis = 5 * density;//滑动边界
        int maxDis = 200 * density;//滑动限制
        boolean isMove = (Math.abs(offX) > slideDis || Math.abs(offY) > slideDis) &&
                         (Math.abs(offX) < maxDis) && (Math.abs(offY) < maxDis);
        boolean moveTooMuch = (Math.abs(offX) > maxDis) || (Math.abs(offY) > maxDis);
        if(isMove && !moveTooMuch)
        {
            if(Math.abs(offX) > Math.abs(offY))
            {
                //横向移动
                if(offX > slideDis)
                {
                    moveToRight();
                }
                else
                {
                    moveToLeft();
                }
            }
            else
            {
                //纵向移动
                if(offY > slideDis)
                {
                    moveToBottom();
                }
                else
                {
                    moveToTop();
                }
            }
        }
        //这里可以加一个back door
    }

    /**
     * 向右滑
     */
    public void moveToRight()
    {
        for(int i = mGameLines - 1;i >= 0;i--)
        {
            for(int j = mGameLines - 1;j >= 0;j--)
            {
                int currentNum = mGameMatrix[i][j].getNum();
                if(currentNum != 0)
                {
                    if(mKeyNum == -1)
                    {
                        mKeyNum = currentNum;
                    }
                    else
                    {
                        if(mKeyNum == currentNum)
                        {
                            mCalList.add(mKeyNum * 2);
                            MyConfig.score = MyConfig.score + mKeyNum * 2;
                            mKeyNum = -1;
                        }
                        else
                        {
                            mCalList.add(mKeyNum);
                            mKeyNum = currentNum;
                        }
                    }
                }
                else
                {
                    continue;
                }
            }
            if(mKeyNum != -1)
            {
                mCalList.add(mKeyNum);
            }

            for(int j = 0;j < mGameLines - mCalList.size();j++)
            {
                mGameMatrix[i][j].setNum(0);
            }
            int index = mCalList.size() - 1;
            for(int m = mGameLines - mCalList.size();m < mGameLines;m++)
            {
                mGameMatrix[i][m].setNum(mCalList.get(index));
                index--;
            }

            mKeyNum = -1;
            mCalList.clear();
            index = 0;
        }
    }

    /**
     * 向左滑
     */
    public void moveToLeft()
    {
        for(int i = 0;i < mGameLines;i++)
        {
            for(int j = 0;j < mGameLines;j++)
            {
                int currentNum = mGameMatrix[i][j].getNum();
                if(currentNum != 0)
                {
                    if(mKeyNum == -1)
                    {
                        mKeyNum = currentNum;
                    }
                    else
                    {
                        if(currentNum == mKeyNum)
                        {
                            mCalList.add(mKeyNum * 2);
                            MyConfig.score = MyConfig.score + mKeyNum * 2;
                            mKeyNum = -1;
                        }
                        else
                        {
                            mCalList.add(mKeyNum);
                            mKeyNum = currentNum;
                        }
                    }
                }
                else
                {
                    continue;
                }
            }
            if(mKeyNum != -1)
            {
                mCalList.add(mKeyNum);
            }
            for(int j = 0;j < mCalList.size();j++)
            {
                mGameMatrix[i][j].setNum(mCalList.get(j));
            }
            for(int m = mCalList.size();m < mGameLines;m++)
            {
                mGameMatrix[i][m].setNum(0);
            }
            mKeyNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 向上滑
     */
    public void moveToTop()
    {
        for(int i = 0;i < mGameLines;i++)
        {
            for(int j = 0;j < mGameLines;j++)
            {
                int currentNum = mGameMatrix[j][i].getNum();
                if(currentNum != 0)
                {
                    if(mKeyNum == -1)
                    {
                        mKeyNum = currentNum;
                    }
                    else
                    {
                        if(currentNum == mKeyNum)
                        {
                            mCalList.add(mKeyNum * 2);
                            MyConfig.score = MyConfig.score + mKeyNum * 2;
                            mKeyNum = -1;
                        }
                        else
                        {
                            mCalList.add(mKeyNum);
                            mKeyNum = currentNum;
                        }
                    }
                }
                else
                {
                    continue;
                }
            }
            if(mKeyNum != -1)
            {
                mCalList.add(mKeyNum);
            }
            for(int j = 0;j < mCalList.size();j++)
            {
                mGameMatrix[j][i].setNum(mCalList.get(j));
            }
            for(int m = mCalList.size();m < mGameLines;m++)
            {
                mGameMatrix[m][i].setNum(0);
            }

            mKeyNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 向下滑
     */
    public void moveToBottom()
    {
        for(int i = mGameLines - 1;i >= 0;i--)
        {
            for(int j = mGameLines - 1;j >= 0;j--)
            {
                int currentNum = mGameMatrix[j][i].getNum();
                if(currentNum != 0)
                {
                    if(mKeyNum == -1)
                    {
                        mKeyNum = currentNum;
                    }
                    else
                    {
                        if(mKeyNum == currentNum)
                        {
                            mCalList.add(mKeyNum * 2);
                            MyConfig.score = MyConfig.score + mKeyNum * 2;
                            mKeyNum  = -1;
                        }
                        else
                        {
                            mCalList.add(mKeyNum);
                            mKeyNum = currentNum;
                        }
                    }
                }
                else
                {
                    continue;
                }
            }
            if(mKeyNum != -1)
            {
                mCalList.add(mKeyNum);
            }
            for(int j = 0;j < mGameLines - mCalList.size();j++)
            {
                mGameMatrix[j][i].setNum(0);
            }
            int index = mCalList.size() - 1;
            for(int m = mGameLines - mCalList.size();m < mGameLines;m++)
            {
                mGameMatrix[m][i].setNum(mCalList.get(index));
                index--;
            }

            index = 0;
            mKeyNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 获取屏幕密度和像素比例
     * @return 屏幕密度
     */
    public int getDeviceDensity()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        display.getMetrics(metrics);
        return (int)metrics.density;
    }
    /**
     * 初始化View
     * @param size Item大小
     */
    public void initGameView(int size)
    {
        removeAllViews();
        MyGameItem gameItem;

        for(int i =0;i < mGameLines;i++)
        {
            for(int j = 0;j < mGameLines;j++)
            {
                gameItem = new MyGameItem(getContext(),0);
                addView(gameItem,size,size);
                mGameMatrix[i][j] = gameItem;
                mBlankList.add(new Point(i,j));
            }
        }

        addRandomNum();
        addRandomNum();
    }

    /**
     * 添加随机数字
     */
    public void addRandomNum()
    {
        getBlanks();
        if(mBlankList.size() > 0)
        {
            int randomNum = (int)(Math.random() * mBlankList.size());
            Point randomPoint = mBlankList.get(randomNum);
            mGameMatrix[randomPoint.x][randomPoint.y].setNum(Math.random() > 0.2d ? 2:4);
            animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
        }
    }

    public void getBlanks()
    {
        mBlankList.clear();
        for(int i = 0;i < mGameLines;i++)
        {
            for(int j = 0;j < mGameLines;j++)
            {
                if(mGameMatrix[i][j].getNum() == 0)
                {
                    mBlankList.add(new Point(i,j));
                }
            }
        }
    }

    /**
     * 保存历史矩阵
     */
    public void saveHistroyMatrix()
    {
        mScoreHistory = MyConfig.score;
        for(int i = 0;i < mGameLines;i++)
        {
            for(int j = 0;j < mGameLines;j++)
            {
                mGameMatrixHistroy[i][j] = mGameMatrix[i][j].getNum();
            }
        }
    }

    /**
     * 判断是否移动
     * @return true移动,false未移动
     */
    public boolean isMoved()
    {
        for(int i = 0;i < mGameLines;i++)
        {
            for(int j = 0;j < mGameLines;j++)
            {
                if(mGameMatrixHistroy[i][j] != mGameMatrix[i][j].getNum())
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断游戏进程
     * @return 0结束，1正常，2胜利
     */
    public int checkNum()
    {
        getBlanks();
        if(mBlankList.size() == 0)
        {
            for(int i = 0;i < mGameLines;i++)
            {
                for(int j = 0;j < mGameLines;j++)
                {
                    if(j < mGameLines - 1)
                    {
                        if(mGameMatrix[i][j].getNum() == mGameMatrix[i][j + 1].getNum())
                        {
                            return 1;
                        }
                    }
                    if(i < mGameLines - 1)
                    {
                        if(mGameMatrix[i][j].getNum() == mGameMatrix[i + 1][j].getNum())
                        {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }
        for(int i = 0;i < mGameLines;i++)
        {
            for(int j = 0;j < mGameLines;j++)
            {
                if(mGameMatrix[i][j].getNum() == mTarget)
                {
                    return 2;
                }
            }
        }
        return 1;
    }

    /**
     * 判断是否结束
     */
    public void checkCompleted()
    {
        int result = checkNum();
        if(result == 0)
        {
            //游戏结束
            if(MyConfig.score > mHighScore)
            {
                SharedPreferences.Editor editor = MyConfig.mSp.edit();
                editor.putInt(MyConfig.KEY_HIGH_SCORE,MyConfig.score);
                editor.apply();
                MainActivity.getActivity().setScre(MyConfig.score,1);
                MyConfig.score = 0;
            }
            if (MyConfig.score>mHighScore){
                SharedPreferences.Editor edit=MyConfig.mSp.edit();
                edit.putInt(MyConfig.SP_HIGH_SCORE,MyConfig.score);
                edit.apply();
                MainActivity.getActivity().setScre(MyConfig.score,1);
                MyConfig.score=0;
            }
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("您已经输了QAQ").setPositiveButton("再给你一次机会",new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface,int i)
                {
                    startGame();
                }
            }).create().show();
            MyConfig.score = 0;
        }
        else if(result == 2)
        {
            //游戏成功
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("挑战成功").setPositiveButton("重新游戏",new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface,int i)
                {
                    startGame();
                }
            }).setNegativeButton("太简单，再来10个",new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface,int i)
                {
                    SharedPreferences.Editor editor = MyConfig.mSp.edit();
                    if(mTarget == 2048)
                    {
                        editor.putInt(MyConfig.KEY_GAME_GOAL,4096);
                        mTarget = 4096;
                        MainActivity.getActivity().setGoal(4096);
                    }
                    else if(mTarget == 4096)
                    {
                        editor.putInt(MyConfig.KEY_GAME_GOAL,8192);
                        mTarget = 8192;
                        MainActivity.getActivity().setGoal(8192);
                    }
                    else
                    {
                        editor.putInt(MyConfig.KEY_GAME_GOAL,8192);
                        mTarget = 8192;
                        MainActivity.getActivity().setGoal(8192);
                    }
                    editor.apply();
                }
            }).create().show();
            MyConfig.score = 0;
        }

    }

    /**
     * 开始游戏
     */
    public void startGame()
    {
        initMarix();
        initGameView(MyConfig.mItemSize);
    }

    /**
     * 撤销上次移动
     */
    public void revertGame()
    {
        int sum = 0;
        for(int i [] : mGameMatrixHistroy)
        {
            for(int j : i)
            {
                sum += j;
            }
        }
        if(sum != 0)
        {
            MainActivity.getActivity().setScre(mScoreHistory,0);
            MyConfig.score = mScoreHistory;
            for(int i = 0;i < mGameLines;i++)
            {
                for(int j = 0;j < mGameLines;j++)
                {
                    mGameMatrix[i][j].setNum(mGameMatrixHistroy[i][j]);
                }
            }
        }
        else
        {
            Toast.makeText(getContext(),"不能再退喽~",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 动画效果
     * @param item
     */
    private void animCreate(MyGameItem item)
    {
        ScaleAnimation animation = new ScaleAnimation(0.1f,1,0.1f,1,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(1000);
        item.setAnimation(null);
        item.getItemView().startAnimation(animation);
    }
}


