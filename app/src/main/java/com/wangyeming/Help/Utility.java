package com.wangyeming.Help;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 固定listView高度
 *
 * @author 王小明
 * @data 2014/12/01
 */

public class Utility{

    protected ListView listView;

    public Utility(ListView listView) {
        this.listView = listView;
    }

    //设置listView高度
    public Boolean setListViewHeightBasedOnChildren()
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            // pre-condition
            return false;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        return true;
    }

}
