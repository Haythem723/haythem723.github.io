package net.diyigemt.mpu

import net.diyigemt.mpu.utils.DataStoreUtil
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.util.SemVersion

/**
 *@Author hjn
 *@Create 2022/11/19
 */
class UpdateManager {
    private val pluginMap: MutableMap<String, UpdaterData> = mutableMapOf()

    init {
        PluginJarManager()
    }

    fun addPlugin(url: String, desc: JvmPluginDescription){
        pluginMap[desc.id] = UpdaterData(url, desc)
        DataStoreUtil.setPropertyFromModel(desc.id, PropertyData(desc.name, desc.version))

        checkUpdate(url, desc.version)
    }

    fun close(){
        DataStoreUtil.storeProperties()
    }

    companion object{
        private fun checkUpdate(url: String, version: SemVersion){
            dispatchAnUpdate()
        }

        private fun dispatchAnUpdate(){
            MiraiPluginUpdater.runSuspend {
                //TODO()
            }
        }
    }
}