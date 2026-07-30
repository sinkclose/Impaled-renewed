/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 */
package ladysnake.sincereloyalty.mixin;

import ladysnake.sincereloyalty.RecallingTridentsPayload;
import ladysnake.sincereloyalty.TridentRecaller;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements TridentRecaller {
    @NotNull
    @Unique
    private RecallStatus recallingTrident = RecallStatus.NONE;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public RecallStatus getCurrentRecallStatus() {
        return this.recallingTrident;
    }

    @Override
    public void updateRecallStatus(RecallStatus recallingTrident) {
        if (this.recallingTrident != recallingTrident) {
            this.recallingTrident = recallingTrident;
            if (!this.getWorld().isClient) {
                ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
                RecallingTridentsPayload payload = new RecallingTridentsPayload(this.getId(), recallingTrident);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(self, payload);
            }
        }
    }
}
