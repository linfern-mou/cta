package com.github.catvod.api;

import cn.hutool.core.io.FileUtil;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
    }


}