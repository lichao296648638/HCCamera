package com.hushijie.hccamera;

/**
 * 常量
 * Created by zhangkun on 2017/5/25.
 */

public class Constants {

    //域名
    public static String SERVER_URL = BuildConfig.SERVER;

    //IMEI
    public static String IMEI = "";

    //蓝牙设备ID
    public static String BLE_ID = "";

    //电量
    public static int BATTERY = 100;

    //成功
    public static final int CODE_SUCCESS = 1;

    //失败
    public static final int CODE_FAIL = 0;

    //此用户已绑定本设备,无需在绑定
    public static final int CODE_USER_BINDED = 20020;

    //已退出房间
    public static final int CODE_ALREADY_EXIT = 5003;


    //是否退出了房间
    public static boolean IN_ROOM = false;
}
