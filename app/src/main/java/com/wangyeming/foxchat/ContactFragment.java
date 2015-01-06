package com.wangyeming.foxchat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.wangyeming.custom.ContactListAdapter;
import com.wangyeming.custom.NewToast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */

    // 当前activity
    private Activity currentActivity;
    // 当前view
    private View currentView;
    // 保存联系人的姓名集合---包含分类名称
    private List<String> namesList = new ArrayList<String>();
    // 保存联系人的姓名集合---不包含分类名称
    private List<String> namesList2 = new ArrayList<String>();
    // 保存匹配联系人的姓名的集合
    private List<String> namesFilterList = new ArrayList<String>();
    // 记录姓名分类名的位置
    private List<Integer> catalogList = new ArrayList<Integer>();
    //保存联系人ContactId的集合---包含分类名称
    protected List<Long> contactIdList = new ArrayList<Long>();
    //保存联系人ContactId的集合---不包含分类名称
    protected List<Long> contactIdList2 = new ArrayList<Long>();
    //保存搜索过滤后的联系人ContactId的集合
    protected List<Long> contactIdFilterList = new ArrayList<Long>();
    //联系人数据操作
    protected ContentResolver cr;
    //联系人游标
    protected Cursor cursorID;
    //自定义Adapter
    protected ContactListAdapter adapter;
    protected ListView lt1;
    protected SearchView searchView;
    protected TextView tv1;
    protected TextView tv2;
    protected Toast nameToast;
    protected boolean isSearch = false;

    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, //display_name
            ContactsContract.CommonDataKinds.Phone.NUMBER, //data1
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, //photo_id
            ContactsContract.CommonDataKinds.Photo.PHOTO_URI,//
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,  //contact_id
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY, //sort_key
            ContactsContract.CommonDataKinds.Photo.RAW_CONTACT_ID
    };

    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ContactFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_contact, container, false);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        System.out.println("onRestart");
        super.onResume();
        if (!isSearch) {
            clearData(); //清除缓存数据
            initRefrash();
        }
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
        currentActivity = getActivity();
        currentView = getView();
        Button button = (Button) currentView.findViewById(R.id.addNewContact);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                newContact();
            }
        });
        cr = currentActivity.getContentResolver();
        getPhoneContacts();
        displayListView(namesList);
        setOnScrollListener();
        setOnItemClickListener();
    }

    public void initRefrash() {
        getPhoneContacts();
        //adapter.notifyDataSetChanged();
        displayListView(namesList);
    }

    /**
     * 得到手机通讯录联系人信息*
     */
    public void getPhoneContacts() {
        readStarredContact(); //读取星标联系人
        readContact(); //读取非星标联系人
    }

    //清除缓存数据
    public void clearData() {
        namesList.clear();
        namesList2.clear();
        namesFilterList.clear();
        catalogList.clear();
        contactIdList.clear();
        contactIdList2.clear();
        contactIdFilterList.clear();
    }

    //读取联系人
    public void readContact() {
        System.out.println("读取未收藏联系人。。。");
        namesList.add(getString(R.string.unstarred_contact)); // 其他联系人
        contactIdList.add((long) 0);
        cursorID = cr.query(CONTENT_URI, PHONES_PROJECTION, "starred=?", new String[]{"0"}, "sort_key");// 设置联系人光标,按汉语拼音排序
        String ContactName = "";//排除联系人重复
        while (cursorID.moveToNext()) {
            int nameFieldColumnIndex = cursorID.getColumnIndex(PHONES_PROJECTION[0]);//返回display_name对应列的index--0
            String contact = cursorID.getString(nameFieldColumnIndex);//获取联系人姓名
            if (contact.equals(ContactName)) {
                ContactName = contact;
                continue;
            } else {
                ContactName = contact;
                namesList.add(ContactName); // 保存联系人姓名
                namesList2.add(ContactName); // 保存联系人姓名
                int index = cursorID.getColumnIndex(PHONES_PROJECTION[4]);
                Long ContactId = cursorID.getLong(index);//获取联系人对应的ID号
                contactIdList.add(ContactId); //保存联系人ContactId
                contactIdList2.add(ContactId);
                contactIdFilterList.add(ContactId);
            }
        }
        System.out.println("一共读取了" + namesList2.size() + "个联系人");
        cursorID.close();
    }

    //读取星标联系人
    public void readStarredContact() {
        catalogList.add(0);
        namesList.add(getString(R.string.starred_contact)); // 星标联系人
        contactIdList.add((long) 0);
        System.out.println("读取收藏了联系人。。。");
        cursorID = cr.query(CONTENT_URI, PHONES_PROJECTION, "starred=?", new String[]{"1"}, "sort_key");// 设置星标联系人光标,按汉语拼音排序
        String ContactName = "";//排除联系人重复
        while (cursorID.moveToNext()) {
            int nameFieldColumnIndex = cursorID.getColumnIndex(PHONES_PROJECTION[0]);//返回display_name对应列的index--0
            String contact = cursorID.getString(nameFieldColumnIndex);//获取联系人姓名
            if (contact.equals(ContactName)) {
                ContactName = contact;
                continue;
            } else {
                ContactName = contact;
                namesList.add(ContactName); // 保存联系人姓名
                namesList2.add(ContactName); // 保存联系人姓名
                int index = cursorID.getColumnIndex(PHONES_PROJECTION[4]);
                Long contactId = cursorID.getLong(index);//获取联系人对应的contactId号
                Long rawContactId = cursorID.getLong(cursorID.getColumnIndex(PHONES_PROJECTION[6])); //获取联系人对应的rawContactId号
                contactIdList.add(contactId); //保存联系人ContactId
                contactIdList2.add(contactId); //保存联系人ContactId
            }
        }
        System.out.println("一共读取了" + namesList2.size() + "个收藏联系人");
        catalogList.add(namesList.size()); //记录“其他联系人”储存的位置
        cursorID.close();
    }

    //设置lisView布局
    public void displayListView(List<String> namesList) {
        lt1 = (ListView) currentView.findViewById(R.id.list_contact_line);
        if (lt1 == null) {
            Log.d(this.getTag(), "NULL");
        }
        if (namesList == null) {
            System.out.println("namesList is nil");
        }

        if (isSearch) {
            adapter = new ContactListAdapter(namesList, currentActivity);
        } else {
            adapter = new ContactListAdapter(namesList, catalogList, currentActivity);
        }
        lt1.setAdapter(adapter);
    }

    //设置lisView布局--包含关键词
    public void displayListView(List<String> namesList, String keyWord) {
        lt1 = (ListView) currentView.findViewById(R.id.list_contact_line);
        if (namesList == null) {
            System.out.println("ContactDisplay is nil");
        }
        adapter = new ContactListAdapter(namesList, keyWord, currentActivity);
        lt1.setAdapter(adapter);
    }


    //设置ListView滑动监听---根据滑动位置Toast提示
    public void setOnScrollListener() {
        lt1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (nameToast != null) {
                    nameToast.cancel();
                }
                int firstPos = view.getFirstVisiblePosition() + 1;
                String name = (String) view.getItemAtPosition(firstPos);
                if (name == null) {
                    System.out.println("name is null");
                    return;
                }
                String surname = name.substring(0, 1);
                nameToast = NewToast.makeText(currentActivity, surname, Toast.LENGTH_SHORT);
                nameToast.show();
                /*
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                }
                */
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    //设置ListView点击监听
    public void setOnItemClickListener() {
        lt1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("position " + position + " id " + id);
                List<Long> ContactIdTmp = isSearch ? contactIdFilterList : contactIdList;
                if (!isSearch && catalogList.contains((Integer) position)) {
                    return;
                }
                Long contactId = ContactIdTmp.get(position);
                System.out.println("ContactId " + contactId);
                Intent intent = new Intent(currentActivity, ContactDetailActivity.class);
                intent.putExtra("ContactId", contactId);
                startActivity(intent);
            }
        });
    }

    //设置搜索框监听
    public void setSearchViewListener() {
        //监听输入框字符串变化
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            //输入框文字改变
            public boolean onQueryTextChange(String newText) {
                System.out.println("自动补全 " + newText);
                matchContact(newText);
                return true;
            }

            //提交搜索请求
            public boolean onQueryTextSubmit(String query) {
                matchContact(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        //监听searchView被点击
        SearchView.OnClickListener clickListener = new SearchView.OnClickListener() {

            @Override
            public void onClick(View view) {
                System.out.println("click");
                isSearch = true;
                matchContact("");
            }
        };
        searchView.setOnSearchClickListener(clickListener);
        //监听searchView关闭
        SearchView.OnCloseListener closeListener = new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                System.out.println("close");
                isSearch = false;
                clearData();
                initRefrash();
                return false;
            }
        };
        searchView.setOnCloseListener(closeListener);
    }

    //匹配输入文字
    public void matchContact(String input) {
        namesFilterList = new ArrayList<String>();
        contactIdFilterList = new ArrayList<Long>();
        if (input.isEmpty()) {
            namesFilterList.addAll(namesList2);
            contactIdFilterList.addAll(contactIdList2);
            displayListView(namesFilterList);
            displayConclusion();
            return;
        }
        for (int i = 0; i < namesList2.size(); i++) {
            String name = namesList2.get(i);
            if (name == null) {
                continue;
            }
            Pattern pattern = Pattern.compile(input);
            Matcher matcher = pattern.matcher(name);
            if (matcher.find()) {
                namesFilterList.add(name);
                contactIdFilterList.add(contactIdList2.get(i));
            }
            displayConclusion(namesFilterList);
            displayListView(namesFilterList, input);
        }
    }

    //输入为空时不显示结果统计
    public void displayConclusion() {
        tv1 = (TextView) currentView.findViewById(R.id.tV1);
        tv2 = (TextView) currentView.findViewById(R.id.tV2);
        tv1.setVisibility(View.GONE);
        tv2.setVisibility(View.GONE);

    }

    //显示搜索结果统计
    public void displayConclusion(List<String> Display) {
        int total_num = this.namesList2.size();
        int match_num = Display.size();
        tv1 = (TextView) currentView.findViewById(R.id.tV1);
        tv2 = (TextView) currentView.findViewById(R.id.tV2);
        String str1 = getResources().getString(R.string.all_contact);
        String str2 = getResources().getString(R.string.find) + match_num + getResources().getString(R.string.contacts);
        tv1.setText(str1);
        tv1.setVisibility(View.VISIBLE);
        tv2.setText(str2);
        tv2.setVisibility(View.VISIBLE);
    }

    //新建联系人
    public void newContact() {
        Intent intent = new Intent(currentActivity, NewContactActivity.class);
        startActivity(intent);
    }
}
