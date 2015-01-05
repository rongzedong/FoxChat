package com.wangyeming.custom.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang on 2015/1/5.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    //返回要滑动的VIew的个数
    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //返回当前要显示的fragment
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    /*
    //将当前视图添加到container中，返回当前View
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //从当前container中删除指定位置（position）的View;
    @Override
    public void destroyItem(ViewGroup container, int position,
                            Object object) {
        // TODO Auto-generated method stub
        container.removeView(fragmentList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        container.addView(fragmentList.get(position));
        return fragmentList.get(position);
    }
    */

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO Auto-generated method stub
        return titleList.get(position);
    }
}
