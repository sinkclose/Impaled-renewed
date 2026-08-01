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
package ladysnake.sincereloyalty.storage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class OwnedTridents implements Iterable<TridentEntry> {
    static final OwnedTridents EMPTY = new OwnedTridents();

    private final LoyalTridentStorage parentStorage;
    private final Map<UUID, TridentEntry> ownedTridents;

    private OwnedTridents() {
        this.parentStorage = null;
        this.ownedTridents = Collections.emptyMap();
    }

    OwnedTridents(LoyalTridentStorage parentStorage) {
        this.parentStorage = parentStorage;
        this.ownedTridents = new HashMap<>();
    }

    public boolean storeTridentPosition(UUID tridentUuid, UUID tridentEntityUuid, BlockPos lastPos) {
        TridentEntry entry = this.ownedTridents.get(tridentUuid);
        if (entry instanceof WorldTridentEntry) {
            return ((WorldTridentEntry) entry).updateLastPos(tridentEntityUuid, lastPos);
        } else {
            this.ownedTridents.put(tridentUuid, new WorldTridentEntry(this.parentStorage.world, tridentUuid, tridentEntityUuid, lastPos));
            return true;
        }
    }

    public boolean storeTridentHolder(UUID tridentUuid, PlayerEntity holder) {
        TridentEntry entry = this.ownedTridents.get(tridentUuid);
        if (!(entry instanceof InventoryTridentEntry) || !((InventoryTridentEntry) entry).isHolder(holder)) {
            this.addEntry(new InventoryTridentEntry(this.parentStorage.world, tridentUuid, holder.getUuid()));
            return true;
        }
        return false;
    }

    private void addEntry(@NotNull TridentEntry entry) {
        this.ownedTridents.put(entry.getTridentUuid(), entry);
    }

    public boolean clearTridentPosition(UUID tridentUuid) {
        return this.ownedTridents.remove(tridentUuid) != null;
    }

    @NotNull
    @Override
    public Iterator<TridentEntry> iterator() {
        return this.ownedTridents.values().iterator();
    }

    public boolean isEmpty() {
        return this.ownedTridents.isEmpty();
    }

    public void fromTag(NbtCompound ownerNbt) {
        NbtList tridentsNbt = ownerNbt.getList("tridents", NbtElement.COMPOUND_TYPE);
        for (int j = 0; j < tridentsNbt.size(); j++) {
            TridentEntry trident = TridentEntry.fromNbt(this.parentStorage.world, tridentsNbt.getCompound(j));
            if (trident != null) {
                this.addEntry(trident);
            }
        }
    }

    public void toTag(NbtCompound ownerNbt) {
        NbtList tridentsNbt = new NbtList();
        for (TridentEntry trident : this.ownedTridents.values()) {
            tridentsNbt.add(trident.toNbt(new NbtCompound()));
        }
        ownerNbt.put("tridents", tridentsNbt);
    }
}
