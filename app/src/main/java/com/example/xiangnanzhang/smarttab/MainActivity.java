package com.example.xiangnanzhang.smarttab;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private FragmentManager fm;
    private Map<Integer,ViewPagerFragment> fragmentsMap;
    private SmartTabLayout mSmartTabLayout;
    private LayoutInflater inflater;
    private DragLinearLayout mDragLinearLayout;
    private LinearLayout mTop;
    private LinearLayout mBottom;
    private TextView tv_data;
    private ViewPager.OnPageChangeListener listener;
    private InviteNewLinearLayout inll_bottom_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        fm = getSupportFragmentManager();
        fragmentsMap = new HashMap<>();
        strList = new ArrayList<>();
        strList.add(zArray);
        strList.add(fArray);
        strList.add(sArray);
        strList.add(tArray);

        mBottom = (LinearLayout)findViewById(R.id.inll_bottom_content);
        mTop = (LinearLayout)findViewById(R.id.ll_top);
        tv_data = (TextView)findViewById(R.id.tv_data);
        mSmartTabLayout = (SmartTabLayout)findViewById(R.id.my_smartlayout);
        mDragLinearLayout = (DragLinearLayout)findViewById(R.id.drg_content);
        inll_bottom_content = (InviteNewLinearLayout)mBottom.findViewById(R.id.inll_bottom_content);
        mViewPager = (ViewPager)mBottom.findViewById(R.id.my_viewpager);

        mDragLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mDragLinearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mTop.getLayoutParams().height = mDragLinearLayout.getHeight();
                mBottom.getLayoutParams().height = mDragLinearLayout.getHeight()+tv_data.getHeight()*2;
                mViewPager.getLayoutParams().height = mDragLinearLayout.getHeight()+tv_data.getHeight();
                inll_bottom_content.setmYOffset(tv_data.getHeight());
                mTop.requestLayout();
                mBottom.requestLayout();
            }
        });

//        mBottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mBottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                int t = mBottom.getHeight() + tv_data.getHeight();
//                mViewPager.getLayoutParams().height = mBottom.getHeight() + tv_data.getHeight();
//
//                inll_bottom_content.setmYOffset(tv_data.getHeight());
//                mViewPager.requestLayout();
//            }
//        });


        mViewPager.setAdapter(new VPAdapter(fm));
        listener = new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ViewPagerFragment fragment = fragmentsMap.get(position);
//                mDragLinearLayout.registerBottomListView(fragment.getXListView());
//                mScrollView.setXListView(fragment.getXListView());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(listener);

        inflater = LayoutInflater.from(this.getApplicationContext());
        mSmartTabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View view = inflater.inflate(R.layout.tab_view, container, false);
                TextView tv_tab_title = (TextView) view.findViewById(R.id.tv_tab_title);
                tv_tab_title.setText("No." + position);
                return view;
            }
        });
        mSmartTabLayout.setDistributeEvenly(true);
        mSmartTabLayout.setFillViewport(true);
        mSmartTabLayout.setViewPager(mViewPager);





//        mDragLinearLayout.registerBottomListView(fragmentsMap.get(mViewPager.getCurrentItem()).getXListView());
//        mDragLinearLayout.registerTabLayout(mSmartTabLayout);
        mDragLinearLayout.registerBottomContent(tv_data);

    }

    private ICallBack mCallback = new ICallBack() {
        @Override
        public void callback(int callCode, Object... param) {
            int i = mViewPager.getCurrentItem();
            ViewPagerFragment fragment = fragmentsMap.get(mViewPager.getCurrentItem());
            inll_bottom_content.setmYOffset(tv_data.getHeight());
        }
    };


    private ICallBack mScrollCallback = new ICallBack() {
        @Override
        public void callback(int callCode, Object... param) {
            inll_bottom_content.doScroll(callCode);
        }
    };


    class VPAdapter extends FragmentStatePagerAdapter {

        public VPAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ViewPagerFragment fragment;
            if(fragmentsMap.get(position) == null){
                fragment = new ViewPagerFragment();
                fragment.setScrollCallback(mScrollCallback);
                fragmentsMap.put(position, fragment);
            }
            fragment = fragmentsMap.get(position);

            Bundle data = new Bundle();
            data.putString("FragmentID", position + "");
            data.putStringArray("ListItems", strList.get(position));
            fragment.setArguments(data);
            if(position == 0)
                fragment.registerCallback(mCallback);
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
    private String[] zArray = new String[]{"A","B","C","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v"};
    private String[] fArray = new String[]{"D","E","F","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v"};
    private String[] sArray = new String[]{"G","H","I","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v"};
    private String[] tArray = new String[]{"J","K","L","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v","v"};
    private List<String[]> strList;




}
