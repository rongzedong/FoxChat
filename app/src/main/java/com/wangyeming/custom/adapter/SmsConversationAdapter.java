package com.wangyeming.custom.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wangyeming.custom.CircleImageView;
import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wang on 2015/1/11.
 */
public class SmsConversationAdapter extends RecyclerView.Adapter<SmsConversationAdapter.ViewHolder> {
    private static List<Map<String, Object>> conversationDisplay = new ArrayList<>();
    private static Context context;
    private LayoutInflater mInflater;

    public SmsConversationAdapter(Context context, List<Map<String, Object>> conversationDisplay) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.conversationDisplay = conversationDisplay;
    }

    @Override
    public SmsConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.sms_conversation_item,
                viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        vh.left_layout = view.findViewById(R.id.left_message);
        vh.right_layout = view.findViewById(R.id.right_message);
        vh.left_imageView = (CircleImageView) view.findViewById(R.id.left_profile_image);
        vh.right_imageView = (CircleImageView) view.findViewById(R.id.right_profile_image);
        vh.left_tv1 = (TextView) view.findViewById(R.id.left_dialog);
        vh.right_tv1 = (TextView) view.findViewById(R.id.right_dialog);
        vh.left_tv2 = (TextView) view.findViewById(R.id.left_date);
        vh.right_tv2 = (TextView) view.findViewById(R.id.right_date);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        Boolean isSent = (Boolean) conversationDisplay.get(position).get("isSent");
        Uri imageUri = (Uri) conversationDisplay.get(position).get("imageUri");
        String content = (String) conversationDisplay.get(position).get("body");
        String date = (String) conversationDisplay.get(position).get("date");
        Boolean isFail = (Boolean) conversationDisplay.get(position).get("isFail");
        if (isSent) {
            //如果是发送的短信
            if (imageUri != null) {
                vh.right_imageView.setImageURI(imageUri);
            }
            if (isFail) {
                vh.right_tv1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_red, 0);
            }
            vh.right_tv1.setText(content);
            vh.right_tv2.setText(date);
            vh.right_layout.setVisibility(View.VISIBLE);
            vh.left_layout.setVisibility(View.GONE);
        } else {
            //如果是接收的短信
            if (imageUri != null) {
                vh.left_imageView.setImageURI(imageUri);
            }
            if (isFail) {
                vh.left_tv1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_red, 0);
            }
            vh.left_tv1.setText(content);
            vh.left_tv2.setText(date);
            vh.left_layout.setVisibility(View.VISIBLE);
            vh.right_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return conversationDisplay.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View left_layout; //左布局
        public View right_layout; //右布局
        public CircleImageView left_imageView;  //左头像
        public CircleImageView right_imageView;  //右头像
        public TextView left_tv1;  //左短信内容
        public TextView right_tv1;  //右短信内容
        public TextView left_tv2;  //左日期
        public TextView right_tv2;  //左日期

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
