package com.hushijie.hccamera.entity;

/**
 * Created by lichao on 2018/6/26.
 */

public class WifiInfoEntity {

    /**
     * ssid : hushijie
     * pass : hushijie84360865
     * accountId : 1
     * idenAccountId : 2
     * organId : 1
     * idenKey : wifi_12:12:...
     * "time": 15234678645
     */

    private String ssid;
    private String pass;
    private int accountId;
    private int idenAccountId;
    private int organId;
    private String idenKey;
//    private long time;


    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getIdenAccountId() {
        return idenAccountId;
    }

    public void setIdenAccountId(int idenAccountId) {
        this.idenAccountId = idenAccountId;
    }

    public int getOrganId() {
        return organId;
    }

    public void setOrganId(int organId) {
        this.organId = organId;
    }

    public String getIdenKey() {
        return idenKey;
    }

    public void setIdenKey(String idenKey) {
        this.idenKey = idenKey;
    }

//    public long getTime() {
//        return time;
//    }
//
//    public void setTime(long time) {
//        this.time = time;
//    }
}
