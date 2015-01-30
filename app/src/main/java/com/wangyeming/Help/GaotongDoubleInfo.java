package com.wangyeming.Help;

/**
 * @author Wang
 * @data 2015/1/31
 */
public class GaotongDoubleInfo {

    //SIM卡id
    private Integer simId_1;
    private Integer simId_2;
    private String imsi_1;
    private String imsi_2;
    private String imei_1;
    private String imei_2;
    private Integer phoneType_1;
    private Integer phoneType_2;
    private String defaultImsi;
    private Boolean gaotongDoubleSim;  //是否为高通双卡

    public void setSimId_1(Integer simId_1) {
        this.simId_1 = simId_1;
    }

    public Integer getSimId_1() {
        return simId_1;
    }

    public void setSimId_2(Integer simId_2) {
        this.simId_2 = simId_2;
    }

    public Integer getSimId_2() {
        return simId_2;
    }

    public void setImsi_1(String imsi_1) {
        this.imsi_1 = imsi_1;
    }

    public String getImsi_1() {
        return imsi_1;
    }

    public void setImsi_2(String imsi_2) {
        this.imsi_2 = imsi_2;
    }

    public String getImsi_2() {
        return imsi_2;
    }

    public void setImei_1(String imei_1) {
        this.imei_1 = imei_1;
    }

    public String getImei_1() {
        return imei_1;
    }

    public void setImei_2(String imei_2) {
        this.imei_2 = imei_2;
    }

    public String getImei_2() {
        return imei_2;
    }

    public void setPhoneType_1(Integer phoneType_1) {
        this.phoneType_1 = phoneType_1;
    }

    public Integer getPhoneType_1() {
        return phoneType_1;
    }

    public void setPhoneType_2(Integer phoneType_2) {
        this.phoneType_2 = phoneType_2;
    }

    public Integer getPhoneType_2() {
        return phoneType_2;
    }

    public void setDefaultImsi(String imsi) {
        this.defaultImsi = imsi;
    }

    public String getDefaultImsi() {
        return defaultImsi;
    }

    public void setGaotongDoubleSim(Boolean gaotongDoubleSim) {
        this.gaotongDoubleSim = gaotongDoubleSim;
    }

    public Boolean isGaotongDoubleSim() {
        return gaotongDoubleSim;
    }
}
