package com.wangyeming.foxchat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.wangyeming.Help.ContactEdit;
import com.wangyeming.Help.Utility;
import com.wangyeming.custom.EditContactPhoneNumAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新建联系人的Activity
 *
 * @author 王小明
 * @data 2015/01/08
 */

public class NewContactActivity extends Activity {

    protected Long contactId;
    public static ListView lt4;  //联系人电话列表listView
    public static EditContactPhoneNumAdapter adapter;  //编辑联系人电话号码的自定义adpter
    public static List<Map<String, Object>> contactDisplay = new ArrayList<Map<String, Object>>();  //用于显示联系人手机号信息的list
    protected Map<String, Object> newContact = new HashMap<>(); //用于新建联系人
    protected ContentResolver cr;  //ContentResolver对象
    protected ContactEdit contactEdit;  //联系人编辑自定义类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        setActionBar();
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init() {
        cr = getContentResolver();
        contactDisplay.clear(); //新建联系人手机号总为空
        displayListView();  //显示电话号码布局
        contactEdit = new ContactEdit(cr);
    }

    public void setActionBar() {
        ActionBar actionBar = getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true); //显示返回箭头
        // 不显示标题
        getActionBar().setDisplayShowTitleEnabled(false);
        //显示自定义视图
        getActionBar().setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(this).inflate(
                R.layout.actionbar_layout, null);
        getActionBar().setCustomView(actionbarLayout);
        //设置取消保存响应
        Button cancelButton = (Button) findViewById(R.id.cancelEdit);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cancelEdit();
            }
        });
        //设置确认保存响应
        Button sureButton = (Button) findViewById(R.id.saveEdit);
        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    ensureEdit();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void cancelEdit() {
        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("确定放弃保存已修改的信息？").
                setIcon(android.R.drawable.ic_dialog_info).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NewContactActivity.this.finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create();
        alertDialog.show();
    }

    public void ensureEdit() throws RemoteException, OperationApplicationException {
        //保存联系人信息。。。
        //账户类型和名称
        saveAccount();
        saveContactName();  //姓名
        saveContactPhoneNum();  //电话号码
        contactEdit.addNewContact(newContact);
        Toast.makeText(this, "保存修改成功", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    //设置电话号码的listView布局
    public void displayListView() {
        lt4 = (ListView) findViewById(R.id.list_new_contact_phone);
        adapter = new EditContactPhoneNumAdapter(contactDisplay,
                this, cr, this, lt4);
        lt4.setAdapter(adapter);
        Utility utility = new Utility(this.lt4);
        utility.setListViewHeightBasedOnChildren();
    }

    //
    public void saveAccount() {
        String accountType = "com.local.contact";
        String accountName = "local_contact";
        newContact.put("accountType",accountType);
        newContact.put("accountName",accountName);
    }

    //判断修改联系人姓名
    public void saveContactName()  {
        EditText editName = (EditText) findViewById(R.id.new_contact_name);
        String name = editName.getText().toString();
        //姓名不能为空
        if (name.isEmpty()) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        newContact.put("displayName", name);
    }

    //保存对手机号的修改
    public void saveContactPhoneNum() {
        contactDisplay.remove(contactDisplay.size()-1);
        System.out.println("重建手机号！");
        List <Map <String, Object>> phoneList = new ArrayList<>();
        for (Map<String, Object> phone : contactDisplay) {
            phoneList.add(phone);
        }
        newContact.put("phoneList", phoneList);
    }
}
