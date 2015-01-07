package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String SMS_URI_ALL = "content://sms/";  //全部短信
    private final String SMS_URI_INBOX = "content://sms/inbox";  //收件箱短信
    private final String SMS_URI_SENT = "content://sms/sent";  //已发送短信
    private final String SMS_URI_DRAFT = "content://sms/draft";  //草稿箱短信

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    // 当前activity
    private Activity currentActivity;
    // 当前view
    private View currentView;
    //ContentResolver
    private ContentResolver cr;
    //短信存储
    private List<Map<String, Object>> smsDisplay = new ArrayList<>();
    //
    private Map<String, Integer> threadIdMap = new HashMap<>();

    private static final String[] SMS_PROJECTION = new String[]{
            "_id",           //Column ID = 0  短信序号
            "thread_id",     //Column ID = 1  对话的序号, 与同一个手机号互发的短信，其序号是相同的
            "address",       //Column ID = 2  发件人地址，即手机号
            "person",        //Column ID = 3  发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
            "date",          //Column ID = 4  日期，long型，如1346988516，可以对日期显示格式进行设置
            "protocol",      //Column ID = 5  协议0SMS_RPOTO短信，1MMS_PROTO彩信
            "read",          //Column ID = 6  是否阅读0未读，1已读
            "status",        //Column ID = 7  短信状态-1接收，0complete,64spending,128failed
            "type",          //Column ID = 8  短信类型1是接收到的，2是已发出,3是发出的
            "reply_path_present", //Column ID = 9
            "subject",       //Column ID = 10  短信的子类，如果存在的话 类型：Text
            "body",          //Column ID = 11  短信具体内容  类型：Text
            "service_center",//Column ID = 12  短信服务中心号码编号，如+8613800755500
            "locked",        //Column ID = 13  短信是否被锁


    };

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

    public MessageFragment() {
        // Required empty public constructor
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

    public void init() {
        currentActivity = getActivity(); //获取当前activity
        currentView = getView(); //获取当前view
        cr = currentActivity.getContentResolver(); //获取contact resolver
        getSmsInPhone();
    }

    //获取手机里所有短信
    public void getSmsInPhone() {
        //获取所有短信
        getAllSms();
        //获取已发送短信
        //getSentSms();
        //获取收件箱短信
        //getInboxSms();
        //获取草稿箱短信
        //getDraftSms();
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
                threadIdMap.put(thread_id, 0);
            }
        }
        cursor.close();
        Log.d(this.getTag(), "size " + threadIdMap.keySet().size());
        for (String key : threadIdMap.keySet()) {
            Log.d(this.getTag(), "thread_id " + key + " value " + threadIdMap.get(key));
            //指定thread_id提取短信，默认按照日期排序
            cursor = cr.query(allURI, SMS_PROJECTION, "thread_id=?", new String[]{key}, "date");
            cursor.moveToLast();
            Map<String, Object> smsMap = new HashMap<String, Object>();
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
            Log.d(this.getTag(), "id " + id + " thread_id " + thread_id + " address " + address
                            + " person " + person + " date " + date + " protocol " + protocol + " read " + read
                            + " status " + status + " type " + type + " reply_path_present " + reply_path_present
                            + " subject " + subject + " body " + body + " service_center " + service_center + " locked " + locked
            );
            //时间转换
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
            String LgTime = sdFormat.format(date);
            String body_header = body.length() > 20 ? body.substring(0, 19) : body;
            Boolean isDraft = type == 3 ? true : false;
            smsMap.put("date", LgTime);
            smsMap.put("number", threadIdMap.get(key));
            smsMap.put("contact", address);
            smsMap.put("content", body_header);
            smsMap.put("isDraft", isDraft);
            Log.d(this.getTag(), "date "+ LgTime + " number" + threadIdMap.get(key)
                +  " contact " + address + " content " + body_header + " isDraft "+ isDraft);
            smsDisplay.add(smsMap);
            cursor.close();
        }
    }

    //获取已发送短信
    public void getSentSms() {
        Uri sentURI = Uri.parse(SMS_URI_SENT);
        Cursor cursor = cr.query(sentURI, SMS_PROJECTION, null, null, "thread_id");
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

}
