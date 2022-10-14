package com.zingit.user.remote;

import com.zingit.user.model.CashFreeToken;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VerifyCloudFunction {
    @GET("status")
    Observable<CashFreeToken> getToken(@Query("orderId") String orderId);
}
