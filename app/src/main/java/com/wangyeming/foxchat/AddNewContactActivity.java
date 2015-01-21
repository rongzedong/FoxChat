package com.wangyeming.foxchat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.wangyeming.custom.adapter.PhoneAdapter;


public class AddNewContactActivity extends ActionBarActivity {

    private RecyclerView mRecyclerView;
    private PhoneAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new_contact, menu);
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

    /**
     * 初始化
     */
    public void init() {
        setToolbar();
        setButtonStyle();
    }

    /**
     * 设置button
     */
    public void setButtonStyle() {
        ButtonFloat buttonFloat = (ButtonFloat) findViewById(R.id.setAvatar);
        buttonFloat.setBackgroundColor(Color.parseColor("#E91E63"));
        ButtonRectangle buttonRectangle = (ButtonRectangle) findViewById(R.id.addAnotherField);
        buttonRectangle.setTextColor(Color.parseColor("#000000"));
    }
    /**
     * 设置toolbar
     */
    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_contact);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false); //隐藏toolBar标题
    }

    //返回主页面按钮
    public void backToMain(View view) {
        this.finish();
    }

}
