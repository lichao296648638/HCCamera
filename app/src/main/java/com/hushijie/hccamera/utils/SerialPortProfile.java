package com.hushijie.hccamera.utils;

/**
 * 串口协议
 * Created by lichao on 2018/7/19.
 */

public class SerialPortProfile {

    private static final String TAG = "SerialPortProfile";

    /**
     * 包头
     */
    private static final int PRF_HEAD = 0x68;

    /**
     * 包尾
     */
    private static final int PRF_END = 0x16;

    /**
     * 长度 命令+命令参数+数据块（没有就不填）默认长度为0x02
     */
    private static final int PRF_LEN = 0x02;

    /**
     * 命令
     * 转动命令
     */
    private static final byte PRF_COMMAND_TURN = -128;

    /**
     * 参数
     * 左转参数
     */
    private static final int PRF_PARAM_LEFT = 0x01;

    /**
     * 参数
     * 右转参数
     */
    private static final int PRF_PARAM_RIGHT = 0x02;

    /**
     * 参数
     * 上转参数
     */
    private static final int PRF_PARAM_UP = 0x03;

    /**
     * 参数
     * 下转参数
     */
    private static final int PRF_PARAM_DOWN = 0x04;


    /**
     * 电机转动
     *
     * @param direction 旋转方向
     */
    public static byte[] turn(String direction) {
        byte[] data = new byte[7];
        data[0] = PRF_HEAD;
        data[1] = PRF_LEN;
        data[2] = PRF_COMMAND_TURN;
        data[4] = 0x11;
        data[5] = 0x22;
        data[6] = PRF_END;
        switch (direction) {
            case "left":
                data[3] = PRF_PARAM_LEFT;
                break;
            case "right":
                data[3] = PRF_PARAM_RIGHT;
                break;
            case "top":
                data[3] = PRF_PARAM_UP;
                break;
            case "bottom":
                data[3] = PRF_PARAM_DOWN;
                break;
        }
        return data;
    }


}
