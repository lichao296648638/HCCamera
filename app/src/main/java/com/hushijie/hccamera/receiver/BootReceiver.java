package com.hushijie.hccamera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hushijie.hccamera.activity.MainActivity;

/**
 * 系统开机监听
 * Created by lichao on 2018/7/11.
 */

public class BootReceiver extends BroadcastReceiver {
    /**
     * 告知wifi连接页开机后自动wifi连接 传输key
     */
    public static final String EXT_KEY_BOOT = "boot";
    /**
     * 告知wifi连接页开机后自动wifi连接 传输value
     */
    public static final String EXT_VALUE_BOOT = "boot";


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Thread.sleep(2000L);
            intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXT_KEY_BOOT, EXT_VALUE_BOOT);
            context.startActivity(intent);
//            Connector.getInstance().connectWifi(SharedPreferencesUtil.getInstance(MyApplication.getContext()).getSP(SP_KEY_WIFI_SSID),
//                    SharedPreferencesUtil.getInstance(MyApplication.getContext()).getSP(SP_KEY_WIFI_PASS));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
