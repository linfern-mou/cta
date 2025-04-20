package com.github.catvod.api;

import cn.hutool.core.io.FileUtil;
import com.github.catvod.utils.Json;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.net.URLEncoder;

@RunWith(RobolectricTestRunner.class)
public class TianYiHandlerTest {

    private TianYiHandler tianYiHandler;


    @Before
    public void setUp() {
        tianYiHandler = new TianYiHandler();

    }

    @Test
    public void startScan() throws Exception {
        // Mock the OkHttp.get method to return a predefined OkResult
        // Execute the method under test
        FileUtil.writeBytes(tianYiHandler.startScan(), "c://qrcode.png");

        while (true) {

        }

    }

    @Test
    public void refreshCookie() throws Exception {

        JsonObject obj = Json.safeObject("{\"open.e.189.cn\":{\"OPENINFO\":\"33c28688ef52ce9e3a9ef87388047efbde5e3e2e4c7ef6ef267632468c7dfaf294ff59fa59d34801\",\"pageOp\":\"f69c75fdedccb502a36c77a24e680020\",\"LT\":\"855366f756091e07\",\"GUID\":\"3956b8eff8b746ecb6529247e942ede9\",\"SSON\":\"dc466c8192e3109eaea837c1d136c1fd065253ce1c7d3a66ca1520d7d6d6307b10a1fe65c7becac73b95f24a6e681e654ec4f47c39533ebcc48bb78d6d6e63d1bbf3334e6e97eaa7092d34f87bf1209ebf5e9fb0cc4acaa19d9244741def867ce3c66f9d8bebba20bbd3850b11abcbf331d1f434623686850171e7f6e60c3af4726febb62ca26b017bf79babc487db070293d8276d861fcaadc58148255113ad473bd52c732bb1c22a095f1a6e76c6ec643eb5cc5fde4629f50d5cd9fd69397c3548cfd8377e57b4793588d4b4707cd037d43c42927c74878296e13016a6974dba12aee0c9d53fa7\"},\"cloud.189.cn\":{\"JSESSIONID\":\"2378013FE97769100CA550798FE9C7A0\",\"COOKIE_LOGIN_USER\":\"CEFA57A70B075914732B7BAB853E481AFC04F65870A4D9D68AD677D97C78BE91C0613178466B68B1E91E2DC5DAF6FAED798CCA1676AAF3D1\"}}");
        tianYiHandler.setCookie(obj);
        tianYiHandler.refreshCookie();

        while (true) {

        }

    }

    @Test
    public void download() throws Exception {
        // Mock the OkHttp.get method to return a predefined OkResult
        // Execute the method under test


    }

    @Test
    public void testgetUUID() throws Exception {
        JsonObject uuid = tianYiHandler.getUUID();
        System.out.println(uuid);
    }

    @Test
    public void testdownloadQRCode() throws Exception {
/*
        JsonObject uuidInfo = tianYiHandler.getUUID();
        String uuid = uuidInfo.get("uuid").getAsString();
        byte[] qrCode = tianYiHandler.downloadQRCode(uuid);
        FileUtil.writeBytes(qrCode, "c://qrcode.png");

        System.out.println(uuid);*/

        String url = "https://cloud.189.cn/api/portal/callbackUnify.action?browserId=dff95dced0b03d9d972d920f03ddd05e&redirectURL=https%3A%2F%2Fcloud.189.cn%2Fweb%2Fredirect.html";

        String encode = "https%3A%2F%2Fcloud.189.cn%2Fapi%2Fportal%2FcallbackUnify.action%3FbrowserId%3Ddff95dced0b03d9d972d920f03ddd05e%26redirectURL%3Dhttps%253A%252F%252Fcloud.189.cn%252Fweb%252Fredirect.html";
        assert URLEncoder.encode(url, "UTF-8").equals(encode);
    }


}