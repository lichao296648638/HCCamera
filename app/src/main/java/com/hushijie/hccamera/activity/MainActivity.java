package com.hushijie.hccamera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hushijie.hccamera.Constants;
import com.hushijie.hccamera.MyApplication;
import com.hushijie.hccamera.R;
import com.hushijie.hccamera.network.Http;
import com.hushijie.hccamera.network.ResponseState;
import com.hushijie.hccamera.network.SimpleSubscriber;
import com.hushijie.hccamera.receiver.BluetoothInstructionReceiver;
import com.hushijie.hccamera.receiver.BootReceiver;
import com.hushijie.hccamera.receiver.PushReceiver;
import com.hushijie.hccamera.utils.MediaUtil;
import com.hushijie.hccamera.utils.SharedPreferencesUtil;
import com.hushijie.hccamera.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hushijie.hccamera.receiver.PushReceiver.EXT_KEY_INS;
import static com.hushijie.hccamera.receiver.PushReceiver.EXT_KEY_OBJ;
import static com.hushijie.hccamera.receiver.PushReceiver.EXT_KEY_REQUEST;

public class MainActivity extends AppCompatActivity {


    /**
     * 联网参数-form
     */
    private Map<String, Object> mMapParam = new HashMap<>();


    /**
     * 联网参数-json
     */
    private JSONObject mJsonParam = new JSONObject();


    private Handler handler;
    /**
     * 上报设备状态消息码
     */
    private final int MSG_POST_STATE = 0;
    @BindView(R.id.bt_init_wifi)
    Button btInitWifi;
    @BindView(R.id.bt_start_video)
    Button btStartVideo;
    @BindView(R.id.bt_create_room)
    Button btCreateRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //判断是不是开机自启动后自动联网
        Intent comingIntent = getIntent();
        if (comingIntent.getStringExtra(BootReceiver.EXT_KEY_BOOT) != null) {
            Intent intent = new Intent(this, WifiActivity.class);
            intent.putExtra(BootReceiver.EXT_KEY_BOOT, BootReceiver.EXT_VALUE_BOOT);
            startActivity(intent);
        }
        //上报设备状态
        postDeviceState();
    }

    @OnClick({R.id.bt_init_wifi, R.id.bt_start_video, R.id.bt_create_room})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_init_wifi:
                startActivity(new Intent(this, WifiActivity.class));
                break;
            case R.id.bt_start_video:
                mMapParam.clear();
                mMapParam.put("no", Constants.IMEI);
                mMapParam.put("sourceType", 301);
                Http.getInstance().callOut(new SimpleSubscriber<ResponseState>() {

                    @Override
                    public void onNext(ResponseState entity) {
                        ToastUtils.s(entity.getTip());
                    }
                }, mMapParam);
                break;
            case R.id.bt_create_room:
                Intent createRoomIntent = new Intent(this, ConversationActivity.class);
                createRoomIntent.putExtra(EXT_KEY_REQUEST, ConversationActivity.REQUEST_CODE_START_PUSH);
                startActivity(createRoomIntent);
                break;

        }
    }

    //开始定时上报设备信息
    private void postDeviceState() {
        //该任务内容
        mMapParam.clear();
        mMapParam.put("equipmentNo", Constants.IMEI);
        mMapParam.put("electricity", Constants.BATTERY);
        mMapParam.put("connectStatus", 1);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MSG_POST_STATE) {
                    Http.getInstance().postDeviceState(new SimpleSubscriber<ResponseState>() {

                        @Override
                        public void onNext(ResponseState entity) {
                            ToastUtils.s(entity.getTip());
                        }
                    }, mMapParam);
                }
            }
        };
        //该任务触发机制
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = MSG_POST_STATE;
                handler.sendMessage(msg);
            }

        };
        //每十分钟触发一次
        timer.schedule(task, 0, 10 * 60 * 1000);

    }
}
