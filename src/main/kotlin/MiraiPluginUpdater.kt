package net.diyigemt.mpu

import kotlinx.coroutines.launch
import net.diyigemt.mpu.command.UpdateCommand
import net.diyigemt.mpu.utils.DataStoreUtil
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.registeredCommands
import net.mamoe.mirai.console.extension.PluginComponentStorage
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin

object MiraiPluginUpdater : KotlinPlugin(
    JvmPluginDescription.loadFromResource()
) {
    lateinit var updateManager: UpdateManager
    override fun onEnable() {
        info("mpu loaded")
        //DataStoreUtil.test()
        updateManager = UpdateManager()
        CommandManager.registerCommand(UpdateCommand)
    }

    override fun onDisable() {
        updateManager.close()
        super.onDisable()
    }

    fun runSuspend(block: suspend () -> Unit) = launch(coroutineContext) {
        block()
    }

    fun info(msg : String) = logger.info(msg)

    fun warning(msg : String) = logger.warning(msg)

    fun error(msg : String) = logger.error(msg)
}