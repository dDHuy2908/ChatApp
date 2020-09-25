package com.ddhuy4298.chatapp.models;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("sender")
    private String sender;
    @SerializedName("receiver")
    private String receiver;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

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
