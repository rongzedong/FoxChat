package com.wangyeming.Help;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.wangyeming.foxchat.R;

/**
 * 固定RecyclerView高度
 *
 * @author 王小明
 * @data 2015/01/2
 */
public class RecyclerViewLayoutManager extends LinearLayoutManager {

    private int layoutWidth = 0;
    private int layoutHeight = 0;

    public RecyclerViewLayoutManager(Context context) {
        super(context);
    }


    public int getWidth() {
        return this.layoutWidth;
    }

    public int getHeight() {
        return this.layoutHeight;
    }

    private int[] mMeasuredDimension = new int[2];

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state,
                          int widthSpec, int heightSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);
        int width = 0;
        int height = 0;
        for (int i = 0; i < getItemCount(); i++) {
            measureScrapChild(recycler, i,
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                    mMeasuredDimension);

            if (getOrientation() == HORIZONTAL) {
                width = width + mMeasuredDimension[0];
                if (i == 0) {
                    height = mMeasuredDimension[1];
                }
            } else {
                height = height + mMeasuredDimension[1];
                if (i == 0) {
                    width = mMeasuredDimension[0];
                }
            }
        }
        switch (widthMode) {
            case View.MeasureSpec.EXACTLY:
                width = widthSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
        }
        width += this.getPaddingLeft() + this.getPaddingRight();
        height += this.getPaddingTop() + this.getPaddingBottom();
        this.layoutWidth = width;
        this.layoutHeight = height;
        Log.d("wym", "layoutHeight " + layoutHeight);
        setMeasuredDimension(width, height);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                   int heightSpec, int[] measuredDimension) {
        View view = recycler.getViewForPosition(position);
        if (view != null) {
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                    getPaddingLeft() + getPaddingRight(), p.width);
            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                    getPaddingTop() + getPaddingBottom(), p.height);
            view.measure(childWidthSpec, childHeightSpec);
            measuredDimension[0] = getDecoratedMeasuredWidth(view) + getLeftDecorationWidth(view)
                    + getRightDecorationWidth(view) + p.leftMargin + p.rightMargin
                    + view.getPaddingLeft() + view.getPaddingRight();
            measuredDimension[1] = getDecoratedMeasuredHeight(view) + getTopDecorationHeight(view)
                    + getBottomDecorationHeight(view) + p.bottomMargin + p.topMargin
                    + view.getPaddingTop() + view.getPaddingBottom();
            recycler.recycleView(view);
        }
    }
}
