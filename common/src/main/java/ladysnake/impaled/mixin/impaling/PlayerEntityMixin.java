package ladysnake.impaled.mixin.impaling;

import ladysnake.impaled.common.enchantment.BetterImpaling;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getDamageAgainst", at = @At("RETURN"), cancellable = true)
    private void impaled$addImpalingDamage(Entity target, float baseDamage, DamageSource source, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValueF() + BetterImpaling.getAttackDamage(this.getMainHandStack(), target, this.getWorld()));
    }
}
