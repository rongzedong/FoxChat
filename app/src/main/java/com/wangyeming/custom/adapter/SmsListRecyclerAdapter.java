package com.wangyeming.custom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wang on 2015/1/7.
 */
public class SmsListRecyclerAdapter extends RecyclerView.Adapter<SmsListRecyclerAdapter.ViewHolder> {
    private List<Map<String, Object>> smsDisplay = new ArrayList<>();
    private LayoutInflater mInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView draftTextView;
        public TextView nameTextView;
        public TextView numberTextView;
        public TextView contentTextView;
        public TextView dateTextView;
        public ViewHolder(View v) {
            super(v);
        }
    }

    public SmsListRecyclerAdapter(Context context, List<Map<String, Object>> smsDisplay){
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
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int i) {
        Boolean isDraft =  (Boolean) smsDisplay.get(i).get("isDraft");
        vh.draftTextView.setVisibility(isDraft ? View.VISIBLE : View.GONE);
        vh.nameTextView.setText((String) smsDisplay.get(i).get("contact"));
        vh.numberTextView.setText("(" + (Integer) smsDisplay.get(i).get("number") + ")");
        vh.contentTextView.setText((String) smsDisplay.get(i).get("content"));
        vh.dateTextView.setText((String) smsDisplay.get(i).get("date"));
    }

    @Override
    public int getItemCount() {
        return smsDisplay.size();
    }
}
