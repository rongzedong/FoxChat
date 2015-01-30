package com.wangyeming.application;

import android.app.Application;

/**
 * @author Wang
 * @data 2015/1/31
 */
public class MyApplication extends Application {

    private Boolean isDualSim; //是否为双卡

    public Boolean getIsDualSim() {
        return isDualSim;
    }

    public void setIsDualSim(Boolean isDualSim) {
        this.isDualSim = isDualSim;
    }
}
