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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


public class ContactDetailActivity extends Activity {

    protected List<Map<String, Object>> ContactDisplay = new ArrayList<Map<String, Object>>();
    protected ListView lt2;
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
        getContactMessage();//获取联系人信息
        displayListView();
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
        readContactPhoneNum(ContactId);
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
        ImageView imageView = (ImageView) findViewById(R.id.pic1);
        imageView.setImageURI(photo_uri);
        return photo_uri;
    }

    //读取联系人手机号
    public void readContactPhoneNum(String ContactId) {
        ContentResolver cr = getContentResolver();//得到ContentResolver对象
        Cursor phoneID = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                PHONES_PROJECTION[4] + "=" + ContactId, null, null);//设置手机号光标
        while (phoneID.moveToNext()) {
            Map<String, Object> PhoneNumMap = new HashMap<String, Object>();
            String phoneNumber = phoneID.getString(phoneID.getColumnIndex(PHONES_PROJECTION[1]));
            String phoneNumberType = phoneID.getString(phoneID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            System.out.println("手机号： "+ phoneNumber + " 手机号类型： "+ phoneNumberType + " ");
            PhoneNumMap.put("phone_icon", R.drawable.ios8_dialer_icon);
            PhoneNumMap.put("phone_num",phoneNumber);
            PhoneNumMap.put("phone_type", phoneNumberType);
            PhoneNumMap.put("phone_location", "北京");
            ContactDisplay.add(PhoneNumMap);
        }
        phoneID.close();
    }

    //设置lisView布局
    public void displayListView(){
        lt2 = (ListView) findViewById(R.id.list2);
        if(ContactDisplay == null){
            System.out.println("ContactDisplay is nil");
        }
        for(Map<String, Object> map: ContactDisplay){
            System.out.println("1111111111111111111111");
            System.out.println(map.get("phone_num"));
            System.out.println(map.get("phone_type"));
            System.out.println(map.get("phone_location"));
        }
        SimpleAdapter adapter = new SimpleAdapter(this, ContactDisplay,
                R.layout.list_item2, new String[]{"phone_icon", "phone_num","phone_type","phone_location"}, new int[]{R.id.phone1, R.id.phone_num,R.id.phone_type, R.id.phone_location});
        lt2.setAdapter(adapter);
    }
}
