package com.hushijie.hccamera.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hushijie.hccamera.entity.InstructionEntity;
import com.hushijie.hccamera.network.Http;
import com.hushijie.hccamera.utils.Logs;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import static com.inuker.bluetooth.library.utils.BluetoothUtils.sendBroadcast;

/**
 * 极光推送消息接收者
 * Created by lichao on 2018/7/10.
 */

public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "PushReceiver";
    /**
     * intent数据key——对象
     */
    public static final String EXT_KEY_OBJ = "object";

    /**
     * intent数据key——指令
     */
    public static final String EXT_KEY_INS = "instruction";

    private String mInstruction;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Logs.d(TAG, "onReceive - " + intent.getAction());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logs.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
        }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Logs.d(TAG, "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            mInstruction = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Logs.d(TAG, "收到了通知" + mInstruction);
            try {
                JSONObject jsonObject = new JSONObject(mInstruction);
                InstructionEntity instructionEntity = Http.getInstance().gson.fromJson(jsonObject.toString(), InstructionEntity.class);

                if (!TextUtils.isEmpty(jsonObject.getString("instruction"))) {
                    switch (jsonObject.getString("instruction")){
                        //查找周围蓝牙设备
                        case BluetoothInstructionReceiver.FIND:
                            Intent findBleIntent = new Intent(BluetoothInstructionReceiver.ACTION_INS_BLE);
                            findBleIntent.putExtra(EXT_KEY_OBJ, instructionEntity);
                            findBleIntent.putExtra(EXT_KEY_INS, BluetoothInstructionReceiver.FIND);
                            context.sendBroadcast(findBleIntent);
                            break;
                        //连接对应蓝牙设备
                        case BluetoothInstructionReceiver.CONNECT:
                            Intent connectBleIntent = new Intent(BluetoothInstructionReceiver.ACTION_INS_BLE);
                            connectBleIntent.putExtra(EXT_KEY_OBJ, instructionEntity);
                            connectBleIntent.putExtra(EXT_KEY_INS, BluetoothInstructionReceiver.CONNECT);
                            context.sendBroadcast(connectBleIntent);
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Logs.d(TAG, "用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
//            Intent i = new Intent(context, TestActivity.class);  //自定义打开的界面
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
        } else {
            Logs.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }
}
