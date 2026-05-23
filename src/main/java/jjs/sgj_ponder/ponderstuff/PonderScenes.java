package jjs.sgj_ponder.ponderstuff;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;

import jjs.sgj_ponder.SGJPonder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.sgjourney.StargateVariant;

public class PonderScenes {
    private static final int[] DEMO_ADDRESS = { 1, 2, 3, 4, 5, 6 };
    private static final float INTRO_SCENE_SCALE = 0.78f;
    private static final float INTRO_SCENE_OFFSET_Y = -2.1f;
    private static final float DISPLAY_SCENE_SCALE = 0.62f;
    private static final float DISPLAY_SCENE_OFFSET_Y = -3.2f;
    private static final float DISPLAY_SCENE_ROTATION_Y = 15.0f;

    private static final GateType MILKY_WAY = new GateType(
        StargateVariant.MILKY_WAY_STARGATE,
        "var_mw_display",
        "Variant Crystals for Milky Way gates",
        BlockInit.MILKY_WAY_STARGATE::get,
        MilkyWayStargateEntity.class,
        nbt -> {
            nbt.putString("Symbols", "sgjourney:galaxy_milky_way");
            nbt.putString("PointOfOrigin", "sgjourney:tauri");
        },
        (nbt, variant) -> {
            nbt.putString("Symbols", "sgjourney:terra");
            nbt.putString("PointOfOrigin", "sgjourney:terra");
            putVariantAddress(nbt, variant);
        }
    );

    private static final GateType PEGASUS = new GateType(
        StargateVariant.PEGASUS_STARGATE,
        "var_pegasus_display",
        "Variant Crystals for Pegasus gates",
        BlockInit.PEGASUS_STARGATE::get,
        PegasusStargateEntity.class,
        nbt -> {
            nbt.putString("Symbols", "sgjourney:galaxy_milky_way");
            nbt.putString("PointOfOrigin", "sgjourney:tauri");
            nbt.putBoolean("DynamicSymbols", false);
        },
        (nbt, variant) -> {
            nbt.putString("Symbols", "sgjourney:terra");
            nbt.putString("PointOfOrigin", "sgjourney:terra");
            nbt.putBoolean("DynamicSymbols", false);
            putVariantAddress(nbt, variant);
        }
    );

    private static final GateType UNIVERSE = new GateType(
        StargateVariant.UNIVERSE_STARGATE,
        "var_universe_display",
        "Variant Crystals for Universe gates",
        BlockInit.UNIVERSE_STARGATE::get,
        UniverseStargateEntity.class,
        nbt -> {
            nbt.putString("Symbols", "sgjourney:universal");
            nbt.putString("PointOfOrigin", "sgjourney:universal");
        },
        (nbt, variant) -> {
            nbt.putString("Symbols", "sgjourney:universal");
            nbt.putString("PointOfOrigin", "sgjourney:universal");
            putVariantAddress(nbt, variant);
        }
    );

    private static final GateType TOLLAN = new GateType(
        StargateVariant.TOLLAN_STARGATE,
        "var_tollan_display",
        "Variant Crystals for Tollan gates",
        BlockInit.TOLLAN_STARGATE::get,
        TollanStargateEntity.class,
        nbt -> {
            nbt.putString("Symbols", "sgjourney:galaxy_milky_way");
            nbt.putString("PointOfOrigin", "sgjourney:tauri");
        },
        PonderScenes::putVariantAddress
    );

    private static final GateType CLASSIC = new GateType(
        StargateVariant.CLASSIC_STARGATE,
        "var_classic_display",
        "Variant Crystals for Classic gates",
        BlockInit.CLASSIC_STARGATE::get,
        ClassicStargateEntity.class,
        nbt -> {
            nbt.putString("Symbols", "sgjourney:galaxy_milky_way");
            nbt.putString("PointOfOrigin", "sgjourney:tauri");
        },
        (nbt, variant) -> {
            nbt.putString("Symbols", "sgjourney:terra");
            nbt.putString("PointOfOrigin", "sgjourney:tauri");
            putVariantAddress(nbt, variant);
        }
    );

    private PonderScenes() {
    }

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.addStoryBoard(SGJPonderPlugin.VARIANT_CRYSTAL_ITEM, "gate_pedestal", PonderScenes::variantCrystal, SGJPonderPlugin.VARIANT_CRYSTAL_TAG);
        helper.addStoryBoard(SGJPonderPlugin.VARIANT_CRYSTAL_ITEM, "gate_pedestal", PonderScenes::variantCrystalDisplayMW, SGJPonderPlugin.VARIANT_CRYSTAL_TAG);
        helper.addStoryBoard(SGJPonderPlugin.VARIANT_CRYSTAL_ITEM, "gate_pedestal", PonderScenes::variantCrystalDisplayPegasus, SGJPonderPlugin.VARIANT_CRYSTAL_TAG);
        helper.addStoryBoard(SGJPonderPlugin.VARIANT_CRYSTAL_ITEM, "gate_pedestal", PonderScenes::variantCrystalDisplayUniverse, SGJPonderPlugin.VARIANT_CRYSTAL_TAG);
        helper.addStoryBoard(SGJPonderPlugin.VARIANT_CRYSTAL_ITEM, "gate_pedestal", PonderScenes::variantCrystalDisplayTollan, SGJPonderPlugin.VARIANT_CRYSTAL_TAG);
        helper.addStoryBoard(SGJPonderPlugin.VARIANT_CRYSTAL_ITEM, "gate_pedestal", PonderScenes::variantCrystalDisplayClassic, SGJPonderPlugin.VARIANT_CRYSTAL_TAG);
    }

    private static void variantCrystal(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("var_crystal", "Variant Crystals");
        scene.configureBasePlate(0, 0, 7);

        BlockPos gate = util.grid().at(3, 1, 4);
        showBaseAndPedestal(scene, util);
        revealGate(scene, util);
        setGate(scene, util, MILKY_WAY, MILKY_WAY.defaultNbt(), true);
        scene.idle(10);

        scene.scaleSceneView(INTRO_SCENE_SCALE);
        scene.setSceneOffsetY(INTRO_SCENE_OFFSET_Y);
        scene.addKeyframe();
        scene.overlay().showControls(util.vector().topOf(gate), Pointing.DOWN, 25)
            .rightClick();
        scene.overlay().showText(35)
            .placeNearTarget()
            .pointAt(util.vector().topOf(gate))
            .text("Right click the gate to apply the variant");

        scene.idle(40);

        scene.overlay().showText(45)
            .placeNearTarget()
            .pointAt(util.vector().topOf(gate))
            .text("Each variant only works on one specific type of gate");

        scene.idle(10);

        setGate(scene, util, PEGASUS, PEGASUS.defaultNbt(), false);
        scene.idle(40);
        setGate(scene, util, UNIVERSE, UNIVERSE.defaultNbt(), false);
        scene.idle(40);
        setGate(scene, util, TOLLAN, TOLLAN.defaultNbt(), false);
        scene.idle(40);
        setGate(scene, util, CLASSIC, CLASSIC.defaultNbt(), false);
        scene.idle(40);
        setGate(scene, util, MILKY_WAY, MILKY_WAY.defaultNbt(), false);

        scene.idle(40);
        scene.markAsFinished();
    }

    private static void variantCrystalDisplayMW(SceneBuilder scene, SceneBuildingUtil util) {
        variantCrystalDisplay(scene, util, MILKY_WAY);
    }

    private static void variantCrystalDisplayPegasus(SceneBuilder scene, SceneBuildingUtil util) {
        variantCrystalDisplay(scene, util, PEGASUS);
    }

    private static void variantCrystalDisplayUniverse(SceneBuilder scene, SceneBuildingUtil util) {
        variantCrystalDisplay(scene, util, UNIVERSE);
    }

    private static void variantCrystalDisplayTollan(SceneBuilder scene, SceneBuildingUtil util) {
        variantCrystalDisplay(scene, util, TOLLAN);
    }

    private static void variantCrystalDisplayClassic(SceneBuilder scene, SceneBuildingUtil util) {
        variantCrystalDisplay(scene, util, CLASSIC);
    }

    private static void variantCrystalDisplay(SceneBuilder scene, SceneBuildingUtil util, GateType gateType) {
        scene.title(gateType.sceneId(), gateType.title());
        scene.configureBasePlate(0, 0, 7);

        showBaseAndPedestal(scene, util);
        showVariantDisplayBoard(scene, util);

        scene.scaleSceneView(DISPLAY_SCENE_SCALE);
        scene.setSceneOffsetY(DISPLAY_SCENE_OFFSET_Y);
        scene.rotateCameraY(DISPLAY_SCENE_ROTATION_Y);

        revealGate(scene, util);
        setGate(scene, util, gateType, gateType.defaultNbt(), true);
        scene.idle(20);

        spinDisplayBoard(scene, util);

        baseVariantDisplay(scene, util, gateType);

        List<String> variants = getVariantsForType(gateType.id());
        for (String variantName : variants) {
            SGJPonder.LOGGER.info("Adding variant: {} for: {}", variantName, gateType.id());
            variantDisplay(scene, util, gateType, variantName);
        }

        scene.markAsFinished();
    }

    private static void baseVariantDisplay(SceneBuilder scene, SceneBuildingUtil util, GateType gateType) {
        SGJPonder.LOGGER.info("Adding implicit base variant for: {}", gateType.id());
        setDisplayBoardText(scene, util, gateType.id().getNamespace(), gateType.id().getPath(), gateType.id().getNamespace(), "base");
        setGate(scene, util, gateType, gateType.defaultNbt(), false);
        scene.idle(5);
        scene.addKeyframe();
        scene.idle(40);
    }

    private static void variantDisplay(SceneBuilder scene, SceneBuildingUtil util, GateType gateType, String variantName) {
        setDisplayBoardText(scene, util, gateType.id().getNamespace(), gateType.id().getPath(), namespace(variantName), path(variantName));
        setGate(scene, util, gateType, nbt -> gateType.variantNbt().accept(nbt, variantName), false);
        scene.idle(5);
        scene.addKeyframe();
        scene.idle(40);
    }

    private static List<String> getVariantsForType(ResourceLocation type) {
        List<String> variants = new ArrayList<>();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return variants;
        }

        var registry = minecraft.level.registryAccess().registryOrThrow(StargateVariant.REGISTRY_KEY);
        var lookup = registry.asLookup();

        SGJPonder.LOGGER.info("Attempting to Load variants..");
        SGJPonder.LOGGER.info("Loading Variants..");
        lookup.listElements().forEach(entry -> {
            String variantName = entry.key().location().toString();
            ResourceLocation variantBase = entry.value().getBaseStargate();
            if (type.equals(variantBase)) {
                variants.add(variantName);
            }
        });
        SGJPonder.LOGGER.info("Loaded {} variants of type {}", variants.size(), type);
        return variants;
    }

    private static void showBaseAndPedestal(SceneBuilder scene, SceneBuildingUtil util) {
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.idle(5);
        scene.world().showSection(util.select().fromTo(0, 1, 0, 7, 1, 3), Direction.SOUTH);
        scene.idle(10);
    }

    private static void showVariantDisplayBoard(SceneBuilder scene, SceneBuildingUtil util) {
        for (int y = 1; y <= 9; y++) {
            scene.world().showSection(util.select().fromTo(6, y, 6, 0, y, 6), Direction.DOWN);
            scene.idle(1);
        }
    }

    private static void revealGate(SceneBuilder scene, SceneBuildingUtil util) {
        scene.world().showSection(util.select().position(util.grid().at(3, 1, 4)), Direction.DOWN);
    }

    private static void setGate(SceneBuilder scene, SceneBuildingUtil util, GateType gateType, Consumer<CompoundTag> nbtWriter, boolean updateBlock) {
        BlockPos gate = util.grid().at(3, 1, 4);
        scene.world().setBlock(gate, gateType.block().get().defaultBlockState(), updateBlock);
        scene.world().modifyBlockEntityNBT(util.select().position(gate), gateType.entityClass(), nbtWriter, true);
    }

    private static void spinDisplayBoard(SceneBuilder scene, SceneBuildingUtil util) {
        for (int x = 0; x <= 6; x++) {
            for (int y = 8; y <= 9; y++) {
                scene.world().modifyBlockEntity(util.grid().at(x, y, 6), KineticBlockEntity.class, display -> display.setSpeed(192.0f));
            }
        }
    }

    private static void setDisplayBoardText(SceneBuilder scene, SceneBuildingUtil util, String typeNamespace, String typePath, String variantNamespace, String variantPath) {
        BlockPos display = util.grid().at(6, 9, 6);
        scene.world().modifyBlockEntity(display, FlapDisplayBlockEntity.class, board -> {
            board.applyTextManually(0, componentJson(typeNamespace));
            board.applyTextManually(1, componentJson(typePath));
            board.applyTextManually(2, componentJson(variantNamespace));
            board.applyTextManually(3, componentJson(variantPath));
        });
    }

    private static String componentJson(String text) {
        return Component.Serializer.toJson(Component.literal(text));
    }

    private static void putVariantAddress(CompoundTag nbt, String variant) {
        nbt.putString("Variant", variant);
        nbt.putIntArray("Address", DEMO_ADDRESS);
    }

    private static String namespace(String id) {
        int delimiter = id.lastIndexOf(':');
        return delimiter >= 0 ? id.substring(0, delimiter) : "";
    }

    private static String path(String id) {
        int delimiter = id.lastIndexOf(':');
        return delimiter >= 0 ? id.substring(delimiter + 1) : id;
    }

    private record GateType(
        ResourceLocation id,
        String sceneId,
        String title,
        Supplier<? extends Block> block,
        Class<? extends BlockEntity> entityClass,
        Consumer<CompoundTag> defaultNbt,
        BiConsumer<CompoundTag, String> variantNbt
    ) {
    }
}
