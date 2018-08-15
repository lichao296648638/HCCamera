package com.hushijie.hccamera.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.hushijie.hccamera.Constants;
import com.hushijie.hccamera.activity.ConversationActivity;
import com.hushijie.hccamera.entity.InstructionEntity;
import com.hushijie.hccamera.entity.JoinRoomEntity;
import com.hushijie.hccamera.network.Http;
import com.hushijie.hccamera.network.ResponseState;
import com.hushijie.hccamera.network.SimpleSubscriber;
import com.hushijie.hccamera.tencent.RoomHelper;
import com.hushijie.hccamera.utils.Logs;
import com.hushijie.hccamera.utils.SerialPortProfile;
import com.hushijie.hccamera.utils.ToastUtils;
import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

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

    /**
     * intent数据key——请求业务码
     */
    public static final String EXT_KEY_REQUEST = "request";

    /**
     * 查询设备是否在线指令
     */
    public final String ONLINE = "api_is_online";

    /**
     * 查询设备是否在线指令
     */
    public final String QUIT = "api_close_call";

    /**
     * 旋转摄像头
     */
    public final String ROTATE = "api_rotate_device";


    /**
     * 开始护理
     */
    public final String SERVICE = "api_start_service";


    /**
     * 来电指令
     */
    private final String INCOMING_CALL = "api_incoming_call";

    /**
     * 串口通信工具
     */
    private SerialPortManager mSerialPortManager = new SerialPortManager();

    /**
     * 串口查找工具
     */
    private SerialPortFinder serialPortFinder = new SerialPortFinder();

    /**
     * 串口列表
     */
    private ArrayList<Device> devices = new ArrayList<>();

    /**
     * 是否已经打开串口
     */
    private boolean port = false;


    private String mInstruction;

    public PushReceiver() {
        //打开串口
        if (!port)
            port = mSerialPortManager.openSerialPort(new File("/dev/ttyMT2"), 9600);
        Logs.i(TAG, "串口状态" + port);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Logs.d(TAG, "onReceive - " + intent.getAction());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Logs.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Logs.d(TAG, "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            mInstruction = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Logs.d(TAG, "收到了通知" + mInstruction);
            try {
                JSONObject jsonObject = new JSONObject(mInstruction);
                InstructionEntity instructionEntity = Http.getInstance().gson.fromJson(jsonObject.toString(), InstructionEntity.class);

                if (!TextUtils.isEmpty(jsonObject.getString("instruction"))) {
                    switch (jsonObject.getString("instruction")) {
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
                        //查询设备在线状态
                        case ONLINE:
                            JSONObject backJson = Http.entity2String("", 1, "设备在线");
                            try {
                                backJson.put("idenKey", instructionEntity.getIdenKey());
                                backJson.put("idenAccountId", "");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            long currTime = System.currentTimeMillis();
                            long sendTime = Long.parseLong(instructionEntity.getSendTime());
                            //超时5秒不处理
                            if ((currTime - sendTime) / 1000 > 5)
                                return;
                            Http.getInstance().postInstruction(new SimpleSubscriber<ResponseState>() {

                                @Override
                                public void onNext(ResponseState entity) {
                                    ToastUtils.s(entity.getTip());
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                }
                            }, backJson);
                            break;
                        //视频来电
                        case INCOMING_CALL:
                            //待用户不在房间内再发起
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (Constants.IN_ROOM){
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //获取房号信息后开启视频页面
                                    Http.getInstance().joinRoom(new SimpleSubscriber<JoinRoomEntity>() {
                                        @Override
                                        public void onNext(JoinRoomEntity entity) {
                                            Intent videoIntent = new Intent();
                                            videoIntent.setComponent(new ComponentName("com.hushijie.hccamera", "com.hushijie.hccamera.activity.ConversationActivity"));
                                            videoIntent.putExtra(EXT_KEY_OBJ, entity);
                                            videoIntent.putExtra(EXT_KEY_REQUEST, ConversationActivity.REQUEST_CODE_CALL);
                                            videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(videoIntent);
                                        }
                                    }, Constants.IMEI);
                                }
                            }).start();

                            break;
                        //退出房间
                        case QUIT:
                            RoomHelper.getInstance().quitRoom();
                            break;
                        //旋转设备
                        case ROTATE:
                            JSONObject backRotateJson = null;
                            //获取旋转方向
                            String direction = instructionEntity.getDirection();
                            //转换成串口数据
                            byte[] data = SerialPortProfile.turn(direction);
                            //发送串口数据
                            if (mSerialPortManager.sendBytes(data)) {
                                backRotateJson = Http.entity2String("", 1, direction + "成功");
                            } else {
                                backRotateJson = Http.entity2String("", 0, direction + "失败");
                            }
                            //完事儿后返回一个反馈
                            try {
                                backRotateJson.put("idenKey", instructionEntity.getIdenKey());
                                backRotateJson.put("idenAccountId", instructionEntity.getIdenAccountId());
                                backRotateJson.put("data", new JSONObject());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Http.getInstance().postInstruction(new SimpleSubscriber<ResponseState>() {

                                @Override
                                public void onNext(ResponseState entity) {
                                    ToastUtils.s(entity.getTip());
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                }
                            }, backRotateJson);
                            break;

                        //护理服务
                        case SERVICE:
                            //开启房间
                            Intent createRoomIntent = new Intent(context, ConversationActivity.class);
                            createRoomIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            createRoomIntent.putExtra(EXT_KEY_REQUEST, ConversationActivity.REQUEST_CODE_START_PUSH);
                            context.startActivity(createRoomIntent);
                            //告知小程序开始准备护理
                            JSONObject backServiceJson = Http.entity2String("", 1, "开始护理");
                            try {
                                backServiceJson.put("idenKey", instructionEntity.getIdenKey());
                                backServiceJson.put("idenAccountId", instructionEntity.getIdenAccountId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Http.getInstance().postInstruction(new SimpleSubscriber<ResponseState>() {

                                @Override
                                public void onNext(ResponseState entity) {
                                    ToastUtils.s(entity.getTip());
                                }

                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                }
                            }, backServiceJson);
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

//    /**
//     * 旋转摄像头
//     *
//     * @param data 待发送的串口数据
//     */
//    private boolean rotateCamera(byte[] data) {
//        boolean sendBytes = false;
//        switch (direction) {
//            case "top":
//                break;
//            case "left":
//                // 转换左转命令代码
//                String strLeft = "34";
//
//                if (strLeft.length() == 0) {
//                    return false;
//                }
//
//
//                byte[] bLeft = new byte[strLeft.length() >> 1];
//
//                try {
//                    for (int i = 0; i < (bLeft.length); i++) {
//                        bLeft[i] = (byte) (Integer.parseInt(
//                                strLeft.substring(i << 1, (i + 1) << 1), 16));
//
//                        Logs.d(TAG,
//                                "bLeft[" + i + "]:" + Integer.toHexString(bLeft[i]));
//                    }
//                } catch (java.lang.NumberFormatException nfe) {
//                    Logs.d(TAG, "nfe:" + nfe.getMessage());
//                    return false;
//                }
//                //向串口发送代码
//                sendBytes = mSerialPortManager.sendBytes(bLeft);
////                mSerialPortManager.closeSerialPort();
//                return sendBytes;
//
//
//            case "right":
//                // 转换右转命令代码
//                String strRight = "33";
//
//                if (strRight.length() == 0) {
//                    return false;
//                }
//
//
//                byte[] bRight = new byte[strRight.length() >> 1];
//
//                try {
//                    for (int i = 0; i < (bRight.length); i++) {
//                        bRight[i] = (byte) (Integer.parseInt(
//                                strRight.substring(i << 1, (i + 1) << 1), 16));
//
//                        Logs.d(TAG,
//                                "bRight[" + i + "]:" + Integer.toHexString(bRight[i]));
//                    }
//                } catch (java.lang.NumberFormatException nfe) {
//                    Logs.d(TAG, "nfe:" + nfe.getMessage());
//                    return false;
//                }
//                //向串口发送代码
//
//                sendBytes = mSerialPortManager.sendBytes(bRight);
////                mSerialPortManager.closeSerialPort();
//                return sendBytes;
//            case "bottom":
//                break;
//        }
//        return false;
//
//    }
}
