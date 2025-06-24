package com.github.catvod.utils;

import com.github.catvod.server.Server;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class ProxyVideoTest {

    @Test
    public void proxyMultiThread() {
        //  ProxyVideo.proxyMultiThread()
        Server.get().start();
        String url = ProxyVideo.buildCommonProxyUrl(
                "http://172.16.1.217:18089/ng-grid/video.mp4", new HashMap<>());
        System.out.println(url);
        while (true) {

        }
    }
}

