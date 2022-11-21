package net.diyigemt.mpu.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import net.diyigemt.mpu.MiraiPluginUpdater
import net.diyigemt.mpu.PropertyData
import net.diyigemt.mpu.exception.DuplicateIdException
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.disable
import java.io.File
import java.util.*

/**
 *@Author hjn
 *@Create 2022/11/19
 */
internal object DataStoreUtil: Properties() {
    private val file = File(MiraiPluginUpdater.dataFolderPath.toUri().path + "/data.map")
    private val mutex = Mutex()

    init {
        kotlin.runCatching {
            if(!file.exists()){
                file.createNewFile()
            }
            load(MiraiPluginUpdater.dataFolder.listFiles()!!.find { it.name == "data.map" }!!.inputStream())
        }.onFailure {
            it.printStackTrace()
            MiraiPluginUpdater.error("fail to init, mpu will not work")
            MiraiPluginUpdater.disable()
        }
    }

    fun test() = setProperty(MiraiPluginUpdater.description.id, Json.encodeToString(
        PropertyData.serializer(), PropertyData(MiraiPluginUpdater.description.name, MiraiPluginUpdater.description.version))
    )

    @Throws(DuplicateIdException::class)
    override fun setProperty(key: String?, value: String?){
        if (contains(key)) throw DuplicateIdException(key?: "null")
        super.setProperty(key, value)
        storeProperties()
    }

    override fun remove(key: Any?): Any? {
        val res = super.remove(key)
        storeProperties()
        return res
    }

    fun getPropertyModel(key: String): PropertyData = Json.decodeFromString(PropertyData.serializer(), getProperty(key))

    @Suppress("UNCHECKED_CAST")
    fun foreach(block: (Map.Entry<String, String>) -> Unit) = forEach{
        block(it as Map.Entry<String, String>)
    }

    fun setPropertyFromModel(key: String, model: PropertyData) = setProperty(
        key, Json.encodeToString(PropertyData.serializer(), model)
    )

    fun storeProperties() = MiraiPluginUpdater.runSuspend {
        mutex.withLock {
            file.outputStream().use{
                store(it, null)
            }
        }
    }
}