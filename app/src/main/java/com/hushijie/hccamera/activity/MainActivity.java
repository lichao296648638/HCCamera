package com.hushijie.hccamera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hushijie.hccamera.Constants;
import com.hushijie.hccamera.R;
import com.hushijie.hccamera.network.Http;
import com.hushijie.hccamera.network.ResponseState;
import com.hushijie.hccamera.network.SimpleSubscriber;
import com.hushijie.hccamera.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    /**
     * 联网参数
     */
    private Map<String, Object> mMapParam = new HashMap<>();

    @BindView(R.id.bt_init_wifi)
    Button btInitWifi;
    @BindView(R.id.bt_start_video)
    Button btStartVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }

    @OnClick({R.id.bt_init_wifi, R.id.bt_start_video})
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

        }
    }

}
