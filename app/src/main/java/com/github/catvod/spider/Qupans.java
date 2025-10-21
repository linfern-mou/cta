package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONObject;

import com.github.catvod.bean.Result;
import com.github.catvod.bean.Vod;
import com.github.catvod.net.OkHttp;
import com.github.catvod.utils.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Qupans extends Cloud {

    private static final String BASE_URL = "https://www.qupanshe.com";
    private static final String DEFAULT_COVER_URL = "https://fs-im-kefu.7moor-fs1.com/ly/4d2c3f00-7d4c-11e5-af15-41bf63ae4ea0/1743950734122/baidu.jpg";
    private static final String BAIDU_PAN_REGEX = "https?:\\/\\/(?:pan\\.)?baidu\\.com\\/(?:s\\/|share\\/link\\?)[^\\s]+";
    private static final String PASSWORD_REGEX = "提取码[:：]\\s*([a-zA-Z0-9]{4})";

    public Qupans() {
        super();
    }

    @Override
    public void init(Context context, String extend) {
        try {
            super.init(context, extend);
            if (!TextUtils.isEmpty(extend)) {
                try {
                    JSONObject json = new JSONObject(extend);
                    String customUrl = json.optString("url");
                    if (!TextUtils.isEmpty(customUrl)) {
                    }
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
        }
    }

    private String requestWithHeaders(String url) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        headers.put("Referer", BASE_URL);
        return OkHttp.string(url, headers);
    }

    @Override
    public String homeContent(boolean filter) {
        try {
            JSONObject result = new JSONObject();
            JSONArray classArray = new JSONArray();
            JSONObject json = new JSONObject("{\"电影\":\"3\",\"电视剧\":\"2\",\"综艺\":\"4\",\"动漫\":\"5\",\"纪录片\":\"6\"}");
            Iterator<String> keys = json.keys();
            
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject category = new JSONObject();
                category.put("type_id", json.optString(key));
                category.put("type_name", key);
                classArray.put(category);
            }
            
            result.put("class", classArray);
            return result.toString();
        } catch (Exception e) {
            return "{\"class\":[]}";
        }
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        String url = String.format("%s/forum.php?mod=forumdisplay&fid=%s&page=%s", BASE_URL, tid, pg);
        String response = requestWithHeaders(url);
        Document doc = Jsoup.parse(response);
        List<Pair<String, String>> list = new ArrayList<>();

        Elements items = doc.select("div.tit_box > a.s");
        for (Element item : items) {
            String title = item.text();
            if (!title.contains("公告") && !title.contains("求")) {
                list.add(new Pair<>(item.attr("href"), title));
            }
        }

        try {
            JSONObject result = new JSONObject();
            JSONArray listArray = new JSONArray();
            for (Pair<String, String> item : list) {
                JSONObject vod = new JSONObject();
                vod.put("vod_id", item.first);
                vod.put("vod_name", item.second);
                vod.put("vod_pic", DEFAULT_COVER_URL);
                vod.put("vod_remarks", "");
                listArray.put(vod);
            }
            result.put("list", listArray);
            result.put("page", pg);
            result.put("pagecount", "0");
            result.put("total", "0");
            return result.toString();
        } catch (Exception e) {
            return "{\"list\":[],\"page\":\"1\",\"pagecount\":\"0\",\"total\":\"0\"}";
        }
    }

    @Override
    public String detailContent(List<String> ids) throws Exception {
        String vodId = ids.get(0);
        String url = vodId.startsWith("http") ? vodId : BASE_URL + "/" + vodId;
        String html = requestWithHeaders(url);
        Document doc = Jsoup.parse(html);

        Vod item = new Vod();
        item.setVodId(vodId);
        
        Element titleElement = doc.select("h1").first();
        String title = titleElement != null ? titleElement.text() : doc.title();
        item.setVodName(title);
        item.setVodPic(DEFAULT_COVER_URL);

        List<String> shareLinks = extractBaiduPanLinks(doc);
        
        if (!shareLinks.isEmpty()) {
            String pwd = Util.findByRegex(doc.text(), PASSWORD_REGEX, 1);
            if (!TextUtils.isEmpty(pwd)) {
                for (int i = 0; i < shareLinks.size(); i++) {
                    String link = shareLinks.get(i);
                    if (!link.contains("pwd=")) {
                        shareLinks.set(i, link + (link.contains("?") ? "&" : "?") + "pwd=" + pwd);
                    }
                }
            }
            
            String playFrom = super.detailContentVodPlayFrom(shareLinks);
            String playUrl = super.detailContentVodPlayUrl(shareLinks);
            item.setVodPlayFrom(playFrom);
            item.setVodPlayUrl(playUrl);
        }

        return Result.string(item);
    }
    
    private List<String> extractBaiduPanLinks(Document doc) {
        List<String> shareLinks = new ArrayList<>();
        
        for (Element link : doc.select("a")) {
            String href = link.attr("href");
            if (href.contains("baidu.com") || href.contains("pan.baidu")) {
                if (!href.startsWith("http")) {
                    if (href.startsWith("/")) {
                        href = BASE_URL + href;
                    }
                }
                if (!shareLinks.contains(href)) {
                    shareLinks.add(href);
                }
            }
        }
        
        for (Element content : doc.select("td.t_f, .content, .post-content")) {
            String foundLink = Util.findByRegex(content.text(), BAIDU_PAN_REGEX, 0);
            if (!TextUtils.isEmpty(foundLink) && !shareLinks.contains(foundLink)) {
                shareLinks.add(foundLink);
            }
        }
        
        if (shareLinks.isEmpty()) {
            String foundLink = Util.findByRegex(doc.text(), BAIDU_PAN_REGEX, 0);
            if (!TextUtils.isEmpty(foundLink) && !shareLinks.contains(foundLink)) {
                shareLinks.add(foundLink);
            }
        }
        
        return shareLinks;
    }

    @Override
    public String searchContent(String key, boolean quick) {
        return searchWithKeyword(key);
    }

    @Override
    public String searchContent(String key, boolean quick, String pg) {
        return searchWithKeyword(key);
    }
    
    private String searchWithKeyword(String keyword) {
        String url = BASE_URL + "/search.php?mod=forum";

        Map<String, String> params = new HashMap<>();
        params.put("srchuname", "");
        params.put("srchfilter", "all");
        params.put("srchfrom", "0");
        params.put("before", "");
        params.put("orderby", "lastpost");
        params.put("ascdesc", "desc");
        params.put("searchsubmit", "yes");
        params.put("srchtxt", keyword);

        String[] forumIds = {"2", "3", "4", "5", "6"};
        for (int i = 0; i < forumIds.length; i++) {
            params.put("srchfid[" + i + "]", forumIds[i]);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("Cookie", "X_CACHE_KEY=4ea0277e01b1880d76edcdb5deaf7d38; f3ll_2132_saltkey=pbaZTtWt; f3ll_2132_lastvisit=1750900013; f3ll_2132_seccodecSKQwk6G=516.5c6d6036e45acc7725; f3ll_2132_sid=pS5p3e; f3ll_2132_ulastactivity=4e54n6SBOHeGXpFs4y2pmBVIZ99rNTklZ8bmLQzNerUezlIdu4SI; f3ll_2132_auth=b46bRtQ%2FO7ckeivggteyEs2twb%2B3HHbhT0FlBF4IlO%2BSb1ndi5elO0d7KDFhy15UqCC9nKGyGUmA9fYu8SWezxcK; f3ll_2132_sendmail=1; f3ll_2132_lastact=1752292107%09home.php%09spacecp; f3ll_2132_lastcheckfeed=3141%7C1752292107");

        String response = OkHttp.post(url, params, headers).getBody();
        Document doc = Jsoup.parse(response);
        Elements items = doc.select("#threadlist ul li");

        List<Pair<String, String>> list = new ArrayList<>();
        for (Element item : items) {
            Elements aElements = item.select("h3 > a");
            if (!aElements.isEmpty()) {
                String href = aElements.first().attr("href");
                String title = aElements.first().text();
                if (title != null && !title.isEmpty()) {
                    title = title.replaceAll("<[^>]+>", "");
                }
                list.add(new Pair<>(href, title));
            }
        }

        try {
            JSONObject result = new JSONObject();
            JSONArray listArray = new JSONArray();
            for (Pair<String, String> item : list) {
                JSONObject vod = new JSONObject();
                vod.put("vod_id", item.first);
                vod.put("vod_name", item.second);
                vod.put("vod_pic", DEFAULT_COVER_URL);
                vod.put("vod_remarks", "");
                listArray.put(vod);
            }
            result.put("list", listArray);
            result.put("page", "1");
            result.put("pagecount", "1");
            result.put("total", String.valueOf(listArray.length()));
            return result.toString();
        } catch (Exception e) {
            return "{\"list\":[],\"page\":\"1\",\"pagecount\":\"1\",\"total\":\"0\"}";
        }
    }
}