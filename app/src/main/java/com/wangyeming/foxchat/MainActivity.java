package com.wangyeming.foxchat;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.astuetz.PagerSlidingTabStrip;
import com.wangyeming.custom.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity
 * 滑动的tabs设计，分别为通讯记录，联系人，短信
 *
 * @author 王小明
 * @data 2015/01/05
 */
public class MainActivity extends ActionBarActivity implements PhoneFragment.OnFragmentInteractionListener,
    ContactFragment.OnFragmentInteractionListener, MessageFragment.OnFragmentInteractionListener{

    private static final String TAG = "FragmentTabs";
    public static final String TAB_PHONES = "phones";
    public static final String TAB_CONTACTS = "contacts";
    public static final String TAB_MESSAGES = "messages";

    private TabHost mTabHost;
    private int mCurrentTab;
    private ViewPager viewPager;  //对应的viewPager
    private Fragment fragment1, fragment2, fragment3;
    private List<android.support.v4.app.Fragment> fragmentList;  //fragment数组
    private List<String> titleList;  //标题列表数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getActionBar().hide();
        initViewPager();
        /*
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.tabcontent, new PlaceholderFragment())
                    .commit();
        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    //加载viewPager
    public void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        LayoutInflater inflater=getLayoutInflater();
        fragmentList = new ArrayList<>();// 将要分页显示的View装入数组中
        fragmentList.add(new PhoneFragment());
        fragmentList.add(new ContactFragment());
        fragmentList.add(new MessageFragment());
        titleList = new ArrayList<String>();// 每个页面的Title数据
        titleList.add("拨号");
        titleList.add("联系人");
        titleList.add("信息");
        MyFragmentPagerAdapter myPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
        viewPager.setAdapter(myPagerAdapter);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabStrip);
        tabStrip.setViewPager(viewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
