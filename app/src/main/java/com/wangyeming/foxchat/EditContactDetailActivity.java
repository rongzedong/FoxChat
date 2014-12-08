package com.wangyeming.foxchat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.KeyEvent;
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
import com.wangyeming.custom.CircleImageView;
import com.wangyeming.custom.EditContactPhoneNumAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EditContactDetailActivity extends Activity {

    protected Long ContactId;
    protected Long RawContactId;
    protected Uri photo_uri;
    protected String contactName;
    public static ListView lt3;
    public static EditContactPhoneNumAdapter adapter;
    public static List<Map<String, Object>> ContactDisplay = new ArrayList<Map<String, Object>>();
    protected ContentResolver cr;
    protected boolean hasImage; //是否有头像
    protected EditText editName; //联系人姓名
    protected ContactEdit contactEdit;
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //display_name
            ContactsContract.CommonDataKinds.Phone.NUMBER, //data1
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, //photo_id
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI,//
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,  //contact_id
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY, //sort_key
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.STARRED
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact_detail);
        setActionBar();
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_contact_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    //监听按下返回键的动作
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //do something...
            cancelEdit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    public void init() {
        cr = getContentResolver();
        getContactMessage();//获取intent传递的联系人信息
        setImage(); //设置联系人头像
        setName(); //设置联系人姓名
        displayListView(); //设置布局
        contactEdit = new ContactEdit(ContactId, cr);
    }

    //读取联系人信息
    public void getContactMessage() {
        Intent intent = getIntent();
        ContactId = intent.getLongExtra("ContactId", 1);
        RawContactId = intent.getLongExtra("RawContactId", 1);
        hasImage = intent.getBooleanExtra("hasImage", true);
        photo_uri = intent.getData();
        ContactDisplay = (List<Map<String, Object>>) intent.getSerializableExtra("ContactDisplay");
        contactName = intent.getStringExtra("contactName");
        System.out.println("contactName " + contactName);
    }


    //设置联系人头像
    public void setImage() {
        if (hasImage) {
            CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);
            circleImageView.setImageURI(photo_uri);
        }
    }

    //设置联系人姓名
    public void setName() {
        EditText editText = (EditText) findViewById(R.id.edit_name);
        editText.setText(contactName);
    }

    //设置lisView布局
    public void displayListView() {
        lt3 = (ListView) findViewById(R.id.list_contact_phone_edit);
        adapter = new EditContactPhoneNumAdapter(ContactDisplay,
                RawContactId, this, cr, this);
        lt3.setAdapter(adapter);
        Utility.setListViewHeightBasedOnChildren(lt3);
    }


    //删除联系人
    public int deleteContact() {
        Uri uri = ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, ContactId);
        int count = cr.delete(uri, null, null);
        return count;
    }

    //删除联系人响应
    public void deleteContactWithAlert(View view) {
        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("确定删除该联系人？").
                setIcon(android.R.drawable.ic_dialog_info).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除联系人
                        deleteContact();
                        Intent intent = new Intent(EditContactDetailActivity.this, LineActivity.class);
                        startActivity(intent);
                        Toast.makeText(EditContactDetailActivity.this, "删除联系人成功！", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create();
        alertDialog.show();
    }

    //取消保存已编辑的信息
    public void cancelEdit() {
        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("确定放弃保存已修改的信息？").
                setIcon(android.R.drawable.ic_dialog_info).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditContactDetailActivity.this.finish();
                        /*
                        Intent intent = new Intent(EditContactDetailActivity.this, ContactDetailActivity.class);
                        intent.putExtra("ContactId", ContactId);
                        startActivity(intent);
                        */

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create();
        alertDialog.show();
    }

    //确认保存修改
    public void ensureEdit() throws RemoteException, OperationApplicationException {
        boolean nameSave = saveContactName();
        if(! nameSave) {
            return;
        }
        Toast.makeText(this, "保存修改成功", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    //判断修改联系人姓名
    public boolean saveContactName() throws RemoteException, OperationApplicationException {
        editName = (EditText) findViewById(R.id.edit_name);
        String name = editName.getText().toString();
        //姓名没有改动，无需修改联系人姓名
        if(name == contactName) {
            return false;
        }
        System.out.println("name " + name);
        //姓名不能为空
        if (name.isEmpty()) {
            Toast.makeText(this, "姓名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        Toast.makeText(this, "保存修改成功", Toast.LENGTH_SHORT).show();
        contactEdit.updateContactName(name);
        return true;
    }

    //保存对手机号的修改
    public void saveContactPhoneNum() {
        List<Map<String, Object>> data = adapter.getData();
        //...
    }

}