package ladysnake.impaled.mixin;

import ladysnake.impaled.common.item.HellforkItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityFireRenderMixin {
    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private void impaled$hideHellforkRiptideFire(MatrixStack matrices, VertexConsumerProvider vertices,
                                                  Entity entity, Quaternionf rotation, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player && player.isUsingRiptide()
                && (player.getMainHandStack().getItem() instanceof HellforkItem
                || player.getOffHandStack().getItem() instanceof HellforkItem)) {
            ci.cancel();
        }
    }
}
