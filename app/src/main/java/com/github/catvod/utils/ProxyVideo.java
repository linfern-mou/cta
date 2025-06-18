package com.github.catvod.utils;

import android.os.SystemClock;
import android.text.TextUtils;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.net.OkHttp;
import com.github.catvod.spider.Proxy;
import com.google.gson.Gson;
import okhttp3.Response;
import org.apache.commons.lang3.ArrayUtils;
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
        Map<String, String> newHeaders = new HashMap<>(headers);
        newHeaders.put("range", "bytes=0-0");
        Object[] info = proxy(url, newHeaders);
        int code = (int) info[0];
        if (code != 206) {
            return proxy(url, headers);
        }
        String contentRange = ((Map<String, String>) info[3]).get("Content-Range");
        //文件总大小
        String total = StringUtils.split(contentRange, "/")[1];


        String range = headers.get("range");
        SpiderDebug.log("---proxyMultiThread,Range:" + range);
        Map<String, String> rangeObj = parseRange(range);
        //没有range,无需分割
        if (rangeObj == null) {
            SpiderDebug.log("没有range,无需分割");
            return proxy(url, headers);
        } else {
            List<long[]> partList = generatePart(rangeObj, total);

            ExecutorService service = Executors.newFixedThreadPool(THREAD_NUM);
            // 存储执行结果的List
            List<Future<Response>> results = new ArrayList<Future<Response>>();
            for (long[] part : partList) {

                String newRange = "bytes=" + part[0] + "-" + part[1];
                SpiderDebug.log("下载开始" + ";newRange:" + newRange);

                Map<String, String> headerNew = new HashMap<>(headers);

                headerNew.put("range", newRange);
                Future<Response> result = service.submit(() -> {
                    try {

                        return OkHttp.newCall(url, headerNew);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                results.add(result);
            }
            byte[] bytes = null;

            Response response = null;
            for (int i = 0; i < THREAD_NUM; i++) {
                // 获取包含返回结果的future对象
                Future<Response> future = results.get(i);
                // 从future中取出执行结果（若尚未返回结果，则get方法被阻塞，直到结果被返回为止）
                response = future.get();
                bytes = ArrayUtils.addAll(bytes, response.body().bytes());
                SpiderDebug.log("---第" + i + "块下载完成" + ";Content-Range:" + response.headers().get("Content-Range"));

            }
            service.shutdown();
            String contentType = response.headers().get("Content-Type");
            String contentDisposition = response.headers().get("Content-Disposition");
            if (contentDisposition != null) contentType = getMimeType(contentDisposition);
            Map<String, String> respHeaders = new HashMap<>();
           /* respHeaders.put("Access-Control-Allow-Credentials", "true");
            respHeaders.put("Access-Control-Allow-Origin", "*");*/

            for (String key : response.headers().names()) {
                respHeaders.put(key, response.headers().get(key));
            }

            respHeaders.put("Content-Length", String.valueOf(bytes.length));
            respHeaders.put("Content-Range", String.format("bytes %s-%s/%s", partList.get(0)[0], partList.get(THREAD_NUM - 1)[1], total));
            SpiderDebug.log("++proxy res contentType:" + contentType);
            //   SpiderDebug.log("++proxy res body:" + response.body());
            SpiderDebug.log("++proxy res respHeaders:" + Json.toJson(respHeaders));
            return new Object[]{response.code(), contentType, new ByteArrayInputStream(bytes), respHeaders};


        }

    }

    private static List<long[]> generatePart(Map<String, String> rangeObj, String total) {
        long start = Long.parseLong(rangeObj.get("start"));
        long end = StringUtils.isAllBlank(rangeObj.get("end")) ? start + 1024 * 1024 * 1 * 4 : Long.parseLong(rangeObj.get("end"));


        long totalSize = Long.parseLong(total);
        end = Math.min(end, totalSize - 1);
        long length = end - start + 1;

        long size = length / THREAD_NUM;
        List<long[]> partList = new ArrayList<>();
        for (int i = 0; i < THREAD_NUM; i++) {
            long partEnd = Math.min(start + size, end);

            partList.add(new long[]{start, partEnd});
            start = partEnd + 1;
        }
        return partList;
    }

    private static Map<String, String> parseRange(String range) {
        SpiderDebug.log("parseRange:" + range);
        if (StringUtils.isNoneBlank(range)) {

            String[] ranges = StringUtils.split(range.replace("bytes=", ""), "-");
            String start = ranges[0];
            String end = ranges.length > 1 ? ranges[1] : "";
            return Map.of("start", start, "end", end);
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

}
