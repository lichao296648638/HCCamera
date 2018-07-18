package com.hushijie.hccamera.entity;

import java.io.Serializable;

/**
 * 加入房间实体
 * Created by lichao on 2018/7/17.
 */

public class JoinRoomEntity implements Serializable{

    /**
     * code : 1
     * tip : 请求成功
     * userID : handler_591_2300
     * roomID : 8287982
     * privateMapKey : eJxNkF1PwjAUhv*K6S1G23WbHQkXRRmQOKQBFL1ptq5lhTHqtvJl-O8OhOi5fN73vOfjC0yfJ3exMTrlcc1xmYI2gOD2FwuxsUXN64OR-7BOZVFrpWXZwCwu0lyW3AsQdzC8eqp0xc*pjQW5ECKIXc*7iHJvdCl5rOpzBPb-2vSiAVGPPQ5pK2bzoWWfTy4Vy1TO5kN-MBUP4YEMcJbsZmKcsFk3UKsJ1RT2GH0-jLr5gtK1D3UO-cKU5H6LlPVMyzlORFLZJDML1ulchtV6fToLeRgRhzjBdT1byTKxqpEopSzuh-ajv89FTtDLNFTRMtpF4ejoZoSqIrFvEK9fd41zTOipLiFbWVZ6U4D2DXAg8tDpN02B7x-L1W9X
     * pushUrl : rtmp://27956.livepush.mycloud.com/live/27956_video_8287982?txSecret=57cec1a0b957d0b38b30e33ce5436411&txTime=164a814d75b
     */

    private int code;
    private String tip;
    private String userID;
    private String roomID;
    private String privateMapKey;
    private String pushUrl;

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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getPrivateMapKey() {
        return privateMapKey;
    }

    public void setPrivateMapKey(String privateMapKey) {
        this.privateMapKey = privateMapKey;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }
}
