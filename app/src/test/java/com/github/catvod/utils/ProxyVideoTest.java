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
               // "https://js.shipin520.com/pc/images/new/banner20250225.mp4", new HashMap<>());
                "https://video.shipin520.com/videos/42/33/21/b_hsTXjZv04HeM1613423321_v1.mp4", new HashMap<>());
        System.out.println(url);
        while (true) {

        }
    }
}

