package com.rpmtw.rpmtw_platform_mod.translation.machineTranslation

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rpmtw.rpmtw_platform_mod.RPMTWPlatformMod
import com.rpmtw.rpmtw_platform_mod.config.RPMTWConfig
import com.rpmtw.rpmtw_platform_mod.utilities.Utilities
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.GsonHelper
import net.minecraft.util.profiling.ProfilerFiller
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

class MTStorage : SimplePreparableReloadListener<CompletableFuture<Void>>() {
    private val unsupportedFormatRegx: Regex = Regex("%(\\d+\\$)?[\\d.]*[df]")

    companion object {
        private var unlocalizedMap: MutableMap<String, String> = HashMap()
        private var currentLangMap: MutableMap<String, String> = HashMap()

        fun getUnlocalizedTranslate(key: String): String? {
            return unlocalizedMap[key]
        }

        fun isTranslate(key: String): Boolean {
            return currentLangMap.containsKey(key)
        }
    }

    override fun prepare(manager: ResourceManager, profilerFiller: ProfilerFiller): CompletableFuture<Void> {
        RPMTWPlatformMod.LOGGER.info("Preparing MTStorage...")
        val currentLangCode: String = Utilities.languageCode
        unlocalizedMap.clear()
        currentLangMap.clear()

        return CompletableFuture.runAsync {
            if (RPMTWConfig.get().translate.unlocalized || RPMTWConfig.get().translate.machineTranslation) {

                for (namespace in manager.namespaces) {
                    load("en_us", { key, value ->
                        unlocalizedMap[key] = value
                    }, namespace, manager)

                    // Only load current language
                    load(currentLangCode, { key, value ->
                        currentLangMap[key] = value
                    }, namespace, manager)
                }
            }
        }
    }

    private fun load(
        lang: String,
        action: (key: String, value: String) -> Unit,
        namespace: String,
        manager: ResourceManager
    ) {
        val path = "lang/$lang.json"
        try {
            val identifier = ResourceLocation(namespace, path)
            val resourceList: List<Resource> = manager.getResources(identifier)
            for (resource in resourceList) {
                val gson = Gson()
                val jsonObject: JsonObject = gson.fromJson(
                    InputStreamReader(resource.inputStream, StandardCharsets.UTF_8),
                    JsonObject::class.java
                )
                for ((key, value1) in jsonObject.entrySet()) {
                    val value: String =
                        unsupportedFormatRegx.replace(GsonHelper.convertToString(value1, key), "%$1s")
                    action(key, value)
                }
            }
        } catch (ignored: FileNotFoundException) {
        } catch (e: Exception) {
            RPMTWPlatformMod.LOGGER.warn(
                "[Machine Translation] Skipped language file: {}:{} ({})",
                namespace,
                path,
                e.toString()
            )
        }
    }

    override fun apply(
        `object`: CompletableFuture<Void>,
        resourceManager: ResourceManager,
        profilerFiller: ProfilerFiller
    ) {
        // no-op
    }
}