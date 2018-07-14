package com.hushijie.hccamera.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hushijie.hccamera.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bt_init_wifi)
    Button btInitWifi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.bt_init_wifi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_init_wifi:
                startActivity(new Intent(this, WifiActivity.class));
                break;

        }
    }
}
