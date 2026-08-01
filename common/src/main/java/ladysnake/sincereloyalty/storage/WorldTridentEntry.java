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

import ladysnake.sincereloyalty.SincereLoyalty;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class WorldTridentEntry extends TridentEntry {
    public static final ChunkTicketType<UUID> TRIDENT_RECALL_TICKET = ChunkTicketType.create(SincereLoyalty.MOD_ID + ":trident_recall", UUID::compareTo, 10);

    private UUID tridentEntityUuid;
    private BlockPos lastPos;
    private @Nullable ChunkPos ticketPos;
    private @Nullable UUID ticketEntityUuid;

    public WorldTridentEntry(ServerWorld world, UUID tridentUuid, UUID tridentEntityUuid, BlockPos lastPos) {
        super(world, tridentUuid);
        this.tridentEntityUuid = tridentEntityUuid;
        this.lastPos = lastPos;
    }

    public WorldTridentEntry(ServerWorld world, NbtCompound tag) {
        super(world, tag);
        this.tridentEntityUuid = tag.getUuid("trident_entity_uuid");
        this.lastPos = NbtHelper.toBlockPos(tag, "last_pos").orElse(BlockPos.ORIGIN);
    }

    @Override
    public NbtCompound toNbt(NbtCompound nbt) {
        super.toNbt(nbt);
        nbt.putString("type", "world");
        nbt.putUuid("trident_entity_uuid", this.tridentEntityUuid);
        nbt.put("last_pos", NbtHelper.fromBlockPos(this.lastPos));
        return nbt;
    }

    public boolean updateLastPos(UUID tridentEntityUuid, BlockPos pos) {
        if (this.tridentEntityUuid.equals(tridentEntityUuid) && this.lastPos.equals(pos)) {
            return false;
        }
        this.tridentEntityUuid = tridentEntityUuid;
        this.lastPos = pos;
        return true;
    }

    @Override
    public void preloadTrident() {
        this.releaseTicket();
        ChunkPos pos = new ChunkPos(this.lastPos);
        this.world.getChunk(pos.x, pos.z);  // just loading it
        this.world.getChunkManager().addTicket(TRIDENT_RECALL_TICKET, pos, 0, this.tridentEntityUuid);
        this.ticketPos = pos;
        this.ticketEntityUuid = this.tridentEntityUuid;
    }

    @Override
    public TridentEntity findTrident() {
        Entity trident = this.world.getEntity(this.tridentEntityUuid);
        if (trident instanceof TridentEntity) {
            return (TridentEntity) trident;
        }
        return null;
    }

    public void releaseTicket() {
        if (this.ticketPos != null && this.ticketEntityUuid != null) {
            this.world.getChunkManager().removeTicket(TRIDENT_RECALL_TICKET, this.ticketPos, 0, this.ticketEntityUuid);
            this.ticketPos = null;
            this.ticketEntityUuid = null;
        }
    }

}
