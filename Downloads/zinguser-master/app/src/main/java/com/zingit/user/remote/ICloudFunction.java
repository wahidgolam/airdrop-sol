package com.zingit.user.remote;

import com.zingit.user.model.CashFreeToken;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ICloudFunction {
    @GET("token")
    Observable<CashFreeToken> getToken(@Query("orderId") String orderId,
                                       @Query("orderAmount") String orderAmount,
                                       @Query("customerId") String customerId,
                                       @Query("customerEmail") String customerEmail,
                                       @Query("customerPhone") String customerPhone);
}
