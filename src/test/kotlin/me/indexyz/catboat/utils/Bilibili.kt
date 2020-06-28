package me.indexyz.catboat.utils

import org.junit.jupiter.api.Test

class BilibiliUtilsTest {
    @Test
    fun `Parse Bilibili URL`() {
        val result = parseUrl("https://b23.tv/UhP9he")
        assert(result == "https://www.bilibili.com/video/BV1Av411z7KG")
    }

    @Test
    fun `Clean Bilibili URL`() {
        val result = cleanUrl("https://www.bilibili.com/video/BV1Av411z7KG?p=1&share_medium=iphone&share_plat=ios&share_source=QQ&share_tag=s_i&timestamp=1593274618&unique_k=UhP9he")
        assert(result == "https://www.bilibili.com/video/BV1Av411z7KG")
    }

    @Test
    fun `Test ParseURL error`() {
        val result = parseUrl("https://b23.tv/2333333333")
        assert(result == null)
    }

    @Test
    fun `Test query user info`() {
        val userInfo = queryUserInfo(2702376)
        assert(userInfo != null)
        assert(userInfo!!.id == 2702376)
        assert(userInfo.name == "Miao_喵鸽")
    }

    @Test
    fun `Test getBVFromUrl`() {
        val res = getBVFromUrl("https://www.bilibili.com/video/BV1Av411z7KG")
        assert(res == "BV1Av411z7KG")
    }

    @Test
    fun `Test search video`() {
        val res = searchVideo("【新人NB神作】李天香永远的东方日鸟传")

        assert(res == "BV1Hs411S7p4")
    }
}
