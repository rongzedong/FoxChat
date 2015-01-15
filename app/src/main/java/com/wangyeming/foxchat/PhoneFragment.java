package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wangyeming.custom.adapter.CallRecordAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


/**
 * 读取通话记录的Fragment
 *
 * @author 王小明
 * @data 2015/01/14
 */
public class PhoneFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    // 当前activity
    private Activity currentActivity;
    // 当前view
    private View currentView;
    //ContentResolver
    private ContentResolver cr;
    //通话记录储存
    private List<Map<String, Object>> callRecordsDisplay = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CallRecordAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final String[] CALL_PROJECTION_ABOVE_16 = new String[]{
            "name",             //姓名
            "numberlabel",      //手机号标签
            "numbertype",       //手机号类型
            "date",             //日期
            "duration",         //持续的时间
            "is_read",          //是否阅读 Boolean
            "new",              //Boolean 通话是否被获知
            "number",           //用户输入的号码
            "type",             //类型（incoming, outgoing or missed）Integer
            //"vnd.android.cursor.item/calls", //MIME TYPE of CONTENT_URI
            //"vnd.android.cursor.dir/calls",  //MIME TYPE of CONTENT_URI and CONTENT_FILTER_URI
    };

    private static final String[] CALL_PROJECTION_AT_17 = new String[]{
            "limit",            //用于限制返回通话记录数目的查询参数
            "offset",           //用于指定返回起始记录的查询参数
    };

    private static final String[] CALL_PROJECTION_AT_19 = new String[]{
            /*
            * 1  号码被允许
            * 2  号码被用户屏蔽
            * 3  号码未被指定或网络未知
            * 4  付费电话
            *
            */
            "presentation",     //权限
    };

    private static final String[] CALL_PROJEXTION_AT_21 = new String[]{
            "formatted_number", //缓存的手机号，基于用户所在的国家格式化，如果号码相关的信息被改变，号码可能不存在
            "lookup_uri",       //联系人的uri(如果存在)，如果号码相关的信息被改变，信息可能不存在
            "matched_number",   //匹配的号码
            "normalized_number",//标准化（E164）的手机号
            "photo_id",         //照片id
            "countryiso",       //接电话所在的国家代码
            "data_usage",       //The data usage of the call in bytes.
            "features",         //通话的Bit-mask 描述的特色，如vedio等（Integer）
            "geocoded_location",//通话号码所在地
            "transcription",    //通话或声音邮件的录音（仅当type VOICEMAIL_TYPE存在的时候）
            "voicemail_ur",     //声音邮件的uri（仅当type VOICEMAIL_TYPE存在的时候）
    };

    public static PhoneFragment newInstance(String param1, String param2) {
        PhoneFragment fragment = new PhoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

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

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public void init() {
        currentActivity = getActivity(); //获取当前activity
        currentView = getView(); //获取当前view
        cr = currentActivity.getContentResolver(); //获取contact resolver
        setRecyclerView();
        new Thread(new Runnable() {

            @Override
            public void run() {
                getCallRecords();
                Message message = Message.obtain();
                message.obj = "ok";
                PhoneFragment.this.handler1.sendMessage(message);
            }
        }).start();
    }

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            mAdapter.notifyDataSetChanged();
        }
    };

    //读取通话记录
    public void getCallRecords() {
        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, CALL_PROJECTION_ABOVE_16,
                null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        while(cursor.moveToNext()) {
            Log.d(this.getTag(), "----------------call record----------------");
            /* min API>=16 */
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            Integer numberType = cursor.getInt(cursor.getColumnIndex(
                    CallLog.Calls.CACHED_NUMBER_TYPE));
            String numberLabel = cursor.getString(cursor.getColumnIndex(
                    CallLog.Calls.CACHED_NUMBER_LABEL));
            Long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            Long duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
            Boolean isRead = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.IS_READ)) > 0;
            Boolean isNew = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.NEW)) > 0;
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            Integer type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            /* min API>=17 */
            /*Integer limit = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.LIMIT_PARAM_KEY));*/
            /* min API>=21 */
            /*String lookupUri = cursor.getString(cursor.getColumnIndex(
                    CallLog.Calls.CACHED_LOOKUP_URI));
            String formattedNumber = cursor.getString(cursor.getColumnIndex(
                    CallLog.Calls.CACHED_FORMATTED_NUMBER));
            String matchedNumber = cursor.getString(cursor.getColumnIndex(
                    CallLog.Calls.CACHED_MATCHED_NUMBER));*/
            /*String normalizedNumber = cursor.getString(cursor.getColumnIndex(
                    CallLog.Calls.CACHED_NORMALIZED_NUMBER));*/
            /*Integer photoId = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_ID));*/
            /*String countryIso = cursor.getString(cursor.getColumnIndex(CallLog.Calls.COUNTRY_ISO));*/
            /*Integer features = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.FEATURES));
            String geocodedLocation = cursor.getString(cursor.getColumnIndex(
                    CallLog.Calls.GEOCODED_LOCATION));*/
            //时间转换
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//24小时制
            String LgTime = sdFormat.format(date);
            /* min API>=16 */
            Log.d(this.getTag(), " name " + name
                            + " numberType " + numberType
                            + " numberLabel " + numberLabel
                            + " date " + date
                            + " LgTime " + LgTime
                            + " duration " + duration
                            + " isRead " + isRead
                            + " isNew " + isNew
                            + " number " + number
                            + " type " + type
            );
            /* min API>=17 */
            /*Log.d(this.getTag()," limit " + limit );*/
            /* min API>=21 */
            /*Log.d(this.getTag(), "lookupUri " + lookupUri
                            + " formattedNumber " + formattedNumber
                            + " matchedNumber " + matchedNumber
                            + " normalizedNumber " + normalizedNumber
                            + " photoId " + photoId
                            + " countryIso " + countryIso
                            + " features " + features
                            + " geocodedLocation " + geocodedLocation);*/

            //获取联系人头像
            Uri avatarUri = name == null ? null : nameToAvatat(name);
            Map<String, Object> callRecordsMap = new HashMap<>();
            callRecordsMap.put("name",name);
            callRecordsMap.put("number",number);
            callRecordsMap.put("date",LgTime);
            callRecordsMap.put("numberType",numberType);
            callRecordsMap.put("numberLabel",numberLabel);
            callRecordsMap.put("isRead",isRead);
            callRecordsMap.put("isNew",isNew);
            callRecordsMap.put("duration",duration);
            callRecordsMap.put("avatarUri",avatarUri);
            callRecordsMap.put("type",type);
            callRecordsDisplay.add(callRecordsMap);
        }
        cursor.close();
    }

    //通过姓名获得头像
    public Uri nameToAvatat(String name) {
        Cursor cursor = cr.query(CONTENT_URI, new String[]{"photo_uri"},
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?", new String[]{name}, null, null);
        Uri avatarUri = null;
        if (cursor.moveToFirst()) {
            String avatarString = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Photo.PHOTO_URI)); //获取联系人头像
            avatarUri = Uri.parse(avatarString);
        }
        cursor.close();
        return avatarUri;
    }

    public void setRecyclerView() {
        mRecyclerView = (RecyclerView) currentView.findViewById(R.id.call_record_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(currentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CallRecordAdapter(currentActivity, callRecordsDisplay);
        mRecyclerView.setAdapter(mAdapter);
    }
}