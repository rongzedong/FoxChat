package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


public class LineActivity extends Activity {

    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //display_name
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);
        System.out.println("start");
        Runnable runnable = new Runnable() {
            @Override
            public void run(){
                System.out.println("thread start!");
                try {
                    getPhoneContacts();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
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
        Cursor cursorID = cr.query(CONTENT_URI, PHONES_PROJECTION, null, null, null);// 设置联系人光标
        while(cursorID.moveToNext()){
            String string = "";
            int nameFieldColumnIndex = cursorID.getColumnIndex(PHONES_PROJECTION[0]);//返回display_name对应列的index--0
            String contact = cursorID.getString(nameFieldColumnIndex);//获取联系人姓名
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
        }
        cursorID.close();
    }
}
