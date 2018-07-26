package com.hushijie.hccamera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hushijie.hccamera.Constants;
import com.hushijie.hccamera.R;
import com.hushijie.hccamera.utils.MediaUtil;

/**
 * 系统电量监听
 * Created by lichao on 2018/7/11.
 */

public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int current = intent.getExtras().getInt("level");// 获得当前电量
        int total = intent.getExtras().getInt("scale");// 获得总电量
        int percent = current * 100 / total;
        Constants.BATTERY = percent + "%";
        if (percent <= 15) {
            MediaUtil.play(R.raw.low_power);
        }
    }
}
