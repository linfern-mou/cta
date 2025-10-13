package com.github.catvod.api

import com.github.catvod.net.OkHttp
import com.github.catvod.utils.Json
import com.github.catvod.utils.ProxyVideo
import com.github.catvod.utils.Util
import com.github.catvod.utils.Util.MEDIA
import com.google.gson.JsonObject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.security.MessageDigest
import java.util.*

class BaiduDrive {

    private val headers = mapOf(
        "User-Agent" to "Mozilla/5.0 (Linux; Android 12; SM-X800) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.40 Safari/537.36",
        "Accept" to "application/json, text/plain, */*",
        "Content-Type" to "application/x-www-form-urlencoded",
        "Origin" to "https://pan.baidu.com",
        "Referer" to "https://pan.baidu.com/"
    )

    private val cookies =
        "BIDUPSID=D122360716A06862B6689D4FE32053A1; PSTM=1756869077; MAWEBCUID=web_KAGxElHEQyrFImqrzARlkIssFfvhfCLGaXDsxmWKbhxsfEsvhs; PANWEB=1; delPer=0; ZFY=LJHc036:AkCyc1OPqFkXugSCgXnfaMIH:AjDQUOfkXtP4:C; BDRCVFR[uPX25oyLwh6]=mk3SLVN4HKm; BAIDUID_REF=B0FC75130A1A8DF92DC8685381870C9E:FG=1; H_WISE_SIDS_BFESS=110085_656765_660924_665265_666660_667682_662823_668348_668761_668756_669314_667565_669628_669693_669848_669685_669681_670119_670114_670116_670307_669051_670308_670357_670598_669700_670824_669791_669664_670557_667838_669418_663788; MCITY=-276%3A; ZD_ENTRY=baidu; PSINO=5; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; BDRCVFR[S4-dAuiWMmn]=I67x6TjHwwYf0; BA_HECTOR=a0a1a50hakakah242g25a18kah41801kejiv325; BAIDUID=102896B6EA4554BB74C3F8C78B676C7B:FG=1; BAIDUID_BFESS=102896B6EA4554BB74C3F8C78B676C7B:FG=1; H_WISE_SIDS=60273_63141_64007_64982_65120_65190_65227_65246_65254_65273_65280_65310_65361_65368_65417_65457_65451_65510_65543_65572_65596_65620_65599_65636_65476; H_PS_PSSID=60273_63141_64007_64982_65120_65190_65227_65246_65254_65273_65280_65310_65361_65368_65417_65457_65451_65510_65543_65572_65596_65620_65599_65636_65476; csrfToken=6tn2uwGoOUgn87ZBElHEGjCD; newlogin=1; ppfuid=FOCoIC3q5fKa8fgJnwzbE67EJ49BGJeplOzf+4l4EOvDuu2RXBRv6R3A1AZMa49I27C0gDDLrJyxcIIeAeEhD8JYsoLTpBiaCXhLqvzbzmvy3SeAW17tKgNq/Xx+RgOdb8TWCFe62MVrDTY6lMf2GrfqL8c87KLF2qFER3obJGmVXQmqM6seEAgB/LlOs0+zGEimjy3MrXEpSuItnI4KDwxAMUTOS16BfoXMcd2lpC/ZmOvCi7lsE0/UM0w4HSMVhh7EfifsXEYHtGj52zkQan6UDL686lBL6BHUyL9m3lfGgLbz7OSojK1zRbqBESR5Pdk2R9IA3lxxOVzA+Iw1TWLSgWjlFVG9Xmh1+20oPSbrzvDjYtVPmZ+9/6evcXmhcO1Y58MgLozKnaQIaLfWRFwa8A3ZyTRp/cDxRMhYc97xUSUZS0ReZYJMPG6nCsxNJlhI2UyeJA6QroZFMelR7tnTNS/pLMWceus0e757/UMPmrThfasmhDJrMFcBfoSrAAv3LCf1Y7/fHL3PTSf9vid/u2VLX4h1nBtx8EF07eCMhWVv+2qjbPV7ZhXk3reaWRFEeso3s/Kc9n/UXtUfNU1sHiCdbrCW5yYsuSM9SPGDZsl7FhTAKw7qIu38vFZiq+DRc8Vbf7jOiN9xPe0lOdZHUhGHZ82rL5jTCsILwcRVCndrarbwmu7G154MpYiKmTXZkqV7Alo4QZzicdyMbWvwvmR2/m//YVTM8qeZWgDSHjDmtehgLWM45zARbPujeqU0T92Gmgs89l2htrSKITeYE0TpfIvjPXsgyQghyP8U3sViHT1z07gbfu1XO5QQ/upk1AkHGkWrkbMWm+rpwpdImdyxYIjA1uSy2hfTFv/d3cnXH4nh+maaicAPllDgrppZTr0lDf2Vsiy73L8egP9ck5gsaaSE4obz9V1JGvyp8lNw+IyCN2Gou0efGgcYWqtuH+3KMtXW4uAv+XUaBDreXqEwrDxmyUrecavkqQ9rGRChHnhPuJeIKACPXiVuli9ItRLEkdb1mLxNHAk3uJy88YX/Rf/sKUjR12zxRTDxxJNDJS+Dlsbqu3n4I65ujli/3rQ8Zk1MjmTOsz9+kTqOM4upsnQ6IWq/zeZTItMCgHpQhuhr4ip73honuzoJgge1cqWBFYvpabAPTOERTOP1kmx5SXPARX5uxyJzAiNILBC8zh7fGfNXOWV37O9gEbYklWGnWpu55tg8Y8GaT7BFVDtu1KaJzjx51nTN1+xVI8c7otOFm3py1Y+wrt2CfI5v5JSd2kRNZE7s6bQrA5yMI31SfUDgxDrsd6lPtUU=; XFI=627d86b0-a655-11f0-b1eb-0f9d37032cfb; XFCS=B8C411ADE99EE9C3B29FDF31558D538BB636F205DF4F52FE2E57F1312E01079F; XFT=yvSTf+EcMsgYHnGyS774OLqljjYcjil0ZkCgy1g0Aso=; BDUSS=55b2xxaGRqOTdSNE0xVUltZFRRTWdkLXZ0VWtmT2steWw5OHNYUFVDa1FZUkZwSVFBQUFBJCQAAAAAAAAAAAEAAAB0wRMxMTIzNDWwtMqxtPLL4wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABDU6WgQ1OloWk; BDUSS_BFESS=55b2xxaGRqOTdSNE0xVUltZFRRTWdkLXZ0VWtmT2steWw5OHNYUFVDa1FZUkZwSVFBQUFBJCQAAAAAAAAAAAEAAAB0wRMxMTIzNDWwtMqxtPLL4wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABDU6WgQ1OloWk; STOKEN=987012cdf0b7fd13bbd965b4d1c68f53eac6c0e7d6b4535e6d027f648dd4acae; Hm_lvt_182d6d59474cf78db37e0b2248640ea5=1760154644; Hm_lpvt_182d6d59474cf78db37e0b2248640ea5=1760161311; BDCLND=nGNVyBu9MKpWH7zlTs5oUxFdRQXvai300GnFk9MGb1A%3D; PANPSC=15679126166052069121%3Au9Rut0jYI4qaUfTgVmgBplcS2d9ns3O5C61tf8CKQkineeMhfK4oNnqD0KGUduys6WRWG5ljYM%2BmSRyxE8aegpNZSd%2B1SXYsBm6rqQ5QdYLMOw0WWU7tUSqLe%2F%2Bg%2FDWP4i2i1JdCg%2BQ3uGXWluwqDsVi7NXtJ9t339k3FioH%2FAqA1gqZJvix0rmqJrAOClKcX0r3JWTW68Ah7IX89yGaj0Eyueu6wBwEDZC8PXQ9dy48%2FX4Y2hjenzCvC2qz1EInzo%2F1llKJ4P7gQiGlDEks0p8M%2Flyi7z69BCKhGHn%2Fawh9ff8YI35KVRVksnPVbyr%2FTaFwbj9JM%2FCSsVZaqyA8TA%3D%3D; ndut_fmt=DACA0AD3BE7AF69A4F5690C5C8C96C528423560F2C90164DB3A1486ED8014AB7; ab_sr=1.0.1_Yjg0ZTYxNzc2MjUxNzUyMjMzNWYzNzY0ODA5Y2JhMmMxMjZiNDg2MTcyNjM0NGJiY2NkMzkzMjEzMjM0ZTVhMThlYTU5YzE5M2VkODRiYzFjYTRkY2Q4OGI5OWE3ZjgxMzM4NmRhZTMzNjE2MTc0MWJkYTMyZmE5OWRlNGIyZGI3ZDhkZDU5ZjZmMGNhODFhZWFhZmUzODAwZDVhOGZhMTYyNTgzNjk3OWRlMWNhYjUxYjBmMTQyZDZhMzQ4ZGRh"
    private val apiHost = "https://pan.baidu.com"
    private val name = "baidu"
    private val displayName = listOf("百度原画", "百度转码")

    fun canHandle(url: String): Boolean {
        return "pan.baidu" in url
    }

    suspend fun processShareLinks(urls: List<String>): Pair<List<String>, List<String>> {
        if (urls.isEmpty()) return emptyList<String>() to emptyList()

        return coroutineScope {
            val results = urls.map { url ->
                async { processSingleLink(url) }
            }.awaitAll()

            val names = mutableListOf<String>()
            val allVideos = mutableListOf<String>()

            results.forEach { result ->
                if (result != null) {
                    val (avideos, videos) = result
                    names.addAll(displayName)
                    allVideos.add(avideos.joinToString("#"))
                    allVideos.add(videos.joinToString("#"))
                }
            }

            names to allVideos
        }
    }

    private suspend fun processSingleLink(url: String): Pair<List<String>, List<String>>? {
        return try {
            val urlInfo = parseShareUrl(url)
            if (urlInfo.containsKey("error")) return null

            val tokenInfo = getShareToken(urlInfo)
            if (tokenInfo?.containsKey("error") == true) return null

            getAllVideos(tokenInfo!!)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun parseShareUrl(url: String): Map<String, String> {
        var lurl = url
        if ("提取码" in url) lurl = url.replace("提取码:", "?pwd=")

        if ("/share/" !in url) {
            val response = OkHttp.getLocation(url, headers)
            lurl = response ?: ""
        }

        val queryParams = parseQueryParams(lurl)
        val finalUrl = if ("/share/" in url) url.replace("share/init?surl=", "s/1") else url

        return mapOf<String, String>(
            "url" to finalUrl,
            "surl" to (queryParams["surl"]?.firstOrNull() ?: ""),
            "pwd" to (queryParams["pwd"]?.firstOrNull() ?: "")
        )
    }

    private fun getShareToken(urlInfo: Map<String, String>): Map<String, String>? {
        val params = mapOf(
            "t" to System.currentTimeMillis().toString(), "surl" to (urlInfo["surl"] ?: "")
        )

        val data = mapOf("pwd" to (urlInfo["pwd"] ?: ""))

        val response = OkHttp.post(
            "$apiHost/share/verify?t=${params["t"]}&surl=${params["surl"]}", data, headers
        )

        // if ("error" in response) return response
        val json = Json.safeObject(response.body)


        val randsk = json.asJsonObject.get("randsk").asString ?: return mapOf("error" to "获取randsk失败")

        return mapOf(
            "yurl" to (urlInfo["url"]?.split("s/")?.last()?.split("?")?.first() ?: ""),
            "randsk" to randsk,
            "surl" to (urlInfo["surl"] ?: "")
        )
    }

    private suspend fun getAllVideos(tokenInfo: Map<String, String>): Pair<List<String>, List<String>> {
        val videos = mutableListOf<String>()
        val avideos = mutableListOf<String>()
        val seenFolders = mutableSetOf<String>()
        val pendingFolders = mutableListOf<Map<String, Any>>()

        try {
            // 处理根目录
            var currentPage = 1
            var uk = ""
            var shareid = ""

            while (true) {
                val rootFolder = mutableMapOf(
                    "surl" to tokenInfo["surl"]!!,
                    "randsk" to tokenInfo["randsk"]!!,
                    "page" to currentPage,
                    "is_root" to true
                )

                val rootResult = getFolderContents(rootFolder)
                // if ("error" in rootResult) break
                val data = rootResult?.asJsonObject

                val items = data?.get("list")?.asJsonArray ?: break

                if (items.isEmpty) break

                // 第一页获取uk和shareid
                if (currentPage == 1) {
                    uk = data["uk"]?.toString() ?: ""
                    shareid = data["share_id"]?.toString() ?: ""
                    if (uk.isEmpty() || shareid.isEmpty()) return emptyList<String>() to emptyList()
                }

                // 处理items
                items.forEach { item ->
                    if (item.asJsonObject["isdir"].asInt == 1) {
                        val folderPath = "/sharelink$uk-$shareid/${item.asJsonObject["server_filename"].asString}"
                        if (folderPath !in seenFolders) {
                            seenFolders.add(folderPath)
                            pendingFolders.add(
                                mapOf(
                                    "surl" to tokenInfo["surl"]!!,
                                    "randsk" to tokenInfo["randsk"]!!,
                                    "uk" to uk,
                                    "shareid" to shareid,
                                    "dir" to folderPath,
                                    "page" to 1
                                )
                            )
                        }
                    } else if (isVideoFile(item.asJsonObject["server_filename"].asString ?: "")) {
                        addVideo(item.asJsonObject, uk, shareid, tokenInfo, avideos, videos)
                    }
                }

                if (items.size() < 9999) break
                currentPage++
            }

            // 处理子文件夹
            while (pendingFolders.isNotEmpty()) {
                val currentBatch = pendingFolders.toList()
                pendingFolders.clear()

                val results = currentBatch.map { folderInfo ->
                    coroutineScope { async { getFolderContents(folderInfo) } }
                }.awaitAll()

                results.forEachIndexed { i, result ->
                    val folderInfo = currentBatch[i]
                    //if ("error" in result) return@forEachIndexed

                    val items = result?.asJsonObject?.get("list")?.asJsonArray ?: return@forEachIndexed

                    items.forEach { item ->
                        if (item.asJsonObject["isdir"].asInt == 1) {
                            val folderPath = item.asJsonObject["path"].asString ?: ""
                            if (folderPath !in seenFolders) {
                                seenFolders.add(folderPath)
                                pendingFolders.add(
                                    mapOf(
                                        "surl" to tokenInfo["surl"]!!,
                                        "randsk" to tokenInfo["randsk"]!!,
                                        "uk" to folderInfo["uk"]!!,
                                        "shareid" to folderInfo["shareid"]!!,
                                        "dir" to folderPath,
                                        "page" to 1
                                    )
                                )
                            }
                        } else if (isVideoFile(item.asJsonObject["server_filename"].asString ?: "")) {
                            addVideo(
                                item.asJsonObject,
                                folderInfo["uk"]?.toString() ?: "",
                                folderInfo["shareid"]?.toString() ?: "",
                                tokenInfo,
                                avideos,
                                videos
                            )
                        }
                    }

                    if (items.size() >= 9999) {
                        pendingFolders.add(folderInfo.toMutableMap().apply {
                            this["page"] = (this["page"] as Int) + 1
                        })
                    }
                }
            }

            return avideos to videos
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList<String>() to emptyList()
        }
    }

    private fun isVideoFile(string: String): Boolean {

        return string.substringAfterLast(".").lowercase(Locale.ROOT) in MEDIA
    }

    private fun addVideo(
        item: JsonObject,
        uk: String,
        shareid: String,
        tokenInfo: Map<String, String>,
        avideos: MutableList<String>,
        videos: MutableList<String>
    ) {
        val sizeStr = formatSize(item["size"].asLong)
        val name = item["server_filename"] ?: ""

        val originalData = mapOf(
            "uk" to uk,
            "shareid" to shareid,
            "fid" to item["fs_id"],
            "randsk" to tokenInfo["randsk"],
            "pname" to name,
            "qtype" to "original"
        )

        val previewData = mapOf(
            "uk" to uk,
            "fid" to item["fs_id"],
            "shareid" to shareid,
            "surl" to tokenInfo["yurl"],
            "pname" to name,
            "qtype" to "preview"
        )

        avideos.add(
            "[$sizeStr]$name$${
                Util.base64Encode(Json.toJson(originalData).toByteArray())
            }"
        )
        videos.add(
            "[$sizeStr]$name$${
                Util.base64Encode(Json.toJson(previewData).toByteArray())
            }"
        )
    }

    private fun getFolderContents(folderInfo: Map<String, Any>): JsonObject? {
        val params = if (folderInfo.containsKey("dir")) {
            mapOf(
                "uk" to folderInfo["uk"]!!.toString(),
                "shareid" to folderInfo["shareid"]!!.toString(),
                "page" to folderInfo["page"].toString(),
                "num" to "9999",
                "dir" to folderInfo["dir"]!!.toString(),
                "desc" to "0",
                "order" to "name",
            )
        } else {
            mapOf(
                "page" to folderInfo["page"].toString(),
                "num" to "9999",
                "shorturl" to folderInfo["surl"]!!.toString(),
                "root" to "1",
                "desc" to "0",
                "order" to "name",
            )
        }


        val tempHeader = headers.toMutableMap()
        tempHeader.put("Cookie", "BDCLND=${folderInfo["randsk"]}")
        val result = OkHttp.string("$apiHost/share/list", params, tempHeader)
        return Json.safeObject(result)

    }

    private fun parseQueryParams(url: String): Map<String, List<String>> {
        val query = url.substringAfter(
            "?"
        ).substringBefore('#')
        return query.split('&').associate {
            val (key, value) = it.split('=', limit = 2)
            key to listOf(value)
        }
    }

    private fun formatSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        return "%.1f %s".format(bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }


    fun getBdUid(): String? {/* if (Cache.get("baidu:uid") != null) {
             return Cache.get("baidu:uid") as? String
         }*/
        val tempHeader = headers.toMutableMap()
        tempHeader.put("Cookie", cookies)
        try {
            val response = OkHttp.string(

                "https://mbd.baidu.com/userx/v1/info/get?appname=baiduboxapp&fields=%20%20%20%20%20%20%20%20%5B%22bg_image%22,%22member%22,%22uid%22,%22avatar%22,%20%22avatar_member%22%5D&client&clientfrom&lang=zh-cn&tpl&ttt",
                emptyMap<String, String>(),
                tempHeader

            )

            val responseJson = Json.safeObject(response)
            val user = responseJson["data"].asJsonObject

            val fields = user?.get("fields")?.asJsonObject

            val uidValue = fields?.get("uid")?.asString

            if (uidValue != null) {
                // Cache.set("baidu:uid", uidValue)
                return uidValue
            } else {
                throw Exception("Failed to retrieve UID from Baidu Drive.")
            }
        } catch (e: Exception) {
            println("获取百度网盘用户ID失败: ${e.message}")
            return ""
        }
    }

    suspend fun _getSign(videoData: JsonObject): Pair<String, String> {
        val tempHeader = headers.toMutableMap()
        tempHeader.put("Cookie", cookies)
        val response: String? = OkHttp.string(
            "${apiHost}/share/tplconfig", mapOf<String, String>(
                "surl" to videoData["surl"].asString, "fields" to "cfrom_id,Espace_info,card_info,sign,timestamp"
            ),

            tempHeader
        )
        return try {
            val data = Json.safeObject(response)["data"].asJsonObject

            data["sign"].asString to data["timestamp"].asString
        } catch (_: Exception) {
            "" to ""
        }
    }


    suspend fun _getDownloadUrl(videoData: JsonObject): String {
        return try {
            var cookie = ""
            val BDCLND = "BDCLND=" + videoData["randsk"].asString
// 更新Cookie中的BDCLND值
            if (!this.cookies.contains("BDCLND")) {
                cookie = this.cookies + ";" + BDCLND

            } else {
                cookie = this.cookies.split(";").joinToString(";") {
                    if (it.contains("BDCLND")) {
                        BDCLND
                    } else {
                        it
                    }
                }

            }


            val transferHeaders = mapOf(
                "User-Agent" to "Android",
                "Connection" to "Keep-Alive",
                "Content-Type" to "application/x-www-form-urlencoded",
                "Accept-Language" to "zh-CN,zh;q=0.8",
                "charset" to "UTF-8",
                "Referer" to "https://pan.baidu.com",
                "Cookie" to cookie
            )


            val data =
                "from=${videoData["uk"].asString}&shareid=${videoData["shareid"].asString}&ondup=newcopy&path=/&fsidlist=[${videoData["fid"].asString}]"

            var to = ""
            for (i in 1..30) {


                val response =
                    OkHttp.post("${apiHost}/share/transfer?${data}", emptyMap<String, String>(), transferHeaders)

                val result = Json.safeObject(response.body)

                try {
                    to =  (result["extra"].asJsonObject)["list"] .asJsonArray[0].asJsonObject["to"].asString
                   // videoData["to"] = to
                    if (to.isNotEmpty()) {
                        println("成功转存文件到: $to")
                        break
                    }
                } catch (e: Exception) {
                    println("解析转存响应出错: ${e.message}")
                    continue
                }
            }

            if (to.isEmpty()) {
                println("转存文件失败，无法获取下载链接")
                return ""
            }

            val mediaInfoHeaders = mapOf(

                "User-Agent" to "netdisk;1.4.2;22021211RC;android-android;12;JSbridge4.4.0;jointBridge;1.1.0;",
                "Connection" to "Keep-Alive",
                "Accept-Language" to "zh-CN,zh;q=0.8",
                "charset" to "UTF-8",
                "Cookie" to cookie
            ).toMutableMap()


            val mediaInfoParams = mapOf(
                "type" to "M3U8_FLV_264_480", "path" to "/$to", "clienttype" to "80", "origin" to "dlna"
            )


            val mediaInfoResponse: String? = OkHttp.string(
                "${apiHost}/api/mediainfo", mediaInfoParams, mediaInfoHeaders
            )
            val responseJson = Json.safeObject(mediaInfoResponse)
            val info = responseJson["info"].asJsonObject
            val downloadUrl = info["dlink"].asString
            println("获取到下载链接: $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            println("获取下载链接过程中出错: ${e.message}")
            e.printStackTrace()
            ""
        }
    }

    suspend fun     getVideoUrl(videoData: JsonObject): Map<String, Any> {
        return try {
            val bdUid = getBdUid()
            println("获取百度网盘用户ID: $bdUid")
            val plist = mutableListOf<Pair<String, String>>()
            if (videoData["qtype"].asString == "original") {
                var app = true
                var headersApp = mapOf("User-Agent" to "netdisk;P2SP;2.2.91.136;android-android;")

                var downloadUrl = _getAppDownloadUrl(videoData)
                if (downloadUrl.isEmpty()) {
                    app = false
                    headersApp = mapOf(

                        "User-Agent" to "netdisk;1.4.2;22021211RC;android-android;12;JSbridge4.4.0;jointBridge;1.1.0;"
                    )

                    downloadUrl = _getDownloadUrl(videoData)
                }
                if (downloadUrl.isNotEmpty()) {
                    plist.add(Pair("原画", ProxyVideo.buildCommonProxyUrl(downloadUrl, headersApp)))
                    val result = mapOf(
                        "parse" to 0, "url" to plist, "header" to headersApp.toString()
                    )
                    // 使用线程而不是asyncio任务
                    Thread {
                        // _runDeleteInThread(videoData["to"] as? String)
                    }.start()
                    result
                } else {
                    _handleError
                }
            } else {
                val (sign, time) = _getSign(videoData)
                if (sign.isEmpty() || time.isEmpty()) {
                    return _handleError
                }
                val plist = _getPlayList(videoData, sign, time)/* val headers = Headers.build {
                     append(HttpHeaders.UserAgent, USER_AGENT)
                     append(HttpHeaders.Cookie, dictToCookieStr(cookies))
                 }*/
                mapOf(
                    "parse" to 0, "url" to plist, "header" to headers.toString()
                )
            }
        } catch (e: Exception) {
            println("获取播放链接失败: ${e.message}")
            _handleError
        }
    }

    private suspend fun _getAppDownloadUrl(videoData: JsonObject): String {
        return try {
            val headers = mapOf<String, String>(

                "User-Agent" to "netdisk;P2SP;2.2.91.136;android-android;",
                "Connection" to "Keep-Alive",
                "Accept-Language" to "zh-CN,zh;q=0.8",
                "charset" to "UTF-8",
                "cookie" to cookies
            )
            val uid = this.getBdUid()
            val t = System.currentTimeMillis()
            val params = mapOf<String, String>(
                "shareid" to videoData["shareid"].asString,
                "uk" to videoData["uk"].asString,
                "fid" to videoData["fid"].asString,
                "sekey" to unquote(videoData["randsk"].asString),
                "origin" to "dlna",
                "devuid" to "73CED981D0F186D12BC18CAE1684FFD5|VSRCQTF6W",
                "clienttype" to "1",
                "channel" to "android_12_zhao_bd-netdisk_1024266h",
                "version" to "11.30.2",
                "time" to t.toString()
            ).toMutableMap()

            val randstr =
                this.sha1(this.sha1(Util.findByRegex("BDUSS=(.+?);",cookies,1)) + uid + "ebrcUYiuxaZv2XGu7KIYKxUrqfnOfpDF$t${params["devuid"]}11.30.2ae5821440fab5e1a61a025f014bd8972")

            params.put("rand", sha1(randstr))

            val response = OkHttp.string(
                "${apiHost}/share/list", params, headers
            )


            /* val url = response["data"] as Map<String, Any>
             val list = url["list"] as List<Map<String, Any>>
             val dlink = list[0]["dlink"] as String

             val pDataResponse = client.get(dlink) {
                 headers { this@_getAppDownloadUrl.headers.forEach { name, value -> append(name, value) } }
                 cookies?.let { setCookie(it) }
                 followRedirects = false
                 timeout.socketTimeoutMillis = 10000
             }

             val pUrl = pDataResponse.headers[HttpHeaders.Location]?.toString()
             pUrl ?: dlink*/
            ""
        } catch (e: Exception) {
            println("获取下载链接失败: ${e.message}")
            ""
        }
    }

    private fun _getPlayList(videoData: JsonObject, sign: String, time: String): List<Pair<String, String>> {
        val hz = listOf("1080P", "720P", "480P")
        val plist = mutableListOf<Pair<String, String>>()

        for (quality in hz) {
            val url =
                ("${apiHost}/share/streaming?" + "uk=${videoData["uk"]}&" + "fid=${videoData["fid"]}&" + "sign=$sign&" + "timestamp=$time&" + "shareid=${videoData["shareid"]}&" + "type=M3U8_AUTO_${
                    quality.replace(
                        "P", ""
                    )
                }")
            plist.add(Pair(quality, url))
        }

        return plist
    }/*

      private suspend fun _deleteTransferFile(filePath: String) {
          try {
              val url = "$API_HOST/api/filemanager"
              val params = Parameters.build {
                  append("opera", "delete")
                  append("clienttype", "1")
              }
              val deleteHeaders = Headers.build {
                  append(HttpHeaders.UserAgent, "Android")
                  append(HttpHeaders.Connection, CONNECTION)
                  append(HttpHeaders.AcceptEncoding, "br,gzip")
                  append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                  append(HttpHeaders.AcceptLanguage, ACCEPT_LANGUAGE)
                  append("charset", CHARSET)
              }
              val data = "filelist=[\"$filePath\"]"

              val response: HttpResponse = client.post(url) {
                  parameters(params)
                  headers { this@_deleteTransferFile.deleteHeaders.forEach { name, value -> append(name, value) } }
                  cookies?.let { setCookie(it) }
                  body = data
                  timeout.socketTimeoutMillis = 10000
              }

              val result = try {
                  response.receive<Map<String, Any>>()
              } catch (e: Exception) {
                  response.readBytes().decodeToString()
              }

              println("删除文件响应: $result")
              println("响应状态码: ${response.status.value}")
          } catch (e: Exception) {
              println("删除文件出错: ${e.message}")
              e.printStackTrace()
          }
      }

      private suspend fun _delayedDeleteFile(to: String) {
          try {
              println("开始延迟删除文件: $to")
              delay(2000)
              println("开始执行删除操作: $to")
              withContext(Dispatchers.IO) {
                  _deleteTransferFile(to)
              }
          } catch (e: Exception) {
              println("延迟删除文件出错: ${e.message}")
          }
      }

      private fun _runDeleteInThread(to: String?) {
          try {
              println("开始延迟删除文件(线程): $to")
              Thread.sleep(2000)
              println("开始执行删除操作(线程): $to")

              runBlocking {
                  _deleteTransferFile(to!!)
              }

              println("删除操作完成: $to")
          } catch (e: Exception) {
              println("线程中删除文件出错: ${e.message}")
              e.printStackTrace()
          }
      }



      private fun dictToCookieStr(cookieDict: Map<String, String>?): String {
          return cookieDict?.map { "${it.key}=${it.value}" }?.joinToString("; ") ?: ""
      }*/

    private fun unquote(encoded: String): String {
        return encoded.replace("%([0-9A-Fa-f]{2})".toRegex()) { match ->
            Integer.parseInt(match.groupValues[1], 16).toChar().toString()
        }
    }

    private fun sha1(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-1").digest(input.toByteArray())
        return Util.base64Encode(bytes)
    }

    private val _handleError = mapOf(
        "parse" to 1, "msg" to "Error retrieving video URL"
    )

    private fun threadUrl(url: String, threads: Int): String {
        // Implement the logic for threading URL processing here
        return url
    }


}
