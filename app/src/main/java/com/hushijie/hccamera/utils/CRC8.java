package com.hushijie.hccamera.utils;

/**
 * CRC8校验工具
 * Created by lichao on 2018/7/19.
 */

public class CRC8 {
    public static int FindCRC(byte[] data) {
        int CRC = 0;
        int genPoly = 0Xaa;
        for (int i = 0; i < data.length; i++) {
            CRC ^= data[i];
            for (int j = 0; j < 8; j++) {
                if ((CRC & 0x80) != 0) {
                    CRC = (CRC << 1) ^ genPoly;
                } else {
                    CRC <<= 1;
                }
            }
        }
        CRC &= 0xff;//保证CRC余码输出为1字节。
        return CRC;
    }


//    public static int FindCRC(byte[] data, int length) {
//        int crci = 0xFF;
//        int j = 0;
//        for (int i = 0; i < length; i++) {
//            crci ^= data[i] & 0xFF;
//
//            for (j = 0; j < 8; j++) {
//                if ((crci & 1) != 0) {
//                    crci >>= 1;
//                    crci ^= 0xB8;
//                } else {
//                    crci >>= 1;
//                }
//            }
//        }
//        crci &= 0xf;//保证CRC余码输出为1字节。
//        return crci;
//    }

}
