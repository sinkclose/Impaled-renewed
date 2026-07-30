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

import ladysnake.impaled.common.Impaled;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class SincereLoyalty {

    public static final String MOD_ID = Impaled.MODID;

    public static final TagKey<Item> LOYALTY_CATALYSTS = TagKey.of(RegistryKeys.ITEM, id("loyalty_catalysts"));
    public static final TagKey<Item> TRIDENTS = TagKey.of(RegistryKeys.ITEM, id("tridents"));

    public static final Identifier RECALL_TRIDENTS_MESSAGE_ID = id("recall_tridents");
    public static final Identifier RECALLING_MESSAGE_ID = id("recalling_tridents");

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    private SincereLoyalty() {}
}
