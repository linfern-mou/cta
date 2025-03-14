package com.github.catvod.spider;

import android.content.Context;
import com.github.catvod.api.UCTokenHandler;
import com.github.catvod.bean.Class;
import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.crawler.Spider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Introduce extends Spider {


    @Override
    public void init(Context context, String extend) throws Exception {
        super.init(context, extend);

    }

    @Override
    public String homeContent(boolean filter) throws Exception {
        List<Class> classes = new ArrayList<>();
        classes.add(new Class("1", "UC"));
        List<Vod> list = new ArrayList<>();
        String pic = "https://androidcatvodspider.netlify.app/wechat.png";
        String name = "关注公众号";
        list.add(new Vod("https://androidcatvodspider.netlify.app/wechat.png", name, pic));
        String pic2 = "https://androidcatvodspider.netlify.app/wechat.png";
        String name2 = "本接口不收费，请不要付费，谢谢！";
        list.add(new Vod("https://androidcatvodspider.netlify.app/wechat.png", name2, pic2));
        return Result.string(classes,list);
    }


    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        List<Vod> vodList = new ArrayList<>();
        //UC
        if (tid.equals("1")) {
            String pic = "https://androidcatvodspider.netlify.app/wechat.png";
            String name = "点击设置Token";
            vodList.add(new Vod("UCToken", name, pic));
        }
        return Result.get().vod(vodList).string();
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String vodId = ids.get(0);

        //UC Token 扫码
        if (vodId.equals("UCToken")) {
            UCTokenHandler qrCodeHandler = new UCTokenHandler();
            qrCodeHandler.startUC_TOKENScan();
        }
        Vod item = new Vod();
        return Result.string(item);
    }

}