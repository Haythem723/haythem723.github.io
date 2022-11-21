package net.diyigemt.mpu

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription

/**
 *@Author hjn
 *@Create 2022/11/19
 */
abstract class PluginUpdater(url: String, desc: JvmPluginDescription) {
    init {
        MiraiPluginUpdater.updateManager.addPlugin(url, desc)
    }

    companion object{
        @Serializable
        lateinit var a: APIData
    }
}