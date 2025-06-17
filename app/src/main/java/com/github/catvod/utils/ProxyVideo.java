package com.github.catvod.utils;

import android.os.SystemClock;
import android.text.TextUtils;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.spider.Proxy;
import com.google.gson.Gson;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProxyVideo {

    private static final String GO_SERVER = "http://127.0.0.1:7777/";
    //线程数4
    private static final int THREAD_NUM = 4;


    public static String buildCommonProxyUrl(String url, Map<String, String> headers) {
        return Proxy.getUrl() + "?do=proxy&url=" + Util.base64Encode(url.getBytes(Charset.defaultCharset())) + "&header=" + Util.base64Encode((new Gson().toJson(headers)).getBytes(Charset.defaultCharset()));
    }

    public static void go() {
        boolean close = OkHttp.string(GO_SERVER).isEmpty();
        if (close) OkHttp.string("http://127.0.0.1:" + Proxy.getPort() + "/go");
        if (close) while (OkHttp.string(GO_SERVER).isEmpty()) SystemClock.sleep(20);
    }

    public static String goVer() {
        try {
            go();
            String result = OkHttp.string(GO_SERVER + "version");
            return new JSONObject(result).optString("version");
        } catch (Exception e) {
            return "";
        }
    }

    public static String url(String url, int thread) {
        if (!TextUtils.isEmpty(goVer()) && url.contains("/proxy?")) url += "&response=url";
        return String.format(Locale.getDefault(), "%s?url=%s&thread=%d", GO_SERVER, URLEncoder.encode(url), thread);
    }

    public static Object[] proxy(String url, Map<String, String> headers) throws Exception {
        SpiderDebug.log(" ++start proxy:");
        SpiderDebug.log(" ++proxy url:" + url);
        SpiderDebug.log(" ++proxy header:" + Json.toJson(headers));

        Response response = OkHttp.newCall(url, headers);
        SpiderDebug.log(" ++end proxy:");
        SpiderDebug.log(" ++proxy res code:" + response.code());
        SpiderDebug.log(" ++proxy res header:" + Json.toJson(response.headers()));
        //    SpiderDebug.log(" ++proxy res data:" + Json.toJson(response.body()));
        String contentType = response.headers().get("Content-Type");
        String contentDisposition = response.headers().get("Content-Disposition");
        if (contentDisposition != null) contentType = getMimeType(contentDisposition);
        Map<String, String> respHeaders = new HashMap<>();
       /* respHeaders.put("Access-Control-Allow-Credentials", "true");
        respHeaders.put("Access-Control-Allow-Origin", "*");*/

        for (String key : response.headers().names()) {
            respHeaders.put(key, response.headers().get(key));
        }
        SpiderDebug.log("++proxy res contentType:" + contentType);
        //   SpiderDebug.log("++proxy res body:" + response.body());
        SpiderDebug.log("++proxy res respHeaders:" + Json.toJson(respHeaders));
        return new Object[]{response.code(), contentType, response.body().byteStream(), respHeaders};
    }


    public static Object[] proxyMultiThread(String url, Map<String, String> headers) throws Exception {

        String range = headers.get("range");
        SpiderDebug.log("---proxyMultiThread,Range:" + range);
        Range rangeObj = parseRange(range);
        //没有range,无需分割
        if (rangeObj == null) {
            SpiderDebug.log("没有range,无需分割");
            return proxy(url, headers);
        } else {
            //end 为空，测试请求
            if (StringUtils.isAllBlank(rangeObj.getEnd())) {
                return proxy(url, headers);
            } else {
                long start = Long.parseLong(rangeObj.getStart());
                long end = Long.parseLong(rangeObj.getEnd());

                long size = end - start;
                //每块大小
                long partSize = size / THREAD_NUM;
                ExecutorService service = Executors.newFixedThreadPool(THREAD_NUM);
// 存储执行结果的List
                List<Future<Response>> results = new ArrayList<Future<Response>>();
                for (int i = 0; i < THREAD_NUM; i++) {
                    long partEnd = start + partSize >= end ? end : start + partSize;
                    String newRange = "range=" + start + "-" + partEnd;
                    start = partEnd;

                    headers.put("Range", newRange);
                    Future<Response> result = service.submit(() -> {
                        try {
                            return OkHttp.newCall(url, headers);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    results.add(result);
                }
                byte[] bytes = new byte[(int) size];
                Response response = null;
                for (int i = 0; i < THREAD_NUM; i++) {
                    // 获取包含返回结果的future对象
                    Future<Response> future = results.get(i);
                    // 从future中取出执行结果（若尚未返回结果，则get方法被阻塞，直到结果被返回为止）
                    response = future.get();
                    response.body().byteStream().read(bytes, (int) (i * partSize), (int) partSize);
                    SpiderDebug.log("---第" + i + "块下载完成" + ";headers:" + Json.toJson(response.headers()));

                }
                String contentType = response.headers().get("Content-Type");
                String contentDisposition = response.headers().get("Content-Disposition");
                if (contentDisposition != null) contentType = getMimeType(contentDisposition);
                Map<String, String> respHeaders = new HashMap<>();
           /* respHeaders.put("Access-Control-Allow-Credentials", "true");
            respHeaders.put("Access-Control-Allow-Origin", "*");*/

                for (String key : response.headers().names()) {
                    respHeaders.put(key, response.headers().get(key));
                }
                SpiderDebug.log("++proxy res contentType:" + contentType);
                //   SpiderDebug.log("++proxy res body:" + response.body());
                SpiderDebug.log("++proxy res respHeaders:" + Json.toJson(respHeaders));
                return new Object[]{response.code(), contentType, new ByteArrayInputStream(bytes), respHeaders};

            }
        }

    }

    private static Range parseRange(String range) {
        if (StringUtils.isNoneBlank(range)) {
            String[] ranges = range.replace("bytes=", "").split("-");
            String start = ranges[0];
            String end = ranges[1];
            return new Range(start, end);
        }
        return null;
    }

    private static String getMimeType(String contentDisposition) {
        if (contentDisposition.endsWith(".mp4")) {
            return "video/mp4";
        } else if (contentDisposition.endsWith(".webm")) {
            return "video/webm";
        } else if (contentDisposition.endsWith(".avi")) {
            return "video/x-msvideo";
        } else if (contentDisposition.endsWith(".wmv")) {
            return "video/x-ms-wmv";
        } else if (contentDisposition.endsWith(".flv")) {
            return "video/x-flv";
        } else if (contentDisposition.endsWith(".mov")) {
            return "video/quicktime";
        } else if (contentDisposition.endsWith(".mkv")) {
            return "video/x-matroska";
        } else if (contentDisposition.endsWith(".mpeg")) {
            return "video/mpeg";
        } else if (contentDisposition.endsWith(".3gp")) {
            return "video/3gpp";
        } else if (contentDisposition.endsWith(".ts")) {
            return "video/MP2T";
        } else if (contentDisposition.endsWith(".mp3")) {
            return "audio/mp3";
        } else if (contentDisposition.endsWith(".wav")) {
            return "audio/wav";
        } else if (contentDisposition.endsWith(".aac")) {
            return "audio/aac";
        } else {
            return null;
        }
    }

    /**
     * 视频range
     */
    private static class Range {
        private String start;
        private String end;

        public Range(String start, String end) {
            start = start;
            end = end;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }
    }
}
