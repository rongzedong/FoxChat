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
 * 联系人信息 PhoneAdapter
 *
 * @author 王小明
 * @data 2015/01/18
 */
public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.ViewHolder> {

    private static List<Map<String, Object>> phoneList = new ArrayList<>();
    private static Context context;
    private LayoutInflater mInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView phonePng;
        public TextView numberTextView;
        public TextView typeTextView;
        public TextView locationTextView;
        public ImageView messagePng;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public PhoneAdapter(Context context, List<Map<String, Object>> phoneList) {
        PhoneAdapter.context = context;
        mInflater = LayoutInflater.from(context);
        PhoneAdapter.phoneList = phoneList;
    }

    @Override
    public PhoneAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.phone_item,
                viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        vh.phonePng = (ImageView) view.findViewById(R.id.phone_png);
        vh.numberTextView = (TextView) view.findViewById(R.id.phone_num);
        vh.typeTextView = (TextView) view.findViewById(R.id.phone_type);
        vh.locationTextView = (TextView) view.findViewById(R.id.phone_location);
        vh.messagePng = (ImageView) view.findViewById(R.id.message_png);
        return vh;
    }

    @Override
    public void onBindViewHolder(PhoneAdapter.ViewHolder vh, int i) {
        String[] typeArr = context.getResources().getStringArray(R.array.phone_type);
        if(i==0) {
            vh.phonePng.setVisibility(View.VISIBLE);
        } else {
            vh.phonePng.setVisibility(View.INVISIBLE);
        }
        vh.numberTextView.setText((String) phoneList.get(i).get("number"));
        String label = (String) phoneList.get(i).get("label");
        int type = (int) phoneList.get(i).get("type");
        String typeString = typeArr[type];
        Log.d("wym","typeString "+typeString);
        if(label == null) {
            vh.typeTextView.setText(typeString);
        } else {
            vh.typeTextView.setText(label);
        }
        //vh.locationTextView.setText((String) phoneList.get(i).get("location"));
    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }
}
