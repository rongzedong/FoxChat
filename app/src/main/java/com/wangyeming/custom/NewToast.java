package com.wangyeming.custom;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wangyeming.foxchat.R;

/**
 * Created by Wang on 2014/10/29.
 */
public class NewToast extends Toast {

    public NewToast(Context context) {
        super(context);
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        Toast result = new Toast(context);

        //获取LayoutInflater对象
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //由layout文件创建一个View对象
        View layout = inflater.inflate(R.layout.newtoast, null);

        //实例化ImageView和TextView对象
        //ImageView imageView = (ImageView) layout.findViewById(R.id.image0);
        TextView textView = (TextView) layout.findViewById(R.id.text0);

        //imageView.setImageResource(resId);
        textView.setText(text);

        result.setView(layout);
        result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        result.setDuration(duration);

        return result;
    }
}
