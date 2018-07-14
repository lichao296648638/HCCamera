package com.hushijie.hccamera.network;

import android.content.Context;



/**
 * 简单的订阅者，封装了加载状态和处理异常
 * Created by zhangkun on 2017/7/27.
 */

public abstract class SimpleSubscriber<T> extends Subscriber<T> {
    private Context mContext;

    public SimpleSubscriber(Context context) {
        mContext = context;

    }

    public SimpleSubscriber() {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onComplete() {
    }

}