package com.example.xiangnanzhang.smarttab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * Created by XiangnanZhang on 4/02/16.
 */
public class InviteNewLinearLayout extends LinearLayout {
    private OverScroller mScroller;
    private int mYOffset;
    private boolean isInTop = false;
    private XListView mList;
    private ViewPager mViewPager;
    private VelocityTracker mVelocityTracker;
    private int mTopViewHeight;
    private View mTop,mNav;
    private float mMaximumVelocity,mMinimumVelocity;

    private boolean isInControl = false, isTopHidden = false, mDragging = false;
    private float mLastY;
    private int mTouchSlop;


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
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w,h,oldw,oldh);
        mTopViewHeight = mTop.getMeasuredHeight();
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();
        mTop = findViewById(R.id.tv_data);
        mNav = findViewById(R.id.my_smartlayout);
        mViewPager = (ViewPager)findViewById(R.id.my_viewpager);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heighMeasureSpec){
        super.onMeasure(widthMeasureSpec,heighMeasureSpec);
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        params.height = getMeasuredHeight() - mNav.getMeasuredHeight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        int eID = ev.getAction();
        float y = ev.getY();
        switch (eID){
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                getCurrentListView();
                View fItem = mList.getChildAt(mList.getFirstVisiblePosition());
                if(!isInControl && fItem != null && fItem.getTop() == 0 && isTopHidden && dy >0){
                    isInControl = true;
                    MotionEvent ev2 = MotionEvent.obtain(ev);
                    ev2.setAction(MotionEvent.ACTION_CANCEL);
                    dispatchTouchEvent(ev2);
                    ev2.setAction(MotionEvent.ACTION_DOWN);
                    return dispatchTouchEvent(ev2);
                }
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
        int eID = ev.getAction();
        float y = ev.getY();
        switch (eID){
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
//                Log.i("mLastY",mLastY+"");
//                Log.i("Y",y+"");
                if(Math.abs(dy) > mTouchSlop){
                    mDragging = true;
                    getCurrentListView();
                    View fItem = mList.getChildAt(mList.getFirstVisiblePosition());
//                    View fItem = mList.getChildAt(0);
                    if(!isTopHidden){
                        if(fItem != null && fItem.getTop() != 0 && dy >0){break;}
                        else{
                            initVelocityTrackerIfNotExists();
                            mVelocityTracker.addMovement(ev);
                            mLastY = y;
                            return true;
                        }
                    }else{
                        if(fItem != null && fItem.getTop()==0 && dy > 0){
                            initVelocityTrackerIfNotExists();
                            mVelocityTracker.addMovement(ev);
                            mLastY = y;
                            return true;
                        }
                    }


//                    if(!isTopHidden && fItem != null && fItem.getTop() != 0 && dy >0){}
//                    if(!isTopHidden || (isTopHidden && fItem != null && fItem.getTop()==0 && dy > 0)){
//                        initVelocityTrackerIfNotExists();
//                        mVelocityTracker.addMovement(ev);
//                        mLastY = y;
//                        return true;
//                    }

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDragging = false;
                recycleVelocityTracker();
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        int eId = ev.getAction();
        float y = ev.getY();
        switch (eId){
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mLastY;
                Log.i("mLastY",mLastY+"");
                Log.i("Y",y+"");
                if(!mDragging && dy > mTouchSlop){
                    mDragging = true;
                }
                if(mDragging){
                    scrollBy(0, (int)-dy);
                    if(getScrollY() == mTopViewHeight && dy < 0){
                        ev.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(ev);
                        isInControl = false;
                    }
                }
                mLastY = y;

                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                recycleVelocityTracker();
                if(!mScroller.isFinished())
                    mScroller.abortAnimation();
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if(Math.abs(velocityY) > mMinimumVelocity){
                    fling(-velocityY);
                }
                recycleVelocityTracker();
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void fling(int velocity){
        mScroller.fling(0,getScrollY(),0,velocity,0,0,0,mTopViewHeight);
    }

    private void initVelocityTrackerIfNotExists(){
        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker(){
        if(mVelocityTracker  != null){
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    @Override
    public void scrollTo(int x, int y){
        if(y < 0) y = 0;
        if(y > mTopViewHeight) y = mTopViewHeight;
        if(y != getScrollY()) super.scrollTo(x,y);
        isTopHidden = getScrollY() == mTopViewHeight;
        int i = getScrollY();
        Log.i("getScrollY",getScrollY()+"");
        Log.i("isTopHidden",isTopHidden+"");
    }

    @Override
    public void computeScroll(){
        if(mScroller.computeScrollOffset())
        {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
            Log.i("InviteNewLayout+CurrY", mScroller.getCurrY() + "");
        }
    }



    public void setmYOffset(int y){
        mYOffset = y;
    }

    private void getCurrentListView(){
        int currentItem = mViewPager.getCurrentItem();
        PagerAdapter pagerAdapter = mViewPager.getAdapter();
        Fragment fragmentItem = (Fragment)pagerAdapter.instantiateItem(mViewPager,currentItem);
        mList = (XListView)fragmentItem.getView().findViewById(R.id.ixl_list);


//        if(pagerAdapter instanceof FragmentPagerAdapter){
//            FragmentPagerAdapter fpadapter = (FragmentPagerAdapter)pagerAdapter;
//            Fragment fragmentItem = (Fragment)fpadapter.instantiateItem(mViewPager,currentItem);
//            mList = (XListView)fragmentItem.getView().findViewById(R.id.ixl_list);
//        }else if(pagerAdapter instanceof FragmentStatePagerAdapter){
//            FragmentPagerAdapter fpadapter = (FragmentPagerAdapter)pagerAdapter;
//            Fragment fragmentItem = (Fragment)fpadapter.instantiateItem(mViewPager,currentItem);
//            mList = (XListView)fragmentItem.getView().findViewById(R.id.ixl_list);
//        }
    }


}
