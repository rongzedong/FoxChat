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
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.wangyeming.custom.ContactDetailAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


public class ContactDetailActivity extends Activity {

    protected List<Map<String, Object>> ContactDisplay = new ArrayList<Map<String, Object>>();
    protected ListView lt2;
    protected TextView tv1;
    protected String contactName;
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //display_name
            ContactsContract.CommonDataKinds.Phone.NUMBER, //data1
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, //photo_id
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI,//
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,  //contact_id
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY //sort_key
    };
    private static final Map<String, String> PHONE_TYPE = new HashMap<String, String>() {
        {
            put("0", "自定义");
            put("1", "住宅");
            put("2", "手机");
            put("3", "单位");
            put("4", "传真");
            put("5", "");
            put("6", "");
            put("7", "其他");
            put("12", "总机");
        }
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
    public void getContactMessage() {
        Intent intent = getIntent();
        String ContactId = intent.getStringExtra("ContactId");
        //System.out.println(ContactId);
        readContactName(ContactId);
        Uri photo_uri = readContactPhoneBim(ContactId);
        readContactPhoneNum(ContactId);
    }

    //读取联系人姓名
    public void readContactName(String ContactId) {
        Cursor cursorID = getContentResolver().query(CONTENT_URI, PHONES_PROJECTION, PHONES_PROJECTION[4] + "=" + ContactId, null, "sort_key");
        cursorID.moveToNext();
        contactName = cursorID.getString(cursorID.getColumnIndex(PHONES_PROJECTION[0]));
        cursorID.close();
    }

    //读取联系人头像
    public Uri readContactPhoneBim(String ContactId) {
        ContentResolver cr = getContentResolver();//得到ContentResolver对象
        Cursor cursorID = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, PHONES_PROJECTION[4] + "=" + ContactId, null, "sort_key");
        cursorID.moveToFirst();
        String photo_string = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
        //System.out.println("this 3" +photo_string);
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
        cursorID.close();
        return photo_uri;
    }

    //读取联系人手机号
    public void readContactPhoneNum(String ContactId) {
        ContentResolver cr = getContentResolver();//得到ContentResolver对象
        Cursor phoneID = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                PHONES_PROJECTION[4] + "=" + ContactId, null, null);//设置手机号光标
        String isFirstNum = "1";
        while (phoneID.moveToNext()) {
            Map<String, Object> PhoneNumMap = new HashMap<String, Object>();
            String phoneNumber = phoneID.getString(phoneID.getColumnIndex(PHONES_PROJECTION[1]));
            String phoneNumberType = phoneID.getString(phoneID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            //System.out.println(phoneNumberType);
            String phoneNumberTypeTrans = PHONE_TYPE.get(phoneNumberType);
            //System.out.println("手机号： "+ phoneNumber + " 手机号类型： "+ phoneNumberType + " ");
            //PhoneNumMap.put("phone_icon", phoneIconMap.get(isFirstNum));
            PhoneNumMap.put("phone_png", R.drawable.type_icon_phone);
            PhoneNumMap.put("phone_num", phoneNumber);
            PhoneNumMap.put("phone_type", phoneNumberTypeTrans);
            PhoneNumMap.put("phone_location", "北京");
            PhoneNumMap.put("message_png", R.drawable.ic_send_sms_p);
            ContactDisplay.add(PhoneNumMap);
            isFirstNum = "0";
        }
        phoneID.close();
    }

    //设置lisView布局
    public void displayListView() {
        tv1 = (TextView) findViewById(R.id.contactName);
        tv1.setText(contactName);
        lt2 = (ListView) findViewById(R.id.list2);
        if (ContactDisplay == null) {
            //System.out.println("ContactDisplay is nil");
        }
        System.out.println(ContactDisplay.size());
        ContactDetailAdapter adapter = new ContactDetailAdapter(ContactDisplay, this);
        // SimpleAdapter adapter = new SimpleAdapter(this, ContactDisplay,
               //  R.layout.list_item2, new String[]{"phone_png", "phone_num", "phone_type", "phone_location", "message_png"}, new int[]{R.id.phone, R.id.phone_num, R.id.phone_type, R.id.phone_location, R.id.mes1});
        lt2.setAdapter(adapter);
    }

    //返回主页面按钮
    public void backToMain(View view) {
        Intent intent = new Intent(this, LineActivity.class);
        startActivity(intent);
    }

    //编辑联系人详细信息
    public void editContactDetail(View view) {
        Intent intent = new Intent(this, EditContactDetailActivity.class);
        startActivity(intent);
    }

    //发送联系人详细信息
    public void sendContactDetail(View view){

    }

    //收藏联系人
    public void starContact(){

    }
}
