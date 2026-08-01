package ladysnake.impaled.mixin;

import ladysnake.impaled.common.DropCapture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityDropMixin {
    @Inject(method = "dropStack", at = @At("HEAD"), cancellable = true)
    private void impaled$captureDrop(ItemStack stack, CallbackInfoReturnable<ItemEntity> cir) {
        if ((Object) this instanceof DropCapture capture) {
            capture.impaled$captureDrop(stack, cir);
        }
    }
}
