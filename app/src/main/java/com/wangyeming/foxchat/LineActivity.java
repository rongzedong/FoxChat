package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wangyeming.custom.NewToast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

public class LineActivity extends Activity {

    protected Map<String, Map<String, Object>> ContactSimple = new HashMap<String, Map<String, Object>>();
    protected Map<String, Map<String, Map<String, String>>> ContactNumMap = new HashMap<String, Map<String, Map<String, String>>>();
    protected List<Map<String, String>> ContactDisplay = new ArrayList<Map<String, String>>();
    protected List<Map<String, String>> ContactFilterDisplay = new ArrayList<Map<String, String>>();
    protected List<String> ContactIdList = new ArrayList<String>();
    protected ContentResolver cr;
    protected Cursor cursorID; //联系人游标
    protected Cursor phoneID;  //手机号游标
    protected Cursor photoID;  //头像游标
    protected ListView lt1;
    protected SearchView searchView;
    protected TextView tv1;
    protected TextView tv2;

    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //display_name
            ContactsContract.CommonDataKinds.Phone.NUMBER, //data1
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, //photo_id
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI,//
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,  //contact_id
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY //sort_key
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        setSearchViewListener();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void init() {
        getPhoneContacts();
        displayListView(ContactDisplay);
        setOnScrollListener();
        setOnItemClickListener();
    }

    /**
     * 得到手机通讯录联系人信息*
     */
    public void getPhoneContacts() {
        //String string = "";
        cr = getContentResolver();//得到ContentResolver对象
        cursorID = cr.query(CONTENT_URI, PHONES_PROJECTION, null, null, "sort_key");// 设置联系人光标,按汉语拼音排序
        System.out.println("PHONES_PROJECTION[0] " + PHONES_PROJECTION[0] + " PHONES_PROJECTION[1] "
                + PHONES_PROJECTION[1] + " PHONES_PROJECTION[2] " + PHONES_PROJECTION[2] +
                " PHONES_PROJECTION[3] " + PHONES_PROJECTION[3] + " PHONES_PROJECTION[4] "
                + PHONES_PROJECTION[4]);
        readContact(); //读取联系人
        System.out.println(ContactDisplay.size());
    }

    //读取联系人
    public void readContact() {
        String ContactName = "";//排除联系人重复
        while (cursorID.moveToNext()) {
            Map<String, Object> contact_map = new HashMap<String, Object>() ;//一个联系人所有手机号
            int nameFieldColumnIndex = cursorID.getColumnIndex(PHONES_PROJECTION[0]);//返回display_name对应列的index--0
            String contact = cursorID.getString(nameFieldColumnIndex);//获取联系人姓名
            if (contact.equals(ContactName)) {
                ContactName = contact;
                continue;
            } else {
                ContactName = contact;
                Map<String, String> ContactNameDisplay = new HashMap<String, String>();
                ContactNameDisplay.put("name", contact);
                System.out.println("姓名 "+ contact +" ");
                ContactDisplay.add(ContactNameDisplay);
                int index = cursorID.getColumnIndex(PHONES_PROJECTION[4]);
                String ContactId = cursorID.getString(index);//获取联系人对应的ID号
                ContactIdList.add(ContactId);
                /*
                phoneID = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        PHONES_PROJECTION[4] + "=" + ContactId, null, null);//设置手机号光标
                //获取手机号
                Map<String, Map<String, String>> phone_num_map = readContactPhoneNum();//读取手机号
                ContactNumMap.put(contact, phone_num_map); //存储手机号Map 姓名->手机号map
                //获取联系人头像
                Uri photo_uri = readContactPhoneBim();
                contact_map.put("photo_url", photo_uri); //存储头像Map 头像url->list
                ContactSimple.put(contact, contact_map);
                //其他操作
                System.out.println("--------------------------");
                */
            }
        }
        cursorID.close();
    }

    //读取联系人头像
    public Uri readContactPhoneBim() {
        String photo_string = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
        Uri photo_uri;
        if (photo_string == null) {
            //没有头像
            photo_uri = Uri.parse("content://com.android.contacts/display_photo/38");
        } else {
            photo_uri = Uri.parse(photo_string);
        }
        //InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), photo_uri);
        //Bitmap bmp_head = BitmapFactory.decodeStream(input);
        return photo_uri;
    }

    //读取联系人手机号
    public Map<String, Map<String, String>> readContactPhoneNum() {
        Map<String, Map<String, String>> phone_num_map = new HashMap<String, Map<String, String>>();
        while (phoneID.moveToNext()) {
            Map<String, String> phone_num_attribute_map = new HashMap<String, String>();
            String phoneNumber = phoneID.getString(phoneID.getColumnIndex(PHONES_PROJECTION[1]));
            String phoneNumberType = phoneID.getString(phoneID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            System.out.println("手机号： "+ phoneNumber + " 手机号类型： "+ phoneNumberType + " ");
            phone_num_attribute_map.put("Type", phoneNumberType);
            phone_num_attribute_map.put("Location", "北京");
            phone_num_map.put(phoneNumber, phone_num_attribute_map);
        }
        phoneID.close();
        return phone_num_map;
    }

    //设置lisView布局
    public void displayListView(List<Map<String, String>> Display) {
        lt1 = (ListView) findViewById(R.id.list1);
        if (ContactDisplay == null) {
            System.out.println("ContactDisplay is nil");
        }
        SimpleAdapter adapter = new SimpleAdapter(this, Display,
                R.layout.list_item1, new String[]{"name"}, new int[]{R.id.name});
        lt1.setAdapter(adapter);
    }

    //设置ListView滑动监听---根据滑动位置Toast提示
    public void setOnScrollListener() {
        lt1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int firstPos = view.getFirstVisiblePosition() + 1;
                    Map<String, String> ContactNameDisplay =
                            (Map<String, String>) view.getItemAtPosition(firstPos);
                    String name = ContactNameDisplay.get("name");
                    String surname = name.substring(0, 1);
                    Toast nameToast = NewToast.makeText(LineActivity.this, surname, Toast.LENGTH_SHORT);
                    nameToast.show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    //设置ListView点击监听
    public void setOnItemClickListener() {
        lt1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String ContactId = ContactIdList.get(arg2);
                System.out.println("111111111111  "+ContactId);
                Intent intent = new Intent(LineActivity.this, ContactDetailActivity.class);
                intent.putExtra("ContactId", ContactId);
                startActivity(intent);
            }
        });
    }

    //设置搜索框监听
    public void setSearchViewListener() {
        //监听输入框字符串变化
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            //输入框文字改变
            public boolean onQueryTextChange(String newText) {
                System.out.println("自动补全 " + newText);
                matchContact(newText);
                return true;
            }

            //提交搜索请求
            public boolean onQueryTextSubmit(String query) {
                matchContact(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
    }

    //匹配输入文字
    public void matchContact(String input) {
        ContactFilterDisplay.clear();
        if (input.equals("")) {
            displayListView(ContactDisplay);
            displayConclusion();
            return;
        }
        Iterator iterator = ContactDisplay.iterator();
        while (iterator.hasNext()) {
            Map<String, String> ContactNameDisplay =
                    (Map<String, String>) iterator.next();
            String name = ContactNameDisplay.get("name");
            Pattern pattern = Pattern.compile(input);
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                ContactNameDisplay.put("name", name);
                ContactFilterDisplay.add(ContactNameDisplay);
            }
            displayConclusion(ContactFilterDisplay);
            displayListView(ContactFilterDisplay);
        }
    }

    //输入为空时不显示结果统计
    public void displayConclusion() {
        tv1 = (TextView) findViewById(R.id.tV1);
        tv2 = (TextView) findViewById(R.id.tV2);
        tv1.setVisibility(8);
        tv2.setVisibility(8);

    }

    //显示搜索结果统计
    public void displayConclusion(List<Map<String, String>> Display) {
        int total_num = this.ContactDisplay.size();
        int match_num = Display.size();
        tv1 = (TextView) findViewById(R.id.tV1);
        tv2 = (TextView) findViewById(R.id.tV2);
        String str1 = "所有联系人";
        String str2 = "找到" + match_num + "个联系人";
        tv1.setText(str1);
        tv1.setVisibility(0);
        tv2.setText(str2);
        tv2.setVisibility(0);
    }

    /*
    //获取联系人信息线程
    public void runnableGetContacts(){
        System.out.println("start");
        Runnable runnable = new Runnable() {
            @Override
            public void run(){
                System.out.println("thread start!");
                getPhoneContacts();
                Message message = Message.obtain();
                message.obj = Contact;
                LineActivity.this.handler1.sendMessage(message);
            }
        };
        new Thread(runnable).start();
    }

    //处理联系人信息显示的handler
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            //ContactDisplay = (List<Map<String, String>>)msg.obj;
        }
    };
    */
}
