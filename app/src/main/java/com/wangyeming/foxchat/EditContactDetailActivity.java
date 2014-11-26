package com.wangyeming.foxchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.wangyeming.custom.CircleImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EditContactDetailActivity extends Activity {

    protected Long ContactId;
    protected Uri photo_uri;
    protected String contactName;
    protected List<Map<String, Object>> ContactDisplay = new ArrayList<Map<String, Object>>();
    protected ContentResolver cr;
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
        if (id == R.id.action_delete) {
            deleteContact();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init(){
        cr = getContentResolver();
        getContactMessage();//获取intent传递的联系人信息
        setImage();
        setName();
    }

    //读取联系人信息
    public void getContactMessage() {
        Intent intent = getIntent();
        ContactId = intent.getLongExtra("ContactId", 1);
        photo_uri = intent.getData();
        ContactDisplay = (List<Map<String, Object>>) intent.getSerializableExtra("ContactDisplay");
        contactName = intent.getStringExtra("contactName");
    }


    //设置联系人头像
    public void setImage() {
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        circleImageView.setImageURI(photo_uri);
    }

    //设置联系人姓名
    public void setName() {
        EditText editText = (EditText) findViewById(R.id.edit_name);
        editText.setText(contactName);
    }


    //删除联系人
    public void deleteContact() {
        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("确定删除该联系人？").
                setIcon(android.R.drawable.ic_dialog_info).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除联系人
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                 }).create();
        alertDialog.show();
    }

}