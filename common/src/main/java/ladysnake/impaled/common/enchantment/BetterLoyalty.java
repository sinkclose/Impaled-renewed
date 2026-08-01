package ladysnake.impaled.common.enchantment;

import ladysnake.sincereloyalty.LoyalTrident;
import ladysnake.sincereloyalty.NbtUtil;
import ladysnake.sincereloyalty.TridentRecaller;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;

public final class BetterLoyalty {
    public static boolean tryInsertTrident(ItemStack stack, PlayerEntity player) {
        NbtCompound tag = NbtUtil.getSubNbt(stack, LoyalTrident.MOD_NBT_KEY);
        if (tag != null) {
            TridentRecaller caller = (TridentRecaller) player;

            if (caller.getCurrentRecallStatus() == TridentRecaller.RecallStatus.RECALLING) {
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, player.getSoundCategory(), 0.7f, 0.5f);
            }

            if (tag.contains(LoyalTrident.RETURN_SLOT_NBT_KEY)) {
                int preferredSlot = tag.getInt(LoyalTrident.RETURN_SLOT_NBT_KEY);
                if (preferredSlot == -1) {
                    if (player.getOffHandStack().isEmpty()) {
                        player.equipStack(EquipmentSlot.OFFHAND, stack.copy());
                        stack.setCount(0);
                        finishReturn(stack, player);
                        return true;
                    }
                } else if (preferredSlot >= 0 && preferredSlot < player.getInventory().size()
                        && player.getInventory().getStack(preferredSlot).isEmpty()
                        && player.getInventory().insertStack(preferredSlot, stack)) {
                    finishReturn(stack, player);
                    return true;
                }
            }
        }
        return false;
    }

    public static void finishReturn(ItemStack stack, PlayerEntity player) {
        NbtCompound tag = NbtUtil.getSubNbt(stack, LoyalTrident.MOD_NBT_KEY);
        if (tag != null && tag.contains(LoyalTrident.RETURN_SLOT_NBT_KEY)) {
            NbtUtil.modifySubNbt(stack, LoyalTrident.MOD_NBT_KEY, sub -> sub.remove(LoyalTrident.RETURN_SLOT_NBT_KEY));
            ((TridentRecaller) player).updateRecallStatus(TridentRecaller.RecallStatus.NONE);
        }
    }
}
