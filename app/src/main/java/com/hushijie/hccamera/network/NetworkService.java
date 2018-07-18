package com.hushijie.hccamera.network;


import com.hushijie.hccamera.entity.JoinRoomEntity;
import com.hushijie.hccamera.entity.TencentSigEntity;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * get接口
 * Created by zhangkun on 2017/3/31.
 */

public interface NetworkService {

//    @GET("app/productOrder/orderView.jhtml")
//    Observable<ResponseState<HealthOrderInfoEntity>> getProductOrderView(@Query("file") String var1);


    /**
     * 表单提交要加 @FormUrlEncoded
     * 绑定设备
     *
     * @param params 参数列表
     */
    @FormUrlEncoded
    @POST("hardware/receive/equipment/user/bind")
    Observable<ResponseState> bindDevice(@FieldMap Map<String, Object> params);

    /**
     * 提交指令
     *
     * @param params 参数列表,为一个json
     */
    @POST("hardware/receive/equipment/instruct/back")
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Observable<ResponseState> postInstruction(@Body RequestBody params);


    /**
     * 获取腾讯云签名
     *
     * @param param 设备编号
     */
    @FormUrlEncoded
    @POST("webrtc/hardware/get_login_info")
    Observable<TencentSigEntity> getTencentSig(@Field("no") String param);


    /**
     * 加入房间
     *
     * @param param 设备编号
     */
    @FormUrlEncoded
    @POST("webrtc/hardware/enter")
    Observable<JoinRoomEntity> joinRoom(@Field("no") String param);


    /**
     * 发起服务通知
     *
     * @param params 参数列表
     */
    @FormUrlEncoded
    @POST("wx/program/robot/nursing/request")
    Observable<ResponseState> callOut(@FieldMap Map<String, Object> params);


    /**
     * 开始推流
     *
     * @param param 设备编号
     */
    @FormUrlEncoded
    @POST("webrtc/hardware/record_service/start")
    Observable<ResponseState> startPush(@Field("no") String param);

    /**
     * 结束推流
     *
     * @param param 设备编号
     */
    @FormUrlEncoded
    @POST("webrtc/hardware/record_service/end")
    Observable<ResponseState> endPush(@Field("no") String param);



}
