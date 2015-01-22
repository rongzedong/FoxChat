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
 * 联系人信息 AddressAdapter
 *
 * @author 王小明
 * @data 2015/01/22
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private static List<Map<String, Object>> addressList = new ArrayList<>();
    private static Context context;
    private LayoutInflater mInflater;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView addressPng;
        public TextView addressTextView;
        public TextView typeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public AddressAdapter(Context context, List<Map<String, Object>> addressList) {
        AddressAdapter.context = context;
        mInflater = LayoutInflater.from(context);
        AddressAdapter.addressList = addressList;
    }

    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.address_item,
                viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        vh.addressPng = (ImageView) view.findViewById(R.id.address_png);
        vh.addressTextView = (TextView) view.findViewById(R.id.address_formattedAddress);
        vh.typeTextView = (TextView) view.findViewById(R.id.address_type);
        return vh;
    }

    @Override
    public void onBindViewHolder(AddressAdapter.ViewHolder vh, int i) {
        String[] typeArr = context.getResources().getStringArray(R.array.address_type);
        if(i==0) {
            vh.addressPng.setVisibility(View.VISIBLE);
        } else {
            vh.addressPng.setVisibility(View.INVISIBLE);
        }
        vh.addressTextView.setText((String) addressList.get(i).get("formattedAddress"));
        String label = (String) addressList.get(i).get("label");
        int type = (int) addressList.get(i).get("type");
        String typeString = typeArr[type];
        if(label == null) {
            vh.typeTextView.setText(typeString);
        } else {
            vh.typeTextView.setText(label);
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }
}
