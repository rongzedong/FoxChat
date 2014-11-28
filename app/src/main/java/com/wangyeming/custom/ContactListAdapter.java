package com.wangyeming.custom;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wang on 2014/11/9.
 * 基础适配器
 */
public class ContactListAdapter extends BaseAdapter {

    private String colorCatalog = "#778899";
    private String colorName = "#000000";
    private int sizeCatalog = 14;
    private int sizeName = 18;
    private int heightCatalog = 70;
    private int heightName = 120;

    private List<String> data = new ArrayList<String>();
    private LayoutInflater mInflater = null;
    private List<Integer> catalogList = new ArrayList<Integer>();
    private String keyWord = new String();

    public ContactListAdapter(List<String> data, Context context) {
        this.data = data;
        mInflater = LayoutInflater.from(context);
    }

    public ContactListAdapter(List<String> data, List<Integer> catalogList, Context context) {
        this.data = data;
        this.catalogList = catalogList;
        mInflater = LayoutInflater.from(context);
    }

    public ContactListAdapter(List<String> data, String keyWord, Context context) {
        this.data = data;
        mInflater = LayoutInflater.from(context);
        this.keyWord = keyWord;
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
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //一般按如下方式将数据与UI联系起来
        //holder.image.setImageResource(mData.get(position).getmIcon());
        if (catalogList.contains((Integer) position)) {
            holder.name.setText(data.get(position));
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeCatalog);
            holder.name.setTextColor(Color.parseColor(colorCatalog)); // Color.BLACK
            holder.name.setHeight(heightCatalog);

        } else {
            String name = data.get(position);
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeName);
            holder.name.setTextColor(Color.parseColor(colorName));
            holder.name.setHeight(heightName);
            if (!keyWord.isEmpty()) {
                System.out.println("搜索字高亮 " + keyWord);
                SpannableString sp = new SpannableString(name);
                Pattern p = Pattern.compile(keyWord);
                Matcher m = p.matcher(name);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    System.out.println("start " + start + "end " + end);
                    sp.setSpan(new ForegroundColorSpan(Color.parseColor("#ff6600")),
                            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                holder.name.setText(sp);
            } else {
                holder.name.setText(name);
            }
        }
        return convertView;
    }

    class ViewHolder {
        public TextView name;
    }
}
