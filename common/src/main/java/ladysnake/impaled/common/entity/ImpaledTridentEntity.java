package ladysnake.impaled.common.entity;

import ladysnake.impaled.mixin.TridentEntityAccessor;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class ImpaledTridentEntity extends TridentEntity {
    public ImpaledTridentEntity(EntityType<? extends ImpaledTridentEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setTridentAttributes(ItemStack stack) {
        this.setStack(stack.copy());
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            byte loyalty = (byte) Math.max(0, EnchantmentHelper.getTridentReturnAcceleration(serverWorld, stack, this));
            this.getDataTracker().set(TridentEntityAccessor.impaled$getLoyalty(), loyalty);
        }
        this.getDataTracker().set(TridentEntityAccessor.impaled$getEnchanted(), stack.hasGlint());
    }

    protected float getDragInWater() {
        return 0.99f;
    }

    public void setTridentStack(ItemStack tridentStack) {
        this.setStack(tridentStack.copy());
    }

    protected void setDealtDamage() {
        ((TridentEntityAccessor) this).impaled$setDealtDamage(true);
    }

    protected boolean hasDealtDamage() {
        return ((TridentEntityAccessor) this).impaled$hasDealtDamage();
    }

    protected boolean hasChanneling(ItemStack stack) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            RegistryEntry<net.minecraft.enchantment.Enchantment> channeling = serverWorld.getRegistryManager()
                    .getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
                    .getOrThrow(net.minecraft.enchantment.Enchantments.CHANNELING);
            return EnchantmentHelper.getEnchantments(stack).getLevel(channeling) > 0;
        }
        return false;
    }
}
