package net.diyigemt.mpu.utils

import net.diyigemt.mpu.MiraiPluginUpdater
import java.io.File

/**
 *@Author hjn
 *@Create 2022/11/22
 */
internal object DataUtil: File(MiraiPluginUpdater.dataFolderPath.toString()) {
    val cacheInstance = File(this, "cache")

    init {
        if(!cacheInstance.exists()) cacheInstance.mkdirs()
    }

    fun wipeCache() = cacheInstance.deleteRecursively()
}