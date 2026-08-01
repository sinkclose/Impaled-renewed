package ladysnake.impaled.common;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface DropCapture {
    void impaled$captureDrop(ItemStack stack, CallbackInfoReturnable<ItemEntity> cir);
}
