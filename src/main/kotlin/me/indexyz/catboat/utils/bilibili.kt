package me.indexyz.catboat.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36"
const val USER_INFO_API = "https://api.bilibili.com/x/space/acc/info?mid=%d"
const val USER_STAT_API = "https://api.bilibili.com/x/relation/stat?vmid=%d"
const val VIDEO_INFO_API = "https://api.bilibili.com/x/web-interface/view?bvid=%s"
const val VIDEO_SEARCH_API = "https://api.bilibili.com/x/web-interface/search/type?highlight=0&search_type=video&keyword=%s"

fun cleanUrl(url: String): String {
    val index = url.indexOf("?")
    if (index >= 0) {
        return url.substring(0, index)
    }
    return url
}

fun parseUrl(url: String): String? {
    val res = Fuel.get(url)
        .header(Headers.USER_AGENT, USER_AGENT)
        .allowRedirects(false)
        .response()

    if (!res.second.headers.containsKey("Location")) {
        return null
    }

    return cleanUrl(res.second.headers["Location"].stream().findFirst().get())
}

fun getBVFromUrl(url: String): String? {
    val indexOf = url.indexOf("/video/")

    if (indexOf >= 0) {
        return url.substring(indexOf + "/video/".length)
    }

    return null
}

data class BilibiliUserInfo(
    // 用户 ID
    val id: Int,
    // 用户名
    val name: String,
    // 关注数量
    val follower: Int
)

data class BilibiliVideoInfo(
    // 标题
    val title: String,
    // 发布日期
    val publishDate: Int,
    // AV 号
    val avNumber: Int,
    // BV 号
    val bvNumber: String,
    // 播放数
    val view: Int,
    // 弹幕数
    val danmaku: Int,
    // 回复数
    val reply: Int,
    // 点赞
    val like: Int,
    // 投币
    val coin: Int,
    // 收藏
    val favorite: Int,
    // 发布者
    val owner: BilibiliUserInfo
) {
    fun ReadableString(): String {
        return "Bilibili 视频信息\n\n" +
                this.title +
                "\nUP 主: %s(关注: %d)\n".format(this.owner.name, this.owner.follower) +
                "\n" +
                "播放: %d, 收藏: %d\n".format(this.view, this.favorite) +
                "硬币: %d, 点赞: %d, 弹幕: %d".format(this.coin, this.like, this.danmaku) +
                "\n\n" +
                "av%d / %s".format(this.avNumber, this.bvNumber) +
                "\n\n" +
                "https://www.bilibili.com/video/%s".format(this.bvNumber)
    }
}


fun queryUserInfo(userId: Int): BilibiliUserInfo? {
    val userInfoRes = USER_INFO_API.format(userId)
        .httpGet()
        .header(Headers.USER_AGENT, USER_AGENT)
        .responseString()

    val userInfo = JSONObject.parseObject(userInfoRes.third.get())

    if (userInfo.getInteger("code") != 0) {
        return null
    }

    val userStatRes = USER_STAT_API.format(userId)
        .httpGet()
        .header(Headers.USER_AGENT, USER_AGENT)
        .responseString()

    val userStat = JSONObject.parseObject(userStatRes.third.get())

    if (userStat.getInteger("code") != 0) {
        return null
    }

    return BilibiliUserInfo(
        userInfo.getJSONObject("data").getInteger("mid"),
        userInfo.getJSONObject("data").getString("name"),
        userStat.getJSONObject("data").getInteger("follower")
    )
}

fun queryVideo(bvNumber: String): BilibiliVideoInfo? {
    val infoRes = VIDEO_INFO_API.format(bvNumber)
        .httpGet()
        .header(Headers.USER_AGENT, USER_AGENT)
        .responseString()

    val info = JSONObject.parseObject(infoRes.third.get())

    if (info.getInteger("code") != 0) {
        return null
    }

    val data = info.getJSONObject("data")
    val owner = queryUserInfo(data.getJSONObject("owner").getInteger("mid")) ?: return null
    val stat = data.getJSONObject("stat")

    return BilibiliVideoInfo(
        data.getString("title"),
        data.getInteger("pubdate"),
        data.getInteger("aid"),
        data.getString("bvid"),
        stat.getInteger("view"),
        stat.getInteger("danmaku"),
        stat.getInteger("reply"),
        stat.getInteger("like"),
        stat.getInteger("coin"),
        stat.getInteger("favorite"),
        owner
    )
}

fun urlEncode(i: String): String {
    return URLEncoder.encode(i, StandardCharsets.UTF_8.toString())
}

fun searchVideo(title: String): String? {
    val searchRes = VIDEO_SEARCH_API.format(urlEncode(title))
        .httpGet()
        .header(Headers.USER_AGENT, USER_AGENT)
        .responseString()

    val data = JSON.parseObject(searchRes.third.get())

    if (data.getInteger("code") != 0) {
        return null
    }

    val res = (data.getJSONObject("data").getJSONArray("result"))
    if (res.size <= 0) {
        return null
    }
    val video = res.getJSONObject(0)

    return video.getString("bvid")
}
