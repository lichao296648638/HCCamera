package com.hushijie.hccamera.network;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhangkun on 2017/11/22.
 */

public abstract class Subscriber<T> implements Observer<T> {

    public void onStart() {

    }

    @Override
    public void onSubscribe(Disposable d) {
        //不实现这个方法
    }
}
