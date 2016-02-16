package com.example.xiangnanzhang.smarttab;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

/**
 * Created by mik_eddy on 15/10/29.
 */
public class RotateAnimUtil {
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private Animation mRotateUpAnimimmediate;
    private Animation mRotateDownAnimimmediate;

    private final int ROTATE_ANIM_DURATION = 180;
    View mView;

    public void init(){
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

        mRotateUpAnimimmediate = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnimimmediate.setDuration(0);
        mRotateUpAnimimmediate.setFillAfter(true);
        mRotateDownAnimimmediate = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnimimmediate.setDuration(0);
        mRotateDownAnimimmediate.setFillAfter(true);
    }

    /**
     * 设置哪个View需要执行动画
     * @param v
     */
    public void setAnimView(View v){
        mView=v;
    }

    /**
     * 清除之前的动画
     */
    public void clear(){
        if(mView!=null)mView.clearAnimation();
    }

    /**
     * 执行向下动画
     */
    public void startRotateDown(){
        if(mView!=null)mView.startAnimation(mRotateDownAnim);
    }


    /**
     * 执行向上动画
     */
    public void startRotateUp(){
        if(mView!=null)mView.startAnimation(mRotateUpAnim);
    }


    /**
     * 立刻执行的旋转动画
     */
    public void startRotateUpImmediate(){
        if(mView!=null)mView.startAnimation(mRotateUpAnimimmediate);
    }

    /**
     * 立刻执行的旋转动画
     */
    public void startRotateDownImmediate(){
        if(mView!=null)mView.startAnimation(mRotateDownAnimimmediate);

    }

    /**
     * 对相应的View执行visiable
     * @param visiable
     */
    public void setVisiable(int visiable){
        if(mView!=null)mView.setVisibility(visiable);
    }
}
