package ladysnake.impaled.common.item;

import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.sincereloyalty.LoyalTrident;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ImpaledTridentItem extends TridentItem {
    EntityType<? extends ImpaledTridentEntity> type;

    public ImpaledTridentItem(Item.Settings settings, EntityType<? extends ImpaledTridentEntity> entityType) {
        super(settings);
        this.type = entityType;
    }

    public EntityType<? extends ImpaledTridentEntity> getEntityType() {
        return type;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            int i = this.getMaxUseTime(stack, user) - remainingUseTicks;
            if (i >= 10) {
                float f = EnchantmentHelper.getTridentSpinAttackStrength(stack, player);
                if (!(f > 0.0F) || canRiptide(player)) {
                    RegistryEntry<SoundEvent> registryEntry = EnchantmentHelper.getEffect(stack, EnchantmentEffectComponentTypes.TRIDENT_SOUND)
                            .orElse(SoundEvents.ITEM_TRIDENT_THROW);
                    if (!world.isClient) {
                        stack.damage(1, player, LivingEntity.getSlotForHand(user.getActiveHand()));
                        if (f == 0.0F) {
                            ImpaledTridentEntity trident = createTrident(world, player, stack);
                            LoyalTrident.of(trident).loyaltrident_setReturnSlot(player.getActiveHand() == Hand.OFF_HAND ? -1 : player.getInventory().selectedSlot);

                            if (player.getAbilities().creativeMode) {
                                trident.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                            }

                            if (!world.spawnEntity(trident)) {
                                return;
                            }
                            world.playSoundFromEntity(null, trident, registryEntry.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                            if (!player.getAbilities().creativeMode) {
                                player.getInventory().removeOne(stack);
                            }
                        }
                    }

                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    if (f > 0.0F) {
                        float g = player.getYaw();
                        float h = player.getPitch();
                        float j = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
                        float k = -MathHelper.sin(h * (float) (Math.PI / 180.0));
                        float l = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(h * (float) (Math.PI / 180.0));
                        float m = MathHelper.sqrt(j * j + k * k + l * l);
                        float n = f;
                        j *= n / m;
                        k *= n / m;
                        l *= n / m;
                        player.addVelocity(j, k, l);
                        player.useRiptide(20, 8.0F, stack);
                        if (player.isOnGround()) {
                            player.move(MovementType.SELF, new Vec3d(0.0, 1.1999999284744263D, 0.0));
                        }

                        world.playSoundFromEntity(null, player, registryEntry.value(), SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    protected boolean canRiptide(PlayerEntity playerEntity) {
        return playerEntity.isTouchingWaterOrRain();
    }

    public @NotNull ImpaledTridentEntity createTrident(World world, LivingEntity user, ItemStack stack) {
        ImpaledTridentEntity impaledTridentEntity = Objects.requireNonNull(this.type.create(world));
        impaledTridentEntity.setTridentAttributes(stack);
        impaledTridentEntity.setOwner(user);
        impaledTridentEntity.setTridentStack(stack);
        impaledTridentEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 2.5F, 1.0F);
        impaledTridentEntity.updatePosition(user.getX(), user.getEyeY() - 0.1, user.getZ());
        return impaledTridentEntity;
    }
}
