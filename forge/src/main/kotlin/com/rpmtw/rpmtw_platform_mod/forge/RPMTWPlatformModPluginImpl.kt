package com.rpmtw.rpmtw_platform_mod.forge

import com.mojang.brigadier.CommandDispatcher
import com.rpmtw.rpmtw_platform_mod.config.RPMTWConfig.getScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.ModLoadingContext
import java.io.File

@Suppress("unused")
object RPMTWPlatformModPluginImpl {
    @JvmStatic
    fun registerConfigScreen() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory::class.java) {
            ConfigScreenFactory { _: Minecraft?, screen: Screen? -> getScreen(screen) }
        }
    }

    @JvmStatic
    fun dispatchClientCommand(callback: (dispatcher: CommandDispatcher<SharedSuggestionProvider>, buildContext: CommandBuildContext) -> Unit) {
        MinecraftForge.EVENT_BUS.addListener { event: RegisterClientCommandsEvent ->
            @Suppress("UNCHECKED_CAST")
            callback(event.dispatcher as CommandDispatcher<SharedSuggestionProvider>, event.buildContext)
        }
    }

    @JvmStatic
    fun executeClientCommand(command: String): Boolean {
        return ClientCommandHandler.runCommand(command)
    }

    @JvmStatic
    fun getGameFolder(): File {
        return Minecraft.getInstance().gameDirectory
    }
}