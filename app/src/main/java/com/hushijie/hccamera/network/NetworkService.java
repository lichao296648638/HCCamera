package com.hushijie.hccamera.network;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
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
    @POST("hardware/receive/equipment/user/bind")
    @FormUrlEncoded
    Observable<ResponseState> bindDevice(@FieldMap Map<String, Object> params);

    /**
     * 表单提交要加
     * 提交指令
     *
     * @param params 参数列表,为一个json
     */
    @POST("hardware/receive/equipment/instruct/back")
    @Headers({"Content-Type: application/json","Accept: application/json"})
    Observable<ResponseState> postInstruction(@Body RequestBody params);

}
