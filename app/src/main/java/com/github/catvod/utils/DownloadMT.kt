package com.github.catvod.utils

import com.github.catvod.crawler.SpiderDebug
import com.github.catvod.net.OkHttp
import com.github.catvod.utils.ProxyVideo.getInfo
import com.github.catvod.utils.ProxyVideo.getMimeType
import com.github.catvod.utils.ProxyVideo.parseRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Response
import org.apache.commons.lang3.StringUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.math.min

object DownloadMT {
    val THREAD_NUM: Int = Runtime.getRuntime().availableProcessors() * 2

    private val infos = mutableMapOf<String, Array<Any>>();

    fun proxyMultiThread(url: String, headers: Map<String, String>): Array<out Any?>? =
        runBlocking {
            proxy(url, headers)
        }

    suspend fun proxy(url: String, headers: Map<String, String>): Array<out Any?>? {

        /*  val service = Executors.newFixedThreadPool(THREAD_NUM)*/
        SpiderDebug.log("--proxyMultiThread: THREAD_NUM " + THREAD_NUM)


        try {
            //缓存，避免每次都请求total等信息


            var info = infos[url]
            if (info == null) {
                infos.clear()
                info = getInfo(url, headers)
                infos[url] = info
            }

            val code = info?.get(0) as Int
            SpiderDebug.log("-----------code:" + code)
            if (code != 206) {
                return proxy(url, headers)
            }
            val resHeader = info[3] as MutableMap<String, String>
            val contentRange =
                if (StringUtils.isAllBlank(resHeader["Content-Range"])) resHeader["content-range"] else resHeader["Content-Range"]

            SpiderDebug.log("--contentRange:$contentRange")
            //文件总大小
            val total = StringUtils.split(contentRange, "/")[1]
            SpiderDebug.log("--文件总大小:$total")

            //如果文件太小，也不走代理
            /* if (total.toLong() < 1024 * 1024 * 100) {
                 return proxy(url, headers)
             }*/
            var range =
                if (StringUtils.isAllBlank(headers["range"])) headers["Range"] else headers["range"]
            if (StringUtils.isAllBlank(range)) range = "bytes=0-";
            SpiderDebug.log("---proxyMultiThread,Range:$range")
            val rangeObj = parseRange(
                range!!
            )
            //没有range,无需分割

            val partList = generatePart(rangeObj, total)

            // 存储执行结果的List
            val jobs = mutableListOf<Job>()
            val channels = List(THREAD_NUM) { Channel<ByteArray>() }
            for ((index, part) in partList.withIndex()) {


                val newRange = "bytes=" + part[0] + "-" + part[1]
                SpiderDebug.log("下载开始;newRange:$newRange")

                val headerNew: MutableMap<String, String> = HashMap(headers)

                headerNew["range"] = newRange
                headerNew["Range"] = newRange
                jobs += CoroutineScope(Dispatchers.IO).launch {
                    val res = downloadRange(url, headerNew)

                    if (res != null) {
                        val buffer = ByteArray(1024)
                        var bytesRead: Int = 0

                        while (res.body()?.byteStream()?.read(buffer).also {
                                if (it != null) {
                                    bytesRead = it
                                }
                            } != -1) {
                            // 处理读取的数据
                            channels[index].send(buffer.copyOfRange(0, bytesRead))

                        }
                        channels[index].close() // 发送完成后关闭通道
                        SpiderDebug.log("---第" + index + "块下载完成" + ";Content-Range:" + res.headers()["Content-Range"])
                    }
                }
            }

            val outputStream = ByteArrayOutputStream();
            var pipedInputStream: ByteArrayInputStream? = null
            var contentType: String? = ""

            val res = CoroutineScope(Dispatchers.Default).async {
                repeat(jobs.size) { index ->

                    for (bytes in channels[index]) {
                        // 处理读取的数据
                        outputStream.write(bytes);
                    }

                }
                // 等待所有下载完成
                jobs.joinAll()
            }
            res.await()

            //    SpiderDebug.log(" ++proxy res data:" + Json.toJson(response.body()));
            contentType = resHeader["Content-Type"]
            if (StringUtils.isAllBlank(contentType)) {
                contentType = resHeader["content-type"]
            }

            if (StringUtils.isAllBlank(contentType) && StringUtils.isNoneBlank(resHeader["Content-Disposition"])) {
                contentType = getMimeType(resHeader["Content-Disposition"])
            }


            /* respHeaders.put("Access-Control-Allow-Credentials", "true");
        respHeaders.put("Access-Control-Allow-Origin", "*");*/
            resHeader["Content-Length"] =
                (partList[THREAD_NUM - 1][1] - partList[0][0] + 1).toString()
            resHeader.remove("content-length")

            resHeader["Content-Range"] = String.format(
                "bytes %s-%s/%s", partList[0][0], partList[THREAD_NUM - 1][1], total
            )
            resHeader.remove("content-range")

            SpiderDebug.log("----proxy res contentType:$contentType")
            //   SpiderDebug.log("++proxy res body:" + response.body());
            SpiderDebug.log("----proxy res respHeaders:" + Json.toJson(resHeader))
            pipedInputStream = ByteArrayInputStream(outputStream.toByteArray());
            outputStream.close()

            return arrayOf(206, contentType, pipedInputStream, resHeader)


        } catch (e: Exception) {
            SpiderDebug.log("proxyMultiThread error:" + e.message)
            e.printStackTrace()
            return null
        }
    }

    fun generatePart(rangeObj: Map<String?, String>, total: String): List<LongArray> {
        val totalSize = total.toLong()
        //超过10GB，分块是80Mb，不然是16MB
        val partSize =
            if (totalSize > 8L * 1024L * 1024L * 1024L * 10L) 1024 * 1024 * 8 * 10L else 1024 * 1024 * 8 * 2L

        var start = rangeObj["start"]!!.toLong()
        var end =
            if (StringUtils.isAllBlank(rangeObj["end"])) start + partSize else rangeObj["end"]!!
                .toLong()


        end = min(end.toDouble(), (totalSize - 1).toDouble()).toLong()
        val length = end - start + 1

        val size = length /THREAD_NUM
        val partList: MutableList<LongArray> = ArrayList()
        for (i in 0..<THREAD_NUM) {
            val partEnd = min((start + size).toDouble(), end.toDouble()).toLong()

            partList.add(longArrayOf(start, partEnd))
            start = partEnd + 1
        }
        return partList
    }

    private fun downloadRange(
        url: String, headerNew: MutableMap<String, String>
    ): Response? = OkHttp.newCall(url, headerNew)
}