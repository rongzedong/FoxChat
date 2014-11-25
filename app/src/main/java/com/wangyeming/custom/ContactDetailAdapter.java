package com.wangyeming.custom;

import android.content.Context;
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
 * Created by Wang on 2014/11/25.
 */
public class ContactDetailAdapter extends BaseAdapter {

    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private LayoutInflater mInflater = null;

    public ContactDetailAdapter(List<Map<String, Object>> data, Context context) {
        this.data = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            convertView = mInflater.inflate(R.layout.list_item2,
                    null);
            holder.phone = (ImageView) convertView.findViewById(R.id.phone);
            holder.phone_num = (TextView) convertView.findViewById(R.id.phone_num);
            holder.phone_type = (TextView) convertView.findViewById(R.id.phone_type);
            holder.phone_location = (TextView) convertView.findViewById(R.id.phone_location);
            holder.mes1 = (ImageView) convertView.findViewById(R.id.mes1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //一般按如下方式将数据与UI联系起来
        if (position == 0) {
            holder.phone.setVisibility(0);
        } else {
            holder.phone.setVisibility(4);
        }
        holder.phone.setImageResource(R.drawable.type_icon_phone);
        holder.phone_num.setText(((String) data.get(position).get("phone_num")));
        holder.phone_type.setText(((String) data.get(position).get("phone_type")));
        holder.phone_location.setText(((String) data.get(position).get("phone_location")));
        holder.mes1.setImageResource(R.drawable.ic_send_sms_p);
        return convertView;
    }

    class ViewHolder {
        public ImageView phone;
        public TextView phone_num;
        public TextView phone_type;
        public TextView phone_location;
        public ImageView mes1;
    }
}
