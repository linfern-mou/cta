package com.github.catvod.api;


import com.github.catvod.bean.tianyi.Cache;
import com.github.catvod.bean.tianyi.User;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Json;
import com.github.catvod.utils.Path;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class SimpleCookieJar implements CookieJar {
    private Map<String, List<Cookie>> cookieStore = new HashMap<>();
    private final Cache cache;

    public Map<String, List<Cookie>> getCookieStore() {
        return cookieStore;
    }

    public void setCookieStore(Map<String, List<Cookie>> cookieStore) {
        this.cookieStore = cookieStore;
    }

    public SimpleCookieJar() {
        this.cache = Cache.objectFrom(Path.read(getCache()));
        String cookieStr = cache.getUser().getCookie();
        if (StringUtils.isNoneBlank(cookieStr)) {
            JsonObject cookieJson = Json.safeObject(cookieStr);
            setGlobalCookie(cookieJson);
        }
    }


    public File getCache() {
        return Path.tv("tianyi");
    }

    @Override
    public void saveFromResponse(HttpUrl url, @NotNull List<Cookie> cookies) {
        SpiderDebug.log(" ====saveFromResponse url: " + url.host() + ": " + Json.toJson(cookies));
        SpiderDebug.log(" ====saveFromResponse cookie: " + Json.toJson(cookies));
        // 创建可修改的 Cookie 列表副本
        List<Cookie> oldCookies = cookieStore.get(url.host()) != null ? cookieStore.get(url.host()) : new ArrayList<>();
        List<Cookie> newCookies = new ArrayList<>(oldCookies);

        // 更新 Cookie
        for (Cookie newCookie : cookies) {
            // 移除同名的旧 Cookie
            for (Cookie oldCookie : newCookies) {
                if (oldCookie.name().equals(newCookie.name())) {
                    oldCookies.remove(oldCookie);
                }
            }
        }
        oldCookies.addAll(cookies);

        cookieStore.put(url.host(), oldCookies);
        cache.setTianyiUser(User.objectFrom(Json.toJson(cookieStore)));
        SpiderDebug.log(" cookieStore now : " + Json.toJson(cookieStore));

    }

    @Override
    public @NotNull List<Cookie> loadForRequest(HttpUrl url) {


        var cookies = cookieStore.get(url.host());
        SpiderDebug.log("  ===loadForRequest url : " + url);
        SpiderDebug.log("  ===loadForRequest cookie: " + Json.toJson(cookies));
        return cookies != null ? cookies : new ArrayList<>();
    }

    public void setGlobalCookie(JsonObject jsonObject) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonArray value = entry.getValue().getAsJsonArray();
            for (JsonElement element : value) {
                JsonObject cookieobj = element.getAsJsonObject();
                Cookie.Builder cookieBuilder = new Cookie.Builder().name(cookieobj.get("name").getAsString()).value(cookieobj.get("value").getAsString())

                        .expiresAt(cookieobj.get("expiresAt").getAsLong()).path(cookieobj.get("path").getAsString());

                boolean secure = cookieobj.get("secure").getAsBoolean();
                if (secure) {
                    cookieBuilder.secure();
                }

                boolean httpOnly = cookieobj.get("httpOnly").getAsBoolean();
                if (httpOnly) {
                    cookieBuilder.httpOnly();
                }
                boolean persistent = cookieobj.get("persistent").getAsBoolean();
              /*  if (persistent) {
                    cookieBuilder.persistent();
                }*/
                boolean hostOnly = cookieobj.get("hostOnly").getAsBoolean();
                if (hostOnly) {
                    cookieBuilder.hostOnlyDomain(cookieobj.get("domain").getAsString());
                } else {
                    cookieBuilder.domain(cookieobj.get("domain").getAsString());

                }
                Cookie cookies = cookieBuilder.build();


                // 设置全局Cookie
                List<Cookie> cookiesForHost = cookieStore.get(key) == null ? new ArrayList<>() : cookieStore.get(key);
                cookiesForHost.add(cookies);
                cookieStore.put(key, cookiesForHost);
            }
        }


    }

    public Map<String, List<Cookie>> getCookie() {
        return cookieStore;
    }
}