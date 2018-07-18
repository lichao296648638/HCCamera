package com.hushijie.hccamera.entity;

/**
 * 获取腾讯云登陆签名接口
 * Created by lichao on 2018/7/17.
 */

public class TencentSigEntity {

    /**
     * code : 1
     * tip : 请求成功
     * sdkAppID : 1400103455
     * accountType : 29378
     * userID : tel_505_0c85
     * userSig : eJxNj11PwjAUhv*K6bWRftC6kngxlmoMA3Q6iVdNXTutYNdtZYjG-*5YwHgunzfPOe-5Bo-pw4Uqimrrggx7b8AEQHA*YKuNC7a0pulhMBtJIZWwiOgxV95bLVWQpNH-tFav5RD1DI0hRJCM6ckxn942RqoyDFsJg3*afe3BXOTJbfxOaMJXy1n3nLMUYdqX47W4f9vU7Soru9Hc7dhUe11lsRVxRL6mC0Ub*JSS8roSd4btsMB1voiTpZttR137EmfuZs2iq*OxYD8OnyKK*SXhnJ3qdaZpbeXA5AxgiCjCBB4G-PwCgzpX6Q__
     */

    private int code;
    private String tip;
    private int sdkAppID;
    private String accountType;
    private String userID;
    private String userSig;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public int getSdkAppID() {
        return sdkAppID;
    }

    public void setSdkAppID(int sdkAppID) {
        this.sdkAppID = sdkAppID;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }
}
