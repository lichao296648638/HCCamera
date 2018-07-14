package com.hushijie.hccamera.network.converter;

import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 *
 * Created by zhangkun on 2017/2/21.
 */

public class StringResponseBodyConverter implements Converter<ResponseBody, String> {
    private static final String TAG = "StringResponseBodyConve";
    @Override
    public String convert(ResponseBody value) throws IOException {
        try {
            Log.i(TAG, "convert: "+value.string());
            return value.string();
        } finally {
            value.close();
        }
    }
}
