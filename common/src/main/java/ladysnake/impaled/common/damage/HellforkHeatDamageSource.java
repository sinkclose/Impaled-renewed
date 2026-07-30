package ladysnake.impaled.common.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class HellforkHeatDamageSource {
    public static final RegistryKey<net.minecraft.entity.damage.DamageType> HELLFORK_HEAT_KEY =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("impaled", "hellfork_heat"));

    public static DamageSource create(World world) {
        RegistryEntry<net.minecraft.entity.damage.DamageType> entry = world.getRegistryManager()
                .getWrapperOrThrow(RegistryKeys.DAMAGE_TYPE)
                .getOrThrow(HELLFORK_HEAT_KEY);
        return new DamageSource(entry);
    }

    private HellforkHeatDamageSource() {}
}
