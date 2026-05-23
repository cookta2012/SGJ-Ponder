package jjs.sgj_ponder.cmd;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import jjs.sgj_ponder.SGJPonder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.povstalec.sgjourney.common.sgjourney.StargateVariant;

public final class ClientCommands {
    private ClientCommands() {
    }

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sgjponder_getvars").executes(ClientCommands::execute));
    }

    private static int execute(final CommandContext<CommandSourceStack> command) {
        SGJPonder.LOGGER.info("Fetching gate variants..");

        ServerLevel level = command.getSource().getLevel();
        var stargateVariantRegistry = level.registryAccess().registryOrThrow(StargateVariant.REGISTRY_KEY);
        var regLookup = stargateVariantRegistry.asLookup();

        regLookup.listElements().forEach(listElement -> {
            String variantName = listElement.key().location().toString();
            String variantBase = listElement.value().getBaseStargate().toString();
            SGJPonder.LOGGER.info("Found variant: {} for: {}", variantName, variantBase);
            command.getSource().sendSystemMessage(Component.literal("Found variant: " + variantName + " for: " + variantBase));
        });

        return Command.SINGLE_SUCCESS;
    }
}
