package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.wangyeming.custom.adapter.ContactListAdapter;
import com.wangyeming.custom.widget.NewToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 读取联系人列表信息的Fragment
 *
 * @author 王小明
 * @date 2015/01/11
 */
public class NewContactFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String[] CONTACT_PROJECTION = new String[]{
            "_id",                  //raw contact id 考虑使用lookup代替,不会改变
            "lookup",               //一个opaque值，包含当name_raw id改变时如何查找联系人的暗示
            //"name_raw_contact_id",  //name_raw_contact_id，随姓名改变
            "display_name",         //联系人姓名 DISPLAY_NAME_PRIMARY
            "display_name_alt",     //两者选一的展现display_name的方式，姓在前或名在前，如果选择不可用，则以DISPLAY_NAME_PRIMARY
            "display_name_source",  //用于产生联系人姓名的数据种类（EMAIL, PHONE, ORGANIZATION, NICKNAME, STRUCTURED_NAME）
            "phonetic_name",        //姓名发音
            "phonetic_name_style",  //姓名发音样式（包括JAPANESE， KOREAN， PINYIN和UNDEFINED）
            "sort_key",             //排序方式（对中国是拼音，对日本是五十音顺序）
            "sort_key_alt",         //可选的排序，对西方，名优先
            "photo_id",             //头像id，为null时查找 PHOTO_URI 或 PHOTO_THUMBNAIL_URI
            "photo_uri",            //全尺寸头像uri
            "photo_thumb_uri",      //缩略图头像uri
            "in_visible_group",     //反映群组课件状态的标识
            "has_phone_number",     //是否有手机号，1有，0没有
            "times_contacted",      //联系人的联系次数
            "last_time_contacted",  //最后联系时间
            "starred",              //是否被收藏
            "custom_ringtone",      //手机铃声uri，如果为null或missing，则为默认
            "send_to_voicemail",    //是否总是向该联系人发送声音邮件，0不是，1是
            "contact_presence",     //联系存在状态
            "contact_status",       //联系人最新的状态更新
            "contact_status_ts",    //联系人被新建或更新距今的时间长度
            "contact_status_res_package",  //包含label和icon的资源包
            "contact_status_label", //联系人状态icon资源的id
            "contact_status_icon",  //联系人状态label资源的id,如“Google Talk”
    };
    //联系人数据操作
    protected ContentResolver cr;
    //滑动时toast姓提示
    protected Toast nameToast;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    // 当前activity
    private Activity currentActivity;
    // 当前view
    private View currentView;
    //联系人数据
    private List<Map<String, Object>> contactList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ContactListAdapter mAdapter;
    //private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayoutManager mLayoutManager;
    //收藏联系人数目
    private int starNum;
    //总联系人数目
    private int totalNum;
    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            mAdapter.notifyDataSetChanged();
            setOnScrollListener();  //设置滑动监听
        }
    };

    public NewContactFragment() {
        // Required empty public constructor
    }

    public static NewContactFragment newInstance(String param1, String param2) {
        NewContactFragment fragment = new NewContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_new_contact, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * 初始化
     */
    public void init() {
        currentActivity = getActivity();  //获取当前activity
        currentView = getView();  //获取当前view
        setNewContact(); //设置新建联系人按钮
        cr = currentActivity.getContentResolver();
        setRecyclerView();  //设置recyclerView
        new Thread(new Runnable() {

            @Override
            public void run() {
                getPhoneContacts();  //获取手机联系人信息
                Message message = Message.obtain();
                message.obj = "ok";
                NewContactFragment.this.handler1.sendMessage(message);
            }
        }).start();
    }

    /**
     * 设置新建联系人按钮
     */
    public void setNewContact() {
        ButtonFloat button = (ButtonFloat) currentView.findViewById(R.id.addNewContact);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newContact();
            }
        });
    }

    /**
     * 新建联系人
     */
    public void newContact() {
        Intent intent = new Intent(currentActivity, NewContactActivity.class);
        startActivity(intent);
    }

    /**
     * 得到手机通讯录联系人信息
     */
    public void getPhoneContacts() {
        Log.d(this.getTag(), "读取收藏联系人。。。");
        starNum = readContact("starred=?", new String[]{"1"}); //读取星标联系人
        Log.d(this.getTag(), "读取未收藏联系人。。。");
        totalNum = readContact("starred=?", new String[]{"0"}); //读取非星标联系人
    }

    /**
     * 读取联系人基本信息
     */
    public int readContact(String selection, String[] selectionArgs) {
        String isStarred = selectionArgs[0];
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, CONTACT_PROJECTION,
                selection, selectionArgs, "sort_key");
        String flag = "first";
        while (cursor.moveToNext()) {
            //获取基本信息
            long _id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String LookUpKey = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.LOOKUP_KEY));
            /*long nameRawContactId = cursor.getLong(cursor.getColumnIndex(
                    ContactsContract.Contacts.NAME_RAW_CONTACT_ID));*/
            String displayName = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            String displayNameAlt = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE));
            String displayNameSource = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME_SOURCE));
            String phoneticName = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.PHONETIC_NAME));
            String phoneticNameStyle = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.PHONETIC_NAME_STYLE));
            String sortKey = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.SORT_KEY_PRIMARY));
            String sortkeyAlt = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.SORT_KEY_ALTERNATIVE));
            long photoId = cursor.getLong(cursor.getColumnIndex(
                    ContactsContract.Contacts.PHOTO_ID));
            String photoUri = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.PHOTO_URI));
            String photoThumbUri = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
            int inVisibleGroup = cursor.getInt(cursor.getColumnIndex(
                    ContactsContract.Contacts.IN_VISIBLE_GROUP));
            int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER));
            int timesContacted = cursor.getInt(cursor.getColumnIndex(
                    ContactsContract.Contacts.TIMES_CONTACTED));
            long lastTimeContacted = cursor.getLong(cursor.getColumnIndex(
                    ContactsContract.Contacts.LAST_TIME_CONTACTED));
            int starred = cursor.getInt(cursor.getColumnIndex(
                    ContactsContract.Contacts.STARRED));
            String customRingtone = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.CUSTOM_RINGTONE));
            int sendToVoicemail = cursor.getInt(cursor.getColumnIndex(
                    ContactsContract.Contacts.SEND_TO_VOICEMAIL));
            int contactPresence = cursor.getInt(cursor.getColumnIndex(
                    ContactsContract.Contacts.CONTACT_PRESENCE));
            String contactStatus = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.CONTACT_STATUS));
            long contactStatusTs = cursor.getLong(cursor.getColumnIndex(
                    ContactsContract.Contacts.CONTACT_STATUS_TIMESTAMP));
            String contactStatusResPackage = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.CONTACT_STATUS_RES_PACKAGE));
            long contactStatusLabel = cursor.getLong(cursor.getColumnIndex(
                    ContactsContract.Contacts.CONTACT_STATUS_LABEL));
            long contactStatusIcon = cursor.getLong(cursor.getColumnIndex(
                    ContactsContract.Contacts.CONTACT_STATUS_ICON));
            //提取信息
            String mark = null;
            String firstPinyin = sortKey.substring(0, 1);
            switch (isStarred) {
                //如果是收藏联系人
                case "1":
                    if (flag.equals("first")) {
                        mark = "star";
                    } else {
                        mark = "none";
                    }
                    flag = "none";
                    break;
                //如果是非收藏联系人
                case "0":
                    if (firstPinyin.equals(flag)) {
                        mark = "none";
                    } else {
                        mark = firstPinyin;
                    }
                    flag = firstPinyin;
                    break;
            }
            //存进Map
            Map<String, Object> contactMap = new HashMap<>();
            contactMap.put("_id", _id);
            contactMap.put("LookUpKey", LookUpKey);
            /*contactMap.put("nameRawContactId",nameRawContactId);*/
            contactMap.put("displayName", displayName);
            contactMap.put("displayNameAlt", displayNameAlt);
            contactMap.put("displayNameSource", displayNameSource);
            contactMap.put("phoneticName", phoneticName);
            contactMap.put("phoneticNameStyle", phoneticNameStyle);
            contactMap.put("sortKey", sortKey);
            contactMap.put("sortkeyAlt", sortkeyAlt);
            contactMap.put("photoId", photoId);
            contactMap.put("photoUri", photoUri);
            contactMap.put("photoThumbUri", photoThumbUri);
            contactMap.put("inVisibleGroup", inVisibleGroup);
            contactMap.put("hasPhoneNumber", hasPhoneNumber);
            contactMap.put("timesContacted", timesContacted);
            contactMap.put("lastTimeContacted", lastTimeContacted);
            contactMap.put("starred", starred);
            contactMap.put("customRingtone", customRingtone);
            contactMap.put("sendToVoicemail", sendToVoicemail);
            contactMap.put("contactPresence", contactPresence);
            contactMap.put("contactStatus", contactStatus);
            contactMap.put("contactStatusTs", contactStatusTs);
            contactMap.put("contactStatusResPackage", contactStatusResPackage);
            contactMap.put("contactStatusLabel", contactStatusLabel);
            contactMap.put("contactStatusIcon", contactStatusIcon);
            contactMap.put("mark", mark);
            String logString = "";
            for (String key : contactMap.keySet()) {
                logString = logString + " " + key + " " + contactMap.get(key);
            }
            Log.d("NewContactFragment", logString);
            contactList.add(contactMap);
        }
        cursor.close();
        return contactList.size();
    }

    /**
     * 设置recyclerview
     */
    public void setRecyclerView() {
        mRecyclerView = (RecyclerView) currentView.findViewById(R.id.contact_list_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(currentActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ContactListAdapter(currentActivity, contactList, starNum);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 设置RecyclerView滑动监听---根据滑动位置Toast提示
     */
    public void setOnScrollListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
                if (nameToast != null) {
                    nameToast.cancel();
                }
                //GridLayoutManager layoutManager = ((GridLayoutManager)view.getLayoutManager());
                //int firstPos = layoutManager.findFirstVisibleItemPosition() + 1;
                int firstPos = mLayoutManager.findFirstVisibleItemPosition() + 1;
                String name = (String) contactList.get(firstPos).get("displayName");
                Log.d("wym", "firstPos " + firstPos + " name " + name);
                if (name == null) {
                    return;
                }
                String surname = name.substring(0, 1);
                nameToast = NewToast.makeText(currentActivity, surname, Toast.LENGTH_SHORT);
                nameToast.show();
            }
        });
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
