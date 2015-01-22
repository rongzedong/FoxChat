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
 * 联系人信息 QuickEmailAdapter
 *
 * @author 王小明
 * @data 2015/01/22
 */
public class QuickEmailAdapter extends RecyclerView.Adapter<QuickEmailAdapter.ViewHolder> {

    private static List<Map<String, Object>> emailList = new ArrayList<>();
    private static Context context;
    private LayoutInflater mInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView emailPng;
        public TextView addressTextView;
        public TextView typeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public QuickEmailAdapter(Context context, List<Map<String, Object>> phoneList) {
        QuickEmailAdapter.context = context;
        mInflater = LayoutInflater.from(context);
        QuickEmailAdapter.emailList = phoneList;
    }

    @Override
    public QuickEmailAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.quick_email_item,
                viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        vh.emailPng = (ImageView) view.findViewById(R.id.email_png);
        vh.addressTextView = (TextView) view.findViewById(R.id.email_address);
        vh.typeTextView = (TextView) view.findViewById(R.id.email_type);
        return vh;
    }

    @Override
    public void onBindViewHolder(QuickEmailAdapter.ViewHolder vh, int i) {
        String[] typeArr = context.getResources().getStringArray(R.array.email_type);
        if(i==0) {
            vh.emailPng.setVisibility(View.VISIBLE);
        } else {
            vh.emailPng.setVisibility(View.INVISIBLE);
        }
        vh.addressTextView.setText((String) emailList.get(i).get("address"));
        String label = (String) emailList.get(i).get("label");
        int type = (int) emailList.get(i).get("type");
        String typeString = typeArr[type];
        Log.d("wym", "typeString " + typeString);
        if(label == null) {
            vh.typeTextView.setText(typeString);
        } else {
            vh.typeTextView.setText(label);
        }
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }
}
