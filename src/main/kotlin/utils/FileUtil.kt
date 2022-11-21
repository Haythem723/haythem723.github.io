package net.diyigemt.mpu.utils

import net.mamoe.mirai.console.plugin.jvm.AbstractJvmPlugin
import net.mamoe.mirai.console.internal.plugin.*
import java.io.File

/**
 *@Author hjn
 *@Create 2022/11/21
 */
@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
internal object FileUtil: File(System.getProperty("user.dir") + "/plugins") {
    fun getPluginFile(plugin: AbstractJvmPlugin): File = (plugin.javaClass.classLoader as JvmPluginClassLoaderN).file
}