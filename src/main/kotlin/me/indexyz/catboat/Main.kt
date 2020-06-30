package me.indexyz.catboat

import dagger.Component
import me.indexyz.catboat.process.BilibiliProcessBindModule
import me.indexyz.catboat.process.NeteaseMusicProcessBindModule
import me.indexyz.catboat.utils.BotFactory
import me.indexyz.catboat.utils.Process
import javax.inject.Singleton

@Singleton
@Component(modules = [
    Process::class, BotFactory::class,
    BilibiliProcessBindModule::class, NeteaseMusicProcessBindModule::class])
interface ProcessComponent {
    fun process(): Process
}

suspend fun main() {
    val process = DaggerProcessComponent.builder().build().process()
    process.initBot()
}
