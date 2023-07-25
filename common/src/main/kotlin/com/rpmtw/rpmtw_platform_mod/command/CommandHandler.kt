package com.rpmtw.rpmtw_platform_mod.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.rpmtw.rpmtw_platform_mod.RPMTWPlatformModPlugin
import dev.architectury.platform.Platform
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.commands.SharedSuggestionProvider

@Environment(EnvType.CLIENT)
object CommandHandler {
    const val success: Int = Command.SINGLE_SUCCESS

    fun init() {
        // Forge not support client command
        if (Platform.isForge()) return

        val mainCommand = literal("rpmtw")
        val commandList =
            listOf(UniverseMessageActionCommand(), LoginRPMTWAccountCommand(), LogoutRPMTWAccountCommand())

        commandList.forEach {
            mainCommand.then(it.register())
        }

        RPMTWPlatformModPlugin.dispatchClientCommand { dispatcher ->
            dispatcher.register(mainCommand)
        }
    }
}

fun literal(name: String): LiteralArgumentBuilder<SharedSuggestionProvider> {
    return LiteralArgumentBuilder.literal(name)
}

fun <T> argument(name: String, argument: ArgumentType<T>): RequiredArgumentBuilder<SharedSuggestionProvider, T> {
    return RequiredArgumentBuilder.argument(name, argument)
}