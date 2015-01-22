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
 * 联系人信息 imAdapter
 *
 * @author 王小明
 * @data 2015/01/22
 */
public class ImAdapter extends RecyclerView.Adapter<ImAdapter.ViewHolder> {

    private static List<Map<String, Object>> imList = new ArrayList<>();
    private static Context context;
    private LayoutInflater mInflater;

    private static final int[] logoId = new int[] {
            R.drawable.logo_aim,
            R.drawable.logo_msn,
            R.drawable.logo_yahoo,
            R.drawable.logo_skype,
            R.drawable.logo_qq,
            R.drawable.logo_google_plus,
            R.drawable.logo_icq,
            R.drawable.logo_jabber,
            R.drawable.logo_windows_metting,

    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imPng;
        public TextView dataTextView;
        public TextView typeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public ImAdapter(Context context, List<Map<String, Object>> imList) {
        ImAdapter.context = context;
        mInflater = LayoutInflater.from(context);
        ImAdapter.imList = imList;
    }

    @Override
    public ImAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.im_item,
                viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        vh.imPng = (ImageView) view.findViewById(R.id.im_png);
        vh.dataTextView = (TextView) view.findViewById(R.id.im_data);
        vh.typeTextView = (TextView) view.findViewById(R.id.im_type);
        return vh;
    }

    @Override
    public void onBindViewHolder(ImAdapter.ViewHolder vh, int i) {
        String[] typeArr = context.getResources().getStringArray(R.array.im_type);
        String[] protocolArr = context.getResources().getStringArray(R.array.im_protocol_type);
        String protocol = (String) imList.get(i).get("protocol");
        int protocolInt = Integer.parseInt(protocol);
        String protocolString = protocolArr[protocolInt+1];
        String customProtocol = (String) imList.get(i).get("customProtocol");
        if(protocolInt == -1) {
            vh.imPng.setVisibility(View.INVISIBLE);
        } else {
            vh.imPng.setImageResource(logoId[protocolInt]);
        }
        vh.dataTextView.setText((String) imList.get(i).get("data"));
        String label = (String) imList.get(i).get("label");
        int type = (int) imList.get(i).get("type");
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
        return imList.size();
    }
}
