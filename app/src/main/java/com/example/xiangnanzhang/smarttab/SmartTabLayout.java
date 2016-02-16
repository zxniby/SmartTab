/**
 * Copyright (C) 2015 ogaclejapan
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.xiangnanzhang.smarttab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as
 * to
 * the user's scroll progress.
 * <p>
 * To use the component, simply add it to your view hierarchy. Then in your
 * {@link android.app.Activity} or {@link android.app.Fragment}, {@link
 * android.support.v4.app.Fragment} call
 * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout
 * is being used for.
 * <p>
 * The colors can be customized in two ways. The first and simplest is to provide an array of
 * colors
 * via {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)}. The
 * alternative is via the {@link TabColorizer} interface which provides you complete control over
 * which color is used for any individual position.
 * <p>
 * The views used as tabs can be customized by calling {@link #setCustomTabView(int, int)},
 * providing the layout ID of your custom layout.
 * <p>
 * Forked from Google Samples &gt; SlidingTabsBasic &gt;
 * <a href="https://developer.android.com/samples/SlidingTabsBasic/src/com.example.android.common/view/SlidingTabLayout.html">SlidingTabLayout</a>
 */
public class SmartTabLayout extends HorizontalScrollView {

  private static final boolean DEFAULT_DISTRIBUTE_EVENLY = false;
  private static final int TITLE_OFFSET_DIPS = 24;
  private static final int TITLE_OFFSET_AUTO_CENTER = -1;
  private static final int TAB_VIEW_PADDING_DIPS = 16;



  private static final int TAB_VIEW_TEXT_MIN_WIDTH = 0;
  private static final boolean TAB_CLICKABLE = true;

  //tab条
  protected final SmartTabStrip tabStrip;
  //title偏移量
  private int titleOffset;
//  padding
//  text最小width
  private ViewPager viewPager;
  private ViewPager.OnPageChangeListener viewPagerPageChangeListener;
  private OnScrollChangeListener onScrollChangeListener;
//  在tab layout上创建tab，可以自定义
  private TabProvider tabProvider;
//  listener for tab被点击时
  private InternalTabClickListener internalTabClickListener;
  private OnTabClickListener onTabClickListener;

  private boolean distributeEvenly;

  public SmartTabLayout(Context context) {
    this(context, null);
  }

  public SmartTabLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SmartTabLayout(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    // Disable the Scroll Bar
    setHorizontalScrollBarEnabled(false);

    final DisplayMetrics dm = getResources().getDisplayMetrics();
    final float density = dm.density;


    int tabBackgroundResId = NO_ID;

    ColorStateList textColors;

    int textHorizontalPadding = (int) (TAB_VIEW_PADDING_DIPS * density);
    int textMinWidth = (int) (30 * density);

    boolean clickable = TAB_CLICKABLE;
    int titleOffset = (int) (TITLE_OFFSET_DIPS * density);

    TypedArray a = context.obtainStyledAttributes(
            attrs, R.styleable.stl_SmartTabLayout, defStyle, 0);
    textColors = a.getColorStateList(
            R.styleable.stl_SmartTabLayout_stl_defaultTabTextColor);
    textHorizontalPadding = a.getDimensionPixelSize(
            R.styleable.stl_SmartTabLayout_stl_defaultTabTextHorizontalPadding, textHorizontalPadding);
//
    this.titleOffset = titleOffset;
    this.internalTabClickListener = clickable ? new InternalTabClickListener() : null;
//    this.distributeEvenly = distributeEvenly;
//    this.distributeEvenly = true;

    this.tabStrip = new SmartTabStrip(context, attrs);
//    tabStrip.setDividerColors(R.color.transparent);

    // Make sure that the Tab Strips fills this View
    setFillViewport(false);
    addView(tabStrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if (onScrollChangeListener != null) {
      onScrollChangeListener.onScrollChanged(l, oldl);
    }
  }


  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    // Ensure first scroll
    if (changed && viewPager != null) {
      scrollToTab(viewPager.getCurrentItem(), 0);
    }
  }




  /**
   * Set the custom layout to be inflated for the tab views.
   *
   * @param provider {@link TabProvider}
   */
  public void setCustomTabView(TabProvider provider) {
    tabProvider = provider;
  }

  /**
   * Sets the associated view pager. Note that the assumption here is that the pager content
   * (number of tabs and tab titles) does not change after this call has been made.
   */
  public void setViewPager(ViewPager viewPager) {
    tabStrip.removeAllViews();

    this.viewPager = viewPager;
    if (viewPager != null && viewPager.getAdapter() != null) {
      viewPager.addOnPageChangeListener(new InternalViewPagerListener());
      populateTabStrip();
    }
  }

  /**
   * Returns the view at the specified position in the tabs.
   *
   * @param position the position at which to get the view from
   * @return the view at the specified position or null if the position does not exist within the
   * tabs
   */
  public View getTabAt(int position) {
    return tabStrip.getChildAt(position);
  }



//  发布Tab条
  private void populateTabStrip() {
    //获得adapter
    final PagerAdapter adapter = viewPager.getAdapter();

    for (int i = 0; i < adapter.getCount(); i++) {

      final View tabView = tabProvider.createTabView(tabStrip, i, adapter);

      if (tabView == null) {
        throw new IllegalStateException("tabView is null.");
      }

      if (distributeEvenly) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
        lp.width = 0;
        lp.weight = 1;
      }

      if (internalTabClickListener != null) {
        tabView.setOnClickListener(internalTabClickListener);
      }

      tabStrip.addView(tabView);

      if (i == viewPager.getCurrentItem()) {
        tabView.setSelected(true);
      }

    }
  }

/*
* @param positionOffset （范围0~1）the offset from the page at position
* */
  private void scrollToTab(int tabIndex, float positionOffset) {
    //tab的个数
    final int tabStripChildCount = tabStrip.getChildCount();
    if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
      return;
    }

    //当前的tab
    View selectedTab = tabStrip.getChildAt(tabIndex);
    //这个tag总共占有的宽度
    int widthPlusMargin = Utils.getWidth(selectedTab) + Utils.getMarginHorizontally(selectedTab);
    //额外的偏移量
    int extraOffset = (int) (positionOffset * widthPlusMargin);
    int x = (tabIndex > 0 || positionOffset > 0) ? -titleOffset : 0;
    int start = Utils.getStart(selectedTab);
    int startMargin = Utils.getMarginStart(selectedTab);
    x += start - startMargin + extraOffset;
    scrollTo(x, 0);

  }


  /**
   * Interface definition for a callback to be invoked when the scroll position of a view changes.
   */
  public interface OnScrollChangeListener {

    /**
     * Called when the scroll position of a view changes.
     *
     * @param scrollX Current horizontal scroll origin.
     * @param oldScrollX Previous horizontal scroll origin.
     */
    void onScrollChanged(int scrollX, int oldScrollX);
  }

  /**
   * Interface definition for a callback to be invoked when a tab is clicked.
   */
  public interface OnTabClickListener {

    /**
     * Called when a tab is clicked.
     *
     * @param position tab's position
     */
    void onTabClicked(int position);
  }

  /**
   * Create the custom tabs in the tab layout. Set with
   *
   */
  public interface TabProvider {

    /**
     * @return Return the View of {@code position} for the Tabs
     */
    View createTabView(ViewGroup container, int position, PagerAdapter adapter);

  }

  /**
   * Set the same weight for tab
   */
  public void setDistributeEvenly(boolean distributeEvenly) {
    this.distributeEvenly = distributeEvenly;
  }

  private static class SimpleTabProvider implements TabProvider {

    private final LayoutInflater inflater;
    private final int tabViewLayoutId;
    private final int tabViewTextViewId;

    private SimpleTabProvider(Context context, int layoutResId, int textViewId) {
      inflater = LayoutInflater.from(context);
      tabViewLayoutId = layoutResId;
      tabViewTextViewId = textViewId;
    }

    /**
     * @return Return the View of {@code position} for the Tabs
     */

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
      View tabView = null;
      TextView tabTitleView = null;

      if (tabViewLayoutId != NO_ID) {
        tabView = inflater.inflate(tabViewLayoutId, container, false);
      }

      if (tabViewTextViewId != NO_ID && tabView != null) {
        tabTitleView = (TextView) tabView.findViewById(tabViewTextViewId);
      }

      if (tabTitleView == null && TextView.class.isInstance(tabView)) {
        tabTitleView = (TextView) tabView;
      }

      if (tabTitleView != null) {
        tabTitleView.setText(adapter.getPageTitle(position));
      }

      return tabView;
    }

  }

  private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {

    private int scrollState;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      int tabStripChildCount = tabStrip.getChildCount();
      if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
        return;
      }

      tabStrip.onViewPagerPageChanged(position, positionOffset);

      scrollToTab(position, positionOffset);

      if (viewPagerPageChangeListener != null) {
        viewPagerPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
      }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
      scrollState = state;

      if (viewPagerPageChangeListener != null) {
        viewPagerPageChangeListener.onPageScrollStateChanged(state);
      }
    }

    @Override
    public void onPageSelected(int position) {
      if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
        tabStrip.onViewPagerPageChanged(position, 0f);
        scrollToTab(position, 0);
      }

      for (int i = 0, size = tabStrip.getChildCount(); i < size; i++) {
        tabStrip.getChildAt(i).setSelected(position == i);
      }

      if (viewPagerPageChangeListener != null) {
        viewPagerPageChangeListener.onPageSelected(position);
      }
    }

  }

  private class InternalTabClickListener implements OnClickListener {
    @Override
    public void onClick(View v) {
      for (int i = 0; i < tabStrip.getChildCount(); i++) {
        if (v == tabStrip.getChildAt(i)) {
          if (onTabClickListener != null) {
            onTabClickListener.onTabClicked(i);
          }
          viewPager.setCurrentItem(i);
          return;
        }
      }
    }
  }

  public interface TabColorizer {

    /**
     * @return return the color of the indicator used when {@code position} is selected.
     */
    int getIndicatorColor(int position);

    /**
     * @return return the color of the divider drawn to the right of {@code position}.
     */
    int getDividerColor(int position);

  }
}
