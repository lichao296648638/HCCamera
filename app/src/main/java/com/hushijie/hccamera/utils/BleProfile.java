package com.hushijie.hccamera.utils;

import com.hushijie.hccamera.utils.CRC8;
import com.hushijie.hccamera.utils.Logs;

/**
 * 蓝牙协议
 * Created by lichao on 2018/7/19.
 */

public class BleProfile {
    private static final String TAG = "BleProfile";

    //蓝牙Service的UUID
    public static final int UUID_SERVER = 0xC3E6FEA0;

    //手机→ble设备的UUID
    public static final int UUID_TX = 0xC3E6FEA1;

    //ble设备→手机的UUID
    public static final int UUID_RX = 0xC3E6FEA2;

    /**
     * 包头
     * 蓝牙协议识别标志
     * 0x55 8bit
     */
    private static final int L1_TAG = 0x55;

    /**
     * 包头
     * 没有下个数据包
     * 0 1bit
     */
    private static final int L1_NO_NEXT = 0;

    /**
     * 包头
     * 有下个数据包
     * 1 1bit
     */
    private static final int L1_HAVE_NEXT = 1;


    /**
     * 包头
     * 预留
     * 0 7bit
     */
    private static final int L1_EXTRA = 0;

    /**
     * 包头
     * CRC校验多项式结果
     * 校验L2后得出 8bit
     */
    private static int L1_CRC;


    /**
     * 数据
     * 查找设备命令
     * 0x05 8bit
     */
    private static final int L2_COMMAND_SEARCH = 0x05;

    /**
     * 数据
     * 查找蓝牙设备key
     * 0x01 8bit
     */
    private static final int L2_KEY_SEARCH_BLE = 0x01;

    /**
     * 数据
     * 默认数据包长度
     * 0x0 16bit
     */
    private static final int L2_LENGTH_DEFAULT = 0;

    /**
     * 数据
     * 数据包
     * 0-Nbit
     */
    private static byte[] DATA;

//    /**
//     * app→蓝牙设备
//     * 查找蓝牙设备指令数据包
//     */
//    public static byte[] searchBle() {
//        //确认L2数据包数据
//        String L2_STR = L2_COMMAND_SEARCH + L2_KEY_SEARCH_BLE + L2_LENGTH_DEFAULT;
//        byte[] command = int2Bytes(Integer.parseInt(L2_COMMAND_SEARCH, 2), 1);
//        byte[] key = int2Bytes(Integer.parseInt(L2_KEY_SEARCH_BLE, 2));
//        byte[] length = int2Bytes(Integer.parseInt(L2_LENGTH_DEFAULT, 2));
//        //确认L1包头中CRC的校验值
//        int L2_INT = Integer.parseInt(L2_STR, 2);
//        L1_CRC = get8BitBinString(CRC8.FindCRC(int2Bytes(L2_INT)));
//        //确认L1包头的数据
//        byte[] tag = int2Bytes(Integer.parseInt(L1_TAG, 2));
//        byte[] noNext = int2Bytes(Integer.parseInt(L1_NO_NEXT, 2));
//        byte[] extra = int2Bytes(Integer.parseInt(L1_EXTRA, 2));
//        byte[] crc = int2Bytes(Integer.parseInt(L1_CRC, 2));
//        //确认该数据包中的数据
//        DATA = new byte[command.length + key.length + length.length + tag.length + noNext.length + extra.length + crc
//                .length];
//        for (int i = 0; i < DATA.length; i++) {
//            //装载标志
//            if (i < tag.length) {
//                DATA[i] = tag[i];
//            }
//            //装载是否有下一个数据包位
//            if (i >= tag.length
//                    &&
//                    i < tag.length + noNext.length) {
//                DATA[i] = noNext[i];
//            }
//            //装载预留位
//            if (i >= tag.length + noNext.length
//                    &&
//                    i < tag.length + noNext.length + extra.length) {
//                DATA[i] = extra[i];
//            }
//            //装载CRC位
//            if (i >= tag.length + noNext.length + extra.length
//                    &&
//                    i < tag.length + noNext.length + extra.length + crc.length) {
//                DATA[i] = crc[i];
//            }
//            //装载命令位
//            if (i >= tag.length + noNext.length + extra.length + crc.length
//                    &&
//                    i < tag.length + noNext.length + extra.length + crc.length + command.length) {
//                DATA[i] = command[i];
//            }
//            //装载key位
//            if (i >= tag.length + noNext.length + extra.length + crc.length + command.length
//                    &&
//                    i < tag.length + noNext.length + extra.length + crc.length + command.length + key.length) {
//                DATA[i] = key[i];
//            }
//            //装载数据长度位
//            if (i >= tag.length + noNext.length + extra.length + crc.length + command.length + key.length
//                    &&
//                    i < tag.length + noNext.length + extra.length + crc.length + command.length + key.length + length.length) {
//                DATA[i] = length[i];
//            }
//        }
//        return DATA;
//    }


    /**
     * app→蓝牙设备
     * 查找蓝牙设备指令数据包
     */
    public static byte[] searchBle() {
        DATA = new byte[7];
        //确认L2数据包数据
        int L2 = (L2_COMMAND_SEARCH << 24) + (L2_KEY_SEARCH_BLE << 16) + L2_LENGTH_DEFAULT;
        //确认L1包头中CRC的校验）
        int CRC = CRC8.FindCRC(int2Bytes(L2));
        //确认整个数据包
        DATA[0] = L1_TAG;
        DATA[1] = (L1_NO_NEXT << 7) + L1_EXTRA;
        DATA[2] = (byte) CRC;
        DATA[3] = L2_COMMAND_SEARCH;
        DATA[4] = L2_KEY_SEARCH_BLE;
        DATA[5] = 0;
        DATA[6] = 0;
        return DATA;
    }

    /**
     * int 转 byte[]
     *
     * @param n 被转值
     * @return
     */

    private static byte[] int2Bytes(int n) {
        byte[] b = new byte[4];

        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));

        }
        return b;
    }


    /**
     * 将整型数字转换为二进制字符串，舍弃前面的0
     *
     * @param number 整型数字
     * @return 二进制字符串
     */
    private static String getSimpleBinString(int number) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sBuilder.append(number & 1);
            number = number >>> 1;
        }
        int index = sBuilder.reverse().indexOf("1");
        return sBuilder.substring(index);
    }


    /**
     * 将整型数字转换为二进制字符串，一共8位，不舍弃前面的0
     *
     * @param number 整型数字
     * @return 二进制字符串
     */
    private static String get8BitBinString(int number) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sBuilder.append(number & 1);
            number = number >>> 1;
        }
        return sBuilder.reverse().toString();
    }
}
