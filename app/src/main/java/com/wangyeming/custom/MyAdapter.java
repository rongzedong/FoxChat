package com.wangyeming.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wang on 2014/11/9.
 * 基础适配器
 */
public class MyAdapter extends BaseAdapter {

    private String colorCatalog = "#778899";
    private String colorName = "#000000";
    private int sizeCatalog = 14;
    private int sizeName = 18;
    private int heightCatalog = 70;
    private int heightName = 120;

    private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    private LayoutInflater mInflater = null;

    public MyAdapter(List<Map<String, String>> data, Context context) {
        this.data = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item1,
                    null);
            //holder.image = (ImageView) convertView.findViewById(R.id.image);
            //holder.catalogue = (TextView) convertView.findViewById(R.id.catalogue);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //一般按如下方式将数据与UI联系起来
        //holder.image.setImageResource(mData.get(position).getmIcon());
        if (data.get(position).get("catalogue") == null) {
            holder.name.setText(data.get(position).get("name"));
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeName);
            holder.name.setTextColor(Color.parseColor(colorName));
            holder.name.setHeight(heightName);
            // holder.name.getLayoutParams().height = heightName;
        } else {
            holder.name.setText(data.get(position).get("catalogue"));
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeCatalog);
            holder.name.setTextColor(Color.parseColor(colorCatalog)); // Color.BLACK
            holder.name.setHeight(heightCatalog);
            // holder.name.getLayoutParams().height = heightCatalog;
        }
        return convertView;
    }

    class ViewHolder {
        //public ImageView image;
        public TextView name;
    }
}
