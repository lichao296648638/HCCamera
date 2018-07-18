package com.hushijie.hccamera.tencent;

/**
 * 腾讯云登陆结果接口
 * Created by lichao on 2018/7/17.
 */

public interface ILoginView {
    // 登录成功
    void onLoginSDKSuccess();
    // 登录失败
    void onLoginSDKFailed(String module, int errCode, String errMsg);

}
