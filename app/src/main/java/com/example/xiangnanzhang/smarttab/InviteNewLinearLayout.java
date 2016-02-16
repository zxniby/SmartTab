package com.example.xiangnanzhang.smarttab;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * Created by XiangnanZhang on 4/02/16.
 */
public class InviteNewLinearLayout extends LinearLayout {
    private OverScroller mScroller;
    private int mYOffset;
    private boolean isInTop = false;
    private InviteNewXListView mList;

    public InviteNewLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public InviteNewLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InviteNewLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        mScroller = new OverScroller(context);
    }
    public void setmList(InviteNewXListView list){this.mList = list;}
    @Override
    public void computeScroll(){
        if(mScroller.computeScrollOffset())
        {
//            mScroller.getCurrY()
            scrollTo(0, mScroller.getCurrY());
            invalidate();
            Log.i("InviteNewLayout+CurrY", mScroller.getCurrY() + "");
        }
    }

    public void doScroll(int whereToScroll){
        if(mScroller.isFinished()){
            switch (whereToScroll){
                case InviteNewXListView.UP:
                    if(!isInTop){
                        mScroller.startScroll(0,0,0,mYOffset,1000);
                        isInTop = true;
                    }
                    break;
                case InviteNewXListView.DOWN:
                    if(isInTop){
                        mScroller.startScroll(0,mYOffset,0,-mYOffset,1000);
                        isInTop = false;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setmYOffset(int y){
        mYOffset = y;
    }



}
