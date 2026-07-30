package ladysnake.impaled.mixin;

import ladysnake.impaled.common.IPlayerTargeting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityTargetingMixin implements IPlayerTargeting {
    @Unique
    private Entity impaled$lastTarget;

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void impaled$updateTarget(CallbackInfo ci) {
        HitResult hit = ProjectileUtil.getCollision((Entity) (Object) this, entity -> entity.isAlive() && entity.canHit(), 64.0);
        if (hit.getType() == HitResult.Type.ENTITY) {
            this.impaled$lastTarget = ((EntityHitResult) hit).getEntity();
        } else {
            this.impaled$lastTarget = null;
        }
    }

    @Override
    public Entity mialeeMisc$getLastTarget() {
        return this.impaled$lastTarget;
    }
}
