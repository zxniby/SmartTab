package com.example.xiangnanzhang.smarttab;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;


/**
 * Created by XiangnanZhang on 2/02/16.
 */
public class InviteViewPager extends ViewPager {
    private XListView mXlistview;

    public InviteViewPager(Context context) {
        super(context);
        init(context);
    }

    public InviteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setListView(XListView lv){
        this.mXlistview = lv;
    }
    private float mTouchSlop,mFloatLastY,mFloatLastX;

    public void init(Context context){
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        int actionID = event.getAction();
        float floatY = event.getY();

        switch (actionID){
            case MotionEvent.ACTION_DOWN:
                mFloatLastY = floatY;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = mFloatLastY - floatY;
                if(Math.abs(dy)>0){
                    return false;
                }
                if(Math.abs(dy)>mTouchSlop && dy > 0){
                    if(mXlistview != null && mXlistview.getFirstVisiblePosition() == 0){
                        int[] fItemLoc = new int[2];
                        int[] lvLoc = new int[2];
                        mXlistview.getChildAt(0).getLocationOnScreen(fItemLoc);
                        mXlistview.getLocationOnScreen(lvLoc);
                        if(fItemLoc[1]==lvLoc[1]){
                            Log.i("InviteViewPager","Intercepted");;
                            return true;
                        }
                    }
                    break;
                }

        }
        return super.onInterceptTouchEvent(event);
    }

}
