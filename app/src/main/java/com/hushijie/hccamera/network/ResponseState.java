package com.hushijie.hccamera.network;

/**
 * 通用数据格式
 * Created by zhangkun on 2017/3/16.
 */

public class ResponseState<T> {


    /**
     * code : 2
     * tip :
     * data:
     */

    private int code;
    private String tip;
    private T data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }


    public T getResult() {
        return data;
    }

    public void setResult(T result) {
        this.data = result;
    }

    @Override
    public String toString() {
        return "ResponseState{" +
                "result=" + data +
                ", code=" + code +
                ", tip='" + tip + '\'' +
                '}';
    }
}
