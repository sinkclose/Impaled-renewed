package ladysnake.impaled.common;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class Impaled {
    public static final String MODID = "impaled";

    public static final RegistryKey<net.minecraft.loot.LootTable> BASTION_TREASURE_CHEST_LOOT_TABLE_ID =
            RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("minecraft", "chests/bastion_treasure"));

    private Impaled() {}
}
