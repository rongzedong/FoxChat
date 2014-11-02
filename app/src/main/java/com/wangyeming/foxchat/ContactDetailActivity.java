package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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


public class ContactDetailActivity extends Activity {

    protected List<Map<String, String>> ContactDisplay = new ArrayList<Map<String, String>>();
    protected ListView lt2;
    Map<String, Map<String, String>> PhoneNumMap = new HashMap<String, Map<String, String>>();
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
        setContentView(R.layout.activity_contact_detail);
        displayListView(ContactDisplay);
        getContactMessage();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_detail, menu);
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

    //读取联系人信息
    public void getContactMessage(){
        Intent intent = getIntent();
        String ContactId = intent.getStringExtra("ContactId");
        System.out.println(ContactId);
        Uri photo_uri = readContactPhoneBim(ContactId);
        //PhoneNumMap = readContactPhoneNum(ContactId);
    }

    //读取联系人头像
    public Uri readContactPhoneBim(String ContactId) {
        ContentResolver cr = getContentResolver();//得到ContentResolver对象
        Cursor cursorID = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, PHONES_PROJECTION[4] + "=" + ContactId, null, "sort_key");
        cursorID.moveToFirst();
        String photo_string = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
        System.out.println("this 3" +photo_string);
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
    public Map<String, Map<String, String>> readContactPhoneNum(String ContactId) {
        ContentResolver cr = getContentResolver();//得到ContentResolver对象
        Map<String, Map<String, String>> phone_num_map = new HashMap<String, Map<String, String>>();
        Cursor phoneID = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                PHONES_PROJECTION[4] + "=" + ContactId, null, null);//设置手机号光标
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
    public void displayListView(List<Map<String, String>> Display ){
        lt2 = (ListView) findViewById(R.id.list2);
        if(ContactDisplay == null){
            System.out.println("ContactDisplay is nil");
        }
        SimpleAdapter adapter = new SimpleAdapter(this, Display,
                R.layout.list_item1, new String[]{"phone_num","phone_type","phone_location"}, new int[]{R.id.phone_num,R.id.phone_type, R.id.phone_location});
        lt2.setAdapter(adapter);
    }
}
