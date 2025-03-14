package com.github.catvod.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class QRCodeHandlerTest {

    private UCTokenHandler qrCodeHandler;


    @Before
    public void setUp() {
        qrCodeHandler = new UCTokenHandler();

    }

    @Test
    public void testStartUC_TOKENScan() throws Exception {
        // Mock the OkHttp.get method to return a predefined OkResult
        // Execute the method under test
        String result = qrCodeHandler.startUC_TOKENScan();
        System.out.println(result);
        while(true){

        }

    }


}