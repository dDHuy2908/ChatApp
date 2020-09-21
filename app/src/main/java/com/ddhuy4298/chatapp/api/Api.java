package com.ddhuy4298.chatapp.api;

import com.ddhuy4298.chatapp.models.FCM;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Api {
    @Headers({
            "Content-type:application/json",
            "Authorization:key=AAAAM8_15Dg:APA91bEkU__RRkbtzz35AISJcrVW-keO2YLx8CITR9RCiGSGxTdjUh3WzWJqdFx2k5g9F6vW8NdOlwuzffszcYmuIAEFgZDpaL97H0IWr8fMhQ6u5ETnD_g6b_zuYmoWDbiHiqRyjj7M"
    })
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body FCM body);
}
