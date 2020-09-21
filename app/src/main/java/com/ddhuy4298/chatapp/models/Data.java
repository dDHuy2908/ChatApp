package com.ddhuy4298.chatapp.models;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;

    public Data() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
