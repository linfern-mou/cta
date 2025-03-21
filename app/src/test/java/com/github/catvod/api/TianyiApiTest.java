package com.github.catvod.api;

import com.github.catvod.bean.tianyi.ShareData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class TianyiApiTest {

//    @Test
//    public void getShareData() {
//
//
//        ShareData shareData = QuarkApi.get().getShareData("https://pan.quark.cn/s/1e386295b8ca");
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//        System.out.println("getShareData--" + gson.toJson(shareData));
//    }

    @Test
    public void getShareData() throws Exception {

        com.github.catvod.bean.tianyi.ShareData shareData = TianyiApi.get().getShareData("https://cloud.189.cn/web/share?code=ZvEjUvq6FNr2", "");
        // TianyiApi.get().getVod(shareData);
        com.github.catvod.bean.tianyi.ShareData shareData1 = TianyiApi.get().getShareData("https://cloud.189.cn/web/share?code=2eyARfBzURZj（访问码：kz6y）", "");

        //  TianyiApi.get().getVod(shareData1);
        ShareData shareData2 = TianyiApi.get().getShareData("https://cloud.189.cn/t/ZvEjUvq6FNr2", "");
        // TianyiApi.get().getVod(shareData2);


    }


    @Test
    public void getVod() throws Exception {

        com.github.catvod.bean.tianyi.ShareData shareData1 = TianyiApi.get().getShareData("https://cloud.189.cn/web/share?code=qEVVjyqM7bY3（访问码：6iel）", "");
        TianyiApi api = TianyiApi.get();
        api.setCookie("JSESSIONID=B35242EB04B3FBE672BED4B42F04D7E3;COOKIE_LOGIN_USER=B0A47E7C883DA2F0AFA9713E5D80E60955214A1445778CC40810306B68D7038FC568A6F20EAE2963519B17746FC9EB976F2317DE786E92E8CFCA5D36");
        api.getVod(shareData1);


    }
}