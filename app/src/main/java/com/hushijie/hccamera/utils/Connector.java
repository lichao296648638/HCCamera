package com.hushijie.hccamera.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.hushijie.hccamera.MyApplication;
import com.hushijie.hccamera.R;
import com.hushijie.hccamera.activity.WifiActivity;

import java.util.List;

import static com.hushijie.hccamera.activity.WifiActivity.SP_KEY_WIFI_PASS;
import static com.hushijie.hccamera.activity.WifiActivity.SP_KEY_WIFI_SSID;

/**
 * wifi和蓝牙连接器
 * Created by lichao on 2018/7/18.
 */

public class Connector {
    private static Connector mConnector;
    private NetworkConnectChangedReceiver mNetworkConnectChangedReceiver;
    /**
     * wifi链接计时器
     */
    private TimeCount mTimeCount;

    /**
     * wifi管理器
     */
    private WifiManager mWifiManager;

    /**
     * 当前网络的ssid
     */
    private String mSSID;

    /**
     * wifi加密方式
     */
    private int wifiEnc = 0;
    private final int WPA = 0;
    private final int WEP = 1;
    private final int OPEN = 2;

    /**
     * 当前网络的密码
     */
    private String mPass;

    private Connector() {
        //加载wifi管理
        mWifiManager = (WifiManager) MyApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mSSID = SharedPreferencesUtil.getInstance(MyApplication.getContext()).getSP(SP_KEY_WIFI_SSID);
        mPass = SharedPreferencesUtil.getInstance(MyApplication.getContext()).getSP(SP_KEY_WIFI_PASS);
        //初始化wifi计时器
        mTimeCount = new TimeCount(10000, 1000);

    }

    public static Connector getInstance() {
        if (mConnector == null) {
            mConnector = new Connector();
        }
        return mConnector;
    }

    /**
     * 链接wifi功能函数
     *
     * @param targetSsid wifi名称
     * @param targetPsd  wifi密码
     */
    public void connectWifi(String targetSsid, String targetPsd) {
        //注册wifi网络状态监听
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        mNetworkConnectChangedReceiver = new NetworkConnectChangedReceiver();
        MyApplication.getContext().registerReceiver(mNetworkConnectChangedReceiver, filter);
        //打开wifi
        mWifiManager.setWifiEnabled(false);
        mWifiManager.setWifiEnabled(true);
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + targetSsid + "\"";
        String psd = "\"" + targetPsd + "\"";
        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (wifiEnc) {
            case WEP:
                // 加密类型为WEP
                if (!TextUtils.isEmpty(targetPsd)) {
                    if (isHexWepKey(targetPsd)) {
                        conf.wepKeys[0] = targetPsd;
                    } else {
                        conf.wepKeys[0] = psd;
                    }
                }
                conf.hiddenSSID = true;
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                conf.wepTxKeyIndex = 0;
                break;
            case WPA:
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            case OPEN:
                //开放网络
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        mWifiManager.addNetwork(conf);

        //移除重复ssid
        WifiConfiguration tempConfig = this.IsExsits(ssid);

        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        //未检测到任何wifi
        if (list == null || list.size() == 0)
            return;
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                mWifiManager.disconnect();
                //true表示操作成功，false操作失败
                boolean enable = mWifiManager.enableNetwork(i.networkId, true);
//                //失败后链接上次链接过的wifi
                if (!enable)
                    mWifiManager.reconnect();
                break;
            }
        }
    }

    /**
     * 判断缓存中是否已经存在同名wifi
     *
     * @param SSID wifi ssid
     * @return 重名wifi的ssid
     */
    private WifiConfiguration IsExsits(String SSID) {
        //待检测SSID出现次数
        int exsits = 0;
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals(SSID)) {
                exsits++;
                if (exsits > 1)
                    return existingConfig;
            }
        }
        return null;
    }

    private boolean isHexWepKey(String wepKey) {
        final int len = wepKey.length();

        // WEP-40, WEP-104, and some vendors using 256-bit WEP (WEP-232?)
        if (len != 10 && len != 26 && len != 58) {
            return false;
        }

        return isHex(wepKey);
    }

    private boolean isHex(String key) {
        for (int i = key.length() - 1; i >= 0; i--) {
            final char c = key.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f')) {
                return false;
            }
        }

        return true;
    }


    /**
     * wifi链接计时逻辑
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            //检测当前网络状态是否已经是wifi，如果不是的话，分别用三种加密方式尝试链接，间隔是10秒尝试一次
            if (getNetWorkInfo() != 1 && wifiEnc < 3) {
                wifiEnc++;
                if (mSSID != null)
                    connectWifi(mSSID, mPass);
                if (wifiEnc < 2)
                    mTimeCount.start();
            } else {
                //连网成功，重置默认加密方式
                wifiEnc = WPA;
                MediaUtil.play(R.raw.bind_success);
                if (wifiEnc == 2) {
                    mTimeCount = new TimeCount(10000, 1000);//重置倒计时
                }
            }


        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
//            tvCount.setText(millisUntilFinished / 1000 + "");
        }

    }


    //  判断手机的网络状态（是否联网）
    public int getNetWorkInfo() {
        //网络状态初始值
        int type = -1;  //-1(当前网络异常，没有联网)
        //通过上下文得到系统服务，参数为网络连接服务，返回网络连接的管理类
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //通过网络管理类的实例得到联网日志的状态，返回联网日志的实例
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //判断联网日志是否为空
        if (activeNetworkInfo == null) {
            //状态为空当前网络异常，没有联网
            return type;
        }
        //不为空得到使用的网络类型
        int type1 = activeNetworkInfo.getType();
        //网络类型为运营商（移动/联通/电信）
        if (type1 == ConnectivityManager.TYPE_MOBILE) {
            // 注：如果想要判断其他网络类型进入ConnectivityManager类中根据常量值判断
            type = 0;
            //网络类型为WIFI（无线网）
        } else if (type1 == ConnectivityManager.TYPE_WIFI) {

            type = 1;
        }
        //返回网络类型
        return type;
    }


    /**
     * 监听网络状态
     */
    public class NetworkConnectChangedReceiver extends BroadcastReceiver {

        private static final String TAG = "xujun";
        public static final String TAG1 = "xxx";

        @Override
        public void onReceive(Context context, Intent intent) {
            // 这个监听wifi的打开与关闭，与wifi的连接无关
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                Logs.e(TAG1, "wifiState" + wifiState);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:

                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        break;
                    //wifi已打开
                    case WifiManager.WIFI_STATE_ENABLED:
                        if (mSSID != null) {
                            connectWifi(mSSID, mPass);
                            mTimeCount.start();
                        }
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        break;
                    default:
                        break;


                }
            }
            // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
            // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
            // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager manager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                Logs.i(TAG1, "CONNECTIVITY_ACTION");

                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (activeNetwork != null) { // connected to the internet
                    if (activeNetwork.isConnected()) {
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            // connected to wifi
                            MediaUtil.play(R.raw.scan_success);//播放音效
                            Logs.e(TAG, "当前WiFi连接可用 ");
                            MyApplication.getContext().unregisterReceiver(mNetworkConnectChangedReceiver);//注销监听
                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            // connected to the mobile provider's data plan
                            Logs.e(TAG, "当前移动网络连接可用 ");
                        }
                    } else {
                        Logs.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                    }


                    Logs.e(TAG1, "info.getTypeName()" + activeNetwork.getTypeName());
                    Logs.e(TAG1, "getSubtypeName()" + activeNetwork.getSubtypeName());
                    Logs.e(TAG1, "getState()" + activeNetwork.getState());
                    Logs.e(TAG1, "getDetailedState()"
                            + activeNetwork.getDetailedState().name());
                    Logs.e(TAG1, "getDetailedState()" + activeNetwork.getExtraInfo());
                    Logs.e(TAG1, "getType()" + activeNetwork.getType());
                } else {   // not connected to the internet
                    Logs.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");

                }


            }
        }


    }

}
