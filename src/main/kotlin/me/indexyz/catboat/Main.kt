package me.indexyz.catboat

import com.alibaba.fastjson.JSONObject
import me.indexyz.catboat.process.BilibiliRichMessage
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.join
import net.mamoe.mirai.message.data.RichMessage

val username = System.getenv("BOT_USERNAME").toLong()
val password = System.getenv("BOT_PASSWORD")!!

suspend fun main() {
    val bot = Bot(username, password) {
        fileBasedDeviceInfo("device.json")
    }.alsoLogin()

    bot.messageDSL()

    bot.join()
}

fun Bot.messageDSL() {
    this.subscribeMessages {
        has<RichMessage> {
            val data = JSONObject.parseObject(it.content)
            if (BilibiliRichMessage.isTargetMessage(data)) {
                BilibiliRichMessage.onMessage(data, this)
            }
        }
    }
}
