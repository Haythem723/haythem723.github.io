package net.diyigemt.mpu

import net.diyigemt.mpu.utils.DataStoreUtil
import net.diyigemt.mpu.utils.FileUtil
import net.mamoe.mirai.console.ConsoleFrontEndImplementation
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.internal.plugin.*
import net.mamoe.mirai.console.plugin.id
import net.mamoe.mirai.console.plugin.info
import net.mamoe.mirai.console.plugin.jvm.AbstractJvmPlugin
import net.mamoe.mirai.console.plugin.name

/**
 *@Author hjn
 *@Create 2022/11/19
 */
internal class PluginJarManager {
    init {
        //data self check
        DataStoreUtil.foreach {entry ->
            getPluginById(entry.key)?: kotlin.runCatching {
                deleteNoneExistPlugin(entry.key)
                DataStoreUtil.remove(entry.key)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    companion object{
        private fun deleteNoneExistPlugin(id: String){
            kotlin.runCatching {
                FileUtil.listFiles()?.filter { it.name == id && it.isDirectory }?.forEach { it.deleteRecursively() }
            }.onSuccess {
                MiraiPluginUpdater.warning("插件: $id 不存在，已删除相关备份和数据")
            }.onFailure {
                it.printStackTrace()
            }
        }

        @OptIn(ConsoleFrontEndImplementation::class)
        @Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
        fun getPluginById(id: String): AbstractJvmPlugin? = (MiraiConsole.pluginManager as PluginManagerImpl)
            .resolvedPlugins
            .filterIsInstance<AbstractJvmPlugin>().find { it.id == id || it.name == id }
    }
}