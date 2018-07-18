package com.hushijie.hccamera.network.exception;

import android.content.Intent;
import android.net.ParseException;

import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;


/**
 * 处理异常引擎
 * Created by zhangkun on 2017/7/27.
 */

public class ExceptionEngine {
    //对应HTTP的状态码
    private static final int UNAUTHORIZED = 401;
    private static final int FAIL = 0;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    private static final int NOT_ANYMORE = 500;

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {             //HTTP错误
            HttpException httpException = (HttpException) e;
            ex = new ApiException(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex.message = "网络错误";  //均视为网络错误
                    break;
            }
            return ex;



        } else if (e instanceof CustomException) {
            //服务器返回的错误
            CustomException resultException = (CustomException) e;
            //300代表登录失效
            if (resultException.getCode() == 300) {
//                UserApplication.user.setLogout();
//                Intent intent = new Intent(UserApplication.getInstance(), LoginActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                UserApplication.getInstance().startActivity(intent);
//                resultException.setMessage("登录失效,请重新登录");
            }

             if (resultException.getCode() == 600) {
//                 Intent intent = new Intent(UserApplication.getInstance(), PayPasswordActivity.class);
//                 intent.putExtra("type", TYPE_NO_PASS);
//                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                 UserApplication.getInstance().startActivity(intent);
//                 resultException.setMessage("请设置支付密码");
             }

            //666代表没得实名
            if (resultException.getCode() == 666) {
//                Intent intent = new Intent(UserApplication.getInstance(), AuthenticateNameActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                UserApplication.getInstance().startActivity(intent);
            }

            ex = new ApiException(resultException, resultException.getCode());
            ex.message = resultException.getMessage();
            return ex;

        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException
                || e instanceof MalformedJsonException) {
            ex = new ApiException(e, ERROR.PARSE_ERROR);
            ex.message = "数据解析错误";            //均视为解析错误
            return ex;
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
            ex = new ApiException(e, ERROR.NETWORK_ERROR);
            ex.message = "网络连接失败";  //均视为网络错误
            return ex;
        } else {
            ex = new ApiException(e, ERROR.UNKNOWN);
            StringBuilder builder = new StringBuilder();
            builder.append(e.getMessage());
            ex.message = builder.toString();          //未知错误
            return ex;
        }
    }

    public static class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = 1001;
        /**
         * 网络错误
         */
        public static final int NETWORK_ERROR = 1002;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = 1003;

        /**
         * 服务器爆炸
         */
        public static final int SERVER_ERROR = 1004;
    }
}
