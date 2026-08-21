package org.ladysnake.impaled.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.impaled.common.entity.ElderTridentEntity;
import org.ladysnake.impaled.common.init.ImpaledItems;
import org.ladysnake.sincereloyalty.SincereLoyalty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends EntityMixin {
    private @Nullable Consumer<ItemStack> impaled$dropSink;

    @Inject(method = "drop", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;shouldDropLoot()Z"))
    private void drop(DamageSource source, CallbackInfo ci) {
        Entity directSource = source.getSource();

        if (directSource instanceof ElderTridentEntity) {
            this.impaled$dropSink = ((ElderTridentEntity) directSource).getStackFetcher();
        }

        if (((Object) this) instanceof ElderGuardianEntity) {
            boolean dropEye = false;
            if (directSource instanceof PlayerEntity player) {
                ItemStack mainHandStack = player.getMainHandStack();
                if (mainHandStack.isIn(SincereLoyalty.TRIDENTS) || EnchantmentHelper.getLevel(Enchantments.IMPALING, mainHandStack) > 0) {
                    dropEye = true;
                }
            } else if (directSource instanceof TridentEntity) {
                ItemStack tridentStack = ((TridentEntityAccessor) directSource).impaled$getTridentStack();
                if (EnchantmentHelper.getLoyalty(tridentStack) > 0 || EnchantmentHelper.getLevel(Enchantments.IMPALING, tridentStack) > 0) {
                    dropEye = true;
                }
            }

            if (dropEye) {
                this.dropStack(new ItemStack(ImpaledItems.ELDER_GUARDIAN_EYE));
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.NEUTRAL, 1.0f, 1.0f, true);
            }
        }
    }

    @Inject(method = "drop", at = @At("RETURN"))
    private void endDrop(DamageSource source, CallbackInfo ci) {
        this.impaled$dropSink = null;
    }

    @Override
    protected void impaled$dropStack(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
        if (this.impaled$dropSink != null) {
            this.impaled$dropSink.accept(stack);
            cir.setReturnValue(null);
        }
    }
}
