package com.hushijie.hccamera.tencent;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.hushijie.hccamera.Constants;
import com.hushijie.hccamera.activity.WifiActivity;
import com.hushijie.hccamera.network.Http;
import com.hushijie.hccamera.network.ResponseState;
import com.hushijie.hccamera.network.SimpleSubscriber;
import com.hushijie.hccamera.utils.ToastUtils;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILivePushOption;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.data.ILivePushRes;
import com.tencent.ilivesdk.data.ILivePushUrl;
import com.tencent.ilivesdk.view.AVRootView;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 腾讯云创建房间帮助类
 * Created by lichao on 2018/7/17.
 */

public class RoomHelper implements ILiveRoomOption.onExceptionListener, ILiveRoomOption.onRoomDisconnectListener {
    /**
     * 心跳定时器
     */
    private Timer timer;
    /**
     * 心跳任务
     */
    private  TimerTask task;
    private IRoomView roomView;
    private Activity mActivity;
    private static RoomHelper mRoomHelper;

    /**
     * 联网参数-map
     */
    private Map<String, Object> mMapParam = new HashMap<>();

    private Handler handler;
    /**
     * 心跳消息
     */
    private int MSG_ROOM_HEART = 0;

    private RoomHelper() {
        //初始化任务
        //test git
        initTask();

        handler = new Handler() {

            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_ROOM_HEART) {
                    Http.getInstance().roomHeart(new SimpleSubscriber<ResponseState>() {

                        @Override
                        public void onNext(ResponseState entity) {
                            ToastUtils.s(entity.getTip());
                            //检测是否已经退出房间
                            if (entity.getCode() != Constants.CODE_ALREADY_EXIT) {
                                quitRoom();
                            }
                        }
                    }, mMapParam);
                }
            }
        };
    }

    public static RoomHelper getInstance() {
        if (mRoomHelper == null) {

            mRoomHelper = new RoomHelper();
        }
        return mRoomHelper;
    }

    public void init(IRoomView view, Activity activity) {
        roomView = view;
        mActivity = activity;
    }


    // 设置渲染控件
    public void setRootView(AVRootView avRootView) {
        ILiveRoomManager.getInstance().initAvRootView(avRootView);
    }

    //

    /**
     * 创建房间
     *
     * @param roomId        房号
     * @param privateMapKey 进房票据
     * @param bRecord       是否由设备录制并上传
     * @return
     */
    public int createRoom(final int roomId, String privateMapKey, final boolean bRecord) {
        ILiveRoomOption option = new ILiveRoomOption()
                .authBuffer(privateMapKey.getBytes())
                .imsupport(false)       // 不需要IM功能
                .exceptionListener(this)  // 监听异常事件处理
                .roomDisconnectListener(this)   // 监听房间中断事件
                .controlRole("user")    // 使用user角色
                .autoCamera(true)       // 进房间后自动打开摄像头并上行
                .autoMic(true);         // 进房间后自动要开Mic并上行

        return ILiveRoomManager.getInstance().createRoom(roomId, option, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                //加入房间
                roomView.onEnterRoom();

                //开启推流
                ILivePushOption.RecordFileType recordFileType = bRecord ?
                        ILivePushOption.RecordFileType.RECORD_HLS_FLV_MP4 : ILivePushOption.RecordFileType.NONE;
                ILivePushOption option = new ILivePushOption()
                        .encode(ILivePushOption.Encode.HLS_AND_RTMP)         // 旁路直播协议类型
                        .setRecordFileType(recordFileType)      // 录制文件格式
                        //手动推流自动录制时，如果需要后台识别特定的录制文件，用户可以通过这个字段做区分。
                        // (使用这个字段时，控制台的“自动旁路直播”开关必须关闭)
                        .setRecordId((int) System.currentTimeMillis());
                ILiveRoomManager.getInstance().startPushStream(option, new ILiveCallBack<ILivePushRes>() {
                    @Override
                    public void onSuccess(ILivePushRes data) {
                        if (null != data.getUrls()) {
                            // 遍历推流类型及地址
                            for (ILivePushUrl url : data.getUrls()) {
                                // 处理播放地址
                            }
                        }
                        //开启直播间心跳
                        roomHeart(roomId);
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        // 处理推流失败
//                        quitRoom();
                    }
                });
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                roomView.onEnterRoomFailed(module, errCode, errMsg);
            }
        });
    }


    // 加入房间
    public int joinRoom(final int roomId, String privateMapKey) {
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
                //开启直播间心跳
                roomHeart(roomId);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                roomView.onEnterRoomFailed(module, errCode, errMsg);
                // 处理推流失败
                quitRoom();
            }
        });
    }

    // 退出房间
    public int quitRoom() {
        //结束心跳
        if(timer != null){
            timer.purge();
            timer.cancel();
            timer = null;
        }
        int i = 0;
        //有网情况下退出房间
        if (WifiActivity.getNetWorkInfo() == 1) {
            i = ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
                @Override
                public void onSuccess(Object data) {
                    if (roomView != null)
                        roomView.onQuitRoomSuccess();

                }

                @Override
                public void onError(String module, int errCode, String errMsg) {
                    if (roomView != null)
                        roomView.onQuitRoomFailed(module, errCode, errMsg);
                }
            });
        } else {
            mActivity.finish();
        }


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


    /**
     * 直播间心跳
     */
    private void roomHeart(int roomID) {
        //该任务内容
        mMapParam.clear();
        mMapParam.put("no", Constants.IMEI);
        mMapParam.put("roomID", roomID);
        //每5S触发一次
        initTask();
        timer.schedule(task, 0, 5 * 1000);
    }

    /**
     * 初始化心跳
     */
    private void initTask() {
        timer = new Timer(true);
        task = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = MSG_ROOM_HEART;
                handler.sendMessage(msg);
            }

        };
    }
}