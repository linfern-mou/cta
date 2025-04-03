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

        JsonObject obj = Json.safeObject("{\"open.e.189.cn\":[{\"name\":\"SSON\",\"value\":\"dc466c8192e3109eaea837c1d136c1fd065253ce1c7d3a66ca1520d7d6d6307b10a1fe65c7becac73b95f24a6e681e654ec4f47c39533ebcc48bb78d6d6e63d1bbf3334e6e97eaa7092d34f87bf1209ee35f344871bc5a329eac34ae948d399d4a6b3b28a929c4f353ade0981657e9e0f09ce27cc1c15d8322c6e45a8ebb21eb431509f1dd7dc3a7856b32b0991d654d5ced73dd20b764ca8737600cbe699c37ccf59b3c610893fc42bdc08b477c5d394e290c14d175d1ca0ee9fa61a1a8dcac7007e9219fd0ae6ccd5dc760524213f85b6b8c6166af01a31336dab797d9118010b81a5a3c26e08e\",\"expiresAt\":253402300799999,\"domain\":\"e.189.cn\",\"path\":\"/\",\"secure\":true,\"httpOnly\":true,\"persistent\":true,\"hostOnly\":false},{\"name\":\"GUID\",\"value\":\"525d8874e53e46a7ba3ed8907e9fef1f\",\"expiresAt\":1775176321000,\"domain\":\"e.189.cn\",\"path\":\"/\",\"secure\":false,\"httpOnly\":false,\"persistent\":true,\"hostOnly\":false},{\"name\":\"pageOp\",\"value\":\"336b9ddc820212fa6c9b5a0cfd7bf5b3\",\"expiresAt\":253402300799999,\"domain\":\"e.189.cn\",\"path\":\"/\",\"secure\":false,\"httpOnly\":false,\"persistent\":false,\"hostOnly\":false},{\"name\":\"OPENINFO\",\"value\":\"33c28688ef52ce9e3a9ef87388047efbde5e3e2e4c7ef6ef267632468c7dfaf294ff59fa59d34801\",\"expiresAt\":253402300799999,\"domain\":\"e.189.cn\",\"path\":\"/\",\"secure\":false,\"httpOnly\":true,\"persistent\":false,\"hostOnly\":false},{\"name\":\"GRAYNUMBER\",\"value\":\"319DE3F68C8730862F3BEF66F3D635B7\",\"expiresAt\":1775177653000,\"domain\":\"e.189.cn\",\"path\":\"/\",\"secure\":false,\"httpOnly\":false,\"persistent\":true,\"hostOnly\":false}],\"cloud.189.cn\":[{\"name\":\"JSESSIONID\",\"value\":\"431787526C43DF21B6373E914FE597EC\",\"expiresAt\":253402300799999,\"domain\":\"cloud.189.cn\",\"path\":\"/\",\"secure\":false,\"httpOnly\":true,\"persistent\":false,\"hostOnly\":true},{\"name\":\"COOKIE_LOGIN_USER\",\"value\":\"0C7407F59A6E5896EB6B777056E160DB020BAE67B121B5930CCD4777073744055308F7E8CD03F2FC2399E4823F60ECDD74120CEE4C529017\",\"expiresAt\":253402300799999,\"domain\":\"cloud.189.cn\",\"path\":\"/\",\"secure\":false,\"httpOnly\":true,\"persistent\":false,\"hostOnly\":false}]}");
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