package com.wangyeming.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wang on 2014/11/26.
 */
public class editContactPhoneNumAdapter extends BaseAdapter {

    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private LayoutInflater mInflater = null;

    public editContactPhoneNumAdapter(List<Map<String, Object>> data, Context context) {
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
        holder = new ViewHolder();
        holder.spinner = (Spinner) convertView.findViewById(R.id.edit_phone_type);
        holder.editText = (EditText) convertView.findViewById(R.id.edit_phonenum);
        holder.button = (Button) convertView.findViewById(R.id.edit_delete_button);
        return null;
    }

    class ViewHolder {
        public Spinner spinner;
        public EditText editText;
        public Button button;
    }
}
