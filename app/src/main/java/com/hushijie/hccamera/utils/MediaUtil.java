package com.hushijie.hccamera.utils;

import android.media.MediaPlayer;
import android.net.Uri;

import com.hushijie.hccamera.MyApplication;

import java.io.IOException;

import static com.tencent.av.sdk.AVClientInfo.getPackageName;

/**
 * 音效工具类
 * Created by lichao on 2018/7/25.
 */

public class MediaUtil {
    private static MediaPlayer mMediaPlayer;


    /**
     * 播放mp3
     *
     * @param id 资源id
     */
    public static void play(int id) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(MyApplication.getContext(), Uri.parse(
                    "android.resource://" + getPackageName() + "/" + id));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
