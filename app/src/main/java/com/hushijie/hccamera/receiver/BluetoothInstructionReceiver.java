package com.hushijie.hccamera.receiver;

import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.hushijie.hccamera.Constants;
import com.hushijie.hccamera.entity.BleAdvertiseEntity;
import com.hushijie.hccamera.entity.BleInfoEntity;
import com.hushijie.hccamera.entity.InstructionEntity;
import com.hushijie.hccamera.entity.PostBleDataEntity;
import com.hushijie.hccamera.network.Http;
import com.hushijie.hccamera.network.ResponseState;
import com.hushijie.hccamera.network.SimpleSubscriber;
import com.hushijie.hccamera.utils.BleProfile;
import com.hushijie.hccamera.utils.BleUtil;
import com.hushijie.hccamera.utils.Logs;
import com.hushijie.hccamera.utils.SharedPreferencesUtil;
import com.hushijie.hccamera.utils.ToastUtils;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.UUIDUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;


/**
 * 蓝牙指令监听
 * Created by lichao on 2018/7/13.
 */

public class BluetoothInstructionReceiver extends BroadcastReceiver {

    private static final String TAG = "BluetoothInstructionRec";
    private static final int MSG_POST_STATE = 0;


    /**
     * 寻找周围蓝牙设备指令
     */
    public static final String FIND = "api_find_device";

    /**
     * 配对蓝牙设备指令
     */
    public static final String CONNECT = "api_connect_device";

    /**
     * 重新连接关机前蓝牙
     */
    public static final String RECONNECT = "api_reconnect_device";

    /**
     * 自定义蓝牙指令action
     */
    public static String ACTION_INS_BLE = "com.hushijie.hccamera.INSTRUCTION_BLUETOOTH";

    /**
     * SharedPreferences 蓝牙名称 key
     */
    public static final String SP_KEY_BLE_NAME = "bleName";

    /**
     * SharedPreferences 蓝牙mac key
     */
    public static final String SP_KEY_BLE_MAC = "bleMac";


    /**
     * 列表用数据
     */
    private List<BleInfoEntity> mDatas = new ArrayList<>();


    /**
     * 蓝牙设备信息，键值对为mac地址和名称
     */
    private HashMap<String, String> mDevices = new HashMap<>();

    /**
     * 蓝牙开关监听
     */
    private BluetoothStateListener mBluetoothStateListener;

    /**
     * 扫描回调结果
     */
    private SearchResponse mSearchResponse;


    /**
     * 扫描策略
     */
    private SearchRequest mSearchRequest;

    /**
     * 蓝牙搜索计时器
     */
    private TimeCount mSearchCount;

    /**
     * 查找蓝牙设备指令集
     */
    private InstructionEntity mInstructionEntity;

    /**
     * 蓝牙管理器
     */
    private BluetoothClient mClient;

    /**
     * 联网参数-form
     */
    private Map<String, Object> mMapParam = new HashMap<>();


    /**
     * 上报的蓝牙数据-form
     */
    private Map<String, Object> mBleParam = new HashMap<>();
    /**
     * 联网参数-json
     */
    private JSONObject mJsonParam = new JSONObject();

    /**
     * 手环采集过来的体征数据组
     */
    JSONArray healthDatas = new JSONArray();

    /**
     * 手环采集过来的体征数据项
     */
    JSONObject healthData = new JSONObject();

    /**
     * 是否已经初始化
     */
    private boolean isInit = false;
    private Handler handler;
    /**
     * 采集蓝牙数据用定时器
     */
    private Timer timer;
    /**
     * 发送蓝牙数据任务
     */
    private TimerTask task;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (!isInit) {
            init(context);
            isInit = true;
        }
        String instruction = intent.getStringExtra(PushReceiver.EXT_KEY_INS);
        mInstructionEntity = (InstructionEntity) intent.getSerializableExtra(PushReceiver.EXT_KEY_OBJ);
        Logs.i(TAG, instruction);
        switch (instruction) {
            //寻找指令
            case FIND:
                scanBluetooth();
                break;
            //链接指令
            case CONNECT:
                connectDevice(context, false);
                break;
//            //重新链接指令
            case RECONNECT:
                connectDevice(context, true);
                break;

//            //寻找指令
//            case FIND:
//                mClient.connect("A4:C1:38:90:6A:BB", new BleConnectResponse() {
//                    @Override
//                    public void onResponse(int code, BleGattProfile profile) {
//                        if (code == REQUEST_SUCCESS) {
//                            ToastUtils.s("配对成功！");
//                            //开启Notify准备接收手环信息
//                            openNotify("A4:C1:38:90:6A:BB",
//                                    UUID.fromString(BleProfile.UUID_SERVER),
//                                    UUID.fromString(BleProfile.UUID_RX));
//
//                        } else {
//                            //装载json返回给小程序
//                            ToastUtils.s("配对失败！");
//
//                        }
//                    }
//                });
//                break;

        }
    }


    /**
     * 初始化
     */
    private void init(Context context) {
        //心跳上传数据
        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_POST_STATE) {
                    Http.getInstance().postBleData(new SimpleSubscriber<PostBleDataEntity>() {

                        @Override
                        public void onNext(PostBleDataEntity entity) {
                            ToastUtils.s("蓝牙上传后key为:" + entity.getCurrentKey());
                        }
                    }, mBleParam);
                }
            }
        };
        mClient = new BluetoothClient(context);
        //设置总时长
        mSearchCount = new TimeCount(3000 * 3 + 5000 + 2000, 100);
        //定义扫描策略
        mSearchRequest = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();

        //蓝牙开启后的回调
        mBluetoothStateListener = new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                //true 打开  false 关闭
                //扫描
                if (openOrClosed)
                    mClient.search(mSearchRequest, mSearchResponse);
            }

        };

        //扫描回调
        mSearchResponse = new SearchResponse() {

            @Override
            public void onSearchStarted() {
                //开始计时
                mSearchCount.start();
                //清空历史蓝牙列表
                mDatas.clear();
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                Beacon beacon = new Beacon(device.scanRecord);
                BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                //使用蓝牙工具类防止NULL名字的出现
                final BleAdvertiseEntity badata = BleUtil.parseAdertisedData(device.scanRecord);
                String deviceName = device.getName();
                if (deviceName.equals("NULL") || deviceName.equals("null") || deviceName == null) {
                    deviceName = badata.getName();
                }
                //装填蓝牙列表
                mDevices.put(device.getAddress(), deviceName);
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {
                //注销蓝牙开关监听
                mClient.unregisterBluetoothStateListener(mBluetoothStateListener);
                //键列
                Set<String> keys = mDevices.keySet();
                //去除重复设备后装填设备数据
                mDatas.clear();
                for (String key : keys) {
                    BleInfoEntity bleInfoEntity = new BleInfoEntity();
                    bleInfoEntity.setBleAddress(key);
                    if (TextUtils.isEmpty(mDevices.get(key))) {
                        bleInfoEntity.setBleName(key);
                    } else {
                        bleInfoEntity.setBleName(mDevices.get(key));
                    }
                    mDatas.add(bleInfoEntity);
                }

                //装载json返回给小程序
                if (mDatas.size() > 0) {
                    JSONObject backJson = Http.entity2String(mDatas, 1, "查找成功");
                    try {
                        backJson.put("idenKey", mInstructionEntity.getIdenKey());
                        backJson.put("idenAccountId", mInstructionEntity.getIdenAccountId());
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
                    }, backJson);
                } else {
                    JSONObject backJson = new JSONObject();
                    try {
                        backJson.put("idenKey", mInstructionEntity.getIdenKey());
                        backJson.put("idenAccountId", mInstructionEntity.getIdenAccountId());
                        backJson.put("code", 0);
                        backJson.put("tip", "周围无蓝牙设备");
                        backJson.put("data", "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Http.getInstance().postInstruction(new SimpleSubscriber<ResponseState>() {

                        @Override
                        public void onNext(ResponseState entity) {
                            ToastUtils.s(entity.getTip());
                        }

                    }, backJson);
                }

            }
        };
        //注册蓝牙开关监听
        mClient.registerBluetoothStateListener(mBluetoothStateListener);
    }


    /**
     * 打开Notify
     */
    private void openNotify(final String mac, final UUID serviceUUID, final UUID characterUUID) {
        mClient.notify(mac, serviceUUID, characterUUID, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                ToastUtils.s("手环传来信息");
                Logs.i(TAG, "蓝牙数据\n" + value.toString());
            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    ToastUtils.s("开启Notify成功");
                    mClient.write(mac,
                            UUID.fromString(BleProfile.UUID_SERVER),
                            UUID.fromString(BleProfile.UUID_TX),
                            new byte[]{0x55, 0x00, 0x2a, 0x04, 0x01, 0x0, 0x0},
//                            BleProfile.searchBle(),
                            new BleWriteResponse() {
                                @Override
                                public void onResponse(int code) {
                                    if (code == REQUEST_SUCCESS) {

                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * 扫描蓝牙设备
     */
    private void scanBluetooth() {
        //打开蓝牙
        if (!mClient.isBluetoothOpened()) {
            mClient.openBluetooth();
        } else {
            //开始扫描
            mClient.search(mSearchRequest, mSearchResponse);
        }
    }

    /**
     * 蓝牙搜索计时逻辑
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            //超时后停止搜索
            mClient.stopSearch();
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
        }

    }


    /**
     * 配对设备
     */
    private void connectDevice(final Context context, final boolean isReconnect) {
        if (mClient == null)
            mClient = new BluetoothClient(context);
        mClient.openBluetooth();
        String address = "";
        //判断是在线配对还是本地缓存配对
        if (isReconnect) {
            address = SharedPreferencesUtil.getInstance(context).getSP(SP_KEY_BLE_MAC);
            mInstructionEntity = new InstructionEntity();
            mInstructionEntity.setBleAddress(address);
            if (TextUtils.isEmpty(address)) {
                ToastUtils.s("mac地址为空，重连失败！");
                return;
            }
        } else {
            address = mInstructionEntity.getBleAddress();
            if (TextUtils.isEmpty(address)) {
                ToastUtils.s("mac地址为空，无法配对！");
                return;
            }

            //判断是否需要重新绑定
            if (mInstructionEntity.isReplace()) {
                //先跟上个设备解绑
                if (TextUtils.isEmpty(mInstructionEntity.getLastBleAddress())) {
                    ToastUtils.s("mac地址为空，无法配对！");
                    return;
                }
                mClient.disconnect(mInstructionEntity.getLastBleAddress());
                //轮询10秒断开结果
                long startTime = System.currentTimeMillis();
                long currentTime = System.currentTimeMillis();
                int bleStatus = -1;
                while ((currentTime - startTime) / 1000 < 10) {
                    bleStatus = mClient.getConnectStatus(mInstructionEntity.getLastBleAddress());
                    Logs.i("lichao", "上一蓝牙设备连接状态：" + bleStatus);
                    if (bleStatus == com.inuker.bluetooth.library.Constants.STATUS_DEVICE_DISCONNECTED) {
                        break;
                    }
                    currentTime = System.currentTimeMillis();
                }
            }
        }


        mClient.connect(mInstructionEntity.getBleAddress(), new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                if (code == REQUEST_SUCCESS) {
                    ToastUtils.s("配对成功！");
                    //开启Notify准备接收手环信息
                    openNotify(mInstructionEntity.getBleAddress(),
                            UUID.fromString(BleProfile.UUID_SERVER),
                            UUID.fromString(BleProfile.UUID_TX));
                    //重连的设备不需要重新上报状态
                    if (isReconnect)
                        return;
                    //上报蓝牙数据
                    postBleData();
                    //保存手环ID
                    Constants.BLE_ID = mInstructionEntity.getBleAddress();
                    //绑定设备
                    bindDevice();
                    //装载json返回给小程序
                    JSONObject backJson = Http.entity2String("", 1, "配对成功");
                    try {
                        backJson.put("idenKey", mInstructionEntity.getIdenKey());
                        backJson.put("idenAccountId", mInstructionEntity.getIdenAccountId());
                        backJson.put("accountId", mInstructionEntity.getAccountId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Http.getInstance().postInstruction(new SimpleSubscriber<ResponseState>() {

                        @Override
                        public void onNext(ResponseState entity) {
                            ToastUtils.s(entity.getTip());
                        }

                    }, backJson);

//                    mClient.write(mInstructionEntity.getBleAddress(), UUIDUtils.makeUUID(BleProfile.UUID_SERVER),
//                            UUIDUtils.makeUUID(BleProfile.UUID_TX), BleProfile.searchBle(), new BleWriteResponse() {
//                                @Override
//                                public void onResponse(int code) {
//                                    if (code == REQUEST_SUCCESS) {
//
//                                    }
//                                }
//                            });
                    //缓存起来
                    SharedPreferencesUtil.getInstance(context).putSP(SP_KEY_BLE_NAME, mInstructionEntity.getBleName());
                    SharedPreferencesUtil.getInstance(context).putSP(SP_KEY_BLE_MAC, mInstructionEntity.getBleAddress());
                } else {
                    //装载json返回给小程序
                    ToastUtils.s("配对失败！");
                    //重连的设备不需要重新上报状态
                    if (isReconnect)
                        return;
                    JSONObject backJson = Http.entity2String("", 0, "配对失败");
                    try {
                        backJson.put("idenKey", mInstructionEntity.getIdenKey());
                        backJson.put("idenAccountId", mInstructionEntity.getIdenAccountId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Http.getInstance().postInstruction(new SimpleSubscriber<ResponseState>() {

                        @Override
                        public void onNext(ResponseState entity) {
                            ToastUtils.s(entity.getTip());
                        }

                    }, backJson);
                }
            }
        });

    }

    /**
     * 绑定设备
     */
    private void bindDevice() {
        mMapParam.clear();
        mMapParam.put("electricity", "0");
        mMapParam.put("equipmentNo", mInstructionEntity.getBleAddress());
        mMapParam.put("equipmentType", 2);
        mMapParam.put("accountId", mInstructionEntity.getAccountId());
        mMapParam.put("idenAccountId", mInstructionEntity.getIdenAccountId());
        mMapParam.put("organId", mInstructionEntity.getOrganId());
        mMapParam.put("isReplace", mInstructionEntity.isReplace());


        Http.getInstance().bindDevice(new SimpleSubscriber<ResponseState>() {

            @Override
            public void onNext(ResponseState entity) {
                ToastUtils.s(entity.getTip());
                //绑定成功后，通过server通道通知小程序
                postInstruction(entity.getCode());
            }

        }, mMapParam);
    }

    /**
     * 发送绑定结果指令
     */
    private void postInstruction(int code) {
        mJsonParam = new JSONObject();
        try {
            mJsonParam.put("idenKey", mInstructionEntity.getIdenKey());
            mJsonParam.put("idenAccountId", mInstructionEntity.getIdenAccountId());
            mJsonParam.put("code", code);
            switch (code) {
                case Constants.CODE_SUCCESS:
                case Constants.CODE_USER_BINDED:
                    mJsonParam.put("tip", "绑定手环成功");
                    break;
                case Constants.CODE_FAIL:
                    mJsonParam.put("tip", "手环设备绑定失败");
                    break;
            }
            mJsonParam.put("data", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Http.getInstance().postInstruction(new SimpleSubscriber<ResponseState>() {

            @Override
            public void onNext(ResponseState entity) {
                ToastUtils.s(entity.getTip());
            }
        }, mJsonParam);
    }

    //定时上报体征数据
    private void postBleData() {
        //判断当前手环是否在线
        if (TextUtils.isEmpty(Constants.BLE_ID))
            return;
        int status = mClient.getConnectStatus(Constants.BLE_ID);
        if (status != com.inuker.bluetooth.library.Constants.STATUS_DEVICE_CONNECTED)
            return;
        //装载手环体征数据
        long firstTime = System.currentTimeMillis();
        mBleParam.clear();
        mBleParam.put("braceletNo", Constants.BLE_ID);
        mBleParam.put("RobotNo", Constants.IMEI);
        mBleParam.put("firstCollectTime", firstTime + "");
        mBleParam.put("lastCollectTime", firstTime + "");
        mBleParam.put("currentKey", firstTime + 1 + "");
        try {
            healthData.put("systolicPressure", 23);
            healthData.put("diastolicPressure", 23);
            healthData.put("heartRate", "34");
            healthData.put("bloodOxygen", 234);
            healthData.put("collectTime", firstTime + 2 + "");

            healthDatas.put(healthData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mBleParam.put("healthCollects", healthDatas.toString());


        //心跳5s一次
        timer = new Timer(true);
        task = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = MSG_POST_STATE;
                handler.sendMessage(msg);
            }

        };
        timer.schedule(task, 0, 5 * 1000);

    }


}
