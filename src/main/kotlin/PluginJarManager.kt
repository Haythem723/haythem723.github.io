package net.diyigemt.mpu

import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import net.diyigemt.mpu.utils.DataStoreUtil
import net.diyigemt.mpu.utils.PluginFileUtil
import net.mamoe.mirai.console.ConsoleFrontEndImplementation
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.MiraiConsoleImplementation
import net.mamoe.mirai.console.internal.plugin.*
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.PluginManager
import net.mamoe.mirai.console.plugin.id
import net.mamoe.mirai.console.plugin.jvm.AbstractJvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.name
import java.io.File

/**
 *@Author hjn
 *@Create 2022/11/19
 */
@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
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
                PluginFileUtil.listFiles()?.filter { it.name == id && it.isDirectory }?.forEach { it.deleteRecursively() }
            }.onSuccess {
                MiraiPluginUpdater.warning("插件: $id 不存在，已删除相关备份和数据")
            }.onFailure {
                it.printStackTrace()
            }
        }

        @OptIn(ConsoleFrontEndImplementation::class)
        fun getPluginById(id: String): AbstractJvmPlugin? = (MiraiConsole.pluginManager as PluginManagerImpl)
            .resolvedPlugins
            .filterIsInstance<AbstractJvmPlugin>().find { it.id == id || it.name == id }

        fun getPluginClassLoader(plugin: AbstractJvmPlugin): JvmPluginClassLoaderN = plugin.javaClass.classLoader as JvmPluginClassLoaderN

        @OptIn(ConsoleFrontEndImplementation::class)
        @Suppress("UNCHECKED_CAST")
        suspend fun disablePlugin(id: String): Boolean{
            val loader = MiraiConsoleImplementation.getInstance().jvmPluginLoader as BuiltInJvmPluginLoaderImpl
            val plugin = getPluginById(id)?: return false
            val cache = loader::class.java.getDeclaredField("pluginFileToInstanceMap").apply {
                isAccessible = true
            }.get(loader) as MutableMap<File, JvmPlugin>
            PluginManager.disablePlugin(plugin)
            kotlin.runCatching {
                plugin.cancel()
                val permissions = PermissionService.INSTANCE.javaClass.getDeclaredField("permissions").apply {
                    isAccessible = true
                }.get(PermissionService.INSTANCE) as MutableMap<*, *>
                withContext(Dispatchers.IO){
                    getPluginClassLoader(plugin).close()
                }
            }.onFailure {
                it.printStackTrace()
                return false
            }

            return true
        }
    }
}