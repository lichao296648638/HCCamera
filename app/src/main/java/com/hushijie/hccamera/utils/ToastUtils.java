package com.hushijie.hccamera.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

/**
 * 快速吐司
 * Created by zhangkun on 2017/5/25.
 */

public class ToastUtils {
    private static ToastUtils instance;

    private static Context mContext;

    public static void init(Context context){
        mContext =context;
        if (instance==null)
            instance = new ToastUtils();
    }

    public static void s(Context context,String s){
        Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
    }

    public static void s(String s){

        Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
    }

    public static void s(@StringRes int res){
        Toast.makeText(mContext,res,Toast.LENGTH_SHORT).show();
    }

    public static void l(String s){
        Toast.makeText(mContext,s,Toast.LENGTH_LONG).show();
    }

    public static void l(@StringRes int res){
        Toast.makeText(mContext,res,Toast.LENGTH_LONG).show();
    }
}
