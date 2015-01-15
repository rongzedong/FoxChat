package com.wangyeming.custom.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangyeming.custom.CircleImageView;
import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wang on 2015/1/14.
 */
public class CallRecordAdapter extends RecyclerView.Adapter<CallRecordAdapter.ViewHolder> {

    private static List<Map<String, Object>> callRecordsDisplay = new ArrayList<>();
    private LayoutInflater mInflater;
    private static Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CircleImageView avatar;
        public TextView nameTextView;
        public ImageView callPng;
        public TextView labelTextView;
        public TextView dateTextView;
        public TextView locationTextView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public CallRecordAdapter(Context context, List<Map<String, Object>> callRecordsDisplay) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.callRecordsDisplay = callRecordsDisplay;
    }

    @Override
    public CallRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.call_record_item,
                viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        vh.avatar = (CircleImageView) view.findViewById(R.id.avatar);
        vh.nameTextView = (TextView) view.findViewById(R.id.name);
        vh.callPng = (ImageView) view.findViewById(R.id.call_png);
        vh.labelTextView = (TextView) view.findViewById(R.id.label);
        vh.dateTextView = (TextView) view.findViewById(R.id.date);
        vh.locationTextView = (TextView) view.findViewById(R.id.location);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int i) {
        //设置头像
        Uri avatarUri = (Uri) callRecordsDisplay.get(i).get("avatarUri");
        if (avatarUri != null) {
            vh.avatar.setImageURI(avatarUri);
        }
        //设置姓名
        String name = (String) callRecordsDisplay.get(i).get("name");
        String number = (String) callRecordsDisplay.get(i).get("number");
        String displayName = name != null ? name : number;
        vh.nameTextView.setText(displayName);
        //设置电话类型图标
        int type = (int) callRecordsDisplay.get(i).get("type");
        switch (type) {
            //来电
            case 1:
                vh.callPng.setImageResource(R.drawable.ic_call_received_black_18dp);
                break;
            //去电
            case 2:
                vh.callPng.setImageResource(R.drawable.ic_call_made_black_18dp);
                break;
            //未接来电
            case 3:
                vh.callPng.setImageResource(R.drawable.ic_call_missed_black_18dp);
                break;
        }
        //设置显示电话号码类型
        String label = (String) callRecordsDisplay.get(i).get("numberLabel");
        String[] phoneNumberTypeArray = context.getResources().getStringArray(R.array.phone_type);
        Integer numberTypeInt = (Integer) callRecordsDisplay.get(i).get("numberType");
        String numberType = phoneNumberTypeArray[numberTypeInt];
        String display = label == null ? numberType : label;
        vh.labelTextView.setText(display);
        //设置日期
        vh.dateTextView.setText((String) callRecordsDisplay.get(i).get("date"));
        //vh.labelTextView.setText((String) callRecordsDisplay.get(i).get("loacation"));
    }

    @Override
    public int getItemCount() {
        return callRecordsDisplay.size();
    }


}
