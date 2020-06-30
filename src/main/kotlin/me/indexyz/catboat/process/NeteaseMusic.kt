package me.indexyz.catboat.process

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Inject

@Module
abstract class NeteaseMusicProcessBindModule {
    @Binds
    @IntoMap
    @StringKey("netease_music")
    abstract fun bindNeteaseMusicProcess(process: NeteaseMusicProcess): IProcess
}


@Module
class NeteaseMusicProcess @Inject constructor() : IProcess {
    override fun register() {

    }
}
