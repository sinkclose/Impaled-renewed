/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package ladysnake.sincereloyalty.mixin;

import ladysnake.sincereloyalty.LoyalTrident;
import ladysnake.sincereloyalty.NbtUtil;
import ladysnake.sincereloyalty.SincereLoyalty;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "canTakeOutput", at = @At("RETURN"), cancellable = true)
    private void canTakeResult(PlayerEntity playerEntity, boolean resultNonEmpty, CallbackInfoReturnable<Boolean> cir) {
        if (resultNonEmpty && !cir.getReturnValueZ()) {
            ItemStack item = this.input.getStack(1);
            ItemStack upgradeItem = this.input.getStack(2);
            cir.setReturnValue(item.isIn(SincereLoyalty.TRIDENTS) && upgradeItem.isIn(SincereLoyalty.LOYALTY_CATALYSTS));
        }
    }

    @Inject(method = "updateResult", at = @At("RETURN"))
    private void impaled$updateResult(CallbackInfo ci) {
        ItemStack item = this.input.getStack(1);
        ItemStack upgradeItem = this.input.getStack(2);
        if (item.isIn(SincereLoyalty.TRIDENTS) && upgradeItem.isIn(SincereLoyalty.LOYALTY_CATALYSTS)) {
            ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(item);
            RegistryEntry<Enchantment> loyaltyEntry = null;
            int loyaltyLevel = 0;
            for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
                if (entry.matchesKey(Enchantments.LOYALTY)) {
                    loyaltyEntry = entry;
                    loyaltyLevel = enchantments.getLevel(entry);
                    break;
                }
            }
            int maxLoyaltyLevel = 3;
            if (loyaltyEntry != null && loyaltyLevel == maxLoyaltyLevel) {
                ItemStack result = item.copy();
                RegistryEntry<Enchantment> finalLoyaltyEntry = loyaltyEntry;
                EnchantmentHelper.apply(result, builder -> builder.set(finalLoyaltyEntry, maxLoyaltyLevel + 1));
                NbtUtil.modifySubNbt(result, LoyalTrident.MOD_NBT_KEY, loyaltyData -> {
                    loyaltyData.putUuid(LoyalTrident.TRIDENT_OWNER_NBT_KEY, this.player.getUuid());
                    loyaltyData.putString(LoyalTrident.OWNER_NAME_NBT_KEY, this.player.getNameForScoreboard());
                });
                this.output.setStack(0, result);
            } else {
                this.output.setStack(0, ItemStack.EMPTY);
            }
        }
    }
}
