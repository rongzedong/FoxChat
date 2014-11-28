package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.wangyeming.custom.ContactListAdapter;
import com.wangyeming.custom.NewToast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

/**
 * 读取联系人的Activity
 *
 * @author 王小明
 * @data 2014/11/28
 */

public class LineActivity extends Activity {

    // 保存联系人的姓名集合---包含分类名称
    private List<String> namesList = new ArrayList<String>();
    // 保存联系人的姓名集合---不包含分类名称
    private List<String> namesList2 = new ArrayList<String>();
    // 保存匹配联系人的姓名的集合
    private List<String> namesFilterList = new ArrayList<String>();
    // 记录姓名分类名的位置
    private List<Integer> catalogList = new ArrayList<Integer>();
    //保存联系人ContactId的集合---包含分类名称
    protected List<Long> contactIdList = new ArrayList<Long>();
    //保存联系人ContactId的集合---不包含分类名称
    protected List<Long> contactIdList2 = new ArrayList<Long>();
    //保存搜索过滤后的联系人ContactId的集合
    protected List<Long> contactIdFilterList = new ArrayList<Long>();
    //联系人数据操作
    protected ContentResolver cr;
    //联系人游标
    protected Cursor cursorID;
    //自定义Adapter
    protected ContactListAdapter adapter;
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
        System.out.println("onRestart");
        super.onRestart();
        if (!isSearch) {
            clearData(); //清除缓存数据
            initRefrash();
        }
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
        displayListView(namesList);
        setOnScrollListener();
        setOnItemClickListener();
    }

    public void initRefrash() {
        getPhoneContacts();
        //adapter.notifyDataSetChanged();
        displayListView(namesList);
    }

    /**
     * 得到手机通讯录联系人信息*
     */
    public void getPhoneContacts() {
        readStarredContact(); //读取星标联系人
        readContact(); //读取非星标联系人
    }

    //清除缓存数据
    public void clearData() {
        namesList.clear();
        namesList2.clear();
        namesFilterList.clear();
        catalogList.clear();
        contactIdList.clear();
        contactIdList2.clear();
        contactIdFilterList.clear();
    }

    //读取联系人
    public void readContact() {
        System.out.println("读取未收藏联系人。。。");
        namesList.add(getString(R.string.unstarred_contact)); // 其他联系人
        contactIdList.add((long) 0);
        cursorID = cr.query(CONTENT_URI, PHONES_PROJECTION, "starred=?", new String[]{"0"}, "sort_key");// 设置联系人光标,按汉语拼音排序
        String ContactName = "";//排除联系人重复
        while (cursorID.moveToNext()) {
            int nameFieldColumnIndex = cursorID.getColumnIndex(PHONES_PROJECTION[0]);//返回display_name对应列的index--0
            String contact = cursorID.getString(nameFieldColumnIndex);//获取联系人姓名
            if (contact.equals(ContactName)) {
                ContactName = contact;
                continue;
            } else {
                ContactName = contact;
                namesList.add(ContactName); // 保存联系人姓名
                namesList2.add(ContactName); // 保存联系人姓名
                int index = cursorID.getColumnIndex(PHONES_PROJECTION[4]);
                Long ContactId = cursorID.getLong(index);//获取联系人对应的ID号
                contactIdList.add(ContactId); //保存联系人ContactId
                contactIdList2.add(ContactId);
                contactIdFilterList.add(ContactId);
            }
        }
        System.out.println("一共读取了" + namesList2.size() + "个联系人");
        cursorID.close();
    }

    //读取星标联系人
    public void readStarredContact() {
        catalogList.add(0);
        namesList.add(getString(R.string.starred_contact)); // 星标联系人
        contactIdList.add((long) 0);
        System.out.println("读取收藏了联系人。。。");
        cursorID = cr.query(CONTENT_URI, PHONES_PROJECTION, "starred=?", new String[]{"1"}, "sort_key");// 设置星标联系人光标,按汉语拼音排序
        String ContactName = "";//排除联系人重复
        while (cursorID.moveToNext()) {
            int nameFieldColumnIndex = cursorID.getColumnIndex(PHONES_PROJECTION[0]);//返回display_name对应列的index--0
            String contact = cursorID.getString(nameFieldColumnIndex);//获取联系人姓名
            if (contact.equals(ContactName)) {
                ContactName = contact;
                continue;
            } else {
                ContactName = contact;
                namesList.add(ContactName); // 保存联系人姓名
                namesList2.add(ContactName); // 保存联系人姓名
                int index = cursorID.getColumnIndex(PHONES_PROJECTION[4]);
                Long ContactId = cursorID.getLong(index);//获取联系人对应的ID号
                contactIdList.add(ContactId); //保存联系人ContactId
                contactIdList2.add(ContactId); //保存联系人ContactId
            }
        }
        System.out.println("一共读取了" + namesList2.size() + "个收藏联系人");
        catalogList.add(namesList.size()); //记录“其他联系人”储存的位置
        cursorID.close();
    }

    //设置lisView布局
    public void displayListView(List<String> namesList) {
        lt1 = (ListView) findViewById(R.id.list1);
        if (namesList == null) {
            System.out.println("namesList is nil");
        }

        if (isSearch) {
            adapter = new ContactListAdapter(namesList, this);
        } else {
            adapter = new ContactListAdapter(namesList, catalogList, this);
        }
        lt1.setAdapter(adapter);
    }

    //设置lisView布局--包含关键词
    public void displayListView(List<String> namesList, String keyWord) {
        lt1 = (ListView) findViewById(R.id.list1);
        if (namesList == null) {
            System.out.println("ContactDisplay is nil");
        }
        adapter = new ContactListAdapter(namesList, keyWord, this);
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
                String name = (String) view.getItemAtPosition(firstPos);
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
                List<Long> ContactIdTmp = isSearch ? contactIdFilterList : contactIdList;
                if (!isSearch && catalogList.contains((Integer) position)) {
                    return;
                }
                Long ContactId = ContactIdTmp.get(position);
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
                matchContact("");
            }
        };
        searchView.setOnSearchClickListener(clickListener);
        //监听searchView关闭
        SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                System.out.println("close");
                isSearch = false;
                clearData();
                initRefrash();
                return false;
            }
        };
        searchView.setOnCloseListener(closeListener);
    }

    //匹配输入文字
    public void matchContact(String input) {
        namesFilterList = new ArrayList<String>();
        contactIdFilterList = new ArrayList<Long>();
        if (input.isEmpty()) {
            namesFilterList.addAll(namesList2);
            contactIdFilterList.addAll(contactIdList2);
            displayListView(namesFilterList);
            displayConclusion();
            return;
        }
        for (int i = 0; i < namesList2.size(); i++) {
            String name = namesList2.get(i);
            if (name == null) {
                continue;
            }
            Pattern pattern = Pattern.compile(input);
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                namesFilterList.add(name);
                contactIdFilterList.add(contactIdList2.get(i));
            }
            displayConclusion(namesFilterList);
            displayListView(namesFilterList, input);
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
    public void displayConclusion(List<String> Display) {
        int total_num = this.namesList2.size();
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

    //新建联系人
    public void newContact(View view) {
        Intent intent = new Intent(this, NewContactActivity.class);
        startActivity(intent);
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
