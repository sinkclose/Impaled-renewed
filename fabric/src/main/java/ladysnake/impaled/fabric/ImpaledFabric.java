package ladysnake.impaled.fabric;

import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.impaled.common.entity.*;
import ladysnake.impaled.common.item.*;
import ladysnake.sincereloyalty.SincereLoyaltyFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ImpaledFabric implements ModInitializer {

    private static final UniformLootNumberProvider LOOT_RANGE = UniformLootNumberProvider.create(1, 1);

    @Override
    public void onInitialize() {
        registerEntities();
        registerItems();
        registerLoot();

        SincereLoyaltyFabric.init();
    }

    private static void registerEntities() {
        ImpaledEntityTypes.PITCHFORK = register("pitchfork", createEntityType(PitchforkEntity::new, true));
        ImpaledEntityTypes.HELLFORK = register("hellfork", createEntityType(HellforkEntity::new, true));
        ImpaledEntityTypes.SOULFORK = register("soulfork", createEntityType(SoulforkEntity::new, true));
        ImpaledEntityTypes.ELDER_TRIDENT = register("elder_trident", createDynamicEntityType(ElderTridentEntity::new));
        ImpaledEntityTypes.GUARDIAN_TRIDENT = register("guardian_trident", createDynamicEntityType(GuardianTridentEntity::new));
        ImpaledEntityTypes.ATLAN = register("atlan", createEntityType(ImpaledTridentEntity::new, true));
    }

    private static void registerItems() {
        ImpaledItems.ELDER_GUARDIAN_EYE = registerItem(new Item(new Item.Settings().rarity(Rarity.UNCOMMON)), "elder_guardian_eye");
        ImpaledItems.ANCIENT_TRIDENT = registerItem(new Item(new Item.Settings().rarity(Rarity.UNCOMMON).fireproof()), "ancient_trident");

        ImpaledItems.PITCHFORK = registerTrident(new PitchforkItem(ImpaledItems.tridentSettings(150), ImpaledEntityTypes.PITCHFORK), "pitchfork");
        ImpaledItems.HELLFORK = registerTrident(new HellforkItem(ImpaledItems.tridentSettings(325).fireproof(), ImpaledEntityTypes.HELLFORK), "hellfork");
        ImpaledItems.SOULFORK = registerTrident(new HellforkItem(ImpaledItems.tridentSettings(325).fireproof(), ImpaledEntityTypes.SOULFORK), "soulfork");
        ImpaledItems.ELDER_TRIDENT = registerTrident(new ElderTridentItem(ImpaledItems.tridentSettings(250), ImpaledEntityTypes.ELDER_TRIDENT), "elder_trident");
        ImpaledItems.ATLAN = registerTrident(new AtlanItem(ImpaledItems.tridentSettings(250), ImpaledEntityTypes.ATLAN), "atlan");
        ImpaledItems.MAELSTROM = registerItem(new MaelstromItem(new Item.Settings().maxDamage(80)), "maelstrom");
    }

    private static void registerLoot() {
        LootTableEvents.MODIFY.register((id, builder, source) -> {
            if (Impaled.BASTION_TREASURE_CHEST_LOOT_TABLE_ID.equals(id) && source.isBuiltin()) {
                builder.pool(LootPool.builder()
                        .rolls(LOOT_RANGE)
                        .conditionally(RandomChanceLootCondition.builder(0.6f))
                        .with(ItemEntry.builder(ImpaledItems.ANCIENT_TRIDENT)));
            }
        });
    }

    private static ImpaledTridentItem registerTrident(ImpaledTridentItem item, String name) {
        Registry.register(Registries.ITEM, Identifier.of(Impaled.MODID, name), item);
        ImpaledItems.ALL_TRIDENTS.add(item);
        DispenserBlock.registerBehavior(item, new ProjectileDispenserBehavior(item));
        return item;
    }

    private static Item registerItem(Item item, String name) {
        return Registry.register(Registries.ITEM, Identifier.of(Impaled.MODID, name), item);
    }

    private static <T extends Entity> EntityType<T> register(String s, EntityType<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, Identifier.of(Impaled.MODID, s), type);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> EntityType<T> createEntityType(EntityType.EntityFactory<T> factory, boolean trackedUpdateRate) {
        var builder = net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.create(SpawnGroup.MISC, factory)
                .dimensions(EntityDimensions.changing(0.5f, 0.5f))
                .trackRangeBlocks(4);
        if (trackedUpdateRate) builder.trackedUpdateRate(20);
        return (EntityType<T>) builder.build();
    }

    private static <T extends Entity> EntityType<T> createDynamicEntityType(EntityType.EntityFactory<T> factory) {
        return (EntityType<T>) net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder.create(SpawnGroup.MISC, factory)
                .dimensions(EntityDimensions.changing(0.5f, 0.5f))
                .trackRangeBlocks(4)
                .build();
    }
}
