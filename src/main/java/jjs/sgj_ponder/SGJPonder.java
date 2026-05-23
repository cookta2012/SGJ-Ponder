package jjs.sgj_ponder;

import jjs.sgj_ponder.cmd.ClientCommands;
import jjs.sgj_ponder.ponderstuff.SGJPonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SGJPonder.MODID)
public class SGJPonder {
    public static final String MODID = "sgj_ponder";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private static boolean ponderPluginRegistered;

    public SGJPonder() {
        LOGGER.log(Level.INFO, "Hello world!");

        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onServerSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        LOGGER.log(Level.INFO, "Initializing client...");
        event.enqueueWork(() -> {
            if (ponderPluginRegistered) {
                return;
            }
            ponderPluginRegistered = true;
            PonderIndex.addPlugin(new SGJPonderPlugin());
        });
    }

    private void onServerSetup(final FMLDedicatedServerSetupEvent event) {
        LOGGER.log(Level.INFO, "Server starting...");
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.FORGE)
    public static class ModEventListener {
        @SubscribeEvent
        public static void registerCommands(final RegisterCommandsEvent event) {
            ClientCommands.register(event.getDispatcher());
        }
    }
}
