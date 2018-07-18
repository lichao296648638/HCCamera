package com.hushijie.hccamera.tencent;

/**
 * 腾讯云加入房间回调接口
 * Created by lichao on 2018/7/17.
 */

public interface IRoomView {
    // 进入房间成功
    void onEnterRoom();
    // 进房间失败
    void onEnterRoomFailed(String module, int errCode, String errMsg);

    // 退出房间成功
    void onQuitRoomSuccess();
    // 退出房间失败
    void onQuitRoomFailed(String module, int errCode, String errMsg);

    // 房间断开
    void onRoomDisconnect(String module, int errCode, String errMsg);

}
