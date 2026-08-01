package ladysnake.impaled.common.item;

import ladysnake.sincereloyalty.SincereLoyalty;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class MaelstromItem extends RangedWeaponItem {
    public MaelstromItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player && world instanceof ServerWorld serverWorld) {
            RegistryEntry<Enchantment> efficiency = serverWorld.getRegistryManager()
                    .getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
                    .getOrThrow(Enchantments.EFFICIENCY);
            int level = EnchantmentHelper.getEnchantments(stack).getLevel(efficiency);
            player.getItemCooldownManager().set(this, 20 - (3 * level));
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.success(itemStack);
    }

    @Override
    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int i, float v1, float v2, float v3, LivingEntity livingEntity) {
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack maelstromStack, int remainingUseTicks) {
        super.usageTick(world, user, maelstromStack, remainingUseTicks);
        if (!(world instanceof ServerWorld serverWorld) || !(user instanceof PlayerEntity playerEntity)) {
            return;
        }
        RegistryEntry<Enchantment> efficiency = serverWorld.getRegistryManager()
                .getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
                .getOrThrow(Enchantments.EFFICIENCY);
        int level = EnchantmentHelper.getEnchantments(maelstromStack).getLevel(efficiency);
        int interval = Math.max(1, 20 - (3 * level));
        if (remainingUseTicks % interval != 0) {
            return;
        }

        Inventory inventory = playerEntity.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stackToThrow = inventory.getStack(i);
            if (stackToThrow.isEmpty() || !stackToThrow.isIn(SincereLoyalty.TRIDENTS)) {
                continue;
            }
            if (EnchantmentHelper.getTridentSpinAttackStrength(stackToThrow, user) > 0.0F) {
                continue;
            }

            TridentEntity trident = null;
            if (stackToThrow.getItem() instanceof ImpaledTridentItem) {
                trident = ((ImpaledTridentItem) stackToThrow.getItem()).createTrident(world, user, stackToThrow);
            } else if (stackToThrow.getItem() instanceof TridentItem) {
                trident = new TridentEntity(world, user, stackToThrow);
                trident.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F, 1.0F);
            }

            if (trident != null) {
                if (playerEntity.getAbilities().creativeMode) {
                    trident.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }
                if (!world.spawnEntity(trident)) {
                    continue;
                }
                stackToThrow.damage(1, user, LivingEntity.getSlotForHand(user.getActiveHand()));
                maelstromStack.damage(1, user, LivingEntity.getSlotForHand(user.getActiveHand()));
                world.playSoundFromEntity(null, playerEntity, SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!playerEntity.getAbilities().creativeMode) {
                    playerEntity.getInventory().removeOne(stackToThrow);
                }
                playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                break;
            }
        }
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return itemStack -> itemStack.isIn(SincereLoyalty.TRIDENTS);
    }

    @Override
    public int getRange() {
        return 15;
    }
}
