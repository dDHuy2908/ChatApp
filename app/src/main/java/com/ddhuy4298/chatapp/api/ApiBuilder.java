package com.ddhuy4298.chatapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiBuilder {
    private static Api api;

    public static Api getInstance() {
        if (api == null) {
            api = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://fcm.googleapis.com/")
                    .build()
                    .create(Api.class);
        }
        return api;
    }
}
