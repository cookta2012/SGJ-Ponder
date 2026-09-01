package jjs.sgj_ponder.ponderstuff

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier
import jjs.sgj_ponder.SGJPonder
import net.createmod.catnip.math.Pointing
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.createmod.ponder.api.scene.PonderStoryBoard
import net.createmod.ponder.api.scene.SceneBuilder
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.minecraft.client.Minecraft
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity
import net.povstalec.sgjourney.common.init.BlockInit
import net.povstalec.sgjourney.common.sgjourney.StargateVariant

/** Defines and registers the six variant-crystal Ponder storyboards. */
object PonderScenes {
    // Shared presentation values keep all gate families aligned to the same structure.
    private val DEMO_ADDRESS = intArrayOf(1, 2, 3, 4, 5, 6)
    private const val INTRO_SCENE_SCALE = 0.78f
    private const val INTRO_SCENE_OFFSET_Y = -2.1f
    private const val DISPLAY_SCENE_SCALE = 0.62f
    private const val DISPLAY_SCENE_OFFSET_Y = -3.2f
    private const val DISPLAY_SCENE_ROTATION_Y = 15.0f

    private val DISPLAY_SCENE_IDS = setOf(
        "var_mw_display",
        "var_pegasus_display",
        "var_universe_display",
        "var_tollan_display",
        "var_classic_display",
    )

    /*
     * Gate descriptors isolate family-specific blocks, entities, and NBT from the common
     * display storyboard. Defaults prepare the base gate; variant writers apply registry data.
     */
    private val MILKY_WAY = GateType(
        StargateVariant.MILKY_WAY_STARGATE,
        "var_mw_display",
        "Variant Crystals for Milky Way gates",
        Supplier { BlockInit.MILKY_WAY_STARGATE.get() },
        MilkyWayStargateEntity::class.java,
        Consumer { nbt ->
            putSymbols(nbt, "sgjourney:galaxy_milky_way", "sgjourney:tauri")
        },
        BiConsumer { nbt, variant ->
            putSymbols(nbt, "sgjourney:terra", "sgjourney:terra")
            putVariantAddress(nbt, variant)
        },
    )

    private val PEGASUS = GateType(
        StargateVariant.PEGASUS_STARGATE,
        "var_pegasus_display",
        "Variant Crystals for Pegasus gates",
        Supplier { BlockInit.PEGASUS_STARGATE.get() },
        PegasusStargateEntity::class.java,
        Consumer { nbt ->
            putSymbols(nbt, "sgjourney:galaxy_milky_way", "sgjourney:tauri")
            putDynamicSymbols(nbt, false)
        },
        BiConsumer { nbt, variant ->
            putSymbols(nbt, "sgjourney:terra", "sgjourney:terra")
            putDynamicSymbols(nbt, false)
            putVariantAddress(nbt, variant)
        },
    )

    private val UNIVERSE = GateType(
        StargateVariant.UNIVERSE_STARGATE,
        "var_universe_display",
        "Variant Crystals for Universe gates",
        Supplier { BlockInit.UNIVERSE_STARGATE.get() },
        UniverseStargateEntity::class.java,
        Consumer { nbt ->
            putSymbols(nbt, "sgjourney:universal", "sgjourney:universal")
        },
        BiConsumer { nbt, variant ->
            putSymbols(nbt, "sgjourney:universal", "sgjourney:universal")
            putVariantAddress(nbt, variant)
        },
    )

    private val TOLLAN = GateType(
        StargateVariant.TOLLAN_STARGATE,
        "var_tollan_display",
        "Variant Crystals for Tollan gates",
        Supplier { BlockInit.TOLLAN_STARGATE.get() },
        TollanStargateEntity::class.java,
        Consumer { nbt ->
            putSymbols(nbt, "sgjourney:galaxy_milky_way", "sgjourney:tauri")
        },
        BiConsumer { nbt, variant ->
            putSymbols(nbt, "sgjourney:galaxy_milky_way", "sgjourney:tauri")
            putVariantAddress(nbt, variant)
        },
    )

    private val CLASSIC = GateType(
        StargateVariant.CLASSIC_STARGATE,
        "var_classic_display",
        "Variant Crystals for Classic gates",
        Supplier { BlockInit.CLASSIC_STARGATE.get() },
        ClassicStargateEntity::class.java,
        Consumer { nbt ->
            putSymbols(nbt, "sgjourney:galaxy_milky_way", "sgjourney:tauri")
        },
        BiConsumer { nbt, variant ->
            putSymbols(nbt, "sgjourney:terra", "sgjourney:tauri")
            putVariantAddress(nbt, variant)
        },
    )

    /** Registers the introduction followed by one registry-driven display for each gate family. */
    fun register(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        registerScene(helper) { scene, util -> variantCrystal(scene, util) }
        registerScene(helper) { scene, util -> variantCrystalDisplay(scene, util, MILKY_WAY) }
        registerScene(helper) { scene, util -> variantCrystalDisplay(scene, util, PEGASUS) }
        registerScene(helper) { scene, util -> variantCrystalDisplay(scene, util, UNIVERSE) }
        registerScene(helper) { scene, util -> variantCrystalDisplay(scene, util, TOLLAN) }
        registerScene(helper) { scene, util -> variantCrystalDisplay(scene, util, CLASSIC) }
    }

    /** Limits responsive scaling to this mod's six registered storyboards. */
    internal fun isSgjPonderScene(sceneId: ResourceLocation): Boolean {
        return sceneId.namespace == SGJPonder.MODID &&
            (sceneId.path == "var_crystal" || sceneId.path in DISPLAY_SCENE_IDS)
    }

    private fun registerScene(
        helper: PonderSceneRegistrationHelper<ResourceLocation>,
        scene: PonderStoryBoard,
    ) {
        // Every storyboard uses the same structure, item entry, and index tag.
        helper.addStoryBoard(
            SGJPonderPlugin.VARIANT_CRYSTAL_ITEM,
            "gate_pedestal",
            scene,
            SGJPonderPlugin.VARIANT_CRYSTAL_TAG,
        )
    }

    /** Introduces variant crystals and cycles through the five compatible gate families. */
    private fun variantCrystal(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.title("var_crystal", "Variant Crystals")
        scene.configureBasePlate(0, 0, 7)

        val gate = util.grid().at(3, 1, 4)
        showBaseAndPedestal(scene, util)
        revealGate(scene, util)
        setGate(scene, util, MILKY_WAY, MILKY_WAY.defaultNbt, true)
        scene.idle(10)

        scene.scaleSceneView(INTRO_SCENE_SCALE)
        scene.setSceneOffsetY(INTRO_SCENE_OFFSET_Y)
        scene.addKeyframe()
        scene.overlay().showControls(util.vector().topOf(gate), Pointing.DOWN, 25).rightClick()
        scene.overlay().showText(35)
            .placeNearTarget()
            .pointAt(util.vector().topOf(gate))
            .text("Right click the gate to apply the variant")

        scene.idle(40)

        scene.overlay().showText(45)
            .placeNearTarget()
            .pointAt(util.vector().topOf(gate))
            .text("Each variant only works on one specific type of gate")

        scene.idle(10)
        setGate(scene, util, PEGASUS, PEGASUS.defaultNbt, false)
        scene.idle(40)
        setGate(scene, util, UNIVERSE, UNIVERSE.defaultNbt, false)
        scene.idle(40)
        setGate(scene, util, TOLLAN, TOLLAN.defaultNbt, false)
        scene.idle(40)
        setGate(scene, util, CLASSIC, CLASSIC.defaultNbt, false)
        scene.idle(40)
        setGate(scene, util, MILKY_WAY, MILKY_WAY.defaultNbt, false)

        scene.idle(40)
        scene.markAsFinished()
    }

    /** Displays the base gate and every data-pack variant registered for [gateType]. */
    private fun variantCrystalDisplay(scene: SceneBuilder, util: SceneBuildingUtil, gateType: GateType) {
        scene.title(gateType.sceneId, gateType.title)
        scene.configureBasePlate(0, 0, 7)

        showBaseAndPedestal(scene, util)
        showVariantDisplayBoard(scene, util)

        scene.scaleSceneView(DISPLAY_SCENE_SCALE)
        scene.setSceneOffsetY(DISPLAY_SCENE_OFFSET_Y)
        scene.rotateCameraY(DISPLAY_SCENE_ROTATION_Y)

        revealGate(scene, util)
        setGate(scene, util, gateType, gateType.defaultNbt, true)
        scene.idle(20)
        spinDisplayBoard(scene, util)
        baseVariantDisplay(scene, util, gateType)

        getVariantsForType(gateType.id).forEach { variantName ->
            SGJPonder.LOGGER.info("Adding variant: {} for: {}", variantName, gateType.id)
            variantDisplay(scene, util, gateType, variantName)
        }

        scene.markAsFinished()
    }

    /** Adds the implicit base gate before explicit registry variants are animated. */
    private fun baseVariantDisplay(scene: SceneBuilder, util: SceneBuildingUtil, gateType: GateType) {
        SGJPonder.LOGGER.info("Adding implicit base variant for: {}", gateType.id)
        setDisplayBoardText(
            scene,
            util,
            gateType.id.namespace,
            gateType.id.path,
            gateType.id.namespace,
            "base",
        )
        setGate(scene, util, gateType, gateType.defaultNbt, false)
        scene.idle(5)
        scene.addKeyframe()
        scene.idle(40)
    }

    /** Updates the board and gate NBT for one registered variant. */
    private fun variantDisplay(
        scene: SceneBuilder,
        util: SceneBuildingUtil,
        gateType: GateType,
        variantName: String,
    ) {
        val variantId = ResourceLocation.parse(variantName)
        setDisplayBoardText(
            scene,
            util,
            gateType.id.namespace,
            gateType.id.path,
            variantId.namespace,
            variantId.path,
        )
        setGate(
            scene,
            util,
            gateType,
            Consumer { nbt -> gateType.variantNbt.accept(nbt, variantName) },
            false,
        )
        scene.idle(5)
        scene.addKeyframe()
        scene.idle(40)
    }

    /** Reads variants from the active client level so data-pack additions appear automatically. */
    private fun getVariantsForType(type: ResourceLocation): List<String> {
        val level = Minecraft.getInstance().level ?: return emptyList()
        val registry = level.registryAccess().registryOrThrow(StargateVariant.REGISTRY_KEY)
        val variants = mutableListOf<String>()

        SGJPonder.LOGGER.info("Loading variants for {}", type)
        registry.asLookup().listElements().forEach { entry ->
            val variantName = entry.key().location().toString()
            if (type == entry.value().baseStargate) {
                variants += variantName
            }
        }
        SGJPonder.LOGGER.info("Loaded {} variants of type {}", variants.size, type)
        return variants
    }

    /** Reveals the structure base and pedestal in two readable animation steps. */
    private fun showBaseAndPedestal(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.world().showSection(util.select().layer(0), Direction.UP)
        scene.idle(5)
        scene.world().showSection(util.select().fromTo(0, 1, 0, 7, 1, 3), Direction.SOUTH)
        scene.idle(10)
    }

    /** Reveals the tall flap-display assembly one layer at a time. */
    private fun showVariantDisplayBoard(scene: SceneBuilder, util: SceneBuildingUtil) {
        for (y in 1..9) {
            scene.world().showSection(util.select().fromTo(6, y, 6, 0, y, 6), Direction.DOWN)
            scene.idle(1)
        }
    }

    /** Reveals the single gate block after the supporting structure is visible. */
    private fun revealGate(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.world().showSection(util.select().position(util.grid().at(3, 1, 4)), Direction.DOWN)
    }

    /** Replaces the gate family and applies its complete block-entity NBT in one scene action. */
    private fun setGate(
        scene: SceneBuilder,
        util: SceneBuildingUtil,
        gateType: GateType,
        nbtWriter: Consumer<CompoundTag>,
        updateBlock: Boolean,
    ) {
        val gate = util.grid().at(3, 1, 4)
        scene.world().setBlock(gate, gateType.block.get().defaultBlockState(), updateBlock)
        scene.world().modifyBlockEntityNBT(
            util.select().position(gate),
            gateType.entityClass,
            nbtWriter,
            true,
        )
    }

    /** Starts the kinetic display rows so text changes animate through their flap transitions. */
    private fun spinDisplayBoard(scene: SceneBuilder, util: SceneBuildingUtil) {
        for (x in 0..6) {
            for (y in 8..9) {
                scene.world().modifyBlockEntity(
                    util.grid().at(x, y, 6),
                    KineticBlockEntity::class.java,
                ) { display -> display.setSpeed(192.0f) }
            }
        }
    }

    /** Splits gate and variant resource IDs across the display board's four rows. */
    private fun setDisplayBoardText(
        scene: SceneBuilder,
        util: SceneBuildingUtil,
        typeNamespace: String,
        typePath: String,
        variantNamespace: String,
        variantPath: String,
    ) {
        val display = util.grid().at(6, 9, 6)
        scene.world().modifyBlockEntity(
            display,
            FlapDisplayBlockEntity::class.java,
        ) { board ->
            board.applyTextManually(0, Component.literal(typeNamespace))
            board.applyTextManually(1, Component.literal(typePath))
            board.applyTextManually(2, Component.literal(variantNamespace))
            board.applyTextManually(3, Component.literal(variantPath))
        }
    }

    /** Writes the selected variant and deterministic demonstration address. */
    private fun putVariantAddress(nbt: CompoundTag, variant: String) {
        nbt.putString(AbstractStargateEntity.VARIANT, variant)
        nbt.putIntArray(AbstractStargateEntity.ADDRESS, DEMO_ADDRESS)
    }

    /** Writes the symbol set and point of origin expected by SGJourney's gate entity. */
    private fun putSymbols(nbt: CompoundTag, symbols: String, pointOfOrigin: String) {
        nbt.putString(AbstractStargateEntity.SYMBOLS, symbols)
        nbt.putString(AbstractStargateEntity.POINT_OF_ORIGIN, pointOfOrigin)
    }

    /** Controls Pegasus dynamic symbols using SGJourney's intentionally misspelled NBT key. */
    private fun putDynamicSymbols(nbt: CompoundTag, dynamicSymbols: Boolean) {
        nbt.putBoolean(PegasusStargateEntity.DYNAMC_SYMBOLS, dynamicSymbols)
    }

    /** Family-specific inputs consumed by the shared registry-variant storyboard. */
    private data class GateType(
        val id: ResourceLocation,
        val sceneId: String,
        val title: String,
        val block: Supplier<out Block>,
        val entityClass: Class<out BlockEntity>,
        val defaultNbt: Consumer<CompoundTag>,
        val variantNbt: BiConsumer<CompoundTag, String>,
    )
}
