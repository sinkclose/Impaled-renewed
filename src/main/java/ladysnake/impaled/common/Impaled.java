package ladysnake.impaled.common;

import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class Impaled implements ModInitializer {
    public static final String MODID = "impaled";

    private static final RegistryKey<net.minecraft.loot.LootTable> BASTION_TREASURE_CHEST_LOOT_TABLE_ID =
            RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("minecraft", "chests/bastion_treasure"));

    @Override
    public void onInitialize() {
        ImpaledEntityTypes.init();
        ImpaledItems.init();

        UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
        RandomChanceLootCondition.builder(0.6f).build();
        LootTableEvents.MODIFY.register((id, builder, source) -> {
            if (BASTION_TREASURE_CHEST_LOOT_TABLE_ID.equals(id) && source.isBuiltin()) {
                LootPool lootPool = LootPool.builder()
                        .rolls(lootTableRange)
                        .conditionally(RandomChanceLootCondition.builder(0.6f))
                        .with(ItemEntry.builder(ImpaledItems.ANCIENT_TRIDENT)).build();

                builder.pool(LootPool.builder()
                        .rolls(lootTableRange)
                        .conditionally(RandomChanceLootCondition.builder(0.6f))
                        .with(ItemEntry.builder(ImpaledItems.ANCIENT_TRIDENT)));
            }
        });
    }
}