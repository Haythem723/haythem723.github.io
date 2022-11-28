package net.diyigemt.mpu.utils

import net.diyigemt.mpu.APIData
import net.diyigemt.mpu.PluginJarManager
import net.mamoe.mirai.console.internal.plugin.*
import net.mamoe.mirai.console.plugin.jvm.AbstractJvmPlugin
import java.io.File

/**
 *@Author hjn
 *@Create 2022/11/21
 */
@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
internal object PluginFileUtil: File(System.getProperty("user.dir") + "/plugins") {
    fun getPluginFile(plugin: AbstractJvmPlugin): File = PluginJarManager.getPluginClassLoader(plugin).file

    fun getPluginJarById(id: String): File? {
        val res = PluginJarManager.getPluginById(id) ?: return null
        return (res.javaClass.classLoader as JvmPluginClassLoaderN).file
    }

    fun generatePluginName(data: APIData): String = data.fileName + "-" + data.version + ".mirai2.jar"
}