package net.diyigemt.mpu.command

import net.diyigemt.mpu.MiraiPluginUpdater
import net.diyigemt.mpu.PluginJarManager
import net.diyigemt.mpu.UpdateManager
import net.mamoe.mirai.console.command.CommandContext
import net.mamoe.mirai.console.command.CompositeCommand
import java.io.File

/**
 *@Author hjn
 *@Create 2022/11/25
 */
object UpdateCommand: CompositeCommand(MiraiPluginUpdater, "mpu"){
  @SubCommand("update")
  @Description("升级所有插件")
  fun updateHandler(context: CommandContext, arg: String) {
    MiraiPluginUpdater.info("update")
  }

  @SubCommand("list")
  @Description("查看可升级插件")
  fun listHandler(context: CommandContext) = MiraiPluginUpdater.info(MiraiPluginUpdater.updateManager.generateUpdateList())

  @SubCommand("rollback")
  @Description("回滚已升级的插件")
  fun rollbackHandler(context: CommandContext){
    MiraiPluginUpdater.info("Under construction")
  }
}