package com.github.catvod.bean.uc;

import com.google.gson.annotations.SerializedName;

public class User {
    public User(String cookie) {
        this.cookie = cookie;
    }

    @SerializedName("cookie")
    private String cookie;
    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public static User objectFrom(String cookie) {
        return new User(cookie);
    }


    public void clean() {
        this.cookie = "";

    }
}
