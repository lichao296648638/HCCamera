package com.hushijie.hccamera.network.exception;

/**
 *  服务器定义的错误，即code非200的所有状态
 * Created by zhangkun on 2017/3/16.
 */

public class CustomException extends RuntimeException {

    private int code;
    private  String message = "";

    public CustomException(int resultCode) {
        code = resultCode;
    }

    public CustomException(String detailMessage) {
        super(detailMessage);
    }



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message){
        this.message = message;
    }
}
