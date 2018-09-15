package com.example.a1.mygame2048;

import android.content.Context;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 设置Item（小方块）初始化
 */
public class MyGameItem extends FrameLayout {

    private int mCardShowNum;//Item显示数字的大小
    private TextView mTvNum;//现实的数字
    private LayoutParams params;

    public MyGameItem(Context context,int cardShowNum)
    {
        super(context);
        this.mCardShowNum = cardShowNum;
        initCardItem();
    }

    /**
     * 初始化Item
     */
    public void initCardItem()
    {
        //setBackgroundColor(Color.GRAY);
        mTvNum = new TextView(getContext());
        setNum(mCardShowNum);

        //根据格子设置字体大小
        int gameLines = MyConfig.mSp.getInt(MyConfig.KEY_GAME_LINES,4);//初始化为4x4
        if(gameLines == 4)
        {
            mTvNum.setTextSize(35);
        }
        else if(gameLines == 5)
        {
            mTvNum.setTextSize(25);
        }
        else
        {
            mTvNum.setTextSize(20);
        }

        TextPaint mPaint = mTvNum.getPaint();
        mPaint.setFakeBoldText(true);//将字体设置为粗体
        mTvNum.setGravity(Gravity.CENTER);//居中

        params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        params.setMargins(5,5,5,5);
        addView(mTvNum,params);
    }

    /**
     * 初始化显示数字
     * @param cardShowNum 显示的数字
     */
    public void setNum(int cardShowNum)
    {
        this.mCardShowNum = cardShowNum;
        if(cardShowNum == 0)
        {
            mTvNum.setText("");
        }
        else
        {
            mTvNum.setText(" " + cardShowNum);
        }

        //设置颜色
        switch(cardShowNum)
        {
            case 0:
                mTvNum.setBackgroundColor(0x00000000);
                break;
            case 2:
                mTvNum.setBackgroundColor(0xffeee5db);
                break;
            case 4:
                mTvNum.setBackgroundColor(0xffeee0ca);
                break;
            case 8:
                mTvNum.setBackgroundColor(0xfff2c17a);
                break;
            case 16:
                mTvNum.setBackgroundColor(0xfff59667);
                break;
            case 32:
                mTvNum.setBackgroundColor(0xfff68c6f);
                break;
            case 64:
                mTvNum.setBackgroundColor(0xfff66e3c);
                break;
            case 128:
                mTvNum.setBackgroundColor(0xffedcf74);
                break;
            case 256:
                mTvNum.setBackgroundColor(0xffedcc64);
                break;
            case 512:
                mTvNum.setBackgroundColor(0xffedc854);
                break;
            case 1024:
                mTvNum.setBackgroundColor(0xffedc54f);
                break;
            case 2048:
                mTvNum.setBackgroundColor(0xffedc32e);
                break;
            default:
                mTvNum.setBackgroundColor(0xff3c4a34);
                break;
        }
    }

    public int getNum()
    {
        return mCardShowNum;
    }

    public View getItemView()
    {
        return mTvNum;
    }
}
