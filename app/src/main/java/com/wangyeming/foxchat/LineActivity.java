package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wangyeming.custom.NewToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

public class LineActivity extends Activity {

    protected Map<String, Map<String, List<String>>> Contact = new HashMap<String, Map<String, List<String>>>();
    protected List<Map<String, String>> ContactDisplay = new ArrayList<Map<String, String>>();
    protected List<Map<String, String>> ContactFilterDisplay = new ArrayList<Map<String, String>>();
    protected ContentResolver cr;
    protected Cursor cursorID; //联系人游标
    protected Cursor phoneID;  //手机号游标
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
        Map<String, List<String>> contact_map = new HashMap<String, List<String>>();//存放单个联系人的各种信息
        while (cursorID.moveToNext()) {
            int nameFieldColumnIndex = cursorID.getColumnIndex(PHONES_PROJECTION[0]);//返回display_name对应列的index--0
            String contact = cursorID.getString(nameFieldColumnIndex);//获取联系人姓名
            Map<String, String> ContactNameDisplay = new HashMap<String, String>();
            ContactNameDisplay.put("name", contact);
            ContactDisplay.add(ContactNameDisplay);
            System.out.println("name " + contact + " ");
            /*获取手机号*/
            int index = cursorID.getColumnIndex(PHONES_PROJECTION[4]);
            System.out.println("index " + index);
            String ContactId = cursorID.getString(index);//获取联系人对应的ID号
            phoneID = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);//设置手机号光标
            List<String> phone_num_list = readContactPhoneNum();//读取手机号
            contact_map.put("phone_num", phone_num_list); //存储手机号Map 手机号->list
            //其他操作
            Contact.put(contact, contact_map);
            System.out.println("--------------------------");
        }
        cursorID.close();
    }

    //读取联系人手机号
    public List<String> readContactPhoneNum() {
        String string = "";//准备输出的字符串
        List<String> phone_num_list = new ArrayList<String>();//存放手机号的数组
        while (phoneID.moveToNext()) {
            String Number = phoneID.getString(phoneID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phone_num_list.add(Number);
            string += (Number + " ");
        }
        phoneID.close();
        return phone_num_list;
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
                startActivity(new Intent(LineActivity.this, ContactDetailActivity.class));
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
