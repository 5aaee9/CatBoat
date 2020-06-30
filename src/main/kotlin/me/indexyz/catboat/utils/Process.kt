package me.indexyz.catboat.utils

import dagger.Module
import me.indexyz.catboat.process.IProcess
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.join
import javax.inject.Inject

@Module
class Process @Inject constructor(
    var bot: Bot,
    var processes: Map<String, @JvmSuppressWildcards IProcess>) {
    suspend fun initBot(): Unit {
        this.bot.alsoLogin()

        this.processes.forEach {
            println("Loading ${it.key}")
            it.value.register()
        }

        this.bot.join()
    }
}

