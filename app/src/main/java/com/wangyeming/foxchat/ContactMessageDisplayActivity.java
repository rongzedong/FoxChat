package com.wangyeming.foxchat;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;
import com.wangyeming.Help.RecyclerViewLayoutManager;
import com.wangyeming.custom.adapter.EmailAdapter;
import com.wangyeming.custom.adapter.ImAdapter;
import com.wangyeming.custom.adapter.PhoneAdapter;
import com.wangyeming.custom.adapter.WebsiteAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人信息 Activity
 * 联系人详细信息
 *
 * @author 王小明
 * @data 2015/01/22
 */
public class ContactMessageDisplayActivity extends ActionBarActivity {

    //联系人数据操作
    protected ContentResolver cr;
    /**
     * phone recycler view
     */
    private RecyclerView phoneRecyclerView;
    private PhoneAdapter phoneAdapter;
    private RecyclerViewLayoutManager phoneLayoutManager;
    /**
     * email recycler view
     */
    private RecyclerView emailRecyclerView;
    private EmailAdapter emailAdapter;
    private RecyclerViewLayoutManager emailLayoutManager;
    /**
     * website recycler view
     */
    private RecyclerView websiteRecyclerView;
    private WebsiteAdapter websiteAdapter;
    private RecyclerViewLayoutManager websiteLayoutManager;
    /**
     * Im recycler view
     */
    private RecyclerView imRecyclerView;
    private ImAdapter imAdapter;
    private RecyclerViewLayoutManager imLayoutManager;
    private Toolbar toolbar;
    //联系人lookupkey
    private String lookUpKey = null;
    //联系人手机号数据
    private List<Map<String, Object>> phoneList = new ArrayList<>();
    //联系人电子邮箱数据
    private List<Map<String, Object>> emailList = new ArrayList<>();
    //联系人网址数据
    private List<Map<String, Object>> websiteList = new ArrayList<>();
    //即时通讯数据
    private List<Map<String, Object>> imList = new ArrayList<>();
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
    //total
    private static final String[] PROJECTION = new String[]{
            ContactsContract.Data.MIMETYPE,                     //mimetype
            ContactsContract.CommonDataKinds.Phone.NUMBER,      //手机号
            ContactsContract.CommonDataKinds.Phone.TYPE,        //手机号类型
            ContactsContract.CommonDataKinds.Phone.LABEL,        //手机号标签
            ContactsContract.CommonDataKinds.Email.ADDRESS,     //邮件地址
            ContactsContract.CommonDataKinds.Email.TYPE,        //邮件类型
            ContactsContract.CommonDataKinds.Email.LABEL,       //邮件标签
            ContactsContract.CommonDataKinds.Website.URL,       //网址url
            ContactsContract.CommonDataKinds.Website.TYPE,      //网址类型
            ContactsContract.CommonDataKinds.Website.LABEL,     //网址标签
            ContactsContract.CommonDataKinds.Im.DATA,           //联系方法的数据
            ContactsContract.CommonDataKinds.Im.TYPE,           //数据类型
            ContactsContract.CommonDataKinds.Im.LABEL,          //用户定义的数据标签
            ContactsContract.CommonDataKinds.Im.PROTOCOL,       //协议
            ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL,//自定义协议
    };

    //ContactsContract.CommonDataKinds.Phone
    private static final String[] PHONE_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.NUMBER,      //手机号
            ContactsContract.CommonDataKinds.Phone.TYPE,        //手机号类型
            ContactsContract.CommonDataKinds.Phone.LABEL,        //手机号标签
    };

    //ContactsContract.CommonDataKinds.Email
    private static final String[] EMAIL_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Email.ADDRESS,     //邮件地址
            ContactsContract.CommonDataKinds.Email.TYPE,        //邮件类型
            ContactsContract.CommonDataKinds.Email.LABEL,       //邮件标签
    };

    //ContactsContract.CommonDataKinds.Website
    private static final String[] WEBSITE_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Website.URL,       //网址url
            ContactsContract.CommonDataKinds.Website.TYPE,      //网址类型
            ContactsContract.CommonDataKinds.Website.LABEL,     //网址标签
    };

    //ContactsContract.CommonDataKinds.Im
    private static final String[] IM_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Im.DATA,       //联系方法的数据
            ContactsContract.CommonDataKinds.Im.TYPE,       //数据类型
            ContactsContract.CommonDataKinds.Im.LABEL,      //用户定义的数据标签
            ContactsContract.CommonDataKinds.Im.PROTOCOL,   //协议
            ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL,    //自定义协议
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
        } else if(id == R.id.edit) {
            //Intent intent = new Intent(this, EditContactDetailActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化
     */
    public void init() {
        setToolbar();
        cr = getContentResolver();
        setPhoneRecyclerView();  //设置phone recyclerView
        setEmailRecyclerView(); //设置email recyclerView
        setWebisteRecyclerView(); //设置website recyclerView
        setImRecyclerView();    //设置im recyclerView
        Intent intent = getIntent();
        ButtonFloat buttonFloat = (ButtonFloat) findViewById(R.id.starButton);
        buttonFloat.setBackgroundColor(Color.parseColor("#E91E63"));
        lookUpKey = intent.getStringExtra("LookUpKey");
        setContactAvatarAndName(lookUpKey);
        new Thread(new Runnable() {

            @Override
            public void run() {
                getContactMessage(lookUpKey);  //获取联系人信息
                Message message = Message.obtain();
                message.obj = "ok";
                ContactMessageDisplayActivity.this.handler1.sendMessage(message);
            }
        }).start();
    }

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            phoneAdapter.notifyDataSetChanged();  //手机号数据更新
            emailAdapter.notifyDataSetChanged();  //email数据更新
            websiteAdapter.notifyDataSetChanged(); //website数据更新
            imAdapter.notifyDataSetChanged();   //im数据更新
        }
    };

    /**
     * 设置toolbar
     */
    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_transparent);
        if (toolbar != null) {
            Log.d("wym","toolbar is not null");
            setSupportActionBar(toolbar);
            //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false); //隐藏toolBar标题
        getSupportActionBar().setCustomView(R.layout.contact_detail_toolbar_layout);
    }

    /**
     * 获取联系人信息
     */
    public void getContactMessage(String lookUpKey) {
        Cursor cursor = cr.query(ContactsContract.Data.CONTENT_URI,
                PROJECTION, "lookup=?" ,new String[]{lookUpKey}, null);
        while (cursor.moveToNext()) {
            String mimetype = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Data.MIMETYPE));
            switch (mimetype) {
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                    getPhone(cursor);
                    break;
                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                    getEmail(cursor);
                    break;
                case ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE:
                    getWebsite(cursor);
                    break;
                case ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE:
                    getIm(cursor);
                    break;
            }
        }
        cursor.close();
    }

    /**
     *获取手机号信息
     */
    public void getPhone(Cursor cursor) {
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

    /**
     *获取电子邮箱信息
     */
    public void getEmail(Cursor cursor) {
        String address = cursor.getString(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Email.ADDRESS));
        int type = cursor.getInt(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Email.TYPE));
        String label = cursor.getString(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Email.LABEL));
        Log.d("quick contact", "address " + address + " type " + type + " label " + label);
        Map<String, Object> emailMap = new HashMap<>();
        emailMap.put("address", address);
        emailMap.put("type", type);
        emailMap.put("label", label);
        emailList.add(emailMap);
    }

    /**
     *获取网址信息
     */
    public void getWebsite(Cursor cursor) {
        String Url = cursor.getString(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Website.URL));
        int type = cursor.getInt(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Website.TYPE));
        String label = cursor.getString(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Website.LABEL));
        Log.d("quick contact", "Url " + Url + " type " + type + " label " + label);
        Map<String, Object> websiteMap = new HashMap<>();
        websiteMap.put("Url", Url);
        websiteMap.put("type", type);
        websiteMap.put("label", label);
        websiteList.add(websiteMap);
    }

    /**
     *获取即时通讯信息
     */
    public void getIm(Cursor cursor) {
        String data = cursor.getString(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Im.DATA));
        int type = cursor.getInt(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.TYPE));
        String label = cursor.getString(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.LABEL));
        String protocol = cursor.getString(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Im.PROTOCOL));
        String customProtocol = cursor.getString(cursor.getColumnIndex(
                ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL));
        Log.d("quick contact", "data " + data + " type " + type + " label " + label + " protocol " +
                protocol + " customProtocol " + customProtocol);
        Map<String, Object> imMap = new HashMap<>();
        imMap.put("data", data);
        imMap.put("type", type);
        imMap.put("label", label);
        imMap.put("protocol", protocol);
        imMap.put("customProtocol", customProtocol);
        imList.add(imMap);
    }

    /**
    * 设置phone recyclerView
     */
    public void setPhoneRecyclerView() {
        phoneRecyclerView = (RecyclerView) findViewById(R.id.contact_phone_list_recycler);
        phoneRecyclerView.setHasFixedSize(true);
        //phoneLayoutManager = new LinearLayoutManager(this);
        phoneLayoutManager = new RecyclerViewLayoutManager(this);
        phoneRecyclerView.setLayoutManager(phoneLayoutManager);
        phoneAdapter = new PhoneAdapter(this, phoneList);
        phoneRecyclerView.setAdapter(phoneAdapter);
    }

    /**
     * 设置email recyclerView
     */
    public void setEmailRecyclerView() {
        emailRecyclerView = (RecyclerView) findViewById(R.id.contact_email_list_recycler);
        emailRecyclerView.setHasFixedSize(true);
        emailLayoutManager = new RecyclerViewLayoutManager(this);
        emailRecyclerView.setLayoutManager(emailLayoutManager);
        emailAdapter = new EmailAdapter(this, emailList);
        emailRecyclerView.setAdapter(emailAdapter);
    }

    /**
     * 设置website recyclerView
     */
    public void setWebisteRecyclerView() {
        websiteRecyclerView = (RecyclerView) findViewById(R.id.contact_website_list_recycler);
        websiteRecyclerView.setHasFixedSize(true);
        websiteLayoutManager = new RecyclerViewLayoutManager(this);
        websiteRecyclerView.setLayoutManager(websiteLayoutManager);
        websiteAdapter = new WebsiteAdapter(this, websiteList);
        websiteRecyclerView.setAdapter(websiteAdapter);
    }

    /**
     * 设置im recyclerView
     */
    public void setImRecyclerView() {
        imRecyclerView = (RecyclerView) findViewById(R.id.contact_im_list_recycler);
        imRecyclerView.setHasFixedSize(true);
        imLayoutManager = new RecyclerViewLayoutManager(this);
        imRecyclerView.setLayoutManager(imLayoutManager);
        imAdapter = new ImAdapter(this, imList);
        imRecyclerView.setAdapter(imAdapter);
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

    /**
     *  返回上一层
     */
    public void backToMain(View view) {
        this.finish();
    }
}
