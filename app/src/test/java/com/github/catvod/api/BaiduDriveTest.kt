package com.github.catvod.api

import com.github.catvod.utils.Json
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BaiduDriveTest {
    var yunDrive: BaiduDrive? = null

    @Before
    fun setUp() {
        yunDrive = BaiduDrive()
    }


    @Test
    fun getShareList() {

        runBlocking {
            val reslut = yunDrive?.processShareLinks(listOf("https://pan.baidu.com/s/1f0y60RkYcSsp4EwWolKgJg?pwd=c26q"))
            System.out.println(Json.toJson(reslut))
        }
    }

    @Test
    fun getBdUid() {

        runBlocking {
            val reslut = yunDrive?.getBdUid()
            System.out.println(reslut)
        }
    }

    @Test
    fun _getSign() {
        val jsonStr =
            com.github.catvod.utils.Util.base64Decode("eyJ1ayI6IjExMDI4NjAxODI4OTgiLCJmaWQiOjE1NTY5Mzk0MDE4MjQ2NCwic2hhcmVpZCI6IjUzNDc0MDY5NzI0Iiwic3VybCI6IjFmMHk2MFJrWWNTc3A0RXdXb2xLZ0pnIiwicG5hbWUiOiIwNeWbveivrS00Sy7pq5jnoIHnjocubXA0IiwicXR5cGUiOiJwcmV2aWV3In0=")
        val obj = Json.safeObject(jsonStr)
        runBlocking {
            val reslut = yunDrive?._getSign(obj)
            System.out.println(reslut)
        }
    }@Test
    fun getVideoUrl() {
       /* val jsonStr =
            com.github.catvod.utils.Util.base64Decode("eyJ1ayI6IjExMDI4NjAxODI4OTgiLCJmaWQiOjE1NTY5Mzk0MDE4MjQ2NCwic2hhcmVpZCI6IjUzNDc0MDY5NzI0Iiwic3VybCI6IjFmMHk2MFJrWWNTc3A0RXdXb2xLZ0pnIiwicG5hbWUiOiIwNeWbveivrS00Sy7pq5jnoIHnjocubXA0IiwicXR5cGUiOiJwcmV2aWV3In0=")
        val obj = Json.safeObject(jsonStr)
        runBlocking {
            val reslut = yunDrive?.getVideoUrl(obj)
            System.out.println(reslut)
        }*/ val jsonStr =
            com.github.catvod.utils.Util.base64Decode("eyJ1ayI6IjExMDI4NjAxODI4OTgiLCJzaGFyZWlkIjoiNTM0NzQwNjk3MjQiLCJmaWQiOjgzNDA3NTIyODU4MjM4NywicmFuZHNrIjoibkdOVnlCdTlNS3BXSDd6bFRzNW9VeEZkUlFYdmFpMzAwR25GazlNR2IxQSUzRCIsInBuYW1lIjoiMDEgNEsubXA0IiwicXR5cGUiOiJvcmlnaW5hbCJ9")
        val obj = Json.safeObject(jsonStr)
        runBlocking {
            val reslut = yunDrive?.getVideoUrl(obj)
            System.out.println(reslut)
        }
    }

}