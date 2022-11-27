package com.rpmtw.rpmtw_platform_mod.events

import com.rpmtw.rpmtw_platform_mod.RPMTWPlatformMod
import com.rpmtw.rpmtw_platform_mod.config.RPMTWConfig
import com.rpmtw.rpmtw_platform_mod.gui.widgets.RPMTWCheckbox
import com.rpmtw.rpmtw_platform_mod.gui.widgets.TranslucentButton
import com.rpmtw.rpmtw_platform_mod.util.Util
import me.shedaniel.architectury.event.events.GuiEvent
import me.shedaniel.architectury.hooks.ScreenHooks
import me.shedaniel.architectury.platform.Platform
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.screens.ChatScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.resources.language.I18n
import net.minecraft.network.chat.TranslatableComponent

@Environment(EnvType.CLIENT)
class OnGuiInitPost : GuiEvent.ScreenInitPost {

    override fun init(screen: Screen, widgets: List<AbstractWidget>, children: List<GuiEventListener>) {
        addedUniverseChatButton(screen, children)
    }

    private fun addedUniverseChatButton(
        screen: Screen,
        children: List<GuiEventListener>
    ) {
        try {
            val scaledWidth = screen.width
            val scaledHeight = screen.height

            if (screen is ChatScreen && (RPMTWConfig.get().universeChat.enable && RPMTWConfig.get().universeChat.enableButton)) {
                val textField: EditBox? = screen.input
                val offsetX: Int
                val hasQuarkMod = Platform.isModLoaded("quark")
                offsetX = if (hasQuarkMod) {
                    // Because quark mod has a button on the chat screen, we need to offset the button by the width of the quark button
                    // https://github.com/VazkiiMods/Quark/blob/9c3334244d508d0b0383e7f397b02c136295067e/src/main/java/vazkii/quark/content/tweaks/module/EmotesModule.java#L224
                    -75
                } else {
                    0
                }

                val sendButton = TranslucentButton(
                    scaledWidth - 185 + offsetX,
                    scaledHeight - 40,
                    90,
                    20,
                    TranslatableComponent("universeChat.rpmtw_platform_mod.button.send"),
                    {
                        Util.openUniverseChatScreen(textField?.value)
                    },
                    TranslatableComponent("universeChat.rpmtw_platform_mod.button.send.tooltip")
                )

                val checkbox = RPMTWCheckbox(
                    scaledWidth - 90 + offsetX,
                    scaledHeight - 40,
                    20,
                    20,
                    TranslatableComponent("universeChat.rpmtw_platform_mod.button.receive"),
                    RPMTWConfig.get().universeChat.enableReceiveMessage,
                    { checked ->
                        RPMTWConfig.get().universeChat.enableReceiveMessage = checked
                        RPMTWConfig.save()
                    },
                    I18n.get("universeChat.rpmtw_platform_mod.button.receive.tooltip")
                )

                ScreenHooks.addButton(screen, sendButton)
                ScreenHooks.addButton(screen, checkbox)
            }
        } catch (e: Exception) {
            RPMTWPlatformMod.LOGGER.error("Adding button to the chat screen failed", e)
        }
    }
}