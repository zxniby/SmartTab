package com.example.xiangnanzhang.smarttab;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.OverScroller;
import android.widget.ScrollView;

/**
 * Created by XiangnanZhang on 3/02/16.
 */
public class InviteNewScrollView extends ScrollView {

    private float mTouchSlop;
    private float lastY;
    private SmartTabLayout mTabLayout;
    private XListView mXListView;
    private ViewPager mViewPager;
    private float mStateBarHeight;


    public InviteNewScrollView(Context context) {
        super(context);
        init(context);
    }

    public InviteNewScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InviteNewScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    public void setTabLayout(SmartTabLayout stl){
        this.mTabLayout = stl;
    }

    public void setXListView(XListView xlv){
        this.mXListView = xlv;
    }

    public void setViewPager(ViewPager vp){
        this.mViewPager = vp;
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mStateBarHeight = getStatusBarHeight();
    }

    /*获取statusBar高度*/
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private float lastX;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        int actionID = event.getAction();
        float y = event.getY();
        float x = event.getX();
        switch (actionID){
            case MotionEvent.ACTION_DOWN:
                lastY = y;
                lastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - lastY;
                float dx = x - lastX;
                lastY = y;
                lastX = x;
                int[] tabLoc = new int[2];
                if(Math.abs(dy) > Math.abs(dx)){
                    if(Math.abs(dy)>mTouchSlop){
                    /*上滑*/
                        if(dy < 0){
                            if(mTabLayout != null){
                                mTabLayout.getLocationOnScreen(tabLoc);
                                if((tabLoc[1]-mStateBarHeight)>0 && mXListView.getFirstVisiblePosition()==0){
                                    Log.i("InviteNewScrollView","up..up..up..up");
                                    return true;
                                }


                            }
                        }
                    /*下滑*/
                        if(dy > 0){
                            if(mTabLayout != null && mViewPager != null && mXListView != null){
                                mTabLayout.getLocationOnScreen(tabLoc);
                                if(mXListView.getFirstVisiblePosition()==0){
                                    int[] fItemLoc = new int[2];
                                    int[] vpLoc = new int[2];
                                    mXListView.getChildAt(0).getLocationOnScreen(fItemLoc);
                                    mViewPager.getLocationOnScreen(vpLoc);
                                    if(fItemLoc[1] == (vpLoc[1]+200)){
                                        Log.i("InviteNewScrollView","down..down..down..down");
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastX = 0;
                lastY = 0;
                break;

        }

        return super.onInterceptTouchEvent(event);
    }
//
//    @Override
//    public void onTouchEvent(){
//
//    }

}
