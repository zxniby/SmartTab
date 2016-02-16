package com.example.xiangnanzhang.smarttab;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by XiangnanZhang on 1/02/16.
 */
public class ViewPagerFragment extends Fragment {
    private TextView mtv;
    public static ViewPagerFragment vpFragment;
    private InviteNewXListView ixl_list;
    private String[] items;
    private ICallBack myCallback;
    private ICallBack scrollCallback;


    public void registerCallback(ICallBack callBack){
        this.myCallback = callBack;
    }
    public void setScrollCallback(ICallBack callback){
        this.scrollCallback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle onSavedInstance){
        View view = inflater.inflate(R.layout.viewpager_fragment,null);
        mtv = (TextView)view.findViewById(R.id.tv_fragment_idenfier);
        mtv.setText(getArguments().getString("FragmentID"));
        items= getArguments().getStringArray("ListItems");
        ixl_list = (InviteNewXListView)view.findViewById(R.id.ixl_list);
        ixl_list.setAdapter(new IxlAdapter());
//        setListViewHeight(ixl_list);
        ixl_list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ixl_list.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (myCallback != null)
                    myCallback.callback(0, "");
            }
        });
//        ixl_list.setPullLoadEnable(false);
        ixl_list.setPullRefreshEnable(false);
//        ixl_list.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        ixl_list.setVerticalScrollBarEnabled(false);
//        ixl_list.setPullRefreshEnable(false);
//        ixl_list.setPullLoadEnable(false);

//        ixl_list.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                v.getParent().getParent().requestDisallowInterceptTouchEvent(false);
//                return false;
//            }
//        });
        ixl_list.setOnScrollListener(new XListView.OnXScrollListener() {
            boolean scrollFlag = false;
            int lastVisibleItemPosition;
            @Override
            public void onXScrolling(View view) {
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == XListView.OnXScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    scrollFlag = true;
                else
                    scrollFlag = false;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                scrollCallback.callback(ixl_list.whereToSroll);
            }
        });
        return view;
    }




    public XListView getXListView(){
        return ixl_list;
    }

    class IxlAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListViewHolder lvh;
            if(convertView == null){
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item,parent,false);
                lvh = new ListViewHolder(convertView);

                convertView.setTag(lvh);
            }
            lvh = (ListViewHolder)convertView.getTag();
            lvh.tv_title.setText(items[position]);
            return convertView;
        }
    }

    class ListViewHolder{
        private TextView tv_title;
        public ListViewHolder(View view){
            tv_title = (TextView)view.findViewById(R.id.tv_item);
        }
    }

    public void setListViewHeight(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }



}
