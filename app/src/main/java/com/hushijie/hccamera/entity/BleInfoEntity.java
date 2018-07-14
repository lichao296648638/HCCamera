package com.hushijie.hccamera.entity;

/**
 * 蓝牙信息实体
 * Created by lichao on 2018/6/12.
 */

public class BleInfoEntity {

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    private String bleName;
    private String bleAddress;
}
