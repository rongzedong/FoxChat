package com.wangyeming.foxchat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class NewContactActivity extends Activity {

    protected Long ContactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        setActionBar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_contact, menu);
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

    public void setActionBar(){
        ActionBar actionBar = getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true); //显示返回箭头
        // 不显示标题
        getActionBar().setDisplayShowTitleEnabled(false);
        //显示自定义视图
        getActionBar().setDisplayShowCustomEnabled(true);
        View actionbarLayout = LayoutInflater.from(this).inflate(
                R.layout.actionbar_layout, null);
        getActionBar().setCustomView(actionbarLayout);
        //设置取消保存响应
        Button cancelButton = (Button)findViewById(R.id.cancelEdit);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                cancelEdit();
            }
        });
        //设置确认保存响应
        Button sureButton = (Button)findViewById(R.id.saveEdit);
        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ensureEdit();
            }
        });
    }

    //删除联系人
    public void deleteContact() {

    }

    public void cancelEdit() {
        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("确定放弃保存已修改的信息？").
                setIcon(android.R.drawable.ic_dialog_info).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除联系人
                        deleteContact();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create();
        alertDialog.show();
    }

    public void ensureEdit() {
        //保存联系人信息。。。
        Toast.makeText(this, "保存修改成功", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra("ContactId", ContactId);
        startActivity(intent);
    }
}
