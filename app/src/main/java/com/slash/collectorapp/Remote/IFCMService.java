package com.slash.collectorapp.Remote;

import com.slash.collectorapp.Model.FCMResponse;
import com.slash.collectorapp.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA6dG_PMY:APA91bF3Ck19JNuNT4XM3Q9ejs3rfIa6xvHG2gb7Y-sKim1gJtgUjUCDxRRSXq1S7rFIap_bHkyWdjfdqIE3_A2MtnAM4_yilxa4vMLzI7mMOeLaU6UEo7bFytJVx7Lyctmsq-6mPwa7"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
