package com.wangyeming.foxchat;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ContactDetailActivity extends Activity {

    protected List<Map<String, String>> ContactDisplay = new ArrayList<Map<String, String>>();
    protected ListView lt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        displayListView(ContactDisplay);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //设置lisView布局
    public void displayListView(List<Map<String, String>> Display ){
        lt2 = (ListView) findViewById(R.id.list2);
        if(ContactDisplay == null){
            System.out.println("ContactDisplay is nil");
        }
        SimpleAdapter adapter = new SimpleAdapter(this, Display,
                R.layout.list_item1, new String[]{"phone_num","phone_type","phone_location"}, new int[]{R.id.phone_num,R.id.phone_type, R.id.phone_location});
        lt2.setAdapter(adapter);
    }
}
