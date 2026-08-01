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
    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true, remap = false)
    private void impaled$captureDrop(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
        if ((Object) this instanceof DropCapture capture) {
            capture.impaled$captureDrop(stack, cir);
        }
    }
}
