package me.indexyz.catboat.process

import com.alibaba.fastjson.JSONObject
import me.indexyz.catboat.utils.getBVFromUrl
import me.indexyz.catboat.utils.parseUrl
import me.indexyz.catboat.utils.queryVideo
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.RichMessage

object BilibiliRichMessage {
    fun register(bot: Bot) {
        bot.subscribeMessages {
            has<RichMessage> {
                val data = JSONObject.parseObject(it.content)
                if (BilibiliRichMessage.isTargetMessage(data)) {
                    BilibiliRichMessage.onMessage(data, this)
                }
            }

            Regex("(https://b23\\.tv/[\\w]{6})") finding {
                if (it.groups.isNotEmpty()) {
                    val bv = parseUrl(it.groups[0]!!.value)?.let { it1 -> getBVFromUrl(it1) }
                    bv?.let { it1 ->
                        val video = queryVideo(it1)
                        if (video != null) {
                            val str = video.ReadableString()
                            this.reply(str)
                        }
                    }
                }
            }
        }
    }

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
