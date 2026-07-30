package ladysnake.impaled.common.enchantment;

import ladysnake.impaled.common.item.HellforkItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;

public final class BetterImpaling {
    public static float getAttackDamage(ItemStack stack, Entity target, World world) {
        RegistryEntry<Enchantment> impaling = world.getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.IMPALING);
        int impalingLevel = EnchantmentHelper.getEnchantments(stack).getLevel(impaling);

        if (impalingLevel > 0) {
            if (stack.getItem() instanceof HellforkItem) {
                if (isFireImmune(target)) {
                    return impalingLevel * 2F;
                }
            } else if (target.isWet()) {
                return impalingLevel * 1.5F;
            }
        }

        return 0;
    }

    private static boolean isFireImmune(Entity target) {
        if (target.isFireImmune()) return true;
        if (!(target instanceof LivingEntity)) return false;
        return ((LivingEntity) target).hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
    }
}
