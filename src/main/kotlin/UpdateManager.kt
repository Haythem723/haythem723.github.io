package net.diyigemt.mpu

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.diyigemt.mpu.utils.DataStoreUtil
import net.diyigemt.mpu.utils.DataUtil
import net.diyigemt.mpu.utils.PluginFileUtil
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.util.SemVersion
import java.io.File
import java.net.SocketException
import kotlin.collections.set

/**
 *@Author hjn
 *@Create 2022/11/19
 */
class UpdateManager {
  private val pluginMap: MutableMap<String, UpdaterData> = mutableMapOf()

  private val updateTask: MutableMap<JvmPluginDescription, APIData> = mutableMapOf()

  init {
    PluginJarManager()
  }

  fun addPlugin(url: String, desc: JvmPluginDescription){
    pluginMap[desc.id] = UpdaterData(url, desc)
    DataStoreUtil.setPropertyFromModel(desc.id, PropertyData(desc.name, desc.version))

    checkUpdate(url, desc)
  }

  fun close(){
    DataStoreUtil.storeProperties()
    //DataUtil.wipeCache()
  }

  fun generateUpdateList(): String {
    var res = "当前所有插件为最新"
    if(updateTask.isNotEmpty()){
      res = "\n\n"
      updateTask.forEach{
        res += "${it.key.name} v${it.key.version} -> v${it.value.version}\n"
      }
      res += "\n输入 “mpu update” 更新以上所有插件"
    }

    return res
  }

  private fun checkUpdate(url: String, desc: JvmPluginDescription) = MiraiPluginUpdater.runSuspend {
    runCatching { client.get(url).body<ResponseData>() }.onSuccess {
      when(it.data.version != desc.version){
        (it.data.version > desc.version) -> {
          MiraiPluginUpdater.info("插件: ${desc.name} 有新版本 v${it.data.version}")
          updateTask[desc] = it.data
        }

        (it.data.version < desc.version) -> MiraiPluginUpdater.warning("插件: ${desc.name} 本地版本高于云端版本")

        else -> {}
      }
    }.onFailure {
      if(judgeException(it)){
        MiraiPluginUpdater.error("插件: ${desc.name} 检查更新超时")
      }
      else{
        it.printStackTrace()
        MiraiPluginUpdater.error("插件: ${desc.name} 远端JSON无法解析，无法获取更新，请联系该插件开发者")
      }
    }
  }

  private fun dispatchAnUpdate(data: APIData, desc: JvmPluginDescription) = MiraiPluginUpdater.runSuspend {
    val file = withContext(Dispatchers.IO) {
      File.createTempFile(desc.id + "-", ".tmp", DataUtil.cacheInstance)
    }

    kotlin.runCatching {
      MiraiPluginUpdater.info("正在获取新版插件: ${desc.name}...")
      val bytes: ByteArray = client.get(data.downloadUrl).body()
      file.writeBytes(bytes)
      MiraiPluginUpdater.info("${desc.name} 下载完成")
    }.onFailure {
      if(judgeException(it)){
        MiraiPluginUpdater.error("插件: ${desc.name} 下载超时")
      }
      else{
        it.printStackTrace()
        MiraiPluginUpdater.error("插件: ${desc.name} 更新时出现错误")
      }
    }
  }

  private companion object{
    private val client = HttpClient(CIO){
      install(ContentNegotiation){
        json(json = Json{ignoreUnknownKeys = true})
      }
      install(HttpTimeout){
        connectTimeoutMillis = 3000L
      }
      install(HttpRequestRetry){
        retryOnExceptionIf { _, throwable ->
          judgeException(throwable)
        }
        maxRetries = 3
      }
    }

    private fun judgeException(throwable: Throwable): Boolean = when(throwable) {
      is HttpRequestTimeoutException -> true
      is ConnectTimeoutException -> true
      is SocketTimeoutException -> true
      is SocketException -> true
      else -> false
    }
  }
}