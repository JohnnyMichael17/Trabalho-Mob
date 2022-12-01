package com.example.mychatbox;

public class Contato {

    private String uid;
    private String username;
    private String lastMessage;

    public String getUid() {
        return uid;
    }

    public Contato setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Contato setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Contato setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Contato setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Contato setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }

    private long timeStamp;
    private String photoUrl;
}
