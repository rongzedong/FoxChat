package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;
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
    private static final long ONEHOUR = 3600L * 1000L;
    private static final long ONEDAY = 86400L * 1000L;
    private static final long TWODAY = 86400L * 2L * 1000L;
    private static final long ONEWEEK = 604800L * 1000L;
    private static final long ONEMOUTH = 2629743L * 1000L;
    private static final long ONEYEAR = 31556926L * 1000L;
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
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    // 当前activity
    private ActionBarActivity currentActivity;
    // 当前view
    private View currentView;
    //ContentResolver
    private ContentResolver cr;
    //通话记录储存
    private List<Map<String, Object>> callRecordsDisplay = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CallRecordAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //手机号输入框
    private TextView numberInput;
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            mAdapter.notifyDataSetChanged();
        }
    };

    public PhoneFragment() {
        // Required empty public constructor
    }

    public static PhoneFragment newInstance(String param1, String param2) {
        PhoneFragment fragment = new PhoneFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onStart() {
        super.onStart();
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

    public void init() {
        currentActivity = (ActionBarActivity) getActivity(); //获取当前activity
        currentView = getView(); //获取当前view
        cr = currentActivity.getContentResolver(); //获取contact resolver
        assert currentView != null;
        numberInput = (TextView) currentView.findViewById(R.id.number_input);
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
        setDialpad();//拨号
    }

    //读取通话记录
    public void getCallRecords() {
        Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, CALL_PROJECTION_ABOVE_16,
                null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        String subheader = "subheader";
        String timeApartMark = "timeApartMark";
        while (cursor.moveToNext()) {
            //Log.d(this.getTag(), "----------------call record----------------");
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
            //计算今天或更早
            String isTodaySp = null;
            String isToday = getTimeApart(date);
            if (!isToday.equals(subheader)) {
                isTodaySp = isToday;
            }
            subheader = isToday;
            //计算时间间隔
//            String apart = null;
//            String timeApart = getCurrentTime(date);
//            if (!timeApart.equals(timeApartMark)) {
//                apart = timeApart;
//            }
//            timeApartMark = timeApart;
            /* min API>=16 */
            /*Log.d(this.getTag(), " name " + name
                            + " numberType " + numberType
                            + " numberLabel " + numberLabel
                            + " date " + date
                            + " LgTime " + LgTime
                            + " duration " + duration
                            + " isRead " + isRead
                            + " isNew " + isNew
                            + " number " + number
                            + " type " + type
                            + " timeApart " + timeApart
            );*/
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
            Uri avatarUri = name == null ? null : nameToAvatar(name);
            Map<String, Object> callRecordsMap = new HashMap<>();
            callRecordsMap.put("name", name);
            callRecordsMap.put("number", number);
            callRecordsMap.put("date", LgTime);
            callRecordsMap.put("numberType", numberType);
            callRecordsMap.put("numberLabel", numberLabel);
            callRecordsMap.put("isRead", isRead);
            callRecordsMap.put("isNew", isNew);
            callRecordsMap.put("duration", duration);
            callRecordsMap.put("avatarUri", avatarUri);
            callRecordsMap.put("type", type);
            callRecordsMap.put("isToday", isTodaySp);
//            callRecordsMap.put("timeapart", apart);
            String logString = "";
            for (String key : callRecordsMap.keySet()) {
                logString = logString + " " + key + " " + callRecordsMap.get(key);
            }
            Log.d("wym", logString);
            callRecordsDisplay.add(callRecordsMap);
        }
        cursor.close();
    }

    //通过姓名获得头像
    public Uri nameToAvatar(String name) {
        Cursor cursor = cr.query(CONTENT_URI, new String[]{"photo_uri"},
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?", new String[]{name}, null, null);
        Uri avatarUri = null;
        if (cursor.moveToFirst()) {
            String avatarString = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.CommonDataKinds.Photo.PHOTO_URI)); //获取联系人头像
            if(avatarString != null ) {
                avatarUri = Uri.parse(avatarString);
            }
        }
        cursor.close();
        return avatarUri;
    }

    //通过电话号码获得头像
    public String numberToAvatar(String number) {
        //Log.d(this.getTag(), "address1 " + address);
        number = number.replaceAll(" ", "");//去除电话号码的空格
        String regex = "\\+\\d\\d";
        number = number.replaceFirst(regex, ""); //去除电话号码的国家区号
        //Log.d(this.getTag(), "address2 " + address);
        Cursor cursor = cr.query(CONTENT_URI, new String[]{"photoThumbUri"},
                ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{number}, null, null);
        String photoThumbUri = null;
        if (cursor.moveToFirst()) {
            photoThumbUri = cursor.getString(cursor.getColumnIndex("photoThumbUri"));
        }
        cursor.close();
        return photoThumbUri;
    }

    public void setRecyclerView() {
        mRecyclerView = (RecyclerView) currentView.findViewById(R.id.call_record_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(currentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CallRecordAdapter(currentActivity, callRecordsDisplay);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 获取当前时间
     */
    public String getCurrentTime(Long date) {
        String[] arr = getResources().getStringArray(R.array.date_apart);
        Long currentTimeMillis = System.currentTimeMillis();
        Long time = currentTimeMillis - date;
        String time_apart;
        if (time < ONEHOUR) {
            time_apart = arr[0];
        } else if (time < ONEDAY) {
            time_apart = arr[1];
        } else if (time < TWODAY) {
            time_apart = arr[2];
        } else if (time < ONEWEEK) {
            time_apart = arr[3];
        } else if (time < ONEMOUTH) {
            time_apart = arr[4];
        } else if (time < ONEYEAR) {
            Log.d("wangyeming", "ONEYEAR " + ONEYEAR + " ONEMOUTH " + ONEMOUTH + " time " + time);
            time_apart = arr[5];
        } else {
            time_apart = arr[6];
        }
        return time_apart;

    }

    /**
     * 获取时间间隔
     */
    public String getTimeApart(Long date) {
        String[] arr = getResources().getStringArray(R.array.is_today);
        Long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat todayFormat = new SimpleDateFormat("yyyyMMdd");
        String callRecordTime = todayFormat.format(date);
        String currentTime = todayFormat.format(currentTimeMillis);
        int apart = Integer.valueOf(currentTime) - Integer.valueOf(callRecordTime);
        String subHeader = "";
        switch (apart) {
            case 0:
                subHeader = arr[0];
                break;
            case 1:
                subHeader = arr[1];
                break;
            default:
                subHeader = arr[2];
                break;
        }
        return subHeader;

    }


    /**
     * 拨号码
     */
    public void setDialpad() {
        ButtonFloat buttonFloat = (ButtonFloat) currentView.findViewById(R.id.dialpad_open);
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(currentActivity, CallNumberActivity.class);
                startActivity(intent);
            }
        });

    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }
}