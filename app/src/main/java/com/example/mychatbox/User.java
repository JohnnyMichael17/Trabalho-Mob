package com.example.mychatbox;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String id;
    private String name;
    private String name2;
    private String phone;
    private String profileUrl;
    private String token;
    private boolean online;

    public User() {
    }

    public User(String id, String name, String name2, String phone, String profileUrl) {
        this.id = id;
        this.name = name;
        this.name2 = name2;
        this.phone = phone;
        this.profileUrl = profileUrl;
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        name2 = in.readString();
        phone = in.readString();
        profileUrl = in.readString();
        token = in.readString();
        online = in.readInt() == 1;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getName2() {
        return name2;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getToken() {
        return token;
    }

    public boolean isOnline() {
        return online;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(name2);
        dest.writeString(phone);
        dest.writeString(profileUrl);
        dest.writeString(token);
        dest.writeInt(online ? 1 : 0);
    }
}

