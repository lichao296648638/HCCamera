package com.hushijie.hccamera.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.hushijie.hccamera.Constants;
import com.hushijie.hccamera.R;
import com.hushijie.hccamera.entity.JoinRoomEntity;
import com.hushijie.hccamera.entity.TencentSigEntity;
import com.hushijie.hccamera.network.Http;
import com.hushijie.hccamera.network.SimpleSubscriber;
import com.hushijie.hccamera.receiver.PushReceiver;
import com.hushijie.hccamera.tencent.ILoginView;
import com.hushijie.hccamera.tencent.IRoomView;
import com.hushijie.hccamera.tencent.LoginHelper;
import com.hushijie.hccamera.tencent.RoomHelper;
import com.hushijie.hccamera.utils.Logs;
import com.hushijie.hccamera.utils.ToastUtils;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.view.AVRootView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 视频通话页
 * Created by lichao on 2017/1/8.
 */

public class VideoActivity extends Activity implements IRoomView, ILoginView{
    private static final String TAG = "VideoActivity";

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
        //登陆腾讯云
        loginHelper = new LoginHelper(this);
        loginTencentSDK();
        roomHelper = new RoomHelper(this);
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
        roomHelper.joinRoom(Integer.parseInt(joinRoomEntity.getRoomID()), joinRoomEntity.getPrivateMapKey());

    }

    @Override
    public void onLoginSDKFailed(String module, int errCode, String errMsg) {
        ToastUtils.s("登陆腾讯云失败");
    }




}
