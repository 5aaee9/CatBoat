package me.indexyz.catboat.process

import com.alibaba.fastjson.JSONObject
import me.indexyz.catboat.utils.getBVFromUrl
import me.indexyz.catboat.utils.parseUrl
import me.indexyz.catboat.utils.queryVideo
import me.indexyz.catboat.utils.searchVideo
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
                    processBV(bv, this)
                }
            }

            Regex("(BV[\\w]{1,10})") finding {
                if (it.groups.isNotEmpty()) {
                    processBV(it.groups[0]!!.value, this)
                }
            }
        }
    }

    private fun isTargetMessage(message: JSONObject): Boolean {
        if (!message.containsKey("appID")) {
            return false
        }

        val appId = message.getString("appID")

        if (appId == "100951776" || appId == "1109937557") {
            return true
        }

        return false
    }

    private suspend fun onMessage(message: JSONObject, event: MessageEvent) {
        if (!message.containsKey("meta")) {
            return
        }

        val meta = message.getJSONObject("meta")

        if (!meta.containsKey("detail_1")) {
            return
        }

        val detail = meta.getJSONObject("detail_1")
        if (detail.containsKey("qqdocurl")) {
            val url = detail.getString("qqdocurl")

            val bv = parseUrl(url)?.let { getBVFromUrl(it) }

            processBV(bv, event)
            return
        }

        val bv = searchVideo(detail.getString("desc"))
        processBV(bv, event)
        // Search video
    }

    private suspend fun processBV(bvNumber: String?, event: MessageEvent) {
        val video = bvNumber?.let { queryVideo(it) } ?: return
        val str = video.ReadableString()
        event.reply(str)
    }
}
