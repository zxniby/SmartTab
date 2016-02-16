package com.example.xiangnanzhang.smarttab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class XListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ImageView mArrowImageView;
	private ProgressBar mProgressBar;
	private TextView mHintTextView;
//	private TextView tv_refreshTime;
	private RelativeLayout rela_content;
	ImageView img_icon;
	private int mState = STATE_NORMAL;

	private String mPullMsg = "下拉即可刷新";
	private String mLoadMsg = "释放即可刷新";
	RotateAnimUtil mRotateUtil;

	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	public enum HEADTYPE{
		NORMAL,RED
	}

	public XListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		mRotateUtil=new RotateAnimUtil();
		// 初始情况，设置下拉刷新view高度为0
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xlistview_header, null);
		rela_content=(RelativeLayout)mContainer.findViewById(R.id.xlistview_header_content);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView) findViewById(R.id.xlistview_header_arrow);
		mHintTextView = (TextView) findViewById(R.id.xlistview_header_hint_textview);
		mProgressBar = (ProgressBar) findViewById(R.id.xlistview_header_progressbar);
//		tv_refreshTime=(TextView)findViewById(R.id.xlistview_header_time);
		img_icon=(ImageView)findViewById(R.id.xlistview_header_img_icon);
		img_icon.setVisibility(View.GONE);
		mRotateUtil.init();
		mRotateUtil.setAnimView(mArrowImageView);
	}

	public void showImgIcon(){
		img_icon.setVisibility(View.VISIBLE);
	}


	public void setHeadType(HEADTYPE paramEnumHeadtype){
		if(paramEnumHeadtype==HEADTYPE.NORMAL){
			mArrowImageView.setImageResource(R.drawable.xlistview_arrow);
//			tv_refreshTime.setTextColor(getResources().getColor(R.color.xlistheadtextcolor));
			mHintTextView.setTextColor(getResources().getColor(R.color.xlistheadtextcolor));
			mProgressBar.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.pull_rotate_prograss));
			mContainer.setBackgroundResource(R.color.transparent);

		}else if (paramEnumHeadtype==HEADTYPE.RED){
			mArrowImageView.setImageResource(R.drawable.xlistview_arrow_white);
//			tv_refreshTime.setTextColor(getResources().getColor(R.color.white));
			mHintTextView.setTextColor(getResources().getColor(R.color.white));
			mProgressBar.setIndeterminateDrawable(this.getResources().getDrawable(R.drawable.pull_rotate_red_prograss));
			mContainer.setBackgroundResource(R.color.ui_320_red1);
		}
	}

	public void setState(int state) {
		if (state == mState)
			return;

		if (state == STATE_REFRESHING) { // 显示进度
			mRotateUtil.clear();
			mRotateUtil.setVisiable(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else { // 显示箭头图片
			mRotateUtil.setVisiable(View.VISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
		}

		switch (state) {
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				mRotateUtil.startRotateDown();
			}
			if (mState == STATE_REFRESHING) {
				mRotateUtil.clear();
			}
			mHintTextView.setText(mPullMsg);
			break;
		case STATE_READY:
			if (mState != STATE_READY) {
				mRotateUtil.clear();
				mRotateUtil.startRotateUp();
				mHintTextView.setText(mLoadMsg);
			}
			break;
		case STATE_REFRESHING:
			mHintTextView.setText("正在加载..");
			break;
		default:
		}

		mState = state;
	}

	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		int height = 0;
		if (null != mContainer.getLayoutParams()) {
			height = mContainer.getLayoutParams().height;
		} else {
			height = mContainer.getHeight();
		}
		return height;
	}

//	public void setLastRefreshTime(String paramStrRefreshtime){
//		tv_refreshTime.setText(getResources().getString(R.string.refreshHeadLastRefreshTime) + paramStrRefreshtime);
//	}

	public View getHeadViewContent(){
		return rela_content;
	}

	public void setPullMsg(String mPullMsg) {
		this.mPullMsg = mPullMsg;
	}

	public void setLoadMsg(String mLoadMsg) {
		this.mLoadMsg = mLoadMsg;
	}

}
