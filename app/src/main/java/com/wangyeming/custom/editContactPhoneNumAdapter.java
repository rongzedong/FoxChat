package com.wangyeming.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.wangyeming.Help.Utility;
import com.wangyeming.foxchat.EditContactDetailActivity;
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
    protected Long RawContactId;
    protected ContentResolver cr;
    protected ViewHolder holder;
    protected LayoutInflater inflater;
    protected LinearLayout layout;
    protected EditText editType;
    protected Activity editActivity;


    public EditContactPhoneNumAdapter(List<Map<String, Object>> data,
                                      Long RawContactId, Context context, ContentResolver cr, Activity editActivity) {
        this.data = data;
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.RawContactId = RawContactId;
        this.cr = cr;
        this.editActivity = editActivity;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_contact_phone_edit,
                    null);
            holder.phoneType = (Button) convertView.findViewById(R.id.edit_phone_type);
            holder.editText = (EditText) convertView.findViewById(R.id.edit_phonenum);
            holder.delete = (Button) convertView.findViewById(R.id.edit_delete_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String typeDisplay = "";
        if (data.get(position).get("phone_type_id") == 0) {
            typeDisplay = (String) data.get(position).get("phone_label");
        } else {
            typeDisplay = (String) data.get(position).get("phone_type");
        }
        holder.phoneType.setText(typeDisplay);
        holder.editText.setText(((String) data.get(position).get("phone_num")));
        holder.phoneType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v.findViewById(R.id.edit_phone_type);
                changePhoneType(button, position);
                System.out.println("click!");
            }
        });
        deletePhoneNum(position, holder.phoneType, holder.editText, holder.delete);
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    class ViewHolder {
        public Button phoneType;
        public EditText editText;
        public Button delete;
    }

    //改变电话号码
    public void changePhoneNum(final EditText editText){

    }

    //改变电话类型
    public void changePhoneType(final Button button, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
        builder.setTitle(R.string.phone_type);
        builder.setItems(R.array.phone_type, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                System.out.println(context.getResources().getStringArray(R.array.phone_type)[which]);
                //如果是自定义，弹出提示框“请输入label”
                if (which == 0) {
                    customAlert(button, position);
                } else {
                    String type = context.getResources().getStringArray(R.array.phone_type)[which];
                    //修改button上文字
                    // button.setText(type);
                    //修改data数据
                    data.get(position).put("phone_type_id", which);
                    data.get(position).put("phone_type", type);
                    data.get(position).put("phone_label", null);
                    EditContactDetailActivity.adapter.notifyDataSetChanged();
                }
            }
        });
        builder.show();
    }

    //修改电话类型为自定义时的提示
    public void customAlert(final Button button, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("请输入自定义名称？");
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout)inflater.inflate(R.layout.edit_alert_dialog, null);
        dialog.setView(layout);
        editType = (EditText) layout.findViewById(R.id.edit_alert);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String label = editType.getText().toString();
                String type = context.getResources().getStringArray(R.array.phone_type)[0];
                //如果内容为空
                if(label.isEmpty()) {
                    // button.setText(type);
                    data.get(position).put("phone_type_id", 0);
                    data.get(position).put("phone_type", type);
                    data.get(position).put("phone_label", null);
                    EditContactDetailActivity.adapter.notifyDataSetChanged();
                } else {
                    // button.setText(label);
                    data.get(position).put("phone_type_id", 0);
                    data.get(position).put("phone_type", type);
                    data.get(position).put("phone_label", label);
                    EditContactDetailActivity.adapter.notifyDataSetChanged();
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create();
        dialog.show();
    }

    public void deletePhoneNum(final int positon , final Button phoneType, final EditText editText, final Button delete) {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("删除第" + positon + "行");
                data.remove(positon);
                EditContactDetailActivity.adapter.notifyDataSetChanged();
                Utility.setListViewHeightBasedOnChildren(EditContactDetailActivity.lt3);
            }
        });
    }


}
