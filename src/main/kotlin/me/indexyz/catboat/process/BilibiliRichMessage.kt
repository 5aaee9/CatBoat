package me.indexyz.catboat.process

import com.alibaba.fastjson.JSONObject
import me.indexyz.catboat.utils.getBVFromUrl
import me.indexyz.catboat.utils.parseUrl
import me.indexyz.catboat.utils.queryVideo
import net.mamoe.mirai.message.MessageEvent

object BilibiliRichMessage {
    fun isTargetMessage(message: JSONObject): Boolean {
        if (!message.containsKey("appID")) {
            return false
        }

        if (message.getString("appID") == "100951776") {
            return true
        }

        return false
    }

    suspend fun onMessage(message: JSONObject, event: MessageEvent) {
        if (!message.containsKey("meta")) {
            return
        }

        val meta = message.getJSONObject("meta")

        if (!meta.containsKey("detail_1")) {
            return
        }

        val detail = meta.getJSONObject("detail_1")
        val url = detail.getString("qqdocurl")

        val bv = parseUrl(url)?.let { getBVFromUrl(it) }

        bv?.let {
            val video = queryVideo(it) ?: return
            val str = video.ReadableString()
            event.reply(str)
        }
    }
}