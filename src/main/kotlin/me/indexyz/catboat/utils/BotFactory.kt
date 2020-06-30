package me.indexyz.catboat.utils

import dagger.Module
import dagger.Provides
import net.mamoe.mirai.Bot
import javax.inject.Singleton

@Module
object BotFactory {
    @Provides
    @Singleton
    fun provideMiraiBot(): Bot {
        val username = System.getenv("BOT_USERNAME").toLong()
        val password = System.getenv("BOT_PASSWORD")!!

        return Bot(username, password) {
            fileBasedDeviceInfo("device.json")
        }
    }
}
