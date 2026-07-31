package ladysnake.impaled.neoforge;

import com.mojang.serialization.MapCodec;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.entity.ElderTridentEntity;
import ladysnake.impaled.common.entity.GuardianTridentEntity;
import ladysnake.impaled.common.entity.HellforkEntity;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.entity.PitchforkEntity;
import ladysnake.impaled.common.entity.SoulforkEntity;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.impaled.common.item.AtlanItem;
import ladysnake.impaled.common.item.ElderTridentItem;
import ladysnake.impaled.common.item.HellforkItem;
import ladysnake.impaled.common.item.MaelstromItem;
import ladysnake.impaled.common.item.PitchforkItem;
import ladysnake.sincereloyalty.SincereLoyaltyNeoForge;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.util.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(Impaled.MODID)
public class ImpaledNeoForge {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Impaled.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Impaled.MODID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Impaled.MODID);

    // Entities
    public static final DeferredHolder<EntityType<?>, EntityType<PitchforkEntity>> PITCHFORK_ENTITY =
            ENTITY_TYPES.register("pitchfork", () -> tridentEntity(PitchforkEntity::new, "pitchfork", true));
    public static final DeferredHolder<EntityType<?>, EntityType<HellforkEntity>> HELLFORK_ENTITY =
            ENTITY_TYPES.register("hellfork", () -> tridentEntity(HellforkEntity::new, "hellfork", true));
    public static final DeferredHolder<EntityType<?>, EntityType<SoulforkEntity>> SOULFORK_ENTITY =
            ENTITY_TYPES.register("soulfork", () -> tridentEntity(SoulforkEntity::new, "soulfork", true));
    public static final DeferredHolder<EntityType<?>, EntityType<ElderTridentEntity>> ELDER_TRIDENT_ENTITY =
            ENTITY_TYPES.register("elder_trident", () -> tridentEntity(ElderTridentEntity::new, "elder_trident", false));
    public static final DeferredHolder<EntityType<?>, EntityType<ElderTridentEntity>> GUARDIAN_TRIDENT_ENTITY =
            ENTITY_TYPES.register("guardian_trident", () -> tridentEntity(GuardianTridentEntity::new, "guardian_trident", false));
    public static final DeferredHolder<EntityType<?>, EntityType<ImpaledTridentEntity>> ATLAN_ENTITY =
            ENTITY_TYPES.register("atlan", () -> tridentEntity(ImpaledTridentEntity::new, "atlan", true));

    // Items
    public static final DeferredItem<Item> ELDER_GUARDIAN_EYE = ITEMS.registerItem("elder_guardian_eye", Item::new, new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<Item> ANCIENT_TRIDENT = ITEMS.registerItem("ancient_trident", Item::new, new Item.Settings().rarity(Rarity.UNCOMMON).fireproof());

    public static final DeferredItem<Item> PITCHFORK = ITEMS.registerItem("pitchfork", p -> new PitchforkItem(p, PITCHFORK_ENTITY.get()), ImpaledItems.tridentSettings(150));
    public static final DeferredItem<Item> HELLFORK = ITEMS.registerItem("hellfork", p -> new HellforkItem(p, HELLFORK_ENTITY.get()), ImpaledItems.tridentSettings(325).fireproof());
    public static final DeferredItem<Item> SOULFORK = ITEMS.registerItem("soulfork", p -> new HellforkItem(p, SOULFORK_ENTITY.get()), ImpaledItems.tridentSettings(325).fireproof());
    public static final DeferredItem<Item> ELDER_TRIDENT = ITEMS.registerItem("elder_trident", p -> new ElderTridentItem(p, ELDER_TRIDENT_ENTITY.get()), ImpaledItems.tridentSettings(250));
    public static final DeferredItem<Item> ATLAN = ITEMS.registerItem("atlan", p -> new AtlanItem(p, ATLAN_ENTITY.get()), ImpaledItems.tridentSettings(250));
    public static final DeferredItem<Item> MAELSTROM = ITEMS.registerItem("maelstrom", MaelstromItem::new, new Item.Settings().maxDamage(80));

    public ImpaledNeoForge(IEventBus modBus) {
        ITEMS.register(modBus);
        ENTITY_TYPES.register(modBus);
        LOOT_MODIFIERS.register("bastion_ancient_trident", () -> ImpaledLootModifier.CODEC);
        LOOT_MODIFIERS.register(modBus);

        modBus.addListener(this::commonSetup);
        modBus.addListener(this::buildCreativeTabs);
        SincereLoyaltyNeoForge.register(modBus);
        // Client setup is loaded lazily to avoid classloading client classes on a dedicated server.
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
            ladysnake.impaled.neoforge.client.ImpaledNeoForgeClient.register(modBus);
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ImpaledEntityTypes.PITCHFORK = PITCHFORK_ENTITY.get();
            ImpaledEntityTypes.HELLFORK = HELLFORK_ENTITY.get();
            ImpaledEntityTypes.SOULFORK = SOULFORK_ENTITY.get();
            ImpaledEntityTypes.ELDER_TRIDENT = ELDER_TRIDENT_ENTITY.get();
            ImpaledEntityTypes.GUARDIAN_TRIDENT = GUARDIAN_TRIDENT_ENTITY.get();
            ImpaledEntityTypes.ATLAN = ATLAN_ENTITY.get();

            ImpaledItems.ELDER_GUARDIAN_EYE = ELDER_GUARDIAN_EYE.get();
            ImpaledItems.ANCIENT_TRIDENT = ANCIENT_TRIDENT.get();
            ImpaledItems.PITCHFORK = PITCHFORK.get();
            ImpaledItems.HELLFORK = HELLFORK.get();
            ImpaledItems.SOULFORK = SOULFORK.get();
            ImpaledItems.ELDER_TRIDENT = ELDER_TRIDENT.get();
            ImpaledItems.ATLAN = ATLAN.get();
            ImpaledItems.MAELSTROM = MAELSTROM.get();

            addTrident(PITCHFORK.get());
            addTrident(HELLFORK.get());
            addTrident(SOULFORK.get());
            addTrident(ELDER_TRIDENT.get());
            addTrident(ATLAN.get());
        });
    }

    private static void addTrident(Item item) {
        ImpaledItems.ALL_TRIDENTS.add((ladysnake.impaled.common.item.ImpaledTridentItem) item);
        DispenserBlock.registerBehavior(item, new ProjectileDispenserBehavior(item));
    }

    private void buildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ItemGroups.INGREDIENTS)) {
            event.add(ELDER_GUARDIAN_EYE.get());
            event.add(ANCIENT_TRIDENT.get());
        } else if (event.getTabKey().equals(ItemGroups.COMBAT)) {
            event.add(PITCHFORK.get());
            event.add(HELLFORK.get());
            event.add(SOULFORK.get());
            event.add(ELDER_TRIDENT.get());
            event.add(ATLAN.get());
            event.add(MAELSTROM.get());
        }
    }

    private static <T extends ImpaledTridentEntity> EntityType<T> tridentEntity(EntityType.EntityFactory<T> factory, String name, boolean trackedUpdateRate) {
        // Match the Fabric build: trackRangeBlocks(4) -> 1 chunk, trackedUpdateRate(20) for non-dynamic.
        var builder = EntityType.Builder.<T>create(factory, SpawnGroup.MISC)
                .dimensions(0.5f, 0.5f)
                .maxTrackingRange(1);
        if (trackedUpdateRate) builder.trackingTickInterval(20);
        return builder.build(Impaled.MODID + ":" + name);
    }
}
