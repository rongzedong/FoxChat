package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wangyeming.custom.ContactDetailAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

/**
 * 读取联系人详细信息的Activity
 *
 * @author 王小明
 * @data 2014/11/28
 */

public class ContactDetailActivity extends Activity {

    protected List<Map<String, Object>> contactDisplay = new ArrayList<Map<String, Object>>(); //手机号（手机号类型、归属地等信息）
    protected ListView lt2;
    protected TextView tv1;
    protected String contactName;
    protected Long contactId;
    protected Long rawContactId;
    protected Uri photoUri;
    protected ContentResolver cr;
    protected Button starButton;
    protected TextView starTextView;
    protected boolean isStarred = false;
    protected boolean hasImage; //是否有头像
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //display_name
            ContactsContract.CommonDataKinds.Phone.NUMBER, //data1
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, //photo_id
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI,//
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,  //contact_id
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY, //sort_key
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.STARRED,
            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_detail, menu);
        return true;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        clearData(); //清除缓存数据
        reInit();
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

    public void init() {
        cr = getContentResolver();
        getContactMessage();
        System.out.println("contactId "+contactId);
        System.out.println("rawContactId "+rawContactId);
        displayListView(); //显示listView
        displayStarred(); //设置收藏/未收藏的图标
    }

    public void reInit() {
        readContactName(rawContactId);
        readContactBim(rawContactId);
        readContactPhoneNum(rawContactId);
        displayListView(); //显示listView
        displayStarred(); //设置收藏/未收藏的图标
    }

    public void clearData() {
        contactDisplay.clear();
    }

    //读取联系人信息
    public void getContactMessage() {
        Intent intent = getIntent();
        contactId = intent.getLongExtra("ContactId", 1);
        getRawContactId(contactId);
        readContactName(rawContactId);
        readContactBim(rawContactId);
        readContactPhoneNum(rawContactId);
    }

    //获取联系人rawContactId
    public void getRawContactId(Long contactId) {
        Cursor cursorID = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, PHONES_PROJECTION[4] + "=" + contactId, null, "sort_key");
        cursorID.moveToFirst();
        rawContactId = cursorID.getLong(cursorID.getColumnIndex(PHONES_PROJECTION[6]));
        cursorID.close();
    }

    //读取联系人姓名
    public void readContactName(Long rawContactId) {
        Cursor cursorID = getContentResolver().query(CONTENT_URI, PHONES_PROJECTION, PHONES_PROJECTION[6] + "=" + rawContactId, null, "sort_key");
        cursorID.moveToNext();
        // contactName = cursorID.getString(cursorID.getColumnIndex(PHONES_PROJECTION[0]));
        contactName = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        cursorID.close();
    }

    //读取联系人头像
    public void readContactBim(Long rawContactId) {
        Cursor cursorID = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, PHONES_PROJECTION[6] + "=" + rawContactId, null, "sort_key");
        cursorID.moveToFirst();
        int starred = cursorID.getInt(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED));
        // System.out.println("starred " + starred);
        isStarred = starred == 1 ? true : false;
        String photo_string = cursorID.getString(cursorID.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
        //System.out.println("this 3" +photo_string);
        if (photo_string == null) {
            //没有头像
            hasImage = false;
        } else {
            photoUri = Uri.parse(photo_string);
            ImageView imageView = (ImageView) findViewById(R.id.pic1);
            imageView.setImageURI(photoUri);
            hasImage = true;
        }
        //InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), photo_uri);
        //Bitmap bmp_head = BitmapFactory.decodeStream(input);
        cursorID.close();
    }

    //读取联系人手机号
    public void readContactPhoneNum(Long rawContactId) {
        ContentResolver cr = getContentResolver();//得到ContentResolver对象
        Cursor phoneID = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                PHONES_PROJECTION[6] + "=" + rawContactId, null, null);//设置手机号光标
        while (phoneID.moveToNext()) {
            // Map<String, Object> PhoneNumMap = new HashMap<String, Object>();
            Map<String, Object> phoneNumMap = new HashMap<String, Object>();
            String phoneNumber = phoneID.getString(phoneID.getColumnIndex(PHONES_PROJECTION[1]));
            int phoneNumberTypeId = phoneID.getInt(phoneID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            String phoneNumberLabel = phoneID.getString(phoneID.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
            // System.out.println("LABEL "+ phoneNumberLabel);
            // String phoneNumberTypeTrans = PHONE_TYPE.get(phoneNumberType);
            String[] phoneNumberTypeArray = getResources().getStringArray(R.array.phone_type);
            String phoneNumberType = phoneNumberTypeArray[phoneNumberTypeId];
            //System.out.println("手机号： " + phoneNumber + " phoneNumberTypeId " + phoneNumberTypeId +" 手机号类型： " + phoneNumberType + " ");
            phoneNumMap.put("phone_png", R.drawable.ic_local_phone_black);
            phoneNumMap.put("phone_num", phoneNumber);
            phoneNumMap.put("phone_type_id", phoneNumberTypeId);
            phoneNumMap.put("phone_type", phoneNumberType);
            phoneNumMap.put("phone_location", "北京");
            phoneNumMap.put("phone_label", phoneNumberLabel);
            phoneNumMap.put("message_png", R.drawable.ic_message_black);
            contactDisplay.add(phoneNumMap);
        }
        phoneID.close();
    }

    //设置lisView布局
    public void displayListView() {
        tv1 = (TextView) findViewById(R.id.contactName);
        tv1.setText(contactName);
        lt2 = (ListView) findViewById(R.id.list_contact_phone_display);
        if (contactDisplay == null) {
            //System.out.println("contactDisplay is nil");
        }
        ContactDetailAdapter adapter = new ContactDetailAdapter(contactDisplay, this);
        lt2.setAdapter(adapter);
    }

    //设置收藏/未收藏的图标
    public void displayStarred() {
        starButton = (Button) findViewById(R.id.starButton);
        starTextView = (TextView) findViewById(R.id.starText);
        if (isStarred) {
            starButton.setBackground(this.getResources().getDrawable(R.drawable.favorite_icon_normal_dark));
            starTextView.setText("取消收藏");
        } else {
            starButton.setBackground(this.getResources().getDrawable(R.drawable.unfavorite_icon_normal_dark));
            starTextView.setText("收藏");
        }
    }

    //返回主页面按钮
    public void backToMain(View view) {
        this.finish();
        // Intent intent = new Intent(this, LineActivity.class);
        // startActivity(intent);
    }

    //编辑联系人详细信息
    public void editContactDetail(View view) {
        Intent intent = new Intent(this, EditContactDetailActivity.class);
        intent.putExtra("ContactId", contactId);
        intent.putExtra("RawContactId",rawContactId);
        intent.putExtra("hasImage", hasImage);
        intent.setData(photoUri);
        intent.putExtra("ContactDisplay", (Serializable) contactDisplay);
        intent.putExtra("contactName", contactName);
        startActivity(intent);
    }

    //发送联系人详细信息
    public void sendContactDetail(View view) {

    }

    //收藏联系人
    public int starContact(View view) {
        Uri rawContactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId);
        Uri ContactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, contactId);
        ContentValues values = new ContentValues();
        int count;
        if (isStarred) {
            values.put(ContactsContract.CommonDataKinds.Phone.STARRED, 0);
            starButton.setBackground(this.getResources().getDrawable(R.drawable.unfavorite_icon_normal_dark));
            starTextView.setText("收藏");
            count = cr.update(rawContactUri, values, null, null);
            //String Where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND   " + ContactsContract.Data.MIMETYPE + " = ?";
            // cr.update(rawContactUri, values, null, null);
            isStarred = false;
        } else {
            values.put(ContactsContract.CommonDataKinds.Phone.STARRED, 1);
            starButton.setBackground(this.getResources().getDrawable(R.drawable.favorite_icon_normal_dark));
            starTextView.setText("取消收藏");
            count = cr.update(rawContactUri, values, null, null);
            //String Where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND   " + ContactsContract.Data.MIMETYPE + " = ?";
            //cr.update(rawContactUri, values, null, null);
            isStarred = true;
        }
        return count;
    }
}
