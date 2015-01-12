package com.wangyeming.foxchat;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.wangyeming.custom.adapter.SmsConversationAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


public class MessageConversationActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SmsConversationAdapter mAdapter;
    private Toolbar toolbar;
    private String thread_id;
    //机主头像Uri
    private Uri ownerAvaterUri;
    //ContentResolver
    private ContentResolver cr;
    //存放draft信息
    private Map<String, Object> draftMap = new HashMap<>();
    //对话列表
    private List<Map<String, Object>> conversationDisplay = new ArrayList<>();
    private Long firstDate; //记录当前显示日期最久的短信的日期
    private final int SMS_NUM = 10;
    private final String SMS_URI_ALL = "content://sms/";  //全部短信
    private static final String[] SMS_PROJECTION = new String[]{
            "_id",           //0  短信序号
            "thread_id",     //1  对话的序号, 与同一个手机号互发的短信，其序号是相同的
            "address",       //2  另一方的地址，即手机号
            "person",        //3  发件人id，如果发件人在通讯录中则为contact_id，陌生人为0
            "date",          //4  接收日期，long型，如1346988516，可以对日期显示格式进行设置
            "date_sent",     //5  发送日期
            "read",          //6  是否阅读0未读，1已读
            "status",        //7  短信状态-1未获取状态，0完成,32发送中，64失败
            "type",          //8  短信类型0是所有的短信，1是接收到的，2是已发出,3是草稿箱，4是发件箱外的，5发送失败，6发送队列中
            "reply_path_present", //9
            "subject",       //10  短信的子类，如果存在的话 类型：Text
            "body",          //11  短信具体内容  类型：Text
            "service_center",//12  短信服务中心号码编号，如+8613800755500
            "locked",        //13  短信是否被锁
            "protocol",      //14  协议0SMS_RPOTO短信，1MMS_PROTO彩信
            //"creator",    //    （报错）发送短信的app的包名
            "error_code",    //15  错误代码
            "seen",          //16  用户是否阅读过短信？决定是否显示通知
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_conversation);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message_conversation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init() {
        cr = getContentResolver();
        setToolbar();
        ownerAvaterUri = getOwnerAvatar();//获取机主头像
        setmRecyclerView();
    }


    //设置RecyclerView
    public void setmRecyclerView() {
        Intent intent = getIntent();
        thread_id = intent.getStringExtra("thread_id");
        mRecyclerView = (RecyclerView) findViewById(R.id.mes_con_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        //mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setReverseLayout(true);  //倒序排列
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SmsConversationAdapter(this, conversationDisplay);
        mRecyclerView.setAdapter(mAdapter);
        //新线程获取sms信息
        new Thread(new Runnable() {

            @Override
            public void run() {
                getConversationMes();
                Message message = Message.obtain();
                message.obj = "ok";
                MessageConversationActivity.this.handler1.sendMessage(message);
            }
        }).start();
    }

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            mAdapter.notifyDataSetChanged();
            setDraft();
            if (mAdapter.getItemCount() > 0) {
                //mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1); //默认滑动到底部
            }
            setRecyclerViewListener();
        }
    };

    //设置RecyclerView的监听
    public void setRecyclerViewListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                if (scrollState == 0) {
                }
            }
        });
    }

    //设置tooolbar
    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.mes_con_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false); //隐藏toolBar标题
    }

    //获取对话信息
    public void getConversationMes() {
        Uri allURI = Uri.parse(SMS_URI_ALL);
        Cursor cursor = cr.query(allURI, SMS_PROJECTION,
                "thread_id=?", new String[]{thread_id}, "date DESC"); //按时间升序
        int i = 0;
        while (cursor.moveToNext()) {
            Map<String, Object> mesMap = new HashMap<>();
            String address = cursor.getString(cursor.getColumnIndex("address"));
            int person = cursor.getInt(cursor.getColumnIndex("person"));
            Long date = cursor.getLong(cursor.getColumnIndex("date"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            //时间转换
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
            String LgTime = sdFormat.format(date);
            Boolean isDraft = type == 3;
            Boolean isFail = type == 5;
            Boolean isSent = type != 1;
            Uri imageUri = null;
            Log.d("wym", "address " + address + " person " + person + " date " + LgTime + " type " + type);
            if (isSent) {
                //发送的短信
                // imageUri为机主的头像
                imageUri = ownerAvaterUri;
            } else {
                //接收的短信--address不可能为空
                //查找对方的头像
                imageUri = person == 0 ? addressToImage(address) : idToImage(person, address);
            }
            if (isDraft) {
                //如果是草稿
                draftMap.put("body", body);
            } else {
                mesMap.put("date", LgTime);
                mesMap.put("isFail", isFail);
                mesMap.put("body", body);
                mesMap.put("isSent", isSent);
                mesMap.put("imageUri", imageUri);
                Log.d("wym", "address " + address + " person " + person + " date " + LgTime + " isFail " + isFail
                        + " body " + body + " isSent " + isSent + " imageUri " + imageUri);
                conversationDisplay.add(mesMap);
            }
            /*
            if (i > SMS_NUM - 1) {
                firstDate = date;
                //只提取日期最近的短信信息
                break;
            }
            */
            i++;
        }
        cursor.close();
    }

    //通过手机号查找联系人头像
    public Uri addressToImage(String address) {
        address = address.replaceAll(" ", ""); //去除电话号码的空格
        String regex = "\\+\\d\\d";
        address = address.replaceFirst(regex, ""); //去除电话号码的国家区号
        Log.d("wym", "------------- " + address);
        Cursor cursor = cr.query(CONTENT_URI, new String[]{"photo_uri"},
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{address}, null, null);
        Uri imageUri = null;
        if (cursor.moveToFirst()) {
            String photoString = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
            if (photoString != null) {
                imageUri = Uri.parse(photoString);
            }
        }
        cursor.close();
        return imageUri;
    }

    //通过联系人rawContactId查找联系人头像
    public Uri idToImage(int person, String address) {
        Cursor cursor = cr.query(CONTENT_URI, new String[]{"photo_uri"},
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{address}, null, null);
        Uri imageUri = null;
        if (cursor.moveToFirst()) {
            String photoString = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
            if (photoString != null) {
                imageUri = Uri.parse(photoString);
            }
        } else {
            imageUri = addressToImage(address);
        }
        cursor.close();
        return imageUri;
    }

    //设置草稿内容显示
    public void setDraft() {
        EditText editText = (EditText) findViewById(R.id.sendBox);
        editText.setText((String) draftMap.get("body"));
    }

    //获取机主头像
    public Uri getOwnerAvatar() {
        Cursor cursor = cr.query(
                ContactsContract.Profile.CONTENT_URI, new String[]{"photo_uri"}, null, null, null);
        Uri imageUri = null;
        if(cursor.moveToFirst() ) {
            String photoString = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
            if (photoString != null) {
                imageUri = Uri.parse(photoString);
            }
        }
        cursor.close();
        return imageUri;
    }
}
