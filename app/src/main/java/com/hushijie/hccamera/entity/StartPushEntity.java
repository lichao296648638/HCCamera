package com.hushijie.hccamera.entity;

/**
 * 推流结果实体
 * Created by lichao on 2018/7/19.
 */

public class StartPushEntity {

    /**
     * code : 1
     * tip : 请求成功
     * userID : tel_591_6b35e5a4
     * roomID : 6062
     * roomInfo : 视频通话房间
     * privateMapKey : eJxNj91vgjAUxf*Vpa8uWwsUxWQP1bmhExU-8OOlqaVqRZFAQWXZ-77qMNl5u79zcu6932Dan7ywJJEhZYqaaQiaAILnP8z5KY8VVddE-MMyFLGSGylSDZU4UOwgaq9NLDCzqkwWRvTeqiPIghBB08K4MsUlkamgbKPuFaYNH9WZ3GrgdWbtbsfz*4ceGq1yI1hMA16ffA350HUhY5Nrjyj1flHdS*HAokdkh9TPbqFKEcU1Z*WPeatM8P645OvylO1SiyG7HjX2Lm*Px9FbtUzJ4*0thE3k2Bijx*15JtJ1vtEWIcQPP4NsUc6wFzSM5d5Dq2mwG3wMSmvXIKQl8jl8rc23OjnSs1ZVUog0k6cYNJ*AARFGhglvAj*-Bt5vIA__
     * videoUrl : rtmp://27956.livepush.mycloud.com/live/27956_video_6062?txSecret=57cec1a0b957d0b38b30e33ce5436411&txTime=164b042a141
     * pushUrl :
     */

    private int code;
    private String tip;
    private String userID;
    private String roomID;
    private String roomInfo;
    private String privateMapKey;
    private String videoUrl;
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

    public String getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(String roomInfo) {
        this.roomInfo = roomInfo;
    }

    public String getPrivateMapKey() {
        return privateMapKey;
    }

    public void setPrivateMapKey(String privateMapKey) {
        this.privateMapKey = privateMapKey;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }
}
