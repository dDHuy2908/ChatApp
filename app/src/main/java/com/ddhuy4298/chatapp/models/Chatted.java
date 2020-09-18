package com.ddhuy4298.chatapp.models;

public class Chatted {
    public String id;
    public long lastedMessageTime;

    public long getLastedMessageTime() {
        return lastedMessageTime;
    }

    public void setLastedMessageTime(long lastedMessageTime) {
        this.lastedMessageTime = lastedMessageTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
