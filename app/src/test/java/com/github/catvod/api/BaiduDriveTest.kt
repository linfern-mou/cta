package com.github.catvod.api

import com.github.catvod.utils.Json
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BaiduDriveTest {


    @Test
    fun getShareList() {

        runBlocking {
            val reslut =
                BaiduDrive.processShareLinks(listOf("https://pan.baidu.com/s/1Ov0S6S7rqnyW_S3AondhmQ?pwd=8888"))
            System.out.println(Json.toJson(reslut))
        }
    }

    @Test
    fun getBdUid() {

        runBlocking {
            val reslut = BaiduDrive.getBdUid()
            System.out.println(reslut)
        }
    }

    @Test
    fun _getSign() {
        val jsonStr =
            com.github.catvod.utils.Util.base64Decode("eyJ1ayI6IjExMDI4NjAxODI4OTgiLCJmaWQiOjE1NTY5Mzk0MDE4MjQ2NCwic2hhcmVpZCI6IjUzNDc0MDY5NzI0Iiwic3VybCI6IjFmMHk2MFJrWWNTc3A0RXdXb2xLZ0pnIiwicG5hbWUiOiIwNeWbveivrS00Sy7pq5jnoIHnjocubXA0IiwicXR5cGUiOiJwcmV2aWV3In0=")
        val obj = Json.safeObject(jsonStr)
        runBlocking {
            val reslut = BaiduDrive._getSign(obj)
            System.out.println(reslut)
        }


    }

    @Test
    fun getVideoUrl() {/* val jsonStr =
             com.github.catvod.utils.Util.base64Decode("eyJ1ayI6IjExMDI4NjAxODI4OTgiLCJmaWQiOjE1NTY5Mzk0MDE4MjQ2NCwic2hhcmVpZCI6IjUzNDc0MDY5NzI0Iiwic3VybCI6IjFmMHk2MFJrWWNTc3A0RXdXb2xLZ0pnIiwicG5hbWUiOiIwNeWbveivrS00Sy7pq5jnoIHnjocubXA0IiwicXR5cGUiOiJwcmV2aWV3In0=")
         val obj = Json.safeObject(jsonStr)
         runBlocking {
             val reslut = yunDrive?.getVideoUrl(obj)
             System.out.println(reslut)
         }*/
        val jsonStr =
            com.github.catvod.utils.Util.base64Decode("eyJ1ayI6IjExMDMyNzkxMjIzNDEiLCJzaGFyZWlkIjoiMjk1NzE4ODcyOTgiLCJmaWQiOjMxOTM5NTUxMTQyOTU4MCwicmFuZHNrIjoidkd4WXh4TVBRcXpucXZialRQeUQ2Q1FFT2VqemtJWmdFdXV2OUQ1Y3R6TSUzRCIsInBuYW1lIjoiRHJhZ29uIEJhbGwgREFJTUEuUzAxRTAxLjIwMjQuMTA4MHAuQ1IuV0VCLURMLngyNjQuQUFDLm1rdiIsInF0eXBlIjoib3JpZ2luYWwifQ==")
        val obj = Json.safeObject(jsonStr)
        runBlocking {
            val reslut = BaiduDrive.getVideoUrl(obj, "BD原画1")
            System.out.println(reslut)
        }
    }

    @Test
    fun createSaveDir() {/* val jsonStr =
             com.github.catvod.utils.Util.base64Decode("eyJ1ayI6IjExMDI4NjAxODI4OTgiLCJmaWQiOjE1NTY5Mzk0MDE4MjQ2NCwic2hhcmVpZCI6IjUzNDc0MDY5NzI0Iiwic3VybCI6IjFmMHk2MFJrWWNTc3A0RXdXb2xLZ0pnIiwicG5hbWUiOiIwNeWbveivrS00Sy7pq5jnoIHnjocubXA0IiwicXR5cGUiOiJwcmV2aWV3In0=")
         val obj = Json.safeObject(jsonStr)
         runBlocking {
             val reslut = yunDrive?.getVideoUrl(obj)
             System.out.println(reslut)
         }*/

        runBlocking {
            val reslut = BaiduDrive.createSaveDir()
            System.out.println(reslut)
        }
    }


}