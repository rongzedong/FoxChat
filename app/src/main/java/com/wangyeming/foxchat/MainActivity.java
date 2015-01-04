package com.wangyeming.foxchat;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;


public class MainActivity extends Activity implements
        PhoneFragment.OnFragmentInteractionListener, MessageFragment.OnFragmentInteractionListener,
        ContactFragment.OnFragmentInteractionListener, TabHost.OnTabChangeListener {

    private static final String TAG = "FragmentTabs";
    public static final String TAB_PHONES = "phones";
    public static final String TAB_CONTACTS = "contacts";
    public static final String TAB_MESSAGES = "messages";

    private TabHost mTabHost;
    private int mCurrentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        setupTabs();
        mTabHost.setOnTabChangedListener(this);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void setupTabs() {
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("电话").setIndicator("电话").setContent(R.id.phoneFragment));
        mTabHost.addTab(mTabHost.newTabSpec("联系人").setIndicator("联系人").setContent(R.id.contactFragment));
        mTabHost.addTab(mTabHost.newTabSpec("短信").setIndicator("短信").setContent(R.id.messageFragment));
    }

    @Override
    public void onTabChanged(String tabId) {
        Log.d(TAG, "onTabChanged(): tabId=" + tabId);
        if (TAB_PHONES.equals(tabId)) {
            updateTab(tabId, R.id.phoneFragment);
            mCurrentTab = 0;
            return;
        }
        if (TAB_CONTACTS.equals(tabId)) {
            updateTab(tabId, R.id.contactFragment);
            mCurrentTab = 1;
            return;
        }
        if (TAB_MESSAGES.equals(tabId)) {
            updateTab(tabId, R.id.messageFragment);
            mCurrentTab = 2;
            return;
        }
    }

    private void updateTab(String tabId, int placeholder) {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag(tabId) == null) {
            fm.beginTransaction()
                    .replace(placeholder, new Fragment(), tabId)
                    .commit();
        }
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
