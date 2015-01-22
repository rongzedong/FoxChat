package com.wangyeming.Help;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 固定RecyclerView高度
 *
 * @author 王小明
 * @data 2015/01/2
 */
public class RecyclerViewUtility {
    protected RecyclerView recyclerView;

    public RecyclerViewUtility(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    //设置listView高度
    public Boolean setRecyclerViewHeightBasedOnChildren() {
        RecyclerView.Adapter recyclerViewAdapter = recyclerView.getAdapter();
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (recyclerViewAdapter == null) {
            // pre-condition
            return false;
        }

        int totalHeight = 0;
        for (int i = 0; i < recyclerViewAdapter.getItemCount(); i++) {
            View child = recyclerView.getChildAt(i);
            if(child != null) {
                Log.d("wym", "i " + i + " child " + child.getHeight());
                totalHeight = totalHeight + child.getHeight();
            }
        }
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeight;
        Log.d("wym", "params.height "+ params.height );
        recyclerView.setLayoutParams(params);
        return true;
    }
}
