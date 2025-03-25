package com.github.catvod.api;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.catvod.bean.tianyi.Cache;
import com.github.catvod.bean.tianyi.User;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.net.OkResult;
import com.github.catvod.spider.Init;
import com.github.catvod.utils.*;
import com.google.gson.JsonObject;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TianYiHandler {

    private ScheduledExecutorService service;
    private AlertDialog dialog;
    private final Cache cache;

    public File getCache() {
        return Path.tv("tianyi");
    }

    private String indexUrl = "";

    private String reqId;
    private String lt;

    public TianYiHandler() {

        cache = Cache.objectFrom(Path.read(getCache()));
    }


    public byte[] startScan() throws Exception {

        OkResult okResult1 = OkHttp.get("https://ux.21cn.com/api/htmlReportRest/getJs.js?pid=25577E0DEEDF48ADBD4459911F5825E4", new HashMap<>(), new HashMap<>());
        List<String> cookie = okResult1.getResp().get("Set-Cookie");
        List<String> cookieList = new ArrayList<>();
        for (String s : cookie) {
            String[] split = s.split(";");
            String cookie1 = split[0];
            cookieList.add(cookie1);
        }
        //String firstCookie = StringUtils.join(cookieList, ";");
        SpiderDebug.log("index ori: " + "https://cloud.189.cn/api/portal/loginUrl.action?redirectURL=https%3A%2F%2Fcloud.189.cn%2Fweb%2Fredirect.html&defaultSaveName=3&defaultSaveNameCheck=uncheck&browserId=dff95dced0b03d9d972d920f03ddd05e");
        String index = OkHttp.getLocation("https://cloud.189.cn/api/portal/loginUrl.action?redirectURL=https://cloud.189.cn/web/redirect.html&defaultSaveName=3&defaultSaveNameCheck=uncheck&browserId=8d38da4344fba4699d13d6e6854319d7", Map.of("Cookie", ""));
        SpiderDebug.log("index red: " + index);
        Map<String, List<String>> resHeaderMap = OkHttp.getLocationHeader(index, new HashMap<>());
        indexUrl = resHeaderMap.get("Location").get(0);
        SpiderDebug.log("indexUrl red: " + indexUrl);

        cookieList.clear();
        for (String s : resHeaderMap.get("Set-Cookie")) {
            String[] split = s.split(";");
            String cookie1 = split[0];
            cookieList.add(cookie1);
        }
        String secondCookie = StringUtils.join(cookieList, ";");
        SpiderDebug.log("secondCookie: " + secondCookie);

        HttpUrl httpParams = HttpUrl.parse(indexUrl);
        reqId = httpParams.queryParameter("reqId");
        lt = httpParams.queryParameter("lt");

        Map<String, String> tHeaders = new HashMap<>();
        tHeaders.put("Content-Type", "application/x-www-form-urlencoded");
        tHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/76.0");
        tHeaders.put("Referer", indexUrl);
        tHeaders.put("origin", "https://open.e.189.cn");
        tHeaders.put("Lt", lt);
        tHeaders.put("Reqid", reqId);
        tHeaders.put("Cookie", secondCookie);
        Map<String, String> param = new HashMap<>();

        param.put("version", "2.0");
        param.put("appKey", "cloud");
        String paramId;
        String returnUrl;
        OkResult okResult = OkHttp.post("https://open.e.189.cn/api/logbox/oauth2/appConf.do", param, tHeaders);
        if (okResult.getCode() == 200) {
            paramId = Json.safeObject(okResult.getBody()).get("data").getAsJsonObject().get("paramId").getAsString();
            returnUrl = Json.safeObject(okResult.getBody()).get("data").getAsJsonObject().get("returnUrl").getAsString();
        } else {
            paramId = "";
            returnUrl = "";
        }
        SpiderDebug.log("paramId: " + paramId);
        SpiderDebug.log("returnUrl: " + returnUrl);

        // Step 1: Get UUID
        JsonObject uuidInfo = getUUID();
        String uuid = uuidInfo.get("uuid").getAsString();
        String encryuuid = uuidInfo.get("encryuuid").getAsString();
        String encodeuuid = uuidInfo.get("encodeuuid").getAsString();

        // Step 2: Get QR Code
        byte[] byteStr = downloadQRCode(encodeuuid, reqId, secondCookie);

        Init.run(() -> showQRCode(byteStr));
        // Step 3: Check login status
        // return
        Init.execute(() -> startService(uuid, encryuuid, reqId, lt, paramId, returnUrl, secondCookie));
        /*Map<String, Object> result = new HashMap<>();
        result.put("qrcode", "data:image/png;base64," + qrCode);
        result.put("status", "NEW");*/
        return byteStr;

    }

    public JsonObject getUUID() {
        Map<String, String> params = new HashMap<>();
        params.put("appId", "cloud");
        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36");
        headers.put("lt", lt);
        headers.put("reqId", reqId);
        headers.put("referer", indexUrl);

        OkResult okResult = OkHttp.post("https://open.e.189.cn/api/logbox/oauth2/getUUID.do", params, headers);
        if (okResult.getCode() == 200) {
            return Json.safeObject(okResult.getBody());
        }
        return null;
    }

    public byte[] downloadQRCode(String uuid, String reqId, String cookie) throws IOException {


        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36");

        headers.put("referer", indexUrl);
        headers.put("cookie", cookie);
        //  OkResult okResult = OkHttp.get("https://open.e.189.cn/api/logbox/oauth2/image.do", params, headers);
//.addQueryParameter("uuid", uuid).addQueryParameter("REQID", reqId)
        HttpUrl url = HttpUrl.parse("https://open.e.189.cn/api/logbox/oauth2/image.do?uuid=" + uuid + "&REQID=" + reqId).newBuilder().build();

        Request request = new Request.Builder().url(url).headers(Headers.of(headers)).build();
        Response response = OkHttp.newCall(request);
        if (response.code() == 200) {
            return response.body().bytes();
        }
        return null;
    }


    private Map<String, Object> checkLoginStatus(String uuid, String encryuuid, String reqId, String lt, String paramId, String returnUrl, String secondCookie) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("appId", "cloud");
        params.put("encryuuid", encryuuid);
        params.put("uuid", uuid);
        params.put("date", DateFormatUtils.format(new Date(), "yyyy-MM-ddHH:mm:ss") + new Random().nextInt(24));
        params.put("returnUrl", URLEncoder.encode(returnUrl, "UTF-8"));
        params.put("clientType", "1");
        params.put("timeStamp", (System.currentTimeMillis() / 1000 + 1) + "000");
        params.put("cb_SaveName", "0");
        params.put("isOauth2", "false");
        params.put("state", "");
        params.put("paramId", paramId);
        Map<String, String> headers = new HashMap<>();
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36");
        headers.put("referer", indexUrl);
        headers.put("Reqid", reqId);
        headers.put("cookie", secondCookie);
        OkResult okResult = OkHttp.post("https://open.e.189.cn/api/logbox/oauth2/qrcodeLoginState.do", params, headers);
        SpiderDebug.log("qrcodeLoginState result------" + okResult.getBody());

        JsonObject obj = Json.safeObject(okResult.getBody()).getAsJsonObject();
        if (okResult.getCode() == 200 && Objects.nonNull(obj.get("status")) && obj.get("status").getAsInt() == 0) {
            SpiderDebug.log("扫码成功------" + obj.get("redirectUrl").getAsString());
            String redirectUrl = obj.get("redirectUrl").getAsString();
            Map<String, Object> result = new HashMap<>();

            fetchUserInfo(redirectUrl, secondCookie, result);


        } else {
            SpiderDebug.log("扫码失败------" + okResult.getBody());
        }


        return null;
    }

    private void fetchUserInfo(String redirectUrl, String secondCookie, Map<String, Object> result) throws IOException {

        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", secondCookie);
        Map<String, List<String>> okResult = OkHttp.getLocationHeader(redirectUrl, headers);
        SpiderDebug.log("扫码返回数据：" + Json.toJson(okResult));
        if (okResult.containsKey("set-cookie")) {
            List<String> cookieList = new ArrayList<>();
            for (String s : okResult.get("set-cookie")) {

                String[] split = s.split(";");
                String cookie1 = split[0];
                cookieList.add(cookie1);

            }
            cache.setTianyiUser(User.objectFrom(StringUtils.join(cookieList, ";")));
            SpiderDebug.log("获取cookie成功：" + StringUtils.join(cookieList, ";"));
            //停止检验线程，关闭弹窗
            stopService();
        }


       /* if (okResult.getCode() == 200) {
            okResult.getBody();
        }*/
        return;

    }


    /**
     * 显示qrcode
     *
     * @param bytes
     */
    public void showQRCode(byte[] bytes) {
        try {
            int size = ResUtil.dp2px(240);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            ImageView image = new ImageView(Init.context());
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setImageBitmap(QRCode.Bytes2Bimap(bytes));
            FrameLayout frame = new FrameLayout(Init.context());
            params.gravity = Gravity.CENTER;
            frame.addView(image, params);
            dialog = new AlertDialog.Builder(Init.getActivity()).setView(frame).setOnCancelListener(this::dismiss).setOnDismissListener(this::dismiss).show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Notify.show("请使用天翼网盘App扫描二维码");
        } catch (Exception ignored) {
        }
    }

    private void dismiss() {
        try {
            if (dialog != null) dialog.dismiss();
        } catch (Exception ignored) {
        }
    }

    private void dismiss(DialogInterface dialog) {
        stopService();
    }

    private void stopService() {
        if (service != null) service.shutdownNow();
        Init.run(this::dismiss);
    }

    public void startService(String uuid, String encryuuid, String reqId, String lt, String paramId, String returnUrl, String secondCookie) {
        SpiderDebug.log("----start  checkLoginStatus  service");

        service = Executors.newScheduledThreadPool(1);

        service.scheduleWithFixedDelay(() -> {
            SpiderDebug.log("----checkLoginStatus ing....");
            try {
                checkLoginStatus(uuid, encryuuid, reqId, lt, paramId, returnUrl, secondCookie);
            } catch (Exception e) {
                SpiderDebug.log("----checkLoginStatus error" + e.getMessage());
                throw new RuntimeException(e);
            }

        }, 1, 3, TimeUnit.SECONDS);
    }

}