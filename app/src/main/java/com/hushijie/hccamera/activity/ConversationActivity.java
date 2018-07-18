package com.hushijie.hccamera.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.hushijie.hccamera.Constants;
import com.hushijie.hccamera.MyApplication;
import com.hushijie.hccamera.R;
import com.hushijie.hccamera.entity.JoinRoomEntity;
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
import com.hushijie.hccamera.utils.ToastUtils;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILivePushOption;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.data.ILivePushRes;
import com.tencent.ilivesdk.data.ILivePushUrl;
import com.tencent.ilivesdk.view.AVRootView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 视频通话页
 * Created by lichao on 2017/1/8.
 */

public class ConversationActivity extends Activity implements IRoomView, ILoginView {
    private static final String TAG = "ConversationActivity";
    @BindView(R.id.bt_startPush)
    Button btStartPush;
    @BindView(R.id.bt_endPush)
    Button btEndPush;

    /**
     * 联网参数
     */
    private Map<String, Object> mapParam = new HashMap();

    /**
     * 腾讯云登陆工具类
     */
    LoginHelper loginHelper;

    /**
     * 腾讯云建房工具类
     */
    RoomHelper roomHelper;

    /**
     * 渲染控件
     */
    AVRootView avRootView;

    /**
     * 房间信息数据
     */
    JoinRoomEntity joinRoomEntity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        //登陆腾讯云
        loginHelper = new LoginHelper(this);
        loginTencentSDK();
        roomHelper = new RoomHelper(this, this);
        // 获取渲染控件
        avRootView = (AVRootView) findViewById(R.id.av_root_view);
        // 设置没有渲染时的背景色为蓝色(注意不支持在布局中直接设置)
        avRootView.getVideoGroup().setBackgroundColor(Color.BLUE);
        // 设置渲染控件
        roomHelper.setRootView(avRootView);
        //获取房号信息
        joinRoomEntity = (JoinRoomEntity) getIntent().getSerializableExtra(PushReceiver.EXT_KEY_OBJ);
    }

    @Override
    public void onEnterRoom() {
        Logs.i(TAG, "onEnterRoom: ");
    }

    @Override
    public void onEnterRoomFailed(String module, int errCode, String errMsg) {
        Logs.i(TAG, "onEnterRoomFailed: ");
    }

    @Override
    public void onQuitRoomSuccess() {
        Logs.i(TAG, "onQuitRoomSuccess: ");
    }

    @Override
    public void onQuitRoomFailed(String module, int errCode, String errMsg) {
        Logs.i(TAG, "onQuitRoomFailed: ");
    }

    @Override
    public void onRoomDisconnect(String module, int errCode, String errMsg) {
        Logs.i(TAG, "onRoomDisconnect: ");
    }


    /**
     * 获取腾讯云签名后登陆
     */
    private void loginTencentSDK() {

        Http.getInstance().getTencentSig(new SimpleSubscriber<TencentSigEntity>() {
            @Override
            public void onNext(TencentSigEntity entity) {
                loginHelper.loginSDK(entity.getUserID(), entity.getUserSig());
            }
        }, Constants.IMEI);
    }

    @Override
    public void onLoginSDKSuccess() {
        ToastUtils.s("登陆腾讯云成功");

        //加入房间
        if (TextUtils.isEmpty(joinRoomEntity.getRoomID()) || TextUtils.isEmpty(joinRoomEntity.getPrivateMapKey())) {
            ToastUtils.s("房间信息错误");
            finish();
            return;
        }
        roomHelper.joinRoom(Integer.parseInt(joinRoomEntity.getRoomID()), joinRoomEntity.getPrivateMapKey());

    }

    @Override
    public void onLoginSDKFailed(String module, int errCode, String errMsg) {
        ToastUtils.s("登陆腾讯云失败");
        finish();
    }


    @OnClick({R.id.bt_startPush, R.id.bt_endPush})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_startPush:
                startPush(false);
                break;
            case R.id.bt_endPush:
                stopPush();
                break;
        }
    }

    /**
     * 开始推流
     *
     * @param bRecord 是否录制
     */
    private void startPush(boolean bRecord) {
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

                //通知server开始推流
                Http.getInstance().startPush(new SimpleSubscriber<ResponseState>() {

                    @Override
                    public void onNext(ResponseState entity) {
                        ToastUtils.s(entity.getTip());
                    }
                }, Constants.IMEI);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                // 处理推流失败
            }
        });
    }

    /**
     * 停止推流
     */
    private void stopPush() {
        ILiveRoomManager.getInstance().stopPushStream(0, // 直播码模式下填0即可
                new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        // 停止成功

                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        // 停止失败
                    }
                });
        //通知server停止推流
        Http.getInstance().endPush(new SimpleSubscriber<ResponseState>() {

            @Override
            public void onNext(ResponseState entity) {
                ToastUtils.s(entity.getTip());
            }
        }, Constants.IMEI);
    }


}
