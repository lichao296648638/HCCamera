package com.hushijie.hccamera.entity;

import java.util.List;
import java.util.UUID;

/**
 * 蓝牙设备信息实体
 * Created by lichao on 2018/7/12.
 */

public class BleAdvertiseEntity {
    private List<UUID> mUuids;
    private String mName;
    public BleAdvertiseEntity(List<UUID> uuids, String name){
        mUuids = uuids;
        mName = name;
    }

    public List<UUID> getUuids(){
        return mUuids;
    }

    public String getName(){
        return mName;
    }

}
