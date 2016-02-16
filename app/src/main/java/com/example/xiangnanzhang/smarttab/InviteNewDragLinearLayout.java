package com.example.xiangnanzhang.smarttab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;

/**
 * Created by XiangnanZhang on 15/02/16.
 */
public class InviteNewDragLinearLayout extends LinearLayout {

    //控件当前所处在的位置
    public static final int LOC_BOTTOM = 1, LOC_TOP = 2;
    public static final int CALLBACK_WILLSCROLLCHANGE=1;//滑动方向改变触发标识
    public static final int CALLBACK_LOCCHANGE=2;//当前处在哪个滑动区域的回调更新
    public int mCurrentLoc = LOC_TOP;//当前处在的位置(顶部or底部)
    public boolean mBoolWillScrollTo=true;//当手指离开屏幕时,此控件的滑动方向(默认是往顶部的,因为刚开始控件是处于顶部)
    private OverScroller mScroller;//全局scroller
    private float mFloatLastY;//最后一次获取到的Y坐标
    private float mFloatFirstY;//第一次触摸屏幕的Y坐标
    private float mFloatOffestY;//Y坐标的偏移量
    private int mTouchSlop;//最小滑动触发阀值
    private boolean mBoolDragging = false;//是否处在拖动状态
    private int mMaximumVelocity;//最大手势速率
    private int mMinimumVelocity = 4000;//最小触发滚屏手势速率

    private VelocityTracker mVelocityTracker;
    /*第二页面里的内容是个list*/
    private ListView lv_content;
    /*第二页面里的内容是个scrollView*/
    private ScrollView sv_content;
    private ICallBack mCallBack;
    private ViewGroup mTabLayout;
    private View mBottomContent;
    private boolean has_lv_content = false, has_sv_content = false;

    private int[] tabOrgLoc = new int[2];

    public InviteNewDragLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public InviteNewDragLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mScroller = new OverScroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mFloatLastY = y;
                mFloatFirstY=y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mFloatLastY;
//                if(dy<0 && Math.abs(dy) > mTouchSlop){
//                    if(!isTabsInTop())
//                        return true;
//                    break;
//                }
                if (!mBoolDragging) {
                    if (Math.abs(dy) > mTouchSlop) {
                        /*如果当前是底部的页面*/
                        if (mCurrentLoc == LOC_BOTTOM) {
                            if(has_lv_content){
                                if(lv_content.getChildCount() == 0)
                                    mBoolDragging = true;
                                else if(isInTop() && dy >0)
                                    mBoolDragging = true;
                            }else if(has_sv_content){
                                if(isScrollToTop() && dy >0)
                                    mBoolDragging = true;
                            }else if(mTabLayout != null && dy > 0){
                                recordTabLoc(mBottomContent);
                                if(tabOrgLoc[1]==0)
                                    mBoolDragging = true;
                            }else{
                            /*既没有注册listview也没有scrollview*/
                                mBoolDragging = true;
                            }

//                            /*如果底部lv_content为空或者没有内容*/
//                            if(lv_content ==null|| lv_content.getChildCount()==0)
//                                mBoolDragging = true;
//                            /*lv_content已经滑到最顶端*/
//                            else if (isInTop()&&dy > 0)
//                                mBoolDragging = true;

                        } else {
                            /*因为上半部分是个LinearLayout，所以当有移动的时候一定是拖动*/
                            mBoolDragging = true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mBoolDragging = false;
                break;
        }
        if (mBoolDragging) return true;
        return super.onInterceptTouchEvent(ev);
    }

    public boolean isTabsInTop(){
        if(mTabLayout != null){
            int[] tabLoc = new int[2];
            mTabLayout.getLocationOnScreen(tabLoc);
            if(tabLoc[1] == 0)
                return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        getVelocityTracker().addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mFloatLastY = y;
                mFloatFirstY=y;
                mFloatOffestY=0;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mFloatLastY;
                mFloatOffestY=mFloatFirstY-y;
                if(mTabLayout != null){
                    if(dy < 0 && Math.abs(dy)>mTouchSlop){
                        int nIntOffset = (int) -dy;
                        scrollBy(0, nIntOffset);
                        mFloatLastY = y;
                        invalidate();
                        break;
                    }
                }

                if (!mBoolDragging && Math.abs(dy) > mTouchSlop) {
                    mBoolDragging = true;
                }
                if (mBoolDragging) {
                    int nIntOffset = (int) -dy;
                    scrollBy(0, nIntOffset);
                    mFloatLastY = y;//只有在mBoolDragging==true.即滑动状态时才计算新的偏移
                }

                boolean tBoolWilltotop=willToTop();
                if(mCallBack!=null&&tBoolWilltotop!=mBoolWillScrollTo){
                    mBoolWillScrollTo=tBoolWilltotop;
                    mCallBack.callback(CALLBACK_WILLSCROLLCHANGE,mBoolWillScrollTo);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mBoolDragging = false;
                getVelocityTracker().computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                //根据速率来判断应该滑到顶部还是底部
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    boolean toTop = velocityY > 0;
                    scrollToTop(toTop);
                } else {
                    //当距离底部距离>距离顶部距离的时候:向顶部滑动,反之向底部滑
                    scrollToTop(willToTop());
                }
                recycleVelocityTracker();
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 判断此时如果手离开屏幕的时候应该是往上滑还是往下滑
     * true:往上滑
     * false:往下滑
     * @return
     */
    private boolean willToTop(){
        if(mFloatOffestY>400){
            return false;
        }else if(mFloatOffestY<-400){
            return true;
        }else{
            //根据已经滑动的距离来判断
            int scrollY = getScrollY();
            return getHeight() - scrollY > scrollY - 0;
        }
    }

    /**
     * 子listview是否已经滑动到顶部了(当子listview滑动到顶部的时候就会触发父控件的上拉操作)
     * @return
     */
    private boolean isInTop(){
        if(lv_content !=null){
            if(mTabLayout !=null){
                int[] tabsLoc = new int[2];
                mTabLayout.getLocationOnScreen(tabsLoc);
                if(tabsLoc[1] == 0)
                    return true;
                else
                    return false;
            }
            if(lv_content.getFirstVisiblePosition() == 0 ){
                int[] nFirstItemLoc = new int[2];
                int[] nLvLoc=new int[2];
                lv_content.getChildAt(0).getLocationOnScreen(nFirstItemLoc);
                lv_content.getLocationOnScreen(nLvLoc);
                if(nFirstItemLoc[1]==nLvLoc[1])return true;
            }
        }

        return false;
    }


    public void recordTabLoc(View view){
        view.getLocationOnScreen(tabOrgLoc);
        tabOrgLoc[1] = tabOrgLoc[1] - this.getHeight();
    }

    /*
    * 子scrollView是否已经滑到了顶部
    * */
    public boolean isScrollToTop(){
        if(sv_content != null){
            if(sv_content.getScrollY() == 0)
                return true;
        }

        if(mTabLayout !=null && mTabLayout.getHeight()==0){
            return true;
        }
        return false;
    }


    /**
     * 控制整体滑动到顶部
     *
     * @param nBooltoTop true:滑动到顶部 false:滑动到底部
     */
    private void scrollToTop(boolean nBooltoTop) {
        int nIntOffset;
        if (nBooltoTop) {
            nIntOffset = 0 - getScrollY();
        } else {
            nIntOffset = getHeight() - getScrollY();
        }
        int nIntDuration = Math.abs(nIntOffset / 2);
        mScroller.startScroll(0, getScrollY(), 0, nIntOffset, nIntDuration);
        invalidate();
        mCurrentLoc = nBooltoTop ? LOC_TOP : LOC_BOTTOM;
//        mCallBack.callback(CALLBACK_LOCCHANGE,"");
    }

    /**
     * 重写scrollTo防止滑过头
     *
     * @param x
     * @param y
     */
    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > getHeight()) {
            y = getHeight();
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    /**
     * 获取当前所处在的位置
     * @return
     */
    public int getCurrentLoc(){
        return mCurrentLoc;
    }

    private VelocityTracker getVelocityTracker() {
        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain();
        return mVelocityTracker;
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void registerBottomListView(ListView lv) {
        lv_content = lv;
        has_lv_content = true;
    }


    public void registerTabLayout(ViewGroup viewGroup){
        mTabLayout = viewGroup;
        mTabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTabLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                recordTabLoc(mTabLayout);
            }
        });
    }

    public void registerBottomContent(View view){
        this.mBottomContent = view;
    }



    public void registerCallBack(ICallBack callback){
        mCallBack=callback;
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
}
