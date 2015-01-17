package com.wangyeming.foxchat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangyeming.custom.adapter.SmsListRecyclerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


/**
 * 读取联系人短信的Fragment
 *
 * @author 王小明
 * @data 2015/01/11
 */
public class MessageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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
    private static final String[] SMS_CONVERSATIONS_PROJECTION = new String[]{
            "msg_count",     //对话中message的数目
            "snippet",       //message前45个字符
    };
    private final String SMS_URI_ALL = "content://sms/";  //全部短信
    private final String SMS_URI_INBOX = "content://sms/inbox";  //收件箱短信
    private final String SMS_URI_SENT = "content://sms/sent";  //已发送短信
    private final String SMS_URI_DRAFT = "content://sms/draft";  //草稿箱短信
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private SmsListRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // 当前activity
    private Activity currentActivity;
    // 当前view
    private View currentView;
    //ContentResolver
    private ContentResolver cr;
    //短信存储
    private List<Map<String, Object>> smsDisplay = new ArrayList<>();
    //存储thread_id对应的短信数量
    private Map<String, Integer> threadIdMap = new HashMap<>();
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            Log.d(MessageFragment.this.getTag(), "--------------------------");
            mAdapter.notifyDataSetChanged();
        }
    };

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // specify an adapter (see also next example)
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void init() {
        currentActivity = getActivity(); //获取当前activity
        currentView = getView(); //获取当前view
        cr = currentActivity.getContentResolver(); //获取contact resolver
        getSmsInPhone();
        setRecyclerView();
    }

    //设置Recycller
    public void setRecyclerView() {
        mRecyclerView = (RecyclerView) currentView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(currentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new SmsListRecyclerAdapter(currentActivity, smsDisplay);
        mRecyclerView.setAdapter(mAdapter);
    }

    //获取手机里所有短信
    public void getSmsInPhone() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                getSmsMes();
                Message message = Message.obtain();
                message.obj = "ok";
                MessageFragment.this.handler1.sendMessage(message);
            }
        }).start();
        //获取所有短信
        //getAllSms();
        //获取已发送短信
        //getSentSms();
        //获取收件箱短信
        //getInboxSms();
        //获取草稿箱短信
        //getDraftSms();
    }

    //获取对话短信信息
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void getConversationsSms() {
        Cursor cursor = cr.query(Telephony.Sms.Conversations.CONTENT_URI,
                SMS_CONVERSATIONS_PROJECTION, null, null, Telephony.Sms.Conversations.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()) {
            int msg_count = cursor.getInt(cursor.getColumnIndex("msg_count"));
            String snippet = cursor.getString(cursor.getColumnIndex("snippet"));
            Log.d(this.getTag(), " msg_count " + msg_count + " snippet " + snippet);
        }

        cursor.close();
    }

    //获取短信列表所需信息
    public void getSmsMes() {
        List<String> failList = new ArrayList<>();
        List<String> threadIdPos = new ArrayList<>();
        Uri allURI = Uri.parse(SMS_URI_ALL);
        //按照对话提取短信
        Cursor cursor = cr.query(allURI, SMS_PROJECTION, null, null, "date DESC");
        while (cursor.moveToNext()) {
            String thread_id = cursor.getString(cursor.getColumnIndex("thread_id"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            int person = cursor.getInt(cursor.getColumnIndex("person"));
            Long date = cursor.getLong(cursor.getColumnIndex("date"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            //Log.d(this.getTag(),"thread_id " + thread_id +" type " + type + " body " + body);
            if (type == 5) {
                failList.add(thread_id);
            }
            //判断是否存在已扫描的对话中
            if (threadIdMap.containsKey(thread_id)) {
                threadIdMap.put(thread_id, threadIdMap.get(thread_id) + 1);
            } else {
                threadIdPos.add(thread_id);
                threadIdMap.put(thread_id, 1);
                //时间转换
                SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
                String LgTime = sdFormat.format(new Date(date));
                String body_header = body.length() > 20 ? body.substring(0, 19) : body;
                Boolean isDraft = type == 3;
                Map<String, Object> smsMap = new HashMap<>();
                smsMap.put("thread_id", thread_id);
                smsMap.put("date", LgTime);
                smsMap.put("content", body_header);
                smsMap.put("isDraft", isDraft);
                String name = "";
                if (address != null) {
                    name = person == 0 ? addressToName(address) : idToName(person, address);
                    smsMap.put("contact", name);
                } else {
                    //address==null,表示来自草稿
                    Cursor cursorNew = cr.query(Uri.parse("content://mms-sms/canonical-address/" + thread_id)
                            , new String[]{"address"}, null, null, null);
                    if (cursorNew.moveToFirst()) {
                        address = cursorNew.getString(cursorNew.getColumnIndex("address"));
                        name = addressToName(address);
                        smsMap.put("contact", name);
                    }
                    cursorNew.close();
                }
                smsDisplay.add(smsMap);
            }
        }
        cursor.close();
        for (int i = 0; i < threadIdPos.size(); i++) {
            smsDisplay.get(i).put("number", threadIdMap.get(threadIdPos.get(i)));
            smsDisplay.get(i).put("hasFail", failList.contains(threadIdPos.get(i)) ? 1 : 0);
        }
    }

    //获取所有短信
    public void getAllSms() {
        Uri allURI = Uri.parse(SMS_URI_ALL);
        //按照对话提取短信
        Cursor cursor = cr.query(allURI, SMS_PROJECTION, null, null, "thread_id");
        //第一遍循环，获取所有的thread_id值
        while (cursor.moveToNext()) {
            String thread_id = cursor.getString(cursor.getColumnIndex("thread_id"));
            if (threadIdMap.containsKey(thread_id)) {
                threadIdMap.put(thread_id, threadIdMap.get(thread_id) + 1);
            } else {
                threadIdMap.put(thread_id, 1);
            }
        }
        cursor.close();
        //Log.d(this.getTag(), "size " + threadIdMap.keySet().size());
        for (String key : threadIdMap.keySet()) {
            String name = "";
            //Log.d(this.getTag(), "thread_id " + key + " value " + threadIdMap.get(key));
            //指定thread_id提取短信，默认按照日期排序
            cursor = cr.query(allURI, SMS_PROJECTION, "thread_id=?", new String[]{key}, null);
            Map<String, Object> smsMap = new HashMap<>();
            cursor.moveToFirst();
            //获取详细信息
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String thread_id = cursor.getString(cursor.getColumnIndex("thread_id"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            int person = cursor.getInt(cursor.getColumnIndex("person"));
            Long date = cursor.getLong(cursor.getColumnIndex("date"));
            int protocol = cursor.getInt(cursor.getColumnIndex("protocol"));
            int read = cursor.getInt(cursor.getColumnIndex("read"));
            int status = cursor.getInt(cursor.getColumnIndex("status"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            Boolean reply_path_present = cursor.getInt(cursor.getColumnIndex("reply_path_present")) > 0;
            String subject = cursor.getString(cursor.getColumnIndex("subject"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            String service_center = cursor.getString(cursor.getColumnIndex("service_center"));
            int locked = cursor.getInt(cursor.getColumnIndex("locked"));
            Long date_sent = cursor.getLong(cursor.getColumnIndex("date_sent"));
            int error_code = cursor.getInt(cursor.getColumnIndex("error_code"));
            Boolean seen = cursor.getInt(cursor.getColumnIndex("seen")) > 0;
            //时间转换
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
            String LgTime = sdFormat.format(date);
            String body_header = body.length() > 20 ? body.substring(0, 19) : body;
            Boolean isDraft = type == 3 ? true : false;
            smsMap.put("date", LgTime);
            smsMap.put("number", threadIdMap.get(key));
            smsMap.put("content", body_header);
            smsMap.put("isDraft", isDraft);
            if (address != null) {
                name = person == 0 ? addressToName(address) : idToName(person, address);
                smsMap.put("contact", name);
            } else {
                //address==null,表示来自草稿
                Cursor cursorNew = cr.query(Uri.parse("content://mms-sms/canonical-address/" + thread_id)
                        , new String[]{"address"}, null, null, null);
                if (cursorNew.moveToFirst()) {
                    address = cursorNew.getString(cursorNew.getColumnIndex("address"));
                    name = addressToName(address);
                    smsMap.put("contact", name);
                }
                cursorNew.close();
            }
            //Log.d(this.getTag(), "date " + LgTime + " number" + threadIdMap.get(key)
            //        + " contact " + name + " content " + body_header + " isDraft " + isDraft);
            /*
             Log.d(this.getTag(), "id " + id + " thread_id " + thread_id + " address " + address
                            + " person " + person + " date " + date + " protocol " + protocol
                            + " read " + read + " status " + status + " type " + type
                            + " reply_path_present " + reply_path_present + " subject "
                            + subject + " body " + body + " service_center " + service_center
                           + " locked " + locked
            );
            */
            cursor.close();
            smsDisplay.add(smsMap);
        }
    }

    //获取已发送短信
    public void getSentSms() {
        Uri sentURI = Uri.parse(SMS_URI_SENT);
        Cursor cursor = cr.query(sentURI, SMS_PROJECTION, null, null, "thread_id");
        cursor.close();
    }

    //获取收件箱短信
    public void getInboxSms() {
        Uri inboxURI = Uri.parse(SMS_URI_INBOX);
        Cursor cursor = cr.query(inboxURI, SMS_PROJECTION, null, null, null);
        while (cursor.moveToNext()) {
            String body = cursor.getString(cursor.getColumnIndex("body"));
            Log.d(this.getTag(), "body " + body);
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            Log.d(this.getTag(), "_id  " + id);
            String address = cursor.getString(cursor.getColumnIndex("address"));
            Log.d(this.getTag(), "address  " + address);
            String thread_id = cursor.getString(cursor.getColumnIndex("thread_id"));
            Log.d(this.getTag(), "thread_id  " + thread_id);

        }
    }

    //获取草稿箱短信
    public void getDraftSms() {
        Uri draftURI = Uri.parse(SMS_URI_DRAFT);
        Cursor cursor = cr.query(draftURI, SMS_PROJECTION, null, null, null);
        while (cursor.moveToNext()) {
            String body = cursor.getString(cursor.getColumnIndex("body"));
            Log.d(this.getTag(), "body " + body);
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            Log.d(this.getTag(), "_id  " + id);
            String address = cursor.getString(cursor.getColumnIndex("address"));
            Log.d(this.getTag(), "address  " + address);
            String service_center = cursor.getString(cursor.getColumnIndex("service_center"));
            Log.d(this.getTag(), "service_center  " + service_center);

        }
    }

    //id转通讯录姓名
    public String idToName(int person, String address) {
        Cursor cursor = cr.query(ContactsContract.RawContacts.CONTENT_URI, new String[]{"display_name"},
                ContactsContract.RawContacts._ID + "=" + person, null, null);
        String name = "";
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex("display_name"));
        } else {
            name = addressToName(address);
        }
        cursor.close();
        return name;
    }

    //address转通讯录姓名
    public String addressToName(String address) {
        //Log.d(this.getTag(), "address1 " + address);
        address = address.replaceAll(" ", "");//去除电话号码的空格
        String regex = "\\+\\d\\d";
        address = address.replaceFirst(regex, ""); //去除电话号码的国家区号
        //Log.d(this.getTag(), "address2 " + address);
        Cursor cursor = cr.query(CONTENT_URI, new String[]{"display_name"},
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{address}, null, null);
        String name = "";
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex("display_name"));
        } else {
            name = address;
        }
        cursor.close();
        return name;
    }

    //item事件
    public void itemEvent() {
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
