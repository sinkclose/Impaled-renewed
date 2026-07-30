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
 * along with this program; If not, see <https://www.gnu.org/licenses/>.
 */
package ladysnake.sincereloyalty;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public final class LoyalTridentTooltip {
    private LoyalTridentTooltip() {}

    public static void appendOwnedByTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipType tooltipType, List<Text> lines) {
        NbtCompound loyaltyNbt = NbtUtil.getSubNbt(stack, LoyalTrident.MOD_NBT_KEY);
        if (loyaltyNbt != null && loyaltyNbt.contains(LoyalTrident.OWNER_NAME_NBT_KEY)) {
            String ownerName = loyaltyNbt.getString(LoyalTrident.OWNER_NAME_NBT_KEY);
            lines.add(Text.translatable("impaled:tooltip.owned_by", ownerName).formatted(Formatting.DARK_GRAY));
        }
    }
}
