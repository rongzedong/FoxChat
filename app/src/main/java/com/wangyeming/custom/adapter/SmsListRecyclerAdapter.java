package com.wangyeming.custom.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangyeming.foxchat.MessageConversationActivity;
import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wang on 2015/1/7.
 */
public class SmsListRecyclerAdapter extends RecyclerView.Adapter<SmsListRecyclerAdapter.ViewHolder> {
    private static List<Map<String, Object>> smsDisplay = new ArrayList<>();
    private static Context context;
    private LayoutInflater mInflater;

    public SmsListRecyclerAdapter(Context context, List<Map<String, Object>> smsDisplay) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.smsDisplay = smsDisplay;
    }

    @Override
    public SmsListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.sms_list_item,
                viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        vh.draftTextView = (TextView) view.findViewById(R.id.draft);
        vh.nameTextView = (TextView) view.findViewById(R.id.sendName);
        vh.numberTextView = (TextView) view.findViewById(R.id.number);
        vh.contentTextView = (TextView) view.findViewById(R.id.content);
        vh.dateTextView = (TextView) view.findViewById(R.id.date);
        vh.sendFailAlert = (ImageView) view.findViewById(R.id.send_fail_alert);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int i) {
        Boolean isDraft = (Boolean) smsDisplay.get(i).get("isDraft");
        vh.draftTextView.setVisibility(isDraft ? View.VISIBLE : View.GONE);
        vh.nameTextView.setText((String) smsDisplay.get(i).get("contact"));
        vh.numberTextView.setText("(" + (Integer) smsDisplay.get(i).get("number") + ")");
        vh.contentTextView.setText((String) smsDisplay.get(i).get("content"));
        vh.dateTextView.setText((String) smsDisplay.get(i).get("date"));
        if ((int) smsDisplay.get(i).get("hasFail") == 1) {
            vh.sendFailAlert.setVisibility(View.VISIBLE);
        } else {
            vh.sendFailAlert.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return smsDisplay.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView draftTextView;
        public TextView nameTextView;
        public TextView numberTextView;
        public TextView contentTextView;
        public TextView dateTextView;
        public ImageView sendFailAlert;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getPosition();
                    Log.d("wym", "当前点击的位置：" + getPosition());
                    String thread_id = (String) smsDisplay.get(postion).get("thread_id");
                    Intent intent = new Intent(context, MessageConversationActivity.class);
                    intent.putExtra("thread_id", thread_id);
                    context.startActivity(intent);
                }
            });
        }
    }
}
