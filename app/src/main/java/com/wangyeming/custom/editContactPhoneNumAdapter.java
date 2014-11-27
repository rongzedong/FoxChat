package com.wangyeming.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Wang on 2014/11/26.
 */
public class EditContactPhoneNumAdapter extends BaseAdapter {

    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private LayoutInflater mInflater = null;
    private Context context;

    public EditContactPhoneNumAdapter(List<Map<String, Object>> data, Context context) {
        this.data = data;
        mInflater = LayoutInflater.from(context);
        this.context = context;
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item3,
                    null);
            holder.phoneType = (Button) convertView.findViewById(R.id.edit_phone_type);
            holder.editText = (EditText) convertView.findViewById(R.id.edit_phonenum);
            holder.delete = (Button) convertView.findViewById(R.id.edit_delete_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.phoneType.setText(((String) data.get(position).get("phone_type")));
        holder.phoneType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoneType();
                System.out.println("click!");
            }
        });
        holder.editText.setText(((String) data.get(position).get("phone_num")));
        return convertView;
    }

    class ViewHolder {
        public Button phoneType;
        public EditText editText;
        public Button delete;
    }

    public void changePhoneType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
        builder.setTitle(R.string.phone_type);
        builder.setItems(R.array.phone_type, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
            }
        });
        builder.show();
    }

    public void deletePhoneNum(){}
}
