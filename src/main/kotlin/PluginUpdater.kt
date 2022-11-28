package net.diyigemt.mpu

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription

/**
 *@Author hjn
 *@Create 2022/11/19
 */
abstract class PluginUpdater(url: String, desc: JvmPluginDescription) {
    init {
        System.setProperty("proxyHost", "127.0.0.1")
        System.setProperty("proxyPort", "7890")
        MiraiPluginUpdater.updateManager.addPlugin(url, desc)
    }

    companion object{
    }
}