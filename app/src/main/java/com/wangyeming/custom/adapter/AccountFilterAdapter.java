package com.wangyeming.custom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 联系人账号过滤spinner的Adapter
 *
 * @author 王小明
 * @data 2015/01/23
 */
public class AccountFilterAdapter extends BaseAdapter {

    private List<Map<String, Object>> accountList = new ArrayList<>();
    private LayoutInflater mInflater = null;

    public AccountFilterAdapter(List<Map<String, Object>> accountList, Context mContext) {
        this.accountList = accountList;
        mInflater = LayoutInflater.from(mContext);
    }

    class ViewHolder {
        public TextView accountName;
        public TextView accountNum;
    }

    @Override
    public int getCount() {
        return accountList.size();
    }

    @Override
    public Object getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_spinner_account,
                    null);
            holder.accountName = (TextView) convertView.findViewById(R.id.accountName);
            holder.accountNum = (TextView) convertView.findViewById(R.id.accountNumber);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String name = (String) accountList.get(position).get("accountName");
        Integer number = (Integer) accountList.get(position).get("accountNum");
        holder.accountName.setText(name);
        holder.accountNum.setText(number.toString());
        return convertView;
    }

}
