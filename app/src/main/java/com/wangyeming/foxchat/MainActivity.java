package com.wangyeming.foxchat;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.wangyeming.custom.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialtabs.MaterialTabHost;

/**
 * Main Activity
 * 滑动的tabs设计，分别为通讯记录，联系人，短信
 *
 * @author 王小明
 * @data 2015/01/05
 */
public class MainActivity extends ActionBarActivity implements PhoneFragment.OnFragmentInteractionListener,
        ContactFragment.OnFragmentInteractionListener, MessageFragment.OnFragmentInteractionListener {

    private static final String TAG = "FragmentTabs";
    public static final String TAB_PHONES = "phones";
    public static final String TAB_CONTACTS = "contacts";
    public static final String TAB_MESSAGES = "messages";
    private long clickTime = 0; //记录第一次点击的时间

    private TabHost mTabHost;
    private int mCurrentTab;
    private ViewPager viewPager;  //对应的viewPager
    private Fragment fragment1, fragment2, fragment3;
    private List<android.support.v4.app.Fragment> fragmentList;  //fragment数组
    private List<String> titleList;  //标题列表数组
    private Toolbar toolbar;
    private android.support.v7.widget.SearchView searchView;
    private BottomSheet.Builder bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false); //隐藏toolBar标题
        initViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //设置search view
        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);//默认展开搜索框
        searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.query_hint) + "</font>"));
        searchView.clearFocus();
        //BottomSheet
        bottomSheet = new BottomSheet.Builder(this).sheet(R.menu.bottom_sheet).
                listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                bottomSheet.show();
                break;
            case KeyEvent.KEYCODE_BACK:
                if ((System.currentTimeMillis() - clickTime) > 2000) {
                    Toast.makeText(this, "再次点击退出！", Toast.LENGTH_SHORT).show();
                    clickTime = System.currentTimeMillis();
                    break;
                } else {
                    this.finish();
                }
        }
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

    @Override
    protected void onRestart() {
        super.onRestart();
        searchView.clearFocus();
    }

    //加载viewPager
    public void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        LayoutInflater inflater = getLayoutInflater();
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
        final MaterialTabHost tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
        //PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabStrip);
        //tabStrip.setViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
                tabHost.setSelectedNavigationItem(position);
            }
        });

        // insert all tabs from pagerAdapter data
        for (int i = 0; i < myPagerAdapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setIcon(getIcon(i))
                    //.setTabListener(this)
            );
        }
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

    public Drawable getIcon(int i) {
        switch (i) {
            case 0:
                return getResources().getDrawable(R.drawable.ic_phone_missed_white);
            case 1:
                return getResources().getDrawable(R.drawable.ic_person_black);
            case 2:
                return getResources().getDrawable(R.drawable.ic_sms_white);
            default:
                return getResources().getDrawable(R.drawable.ic_phone_missed_white);
        }
    }
}
