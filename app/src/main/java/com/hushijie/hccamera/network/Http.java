package com.hushijie.hccamera.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.hushijie.hccamera.MyApplication;
import com.hushijie.hccamera.entity.JoinRoomEntity;
import com.hushijie.hccamera.entity.TencentSigEntity;
import com.hushijie.hccamera.network.exception.CustomException;
import com.hushijie.hccamera.network.exception.ExceptionEngine;
import com.hushijie.hccamera.utils.Logs;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.progressmanager.ProgressManager;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.hushijie.hccamera.Constants.SERVER_URL;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

/**
 * 网络请求封装
 * Created by zhangkun on 2017/5/25.
 */

public class Http {
    private static Http instance;

    private static Retrofit retrofit;

    /**
     * 全局gson
     */
    public static Gson gson;

    public Http() {
        retrofit = new Retrofit.Builder().client(getClient())
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        gson = new Gson();

    }

    public static Http getInstance() {
        if (instance == null) {
            instance = new Http();
        }
        return instance;
    }


    /**
     * 生成一个ok http客户端
     * 注入进度管理
     *
     * @return ok http client
     */
    private static OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
//                                .addHeader("host", "btso.pw")
//                                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
//                                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                                .build();
                        return chain.proceed(request);
                    }

                });
        return ProgressManager.getInstance().with(builder).build();
    }

    /**
     * 网络请求的第一层，传入rxjava的map里
     *
     * @param <T>
     */
    private class HttpResultFunc<T> implements Function<ResponseState<T>, T> {

        @Override
        public T apply(ResponseState<T> httpResult) {
            Logs.i("network", "call: httpResult:" + httpResult.toString());

            if (httpResult.getCode() != 1) {
                CustomException exception = new CustomException(httpResult.getTip());
                exception.setCode(httpResult.getCode());
                throw exception;
            }
            return httpResult.getResult();
        }
    }

    private class ErrorFunc<T> implements Function<Throwable, Observable<T>> {

        @Override
        public Observable<T> apply(Throwable throwable) {
            return Observable.error(ExceptionEngine.handleException(throwable));
        }
    }

    /**
     * map转为json字符串，服务器接收这样的参数
     *
     * @param param 存有请求参数的map
     * @return
     */
    public static String map2json(Map<String, Object> param) {
        Iterator<Map.Entry<String, Object>> iterator = param.entrySet().iterator();
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            String key = next.getKey();
            Object val = next.getValue();

            builder.append("\"" + key + "\"");
            builder.append(":\"" + val + "\",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append("}");
        Logs.i("test", "map2json: str:" + builder.toString());
        return builder.toString();
    }

    /**
     * map转为url参数
     *
     * @param map 存储参数的map
     * @return
     */
    public static String map2UrlParam(Map<String, Object> map) {
        StringBuffer sb = new StringBuffer();
        if (map.size() > 0) {
            for (String key : map.keySet()) {
                sb.append(key + "=");
                if (TextUtils.isEmpty(map.get(key).toString())) {
                    sb.append("&");
                } else {
                    String value = map.get(key).toString();
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sb.append(value + "&");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 实体转json，一般用于向小程序返回数据
     *
     * @param entity 实体
     * @param code   状态码
     * @param tip    提示
     * @param <T>    不确定具体实体类型故采用泛型函数
     * @return
     */
    public static <T> JSONObject entity2String(T entity, int code, String tip) {
        //数据体是对象还是数组
        boolean isArray = false;
        JSONObject jsonObject = new JSONObject();
        try {
            //实体为空
            if (entity.toString().equals("")) {
                jsonObject.put("code", code);
                jsonObject.put("tip", tip);
                jsonObject.put("data", "");
            } else {
                String data = gson.toJson(entity);
                //判断数据类型
                if (data.charAt(0) == '[') {
                    isArray = true;
                } else {
                    isArray = false;
                }
                //转换

                if (isArray) {
                    JSONArray dataJson = new JSONArray(data);
                    jsonObject.put("data", dataJson);

                } else {
                    JSONObject dataJson = new JSONObject(data);
                    jsonObject.put("data", dataJson);

                }
                jsonObject.put("code", code);
                jsonObject.put("tip", tip);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    /**
     * 1.绑定设备
     *
     * @param subscriber 订阅
     * @param param      参数列表
     */
    public void bindDevice(Subscriber<ResponseState> subscriber, Map<String, Object> param) {
        Observable<ResponseState> observable = retrofit.create(NetworkService.class)
                .bindDevice(param).onErrorResumeNext(new ErrorFunc<ResponseState>());
        observable.observeOn(mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }

    /**
     * 2.提交指令
     *
     * @param subscriber 订阅
     * @param param      参数列表
     */
    public void postInstruction(Subscriber<ResponseState> subscriber, JSONObject param) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), param.toString());
        Observable<ResponseState> observable = retrofit.create(NetworkService.class)
                .postInstruction(body).onErrorResumeNext(new ErrorFunc<ResponseState>());
        observable.observeOn(mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }

    /**
     * 3.获取腾讯云签名
     *
     * @param subscriber 订阅
     * @param param      设备编号
     */
    public void getTencentSig(Subscriber<TencentSigEntity> subscriber, String param) {
        Observable<TencentSigEntity> observable = retrofit.create(NetworkService.class)
                .getTencentSig(param).onErrorResumeNext(new ErrorFunc<TencentSigEntity>());
        observable.observeOn(mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }

    /**
     * 4.加入腾讯云房间
     *
     * @param subscriber 订阅
     * @param param      设备编号
     */
    public void joinRoom(Subscriber<JoinRoomEntity> subscriber, String param) {
        Observable<JoinRoomEntity> observable = retrofit.create(NetworkService.class)
                .joinRoom(param).onErrorResumeNext(new ErrorFunc<JoinRoomEntity>());
        observable.observeOn(mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(subscriber);
    }
}
