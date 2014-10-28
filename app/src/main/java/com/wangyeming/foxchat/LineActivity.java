package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


public class LineActivity extends Activity {

    protected List<Map<String, String>> ContactDisplay = new ArrayList<Map<String, String>>();
    protected ListView lt1;

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
        runnableGetContacts();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.line, menu);
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
            ContactDisplay.add(ContactNameDisplay);
            System.out.println("name "+ contact +" ");
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
    public void displayListView(){
        lt1 = (ListView) findViewById(R.id.list1);
        if(ContactDisplay == null){
            System.out.println("ContactDisplay is nil");
        }
        SimpleAdapter adapter = new SimpleAdapter(this, ContactDisplay,
                R.layout.list_item, new String[]{"name"}, new int[]{R.id.name});
        lt1.setAdapter(adapter);
    }

    public void runnableGetContacts(){
        System.out.println("start");
        Runnable runnable = new Runnable() {
            @Override
            public void run(){
                System.out.println("thread start!");
                getPhoneContacts();
                try {
                    displayListView();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

}
