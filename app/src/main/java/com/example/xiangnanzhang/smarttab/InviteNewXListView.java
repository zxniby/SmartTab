package com.example.xiangnanzhang.smarttab;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * Created by XiangnanZhang on 2/02/16.
 */
public class InviteNewXListView extends XListView {
    public final static int UP = 1, DOWN=2, STAY=0;
    private float mTouchSlop;
    public int whereToSroll = STAY;
    private float mFloatLastY;

    public InviteNewXListView(Context context) {
        super(context);
        init(context);
    }

    public InviteNewXListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InviteNewXListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    public void init(Context context){
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int actionID = event.getAction();
        float floatY = event.getY();
        switch (actionID){
            case MotionEvent.ACTION_DOWN:
                mFloatLastY = floatY;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = floatY - mFloatLastY;
                if(Math.abs(dy)> mTouchSlop){
                    /*下拉*/
                    if(dy >0){
                        if(this.getFirstVisiblePosition() == 0){
                            whereToSroll = DOWN;
                            Log.i("WhereToScroll","DOWN");
                        }else{
                            whereToSroll = STAY;
                            Log.i("WhereToScroll","STAY");
                        }
                    }
                    /*上滑*/
                    else{
                        if(this.getFirstVisiblePosition() == this.getChildCount()){
                            whereToSroll = STAY;
                            Log.i("WhereToScroll","STAY");
                        }else{
                            whereToSroll = UP;
                            Log.i("WhereToScroll","UP");
                        }
                    }
                }
                mFloatLastY = floatY;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
