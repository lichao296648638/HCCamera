package com.hushijie.hccamera.utils;

import android.util.Log;

import com.hushijie.hccamera.BuildConfig;
import com.hushijie.hccamera.Constants;

/**
 * 根据是否是debug版本屏蔽log的工具
 * Created by lichao on 2018/7/13.
 */

public class Logs {
    /**
     * information 级别日志
     *
     * @param tag 标签
     * @param log 日志信息
     */
    public static void i(String tag, String log) {
        if (BuildConfig.DEBUG)
            Log.i(tag, log);
    }

    /**
     * debug 级别日志
     *
     * @param tag 标签
     * @param log 日志信息
     */
    public static void d(String tag, String log) {
        if (BuildConfig.DEBUG)
            Log.d(tag, log);
    }

    /**
     * error 级别日志
     *
     * @param tag 标签
     * @param log 日志信息
     */
    public static void e(String tag, String log) {
        if (BuildConfig.DEBUG)
            Log.e(tag, log);
    }

    /**
     * verbose 级别日志
     *
     * @param tag 标签
     * @param log 日志信息
     */
    public static void v(String tag, String log) {
        if (BuildConfig.DEBUG)
            Log.v(tag, log);
    }

    /**
     * warning 级别日志
     *
     * @param tag 标签
     * @param log 日志信息
     */
    public static void w(String tag, String log) {
        if (BuildConfig.DEBUG)
            Log.w(tag, log);
    }

}
