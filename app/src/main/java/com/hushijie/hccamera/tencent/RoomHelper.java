package com.hushijie.hccamera.tencent;

import android.app.Activity;
import android.content.Context;

import com.hushijie.hccamera.activity.WifiActivity;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.view.AVRootView;

/**
 * 腾讯云创建房间帮助类
 * Created by lichao on 2018/7/17.
 */

public class RoomHelper implements ILiveRoomOption.onExceptionListener, ILiveRoomOption.onRoomDisconnectListener {
    private IRoomView roomView;
    private Activity mActivity;

    public RoomHelper(IRoomView view, Activity activity) {
        roomView = view;
        mActivity = activity;
    }

    // 设置渲染控件
    public void setRootView(AVRootView avRootView) {
        ILiveRoomManager.getInstance().initAvRootView(avRootView);
    }

    // 创建房间
    public int createRoom(int roomId) {
        ILiveRoomOption option = new ILiveRoomOption()
                .imsupport(false)       // 不需要IM功能
                .exceptionListener(this)  // 监听异常事件处理
                .roomDisconnectListener(this)   // 监听房间中断事件
                .controlRole("user")    // 使用user角色
                .autoCamera(true)       // 进房间后自动打开摄像头并上行
                .autoMic(true);         // 进房间后自动要开Mic并上行

        return ILiveRoomManager.getInstance().createRoom(roomId, option, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                roomView.onEnterRoom();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                roomView.onEnterRoomFailed(module, errCode, errMsg);
            }
        });
    }


    // 加入房间
    public int joinRoom(int roomId, String privateMapKey) {
        ILiveRoomOption option = new ILiveRoomOption()
                .authBuffer(privateMapKey.getBytes())
                .imsupport(false)       // 不需要IM功能
                .exceptionListener(this)  // 监听异常事件处理
                .roomDisconnectListener(this)   // 监听房间中断事件
                .controlRole("user")  // 使用user角色
                .autoCamera(false)       // 进房间后不需要打开摄像头
                .autoMic(false);         // 进房间后不需打开Mic

        return ILiveRoomManager.getInstance().joinRoom(roomId, option, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                roomView.onEnterRoom();
                //打开摄像头和麦克风
                enableCamera(ILiveConstants.FRONT_CAMERA, true);
                enableMic(true);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                roomView.onEnterRoomFailed(module, errCode, errMsg);
            }
        });
    }

    // 退出房间
    public int quitRoom() {
        int i = 0;
        //有网情况下退出房间
        if (WifiActivity.getNetWorkInfo() == 1) {
            i = ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    roomView.onQuitRoomSuccess();

                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    roomView.onQuitRoomFailed(module, errCode, errMsg);

                }
            });
        }

        mActivity.finish();
        return i;
    }

    // 处理Activity事件
    public void onPause() {
        ILiveRoomManager.getInstance().onPause();
    }

    public void onResume() {
        ILiveRoomManager.getInstance().onResume();
    }

    @Override
    public void onException(int exceptionId, int errCode, String errMsg) {
        //处理异常事件

    }

    @Override
    public void onRoomDisconnect(int errCode, String errMsg) {
        // 处理房间中断(一般为断网或长时间无长行后台回收房间)
        quitRoom();

    }

    // 摄像头
    public int enableCamera(int cameraId, boolean enable) {
        return ILiveRoomManager.getInstance().enableCamera(cameraId, enable);
    }

    // 麦克风
    public int enableMic(boolean enable) {
        return ILiveRoomManager.getInstance().enableMic(enable);
    }
}