package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
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

    protected List<Map<String, String>> ContactDisplay = new ArrayList<Map<String, String>>();
    protected List<Map<String, String>> ContactFilterDisplay = new ArrayList<Map<String, String>>();
    //protected ListView lt1 = (ListView) findViewById(R.id.list1);
    protected ListView lt1;
    protected SearchView searchView;

    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //display_name
            ContactsContract.CommonDataKinds.Phone.NUMBER, //data1
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, //photo_id
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,  //contact_id
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY //sort_key
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        //getActionBar().hide();//隐藏ActionBar
        runnableGetContacts();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.line, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        setSearchViewListener();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**得到手机通讯录联系人信息**/
    public void getPhoneContacts() {
        //String string = "";
        List<Map<String, String>> ContactDisplayTmp = new ArrayList<Map<String, String>>();
        ContentResolver cr = getContentResolver();//得到ContentResolver对象
        Cursor cursorID = cr.query(CONTENT_URI, PHONES_PROJECTION, null, null, "sort_key");// 设置联系人光标,按汉语拼音排序
        System.out.println("PHONES_PROJECTION[0] "+ PHONES_PROJECTION[0] +  " PHONES_PROJECTION[1] "
                + PHONES_PROJECTION[1] +" PHONES_PROJECTION[2] "+ PHONES_PROJECTION[2] +
                " PHONES_PROJECTION[3] "+ PHONES_PROJECTION[3] + " PHONES_PROJECTION[4] "
                + PHONES_PROJECTION[4]);
        while(cursorID.moveToNext()){
            String string = "";
            int nameFieldColumnIndex = cursorID.getColumnIndex(PHONES_PROJECTION[0]);//返回display_name对应列的index--0
            String contact = cursorID.getString(nameFieldColumnIndex);//获取联系人姓名
            Map<String, String> ContactNameDisplay = new HashMap<String, String>();
            ContactNameDisplay.put("name", contact);
            ContactDisplayTmp.add(ContactNameDisplay);
            System.out.println("name "+ contact +" ");
            Message message = Message.obtain();
            message.obj = ContactDisplayTmp;
            LineActivity.this.handler1.sendMessage(message);
            /*
            int index = cursorID.getColumnIndex(PHONES_PROJECTION[3]);//通过_id获取index值-- -1
            String ContactId = cursorID.getString(index);//获取联系人对应的ID号
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);//设置手机号光标
            while(phone.moveToNext()){
                String Number = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                string += (contact + ":" + Number + " ");
            }
            System.out.println(string);
            System.out.println("--------------------------");
            phone.close();
            */
        }
        cursorID.close();
        System.out.println(ContactDisplay.size());
    }

    //设置lisView布局
    public void displayListView(List<Map<String, String>> Display ){
        lt1 = (ListView) findViewById(R.id.list1);
        if(ContactDisplay == null){
            System.out.println("ContactDisplay is nil");
        }
        SimpleAdapter adapter = new SimpleAdapter(this, Display,
                R.layout.list_item, new String[]{"name"}, new int[]{R.id.name});
        lt1.setAdapter(adapter);
    }

    //获取联系人信息线程
    public void runnableGetContacts(){
        System.out.println("start");
        Runnable runnable = new Runnable() {
            @Override
            public void run(){
                System.out.println("thread start!");
                getPhoneContacts();
            }
        };
        new Thread(runnable).start();
    }

    //处理联系人信息显示的handler
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            ContactDisplay = (List<Map<String, String>>)msg.obj;
            try {
                displayListView(ContactDisplay);
            }catch(Exception e){
                e.printStackTrace();
            }
            setListViewListener();
        }
    };

    //设置ListView监听---根据滑动位置Toast提示
    public void setListViewListener() {
        lt1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int firstPos = view.getFirstVisiblePosition()+1;
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

    //设置搜索框监听
    public void setSearchViewListener(){
        //监听输入框字符串变化
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            //输入框文字改变
            public boolean onQueryTextChange(String newText) {
                System.out.println("自动补全 "+ newText);
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
    public void matchContact(String input){
        ContactFilterDisplay.clear();
        if(input.equals("")){
            displayListView(ContactDisplay);
        }
        Iterator iterator = ContactDisplay.iterator();
        while(iterator.hasNext()) {
            Map<String, String> ContactNameDisplay =
                    (Map<String, String>) iterator.next();
            String name = ContactNameDisplay.get("name");
            Pattern pattern = Pattern.compile(input);
            Matcher matcher = pattern.matcher(name);
            if(matcher.find()){
                ContactNameDisplay.put("name", name);
                ContactFilterDisplay.add(ContactNameDisplay);
            }
            displayListView(ContactFilterDisplay);
        }
    }
}
