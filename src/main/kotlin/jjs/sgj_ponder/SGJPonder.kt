package jjs.sgj_ponder

import jjs.sgj_ponder.cmd.ClientCommands
import jjs.sgj_ponder.ponderstuff.PonderSceneScaler
import jjs.sgj_ponder.ponderstuff.SGJPonderPlugin
import net.createmod.ponder.foundation.PonderIndex
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

/** NeoForge entry point and lifecycle coordinator for SGJ Ponder. */
@Mod(SGJPonder.MODID)
object SGJPonder {
    /** Stable namespace shared by code, metadata, commands, and resources. */
    const val MODID = "sgj_ponder"

    /** Shared logger used by initialization, registry discovery, and diagnostics. */
    val LOGGER: Logger = LogManager.getLogger(MODID)

    /** Guards Ponder's global index against duplicate client lifecycle callbacks. */
    private var ponderPluginRegistered = false

    init {
        LOGGER.info("Initializing SGJ Ponder")
        // Lifecycle events use the mod bus; commands belong on NeoForge's game bus.
        MOD_BUS.addListener(::onClientSetup)
        MOD_BUS.addListener(::onServerSetup)
        NeoForge.EVENT_BUS.addListener(::registerCommands)
    }

    /** Installs client-only Ponder integration after mod construction has completed. */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.info("Initializing SGJ Ponder client support")
        event.enqueueWork {
            if (!ponderPluginRegistered) {
                ponderPluginRegistered = true
                PonderIndex.addPlugin(SGJPonderPlugin)
                PonderSceneScaler.register()
            }
        }
    }

    /** Confirms dedicated-server setup without touching client or Ponder classes. */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.info("Initializing SGJ Ponder server support")
    }

    /** Adds the diagnostic Stargate-variant command to the active command dispatcher. */
    private fun registerCommands(event: RegisterCommandsEvent) {
        ClientCommands.register(event.dispatcher)
    }
}
