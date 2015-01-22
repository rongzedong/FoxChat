package com.wangyeming.custom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 联系人信息 WebsiteAdapter
 *
 * @author 王小明
 * @data 2015/01/22
 */
public class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.ViewHolder> {

    private static List<Map<String, Object>> websiteList = new ArrayList<>();
    private static Context context;
    private LayoutInflater mInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView websitePng;
        public TextView urlTextView;
        public TextView typeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public WebsiteAdapter(Context context, List<Map<String, Object>> phoneList) {
        WebsiteAdapter.context = context;
        mInflater = LayoutInflater.from(context);
        WebsiteAdapter.websiteList = phoneList;
    }

    @Override
    public WebsiteAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.website_item,
                viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        vh.websitePng = (ImageView) view.findViewById(R.id.website_png);
        vh.urlTextView = (TextView) view.findViewById(R.id.website_address);
        vh.typeTextView = (TextView) view.findViewById(R.id.website_type);
        return vh;
    }

    @Override
    public void onBindViewHolder(WebsiteAdapter.ViewHolder vh, int i) {
        String[] typeArr = context.getResources().getStringArray(R.array.website_type);
        if(i==0) {
            vh.websitePng.setVisibility(View.VISIBLE);
        } else {
            vh.websitePng.setVisibility(View.INVISIBLE);
        }
        vh.urlTextView.setText((String) websiteList.get(i).get("Url"));
        String label = (String) websiteList.get(i).get("label");
        int type = (int) websiteList.get(i).get("type");
        String typeString = typeArr[type-1];
        Log.d("wym", "typeString " + typeString);
        if(label == null) {
            vh.typeTextView.setText(typeString);
        } else {
            vh.typeTextView.setText(label);
        }
    }

    @Override
    public int getItemCount() {
        return websiteList.size();
    }
}
