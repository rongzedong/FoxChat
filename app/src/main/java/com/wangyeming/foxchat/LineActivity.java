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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wangyeming.custom.MyAdapter;
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

    protected List<Map<String, Object>> ContactDisplay = new ArrayList<Map<String, Object>>();
    protected List<Map<String, Object>> ContactFilterDisplay = new ArrayList<Map<String, Object>>();
    protected List<Map<String, Object>> contactDisplayNoCata = new ArrayList<Map<String, Object>>();
    protected List<Long> ContactIdList = new ArrayList<Long>();
    protected List<Long> ContactIdFilterList = new ArrayList<Long>();
    protected ContentResolver cr;
    protected Cursor cursorID; //联系人游标
    protected ListView lt1;
    protected SearchView searchView;
    protected TextView tv1;
    protected TextView tv2;
    protected Toast nameToast;
    protected boolean isSearch = false;

    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //display_name
            ContactsContract.CommonDataKinds.Phone.NUMBER, //data1
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, //photo_id
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI,//
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,  //contact_id
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY, //sort_key
            ContactsContract.CommonDataKinds.Photo.RAW_CONTACT_ID
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
        cr = getContentResolver();
        getPhoneContacts();
        displayListView(ContactDisplay);
        setOnScrollListener();
        setOnItemClickListener();
    }

    /**
     * 得到手机通讯录联系人信息*
     */
    public void getPhoneContacts() {
        readStarredContact(); //读取星标联系人
        readContact(); //读取非星标联系人
    }

    //读取联系人
    public void readContact() {
        cursorID = cr.query(CONTENT_URI, PHONES_PROJECTION, "starred=?", new String[]{"0"}, "sort_key");// 设置联系人光标,按汉语拼音排序
        String ContactName = "";//排除联系人重复
        Map<String, Object> catalogueDisplay = new HashMap<String, Object>();
        catalogueDisplay.put("catalogue", "其他联系人");
        ContactDisplay.add(catalogueDisplay);
        while (cursorID.moveToNext()) {
            Map<String, Object> contact_map = new HashMap<String, Object>();//一个联系人所有手机号
            int nameFieldColumnIndex = cursorID.getColumnIndex(PHONES_PROJECTION[0]);//返回display_name对应列的index--0
            String contact = cursorID.getString(nameFieldColumnIndex);//获取联系人姓名
            if (contact.equals(ContactName)) {
                ContactName = contact;
                continue;
            } else {
                ContactName = contact;
                int index = cursorID.getColumnIndex(PHONES_PROJECTION[4]);
                Long ContactId = cursorID.getLong(index);//获取联系人对应的ID号
                Map<String, Object> ContactNameDisplay = new HashMap<String, Object>();
                ContactNameDisplay.put("name", contact);
                ContactNameDisplay.put("contactId", ContactId);
                System.out.println("姓名 " + contact + " ");
                ContactDisplay.add(ContactNameDisplay);
                contactDisplayNoCata.add(ContactNameDisplay);
                ContactFilterDisplay.add(ContactNameDisplay);
                ContactIdList.add(ContactId);
                ContactIdFilterList.add(ContactId);
            }
        }
        cursorID.close();
    }

    //读取星标联系人
    public void readStarredContact() {
        cursorID = cr.query(CONTENT_URI, PHONES_PROJECTION, "starred=?", new String[]{"1"}, "sort_key");// 设置星标联系人光标,按汉语拼音排序
        String ContactName = "";//排除联系人重复
        Map<String, Object> catalogueDisplay = new HashMap<String, Object>();
        catalogueDisplay.put("catalogue", "星标联系人");
        ContactDisplay.add(catalogueDisplay);
        while (cursorID.moveToNext()) {
            int nameFieldColumnIndex = cursorID.getColumnIndex(PHONES_PROJECTION[0]);//返回display_name对应列的index--0
            String contact = cursorID.getString(nameFieldColumnIndex);//获取联系人姓名
            if (contact.equals(ContactName)) {
                ContactName = contact;
                continue;
            } else {
                ContactName = contact;
                int index = cursorID.getColumnIndex(PHONES_PROJECTION[4]);
                Long ContactId = cursorID.getLong(index);//获取联系人对应的ID号
                Map<String, Object> ContactNameDisplay = new HashMap<String, Object>();
                ContactNameDisplay.put("name", contact);
                System.out.println("姓名 " + contact + " ");
                ContactNameDisplay.put("contactId", ContactId);
                ContactDisplay.add(ContactNameDisplay);
                contactDisplayNoCata.add(ContactNameDisplay);
                ContactFilterDisplay.add(ContactNameDisplay);
                ContactIdList.add(ContactId);
                ContactIdFilterList.add(ContactId);
            }
        }
        cursorID.close();
    }

    //设置lisView布局
    public void displayListView(List<Map<String, Object>> Display) {
        lt1 = (ListView) findViewById(R.id.list1);
        if (Display == null) {
            System.out.println("ContactDisplay is nil");
        }
        MyAdapter adapter = new MyAdapter(Display, this);
        //SimpleAdapter adapter = new SimpleAdapter(this, Display,
        //R.layout.list_item1, new String[]{"name"}, new int[]{R.id.name});
        lt1.setAdapter(adapter);
    }

    //设置ListView滑动监听---根据滑动位置Toast提示
    public void setOnScrollListener() {
        lt1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (nameToast != null) {
                    nameToast.cancel();
                }
                int firstPos = view.getFirstVisiblePosition() + 1;
                Map<String, String> ContactNameDisplay =
                        (Map<String, String>) view.getItemAtPosition(firstPos);
                String name = ContactNameDisplay.get("name");
                if (name == null) {
                    System.out.println("name is null");
                    return;
                }
                String surname = name.substring(0, 1);
                nameToast = NewToast.makeText(LineActivity.this, surname, Toast.LENGTH_SHORT);
                nameToast.show();
                /*
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                }
                */
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("position " + position + " id " + id);
                List<Map<String, Object>> displayTmp = isSearch ? ContactFilterDisplay : ContactDisplay;
                if (displayTmp.get(position).get("contactId") == null) {
                    return;
                }
                Long ContactId = (Long) displayTmp.get(position).get("contactId");
                System.out.println("ContactId " + ContactId);
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
        //监听searchView被点击
        SearchView.OnClickListener clickListener = new SearchView.OnClickListener() {

            @Override
            public void onClick(View view) {
                System.out.println("click");
                isSearch = true;
                displayListView(contactDisplayNoCata);
            }
        };
        searchView.setOnSearchClickListener(clickListener);
        //监听searchView关闭
        SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                System.out.println("close");
                isSearch = false;
                displayListView(ContactDisplay);
                return false;
            }
        };
        searchView.setOnCloseListener(closeListener);
    }

    //匹配输入文字
    public void matchContact(String input) {
        ContactFilterDisplay.clear();
        if (input.equals("")) {
            ContactFilterDisplay = isSearch ? contactDisplayNoCata : ContactDisplay;
            displayListView(ContactFilterDisplay);
            displayConclusion();
            return;
        }
        Iterator iterator = ContactDisplay.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> ContactNameDisplay =
                    (Map<String, Object>) iterator.next();
            String name = (String) ContactNameDisplay.get("name");
            if (name == null) {
                continue;
            }
            Long contactId = (Long) ContactNameDisplay.get("contactId");
            Pattern pattern = Pattern.compile(input);
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                ContactNameDisplay.put("name", name);
                ContactNameDisplay.put("contactId", contactId);
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
    public void displayConclusion(List<Map<String, Object>> Display) {
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
