package jjs.sgj_ponder.ponderstuff;

import jjs.sgj_ponder.SGJPonder;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class SGJPonderPlugin implements PonderPlugin {
    public static final ResourceLocation VARIANT_CRYSTAL_ITEM = new ResourceLocation("sgjourney", "stargate_variant_crystal");
    public static final ResourceLocation VARIANT_CRYSTAL_TAG = new ResourceLocation(SGJPonder.MODID, "var_crystal");

    @Override
    public String getModId() {
        return SGJPonder.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        Item iconItem = ForgeRegistries.ITEMS.getValue(VARIANT_CRYSTAL_ITEM);
        if (iconItem == null) {
            iconItem = Items.AMETHYST_SHARD;
        }

        helper.registerTag(VARIANT_CRYSTAL_TAG)
            .title("Stargate Variant Crystal")
            .description("Used to change the appearance of stargates")
            .item(iconItem)
            .addToIndex()
            .register();

        helper.addToTag(VARIANT_CRYSTAL_TAG).add(VARIANT_CRYSTAL_ITEM);
    }
}
