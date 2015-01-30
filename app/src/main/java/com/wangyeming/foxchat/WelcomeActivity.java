package com.wangyeming.foxchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.wangyeming.Help.GaotongDoubleInfo;
import com.wangyeming.Help.MtkDoubleInfo;
import com.wangyeming.Help.MultiSimUtility;


public class WelcomeActivity extends ActionBarActivity {

    private final int SPLASH_DISPLAY_LENGHT = 3000; //延迟三秒
    private final int SPLASH_ANIMATION_LENGHT = 1500; //动画延迟1.5秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        setWelcomeAnomation();
        //isDoubleSim(this);
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                WelcomeActivity.this.startActivity(mainIntent);
                WelcomeActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGHT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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
     * 欢迎文字的动画
     */
    public void setWelcomeAnomation() {
        YoYo.with(Techniques.FadeIn)
                .duration(1500)
                .playOn(findViewById(R.id.welcome_fox));
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                YoYo.with(Techniques.FadeOut)
                        .duration(1500)
                        .playOn(findViewById(R.id.welcome_fox));
            }

        }, SPLASH_ANIMATION_LENGHT);

    }

    /**
     * 盘点是否为双卡
     * @param c
     * @return
     */
    public Object isDoubleSim(Context c) {
        MtkDoubleInfo mtkDoubleInfo = MultiSimUtility.initMtkDoubleSim(c);
        GaotongDoubleInfo gaotongDoubleInfo = MultiSimUtility
                .initQualcommDoubleSim(c);

        boolean isGaoTongCpu = gaotongDoubleInfo.isGaotongDoubleSim();
        boolean isMtkCpu = mtkDoubleInfo.isMtkDoubleSim();
        if (isGaoTongCpu) {
            // 高通芯片双卡
            Log.d("wym", "高通双卡");
            return gaotongDoubleInfo;
        } else if (isMtkCpu) {
            // MTK芯片双卡
            Log.d("wym", "MTK双卡");
            return mtkDoubleInfo;
        } else {
            //普通单卡手机
            Log.d("wym", "其他");
            return null;
        }
    }


}
