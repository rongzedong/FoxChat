package com.wangyeming.custom.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangyeming.custom.CircleImageView;
import com.wangyeming.foxchat.QuickContactActivity;
import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 联系人列表adapter
 *
 * @author 王小明
 * @date 2015/01/16
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

    private static List<Map<String, Object>> contactList = new ArrayList<>();
    private static Context context;
    private LayoutInflater mInflater;
    private int starNum;

    public ContactListAdapter(Context context, List<Map<String, Object>> contactList, int starNum) {
        ContactListAdapter.context = context;
        mInflater = LayoutInflater.from(context);
        ContactListAdapter.contactList = contactList;
        this.starNum = starNum;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mInflater.inflate(R.layout.list_contact_line,
                viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        vh.name = (TextView) view.findViewById(R.id.name);
        vh.avatar = (CircleImageView) view.findViewById(R.id.profile_image);
        vh.id = (ImageView) view.findViewById(R.id.identification);
        vh.letter = (TextView) view.findViewById(R.id.letter);
        vh.divider = (ImageView) view.findViewById(R.id.divider);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int i) {
        vh.name.setText((String) contactList.get(i).get("displayName"));
        String photoThumbUri = (String) contactList.get(i).get("photoThumbUri");
        if (photoThumbUri != null) {
            vh.avatar.setImageURI(Uri.parse(photoThumbUri));
        }
        //设置分割线
        if (i == starNum - 1) {
            vh.divider.setVisibility(View.VISIBLE);
        } else {
            vh.divider.setVisibility(View.INVISIBLE);
        }
        //设置mark
        String mark = (String) contactList.get(i).get("mark");
        switch (mark) {
            case "star":
                vh.divider.setVisibility(View.INVISIBLE); //设置分割线:不显示
                vh.id.setVisibility(View.VISIBLE);
                vh.letter.setVisibility(View.GONE);
                break;
            case "none":
                vh.divider.setVisibility(View.INVISIBLE); //设置分割线:不显示
                vh.id.setVisibility(View.INVISIBLE);
                vh.letter.setVisibility(View.GONE);
                break;
            default:
                vh.divider.setVisibility(View.VISIBLE); //设置分割线:显示
                vh.id.setVisibility(View.GONE);
                vh.letter.setVisibility(View.VISIBLE);
                vh.letter.setText(mark);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name; //姓名
        public CircleImageView avatar;  //头像
        public ImageView id;  //星标
        public TextView letter;  //字母标识
        public ImageView divider; //分割线

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int postion = getPosition();
                    String lookUpKey = (String) contactList.get(postion).get("LookUpKey");
                    Log.d("wym", "当前点击的位置：" + getPosition() + " lookUpKey " + lookUpKey);
                    Intent intent = new Intent(context, QuickContactActivity.class);
                    intent.putExtra("LookUpKey", lookUpKey);
                    context.startActivity(intent);
                }
            });
        }
    }
}
