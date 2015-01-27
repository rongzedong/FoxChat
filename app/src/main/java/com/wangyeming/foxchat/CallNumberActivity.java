package com.wangyeming.foxchat;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;


public class CallNumberActivity extends ActionBarActivity {

    //手机号输入框
    private TextView numberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_number);
        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call_number, menu);
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

    public void init() {
        setClickNumber();//设置拨号盘点击
    }

    /**
     * 设置拨号键
     */
    public void setClickNumber() {
        numberInput = (TextView) findViewById(R.id.number_input);
        Button numberofOne = (Button) findViewById(R.id.number_1);
        Button numberofTwo = (Button) findViewById(R.id.number_2);
        Button numberofThree = (Button) findViewById(R.id.number_3);
        Button numberofFour = (Button) findViewById(R.id.number_4);
        Button numberofFive = (Button) findViewById(R.id.number_5);
        Button numberofSix = (Button) findViewById(R.id.number_6);
        Button numberofSeven = (Button) findViewById(R.id.number_7);
        Button numberofEight = (Button) findViewById(R.id.number_8);
        Button numberofNine = (Button) findViewById(R.id.number_9);
        Button numberofZero = (Button) findViewById(R.id.number_0);
        Button asterisk = (Button) findViewById(R.id.asterisk);
        Button numberSign = (Button) findViewById(R.id.number_sign);
        Button[] buttonArr = new Button[]{numberofOne, numberofTwo, numberofThree, numberofFour,
                numberofFive, numberofSix, numberofSeven, numberofEight, numberofNine,
                numberofZero, asterisk, numberSign
        };
        String[] numberArr = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "*", "#"};
        for (int i = 0; i < 12; i++) {
            setNumberButtonClick(buttonArr[i], numberArr[i]);
        }
        ImageButton deleteButton = (ImageButton) findViewById(R.id.delete_number);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence display = numberInput.getText();
                if (display.length() != 0) {
                    numberInput.setText(display.subSequence(0, display.length() - 1));
                }
            }
        });
        ButtonFloat buttonFloat = (ButtonFloat) findViewById(R.id.call);
        buttonFloat.setBackgroundColor(Color.parseColor("#8BC34A"));
    }

    /**
     * 设置Button键click
     */
    public void setNumberButtonClick(Button numberButton, final String number) {
        numberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence display = numberInput.getText();
                numberInput.setText(display + number);
            }
        });
    }
}
