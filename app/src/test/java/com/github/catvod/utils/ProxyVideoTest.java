package com.github.catvod.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;

//@RunWith(RobolectricTestRunner.class)
public class ProxyVideoTest {

    @Test
    public void proxyMultiThread() {
        //  ProxyVideo.proxyMultiThread()
      /*  Server.get().start();
        String url = ProxyVideo.buildCommonProxyUrl(
               // "https://js.shipin520.com/pc/images/new/banner20250225.mp4", new HashMap<>());
                "http://172.16.1.217:18089/ng-grid/video.mp4", new HashMap<>());
        System.out.println(url);*/
        ProxyServer.INSTANCE.start();
        System.out.println(ProxyServer.INSTANCE.buildProxyUrl("http://172.16.1.217:18089/ng-grid/video.mp4", new HashMap<>()));
        while (true) {

        }
    }
}

