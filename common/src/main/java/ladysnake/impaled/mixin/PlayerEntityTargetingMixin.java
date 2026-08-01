package ladysnake.impaled.mixin;

import ladysnake.impaled.common.IPlayerTargeting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityTargetingMixin implements IPlayerTargeting {
    @Unique
    private Entity impaled$lastTarget;

    @Unique
    private int impaled$targetDecayTime;

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void impaled$updateTarget(CallbackInfo ci) {
        Entity player = (Entity) (Object) this;
        Vec3d eyePos = player.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0f);
        double range = 128.0;
        Vec3d endPos = eyePos.add(lookVec.multiply(range));
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0, 1.0, 1.0);
        EntityHitResult hit = ProjectileUtil.getEntityCollision(
                player.getWorld(),
                player,
                eyePos,
                endPos,
                searchBox,
                entity -> entity instanceof LivingEntity && entity.isAlive() && entity.canHit() && !entity.equals(player)
        );
        if (hit != null) {
            this.impaled$lastTarget = hit.getEntity();
            this.impaled$targetDecayTime = 60;
        } else if (this.impaled$targetDecayTime > 0) {
            this.impaled$targetDecayTime--;
            if (this.impaled$targetDecayTime == 0) {
                this.impaled$lastTarget = null;
            }
        }
    }

    @Override
    public Entity mialeeMisc$getLastTarget() {
        return this.impaled$lastTarget;
    }
}
