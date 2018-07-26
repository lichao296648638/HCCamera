package com.hushijie.hccamera.tencent;

import com.hushijie.hccamera.tencent.ILoginView;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

/**
 * 腾讯云登陆结果工具类
 * Created by lichao on 2018/7/17.
 */

public class LoginHelper {
    private ILoginView loginView;
    private static LoginHelper mLoginHelper;

    private LoginHelper() {

    }

    public static LoginHelper getInstance() {
        if (mLoginHelper == null) {
            mLoginHelper = new LoginHelper();
        }
        return mLoginHelper;
    }

    public void init(ILoginView view) {
        loginView = view;

    }

    public void loginSDK(String userId, String userSig) {
        ILiveLoginManager.getInstance().iLiveLogin(userId, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                loginView.onLoginSDKSuccess();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                loginView.onLoginSDKFailed(module, errCode, errMsg);
            }
        });
    }



    public boolean isLoginIn() {
        return ILiveLoginManager.getInstance().isLogin();
    }
}