package com.hushijie.hccamera.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.hushijie.hccamera.Constants;
import com.hushijie.hccamera.R;
import com.hushijie.hccamera.entity.JoinRoomEntity;
import com.hushijie.hccamera.entity.StartPushEntity;
import com.hushijie.hccamera.entity.TencentSigEntity;
import com.hushijie.hccamera.network.Http;
import com.hushijie.hccamera.network.ResponseState;
import com.hushijie.hccamera.network.SimpleSubscriber;
import com.hushijie.hccamera.receiver.PushReceiver;
import com.hushijie.hccamera.tencent.ILoginView;
import com.hushijie.hccamera.tencent.IRoomView;
import com.hushijie.hccamera.tencent.LoginHelper;
import com.hushijie.hccamera.tencent.RoomHelper;
import com.hushijie.hccamera.utils.Logs;
import com.hushijie.hccamera.utils.MediaUtil;
import com.hushijie.hccamera.utils.ToastUtils;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.view.AVRootView;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 视频通话页
 * Created by lichao on 2017/1/8.
 */

public class ConversationActivity extends Activity implements IRoomView, ILoginView {

    /**
     * 呼叫服务业务码
     */
    public static final int REQUEST_CODE_CALL = 0;

    /**
     * 开启直播业务码
     */
    public static final int REQUEST_CODE_START_PUSH = 1;

    private static final String TAG = "ConversationActivity";
    @BindView(R.id.bt_quit_room)
    Button btQuitRoom;

    /**
     * 联网参数
     */
    private Map<String, Object> mMapParam = new HashMap();

//    /**
//     * 腾讯云登陆工具类
//     */
//    private LoginHelper loginHelper;
//
//    /**
//     * 腾讯云建房工具类
//     */
//    private RoomHelper roomHelper;

    /**
     * 渲染控件
     */
    private AVRootView avRootView;

    /**
     * 房间信息数据
     */
    private JoinRoomEntity joinRoomEntity;

    /**
     * 腾讯云登陆信息
     */
    private TencentSigEntity tencentSigEntity;

    /**
     * 业务请求码
     */
    private int mRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this);
        //获取房号信息
        joinRoomEntity = (JoinRoomEntity) getIntent().getSerializableExtra(PushReceiver.EXT_KEY_OBJ);
        //获取业务请求码
        mRequestCode = getIntent().getIntExtra(PushReceiver.EXT_KEY_REQUEST, -1);
        //登录腾讯云
        LoginHelper.getInstance().init(this);
        loginTencentSDK();
        RoomHelper.getInstance().init(this, this);
        // 获取渲染控件
        avRootView = (AVRootView) findViewById(R.id.av_root_view);
        // 设置没有渲染时的背景色为蓝色(注意不支持在布局中直接设置)
        avRootView.getVideoGroup().setBackgroundColor(Color.BLUE);
        // 设置渲染控件
        RoomHelper.getInstance().setRootView(avRootView);
    }

    @Override
    public void onEnterRoom() {
        Logs.i(TAG, "onEnterRoom: ");
        MediaUtil.play(R.raw.incoming_call);

    }

    @Override
    public void onEnterRoomFailed(String module, int errCode, String errMsg) {
        Logs.i(TAG, "onEnterRoomFailed: ");
        finish();
    }

    @Override
    public void onQuitRoomSuccess() {
        Logs.i(TAG, "onQuitRoomSuccess: ");
        MediaUtil.play(R.raw.call_over);
        finish();

    }

    @Override
    public void onQuitRoomFailed(String module, int errCode, String errMsg) {
        Logs.i(TAG, "onQuitRoomFailed: ");
        finish();
    }

    @Override
    public void onRoomDisconnect(String module, int errCode, String errMsg) {
        Logs.i(TAG, "onRoomDisconnect: ");
        finish();
    }


    /**
     * 获取腾讯云签名后登陆
     */
    private void loginTencentSDK() {

        Http.getInstance().getTencentSig(new SimpleSubscriber<TencentSigEntity>() {
            @Override
            public void onNext(TencentSigEntity entity) {
                if (entity.getCode() == 1) {
                    tencentSigEntity = entity;
                    //是否登陆腾讯云
                    if (!LoginHelper.getInstance().isLoginIn()) {
                        LoginHelper.getInstance().loginSDK(entity.getUserID(), entity.getUserSig());
                    } else {
                        parseRequestCode(mRequestCode);
                    }
                }
            }
        }, Constants.IMEI);
    }

    @Override
    public void onLoginSDKSuccess() {
        ToastUtils.s("登陆腾讯云成功");

        parseRequestCode(mRequestCode);

    }

    @Override
    public void onLoginSDKFailed(String module, int errCode, String errMsg) {
        ToastUtils.s("登陆腾讯云失败");
        finish();
    }


    @OnClick({ R.id.bt_quit_room})
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.bt_stop_push:
//                stopPush();
//                break;
            case R.id.bt_quit_room:
                quitRoom();
                break;
        }
    }

    /**
     * 创建房间并推流
     *
     * @param bRecord 是否录制
     */
    private void createRoom(boolean bRecord) {
        //通知server开始推流
        Http.getInstance().startPush(new SimpleSubscriber<StartPushEntity>() {

            @Override
            public void onNext(StartPushEntity entity) {
                ToastUtils.s(entity.getTip());
                //如果server请求成功，则开启房间
                if (entity.getCode() == 1) {
                    RoomHelper.getInstance().createRoom(Integer.parseInt(entity.getRoomID()), entity.getPrivateMapKey(), false);
                }
            }
        }, tencentSigEntity.getUserID());
    }

    /**
     * 停止推流
     */
    private void stopPush() {
//        //通知server停止推流
//        Http.getInstance().endPush(new SimpleSubscriber<ResponseState>() {
//
//            @Override
//            public void onNext(ResponseState entity) {
//                ToastUtils.s(entity.getTip());
//            }
//        }, Constants.IMEI);
//
//        ILiveRoomManager.getInstance().stopPushStream(0, // 直播码模式下填0即可
//                new ILiveCallBack() {
//                    @Override
//                    public void onSuccess(Object data) {
//                        // 停止成功
//
//                    }
//
//                    @Override
//                    public void onError(String module, int errCode, String errMsg) {
//                        // 停止失败
//                    }
//                });
    }

    /**
     * 根据业务码开启对应功能
     *
     * @param code 业务码
     */
    private void parseRequestCode(int code) {
        switch (code) {
            //加入某个房间
            case REQUEST_CODE_CALL:
                if (TextUtils.isEmpty(joinRoomEntity.getRoomID()) || TextUtils.isEmpty(joinRoomEntity.getPrivateMapKey())) {
                    ToastUtils.s("房间信息错误");
                    finish();
                    return;
                }
                RoomHelper.getInstance().joinRoom(Integer.parseInt(joinRoomEntity.getRoomID()), joinRoomEntity.getPrivateMapKey());
                break;
            //创建房间并推流
            case REQUEST_CODE_START_PUSH:
                createRoom(false);
                break;
        }
    }

    /**
     * 退出房间
     */
    private void quitRoom() {
        stopPush();
        RoomHelper.getInstance().quitRoom();
    }



}
