package ladysnake.impaled.mixin;

import ladysnake.impaled.common.entity.ElderTridentEntity;
import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.impaled.common.DropCapture;
import ladysnake.sincereloyalty.LoyalTrident;
import ladysnake.sincereloyalty.SincereLoyalty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements DropCapture {
    private Consumer<ItemStack> impaled$dropSink;

    @Inject(method = "drop", at = @At("HEAD"))
    private void drop(ServerWorld world, DamageSource source, CallbackInfo ci) {
        Entity directSource = source.getSource();

        if (directSource instanceof ElderTridentEntity elderTrident) {
            this.impaled$dropSink = elderTrident.getStackFetcher();
        }

        if (((Object) this) instanceof ElderGuardianEntity && (directSource instanceof PlayerEntity player && player.getMainHandStack().isIn(SincereLoyalty.TRIDENTS) || (directSource instanceof TridentEntity trident && LoyalTrident.getLoyaltyLevel(trident.getWeaponStack()) > 0))) {
            Entity self = (Entity) (Object) this;
            self.dropStack(new ItemStack(ImpaledItems.ELDER_GUARDIAN_EYE));
            world.playSound(self.getX(), self.getY(), self.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.NEUTRAL, 1.0f, 1.0f, true);
        }
    }

    @Inject(method = "drop", at = @At("RETURN"))
    private void impaled$finishDrop(ServerWorld world, DamageSource source, CallbackInfo ci) {
        this.impaled$dropSink = null;
    }

    @Override
    public void impaled$captureDrop(ItemStack stack, CallbackInfoReturnable<net.minecraft.entity.ItemEntity> cir) {
        if (this.impaled$dropSink != null) {
            this.impaled$dropSink.accept(stack.copy());
            cir.setReturnValue(null);
        }
    }
}
