package com.wangyeming.foxchat;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangyeming.custom.CircleImageView;
import com.wangyeming.custom.adapter.PhoneAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContactMessageDisplayActivity extends ActionBarActivity {

    //联系人数据操作
    protected ContentResolver cr;
    private RecyclerView mRecyclerView;
    private PhoneAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Toolbar toolbar;
    //联系人lookupkey
    private String lookUpKey = null;
    //联系人数据
    private List<Map<String, Object>> phoneList = new ArrayList<>();
    //ContactsContract.Contacts
    private static final String[] CONTACT_PROJECTION = new String[]{
            "_id",                  //raw contact id 考虑使用lookup代替,不会改变
            "lookup",               //一个opaque值，包含当name_raw id改变时如何查找联系人的暗示
            //"name_raw_contact_id",  //name_raw_contact_id，随姓名改变
            "display_name",         //联系人姓名 DISPLAY_NAME_PRIMARY
            "display_name_alt",     //两者选一的展现display_name的方式，姓在前或名在前，如果选择不可用，则以DISPLAY_NAME_PRIMARY
            "display_name_source",  //用于产生联系人姓名的数据种类（EMAIL, PHONE, ORGANIZATION, NICKNAME, STRUCTURED_NAME）
            "phonetic_name",        //姓名发音
            "phonetic_name_style",  //姓名发音样式（包括JAPANESE， KOREAN， PINYIN和UNDEFINED）
            "sort_key",             //排序方式（对中国是拼音，对日本是五十音顺序）
            "sort_key_alt",         //可选的排序，对西方，名优先
            "photo_id",             //头像id，为null时查找 PHOTO_URI 或 PHOTO_THUMBNAIL_URI
            "photo_uri",            //全尺寸头像uri
            "photo_thumb_uri",      //缩略图头像uri
            "in_visible_group",     //反映群组课件状态的标识
            "has_phone_number",     //是否有手机号，1有，0没有
            "times_contacted",      //联系人的联系次数
            "last_time_contacted",  //最后联系时间
            "starred",              //是否被收藏
            "custom_ringtone",      //手机铃声uri，如果为null或missing，则为默认
            "send_to_voicemail",    //是否总是向该联系人发送声音邮件，0不是，1是
            "contact_presence",     //联系存在状态
            "contact_status",       //联系人最新的状态更新
            "contact_status_ts",    //联系人被新建或更新距今的时间长度
            "contact_status_res_package",  //包含label和icon的资源包
            "contact_status_label", //联系人状态icon资源的id
            "contact_status_icon",  //联系人状态label资源的id,如“Google Talk”
    };
    //ContactsContract.CommonDataKinds.Phone
    private static final String[] PHONE_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.NUMBER,      //手机号
            ContactsContract.CommonDataKinds.Phone.TYPE,        //手机号类型
            ContactsContract.CommonDataKinds.Phone.LABEL,        //手机号标签
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_message_display);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_message_display, menu);
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

    /**
     * 初始化
     */
    public void init() {
        setToolbar();
        cr = getContentResolver();
        setRecyclerView();
        Intent intent = getIntent();
        lookUpKey = intent.getStringExtra("LookUpKey");
        setContactAvatarAndName(lookUpKey);
        new Thread(new Runnable() {

            @Override
            public void run() {
                getContactPhoneNumbers(lookUpKey);  //获取手机联系人信息
                Message message = Message.obtain();
                message.obj = "ok";
                ContactMessageDisplayActivity.this.handler1.sendMessage(message);
            }
        }).start();
    }

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * 设置toolbar
     */
    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_transparent);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false); //隐藏toolBar标题
        //getSupportActionBar().setDisplayShowCustomEnabled(true); //设定自定义布局
        getSupportActionBar().setDisplayShowHomeEnabled(true);   //显示返回按钮
        //View actionbarLayout = LayoutInflater.from(this).inflate(
                //R.layout.contact_detail_toolbar_layout, null);
        //getSupportActionBar().setCustomView(actionbarLayout);
    }

    /**
    *获取手机号信息
     */
    public void getContactPhoneNumbers(String lookUpKey) {
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PHONE_PROJECTION, "lookup=?" ,new String[]{lookUpKey}, null);
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.NUMBER));
            int type = cursor.getInt(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.TYPE));
            String label = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Phone.LABEL));
            Log.d("quick contact", "number " + number + " type " + type + " label " + label);
            Map<String, Object> phoneMap = new HashMap<>();
            phoneMap.put("number", number);
            phoneMap.put("type", type);
            phoneMap.put("label", label);
            phoneList.add(phoneMap);
        }
        cursor.close();
    }

    /**
    * 设置recyclerView
     */
    public void setRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.contact_phone_list_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PhoneAdapter(this, phoneList);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
    * 获取联系人头像和姓名
     */
    public void setContactAvatarAndName(String lookUpKey) {
        String displayName = null;
        String photoUri = null;
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, CONTACT_PROJECTION,
                "lookup=?" ,new String[]{lookUpKey}, null);
        if (cursor.moveToFirst()) {
            displayName = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            photoUri = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.PHOTO_URI));
        }
        cursor.close();
        if(photoUri != null) {
            setAvatarBackground(Uri.parse(photoUri));
        }
        setName(displayName);
    }

    /**
     * 设置大头像
     */
    public void setAvatarBackground(Uri uri) {
        ImageView avatarBackground = (ImageView) findViewById(R.id.avatar_background);
        avatarBackground.setImageURI(uri);
    }

    /**
     * 设置姓名
     */
    public void setName(String name) {
        TextView contactName = (TextView) findViewById(R.id.contactName);
        contactName.setText(name);
    }
}
