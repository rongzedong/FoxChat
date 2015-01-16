package com.wangyeming.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.wangyeming.Help.Utility;
import com.wangyeming.foxchat.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编辑联系人信息的Activity
 *
 * @author 王小明
 * @data 2011/01/03
 */

public class EditContactPhoneNumAdapter extends BaseAdapter {

    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>(); //adapter数据--记录电话号码信息
    private LayoutInflater mInflater = null;
    private Context context;
    protected ContentResolver cr;
    protected ViewHolder holder;
    protected LayoutInflater inflater;
    protected LinearLayout layout;
    protected EditText editType;
    protected Activity editActivity;
    protected int blankPosition; //记录哪一行电话号码为空
    protected Utility utility;
    protected Boolean isRecoveryFocus;  //是否需要恢复焦点
    protected int focusPosition;  //记录焦点在哪个view
    protected int focusSelection; //记录焦点在文字的哪个位置

    public EditContactPhoneNumAdapter(List<Map<String, Object>> data,
                                      Context context, ContentResolver cr, Activity editActivity, ListView lt) {
        this.data = data;
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.cr = cr;
        this.editActivity = editActivity;
        insertBlankPhoneNum(this.data.size());//增加空行填写新手机号
        blankPosition = this.data.size() - 1;
        System.out.println("size " + this.data.size());
        utility = new Utility(lt);
        isRecoveryFocus = false;
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return this.data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        System.out.println(position + " 执行getView");
        if (position >= data.size()) {
            System.out.println("position比总数大");
            return convertView;
        }
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
        if (data.get(position).get("phone_type_id") == 0 && data.get(position).get("phone_label") != null) {
            typeDisplay = (String) data.get(position).get("phone_label");
        } else {
            typeDisplay = (String) data.get(position).get("phone_type");
        }
        holder.editText.setText(((String) data.get(position).get("phone_num")));
        holder.phoneType.setText(typeDisplay);
        holder.editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                View focusView = editActivity.getCurrentFocus();
                if (focusView == null) {
                    System.out.println(position + " 当前无焦点！");
                    return;
                }
                System.out.println(focusView.getId());
                System.out.println(holder.editText.getId());
                if (focusView.getId() != holder.editText.getId()) {
                    System.out.println("不为当前焦点的view");
                    return;
                }
                System.out.println("s " + s);
                //如果修改行大于总行数或当前空行数大于总行数---表示数据刷新的更改
                if (position >= data.size() || blankPosition >= data.size()) {
                    System.out.println("position比总数大");
                    return;
                }
                //如果修改手机号为空
                if (s.toString().matches("")) {
                    System.out.println("当前空行为 " + blankPosition + " 当前行为： " + position + " 总行数 " + (data.size() - 1));
                    //如果修改为空的行大于等于总行数或者为空行---表示数据刷新的更改
                    if (position == blankPosition) {
                        return;
                    }
                    System.out.println("修改为空：修改第" + position + "行");
                    data.get(position).put("phone_num", "");
                    data.remove(blankPosition);
                    blankPosition = blankPosition > position ? position : blankPosition;
                    utility.setListViewHeightBasedOnChildren();
                    focusPosition = blankPosition;//记录焦点的位置
                    isRecoveryFocus = true;//需要恢复焦点
                    focusSelection = 0;
                    System.out.println("data长度" + data.size() + "去除空行：" + blankPosition);
                    //如果修改手机号不为空
                } else {
                    if (blankPosition == position) {
                        //修改空行
                        System.out.println("修改不为空（空行）：修改第" + position + "行");
                        data.get(position).put("phone_num", s.toString());
                        insertBlankPhoneNum(data.size());
                        utility.setListViewHeightBasedOnChildren();
                        blankPosition = data.size() - 1;
                        focusPosition = position;//记录焦点的位置
                        isRecoveryFocus = true;//需要恢复焦点
                        focusSelection = 1;
                        System.out.println("data长度" + data.size() + "增加空行：" + blankPosition);
                    } else {
                        System.out.println("修改不为空（非空行）：修改第" + position + "行");
                        //修改非空行
                        data.get(position).put("phone_num", s.toString());
                    }
                }
            }
        });
        holder.phoneType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v.findViewById(R.id.edit_phone_type);
                changePhoneType(button, position);
                System.out.println("click!");
            }
        });
        if (position == blankPosition) {
            holder.delete.setVisibility(View.INVISIBLE);
        } else {
            holder.delete.setVisibility(View.VISIBLE);
        }
        addDeletePhoneNumListener(position, holder.phoneType, holder.editText, holder.delete); //增加删除手机号的监听
        if (isRecoveryFocus && position == focusPosition) {
            holder.editText.requestFocus();
            holder.editText.setSelection(focusSelection);
            isRecoveryFocus = false;//焦点恢复完毕
        }
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
    public void changePhoneNum(EditText editText, int position) {
        //修改data数据
        data.get(position).put("phone_num", editText.getText());
    }

    //改变电话类型
    public void changePhoneType(final Button button, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
        builder.setTitle(R.string.phone_type);
        builder.setItems(R.array.phone_type, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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
                    EditContactPhoneNumAdapter.this.notifyDataSetChanged();
                    // EditContactDetailActivity.adapter.notifyDataSetChanged();
                }
            }
        });
        builder.show();
    }

    //修改电话类型为自定义时的提示
    public void customAlert(final Button button, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("请输入自定义名称？");
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.edit_alert_dialog, null);
        dialog.setView(layout);
        editType = (EditText) layout.findViewById(R.id.edit_alert);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String label = editType.getText().toString();
                String type = context.getResources().getStringArray(R.array.phone_type)[0];
                //如果内容为空
                if (label.isEmpty()) {
                    // button.setText(type);
                    data.get(position).put("phone_type_id", 0);
                    data.get(position).put("phone_type", type);
                    data.get(position).put("phone_label", null);
                    EditContactPhoneNumAdapter.this.notifyDataSetChanged();
                    // EditContactDetailActivity.adapter.notifyDataSetChanged();
                } else {
                    // button.setText(label);
                    data.get(position).put("phone_type_id", 0);
                    data.get(position).put("phone_type", type);
                    data.get(position).put("phone_label", label);
                    EditContactPhoneNumAdapter.this.notifyDataSetChanged();
                    // EditContactDetailActivity.adapter.notifyDataSetChanged();
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create();
        dialog.show();
    }

    //删除手机号的监听
    public void addDeletePhoneNumListener(final int positon, final Button phoneType, final EditText editText, final Button delete) {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(positon);
                //删除行若大于空行，则空行不变，否则空行-1
                blankPosition = positon > blankPosition ? blankPosition : blankPosition - 1;
                // EditContactDetailActivity.adapter.notifyDataSetChanged();
                utility.setListViewHeightBasedOnChildren();
                focusPosition = blankPosition;//记录焦点的位置,默认位于空行
                isRecoveryFocus = true;//需要恢复焦点
                focusSelection = 0;
            }
        });
    }

    public void insertBlankPhoneNum(int positon) {
        //设置编辑时最后一行的空号码用于显示
        Map<String, Object> phoneNumMap = new HashMap<String, Object>();
        phoneNumMap.put("phone_png", R.drawable.type_icon_phone);
        phoneNumMap.put("phone_num", "");
        phoneNumMap.put("phone_type_id", 1);
        phoneNumMap.put("phone_type", context.getResources().getStringArray(R.array.phone_type)[1]);
        phoneNumMap.put("phone_location", "北京");
        phoneNumMap.put("phone_label", "");
        phoneNumMap.put("message_png", R.drawable.ic_send_sms_p);
        data.add(positon, phoneNumMap);
    }

}
