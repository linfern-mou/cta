package com.github.catvod.api;

import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@RunWith(RobolectricTestRunner.class)
public class QRCodeHandlerTest {

    private QRCodeHandler qrCodeHandler;


    @Before
    public void setUp() {
        qrCodeHandler = new QRCodeHandler();

    }

    @Test
    public void testStartUC_TOKENScan() throws Exception {
        // Mock the OkHttp.get method to return a predefined OkResult


        // Execute the method under test
        String result = qrCodeHandler.startUC_TOKENScan();
        System.out.println(result);



    }
}