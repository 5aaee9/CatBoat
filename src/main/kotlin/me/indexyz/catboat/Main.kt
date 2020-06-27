package me.indexyz.catboat

import me.indexyz.catboat.process.BilibiliRichMessage
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.join

val username = System.getenv("BOT_USERNAME").toLong()
val password = System.getenv("BOT_PASSWORD")!!

suspend fun main() {
    val bot = Bot(username, password) {
        fileBasedDeviceInfo("device.json")
    }.alsoLogin()

    BilibiliRichMessage.register(bot)

    bot.join()
}
