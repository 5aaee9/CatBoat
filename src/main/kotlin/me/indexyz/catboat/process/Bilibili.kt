package me.indexyz.catboat.process

import com.alibaba.fastjson.JSONObject
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import me.indexyz.catboat.utils.getBVFromUrl
import me.indexyz.catboat.utils.parseUrl
import me.indexyz.catboat.utils.queryVideo
import me.indexyz.catboat.utils.searchVideo
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.RichMessage
import javax.inject.Inject

@Module
abstract class BilibiliProcessBindModule {
    @Binds
    @IntoMap
    @StringKey("bilibili")
    internal abstract fun bindBilibiliProcess(command: BilibiliProcess): IProcess
}

@Module
class BilibiliProcess @Inject constructor() : IProcess {
    @Inject lateinit var bot: Bot

    private fun isTargetMessage(message: JSONObject): Boolean {
        if (message.getString("prompt") == "[QQ小程序]哔哩哔哩") {
            return true
        }

        if (!message.containsKey("appID")) {
            return false
        }

        if (message.getString("appID") == "100951776") {
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

    override fun register() {
        this.bot.subscribeMessages {
            has<RichMessage> {
                val data = JSONObject.parseObject(it.content)
                if (this@BilibiliProcess.isTargetMessage(data)) {
                    this@BilibiliProcess.onMessage(data, this)
                }
            }

            Regex("(https://b23\\.tv/[\\w]{6})") finding {
                if (it.groups.isNotEmpty()) {
                    val bv = parseUrl(it.groups[0]!!.value)?.let { it1 -> getBVFromUrl(it1) }
                    this@BilibiliProcess.processBV(bv, this)
                }
            }

            Regex("(BV[\\w]{1,10})") finding {
                if (it.groups.isNotEmpty()) {
                    this@BilibiliProcess.processBV(it.groups[0]!!.value, this)
                }
            }
        }
    }
}
