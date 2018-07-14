package com.hushijie.hccamera;

import android.annotation.SuppressLint;
import android.app.Application;
import android.telephony.TelephonyManager;


import com.hushijie.hccamera.activity.WifiActivity;
import com.hushijie.hccamera.entity.DeviceInfo;
import com.hushijie.hccamera.receiver.BluetoothInstructionReceiver;
import com.hushijie.hccamera.utils.SharedPreferencesUtil;
import com.hushijie.hccamera.utils.ToastUtils;

import cn.jpush.android.api.JPushInterface;


/**
 * 入口
 * Created by lichao on 2018/6/26.
 */

public class MyApplication extends Application {

    /**
     * 设备信息
     */
    DeviceInfo deviceInfo;

    private static MyApplication mInstance;

    public static MyApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化设备信息
        deviceInfo = new DeviceInfo();
        String wifiSSID = SharedPreferencesUtil.getInstance(this).getSP(WifiActivity.SP_KEY_WIFI_SSID);
        String wifiPass = SharedPreferencesUtil.getInstance(this).getSP(WifiActivity.SP_KEY_WIFI_PASS);
        String bleName = SharedPreferencesUtil.getInstance(this).getSP(BluetoothInstructionReceiver.SP_KEY_BLE_NAME);
        String bleMac = SharedPreferencesUtil.getInstance(this).getSP(BluetoothInstructionReceiver.SP_KEY_BLE_MAC);
        deviceInfo.setBleMac(bleMac);
        deviceInfo.setBleName(bleName);
        deviceInfo.setWifiSSID(wifiSSID);
        deviceInfo.setWifiPass(wifiPass);
        //初始化吐司
        ToastUtils.init(this);
        //初始化极光
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission")
        String imei = TelephonyMgr.getDeviceId();
        Constants.IMEI = imei;
        String alias = String.format("hc_equipmentNo_%s", imei);
        JPushInterface.setAlias(this, 0, alias);

    }



}
