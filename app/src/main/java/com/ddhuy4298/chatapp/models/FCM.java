package com.ddhuy4298.chatapp.models;

import com.google.gson.annotations.SerializedName;

public class FCM {
    @SerializedName("to")
    private String to;
    @SerializedName("data")
    private Data data;

    public FCM() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
