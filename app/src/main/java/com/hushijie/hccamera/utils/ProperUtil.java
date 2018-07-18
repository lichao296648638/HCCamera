package com.hushijie.hccamera.utils;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取properties配置文件
 * Created by lichao on 2018/7/14.
 */

public class ProperUtil {
    private static final String TAG = "ProperUtil";
    private static Properties props;

    static {
        loadProps();
    }

    synchronized static private void loadProps() {
        Logs.i(TAG, "开始加载properties文件内容.......");
        props = new Properties();
        InputStream in = null;
        try {
            in = ProperUtil.class.getResourceAsStream("/assets/key.properties");
            props.load(in);
        } catch (FileNotFoundException e) {
            Logs.i(TAG, "key.properties文件未找到");

        } catch (IOException e) {
            Logs.i(TAG, "出现IOException");
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                Logs.i(TAG, "key.properties文件流关闭出现异常");

            }
        }
        Logs.i(TAG, "加载properties文件内容完成...........");
        Logs.i(TAG, "properties文件内容：" + props);
    }

    public static String getProperty(String key) {
        if (null == props) {
            loadProps();
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if (null == props) {
            loadProps();
        }
        return props.getProperty(key, defaultValue);
    }
}
