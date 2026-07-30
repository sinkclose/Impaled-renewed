/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 */
package ladysnake.sincereloyalty;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import ladysnake.sincereloyalty.storage.LoyalTridentStorage;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public final class SincereLoyaltyFabric {

    public static void init() {
        PayloadTypeRegistry.playC2S().register(RecallTridentsPayload.ID, RecallTridentsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RecallingTridentsPayload.ID, RecallingTridentsPayload.CODEC);

        Object2IntMap<UUID> recallingPlayers = new Object2IntOpenHashMap<>();
        ServerTickEvents.START_SERVER_TICK.register(server -> recallingPlayers.object2IntEntrySet().removeIf(entry -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null) return true;

            if (entry.getIntValue() > 0) {
                entry.setValue(entry.getIntValue() - 1);
                return false;
            }

            LoyalTridentStorage loyalTridentStorage = LoyalTridentStorage.get(player.getServerWorld());
            TridentRecaller.RecallStatus newRecallStatus;
            if (loyalTridentStorage.recallTridents(player)) {
                newRecallStatus = TridentRecaller.RecallStatus.RECALLING;
            } else {
                player.sendMessage(Text.translatable("impaled:trident_recall_fail"), true);
                newRecallStatus = TridentRecaller.RecallStatus.NONE;
            }
            ((TridentRecaller) player).updateRecallStatus(newRecallStatus);
            return true;
        }));
        ServerPlayNetworking.registerGlobalReceiver(RecallTridentsPayload.ID, (payload, context) -> {
            MinecraftServer server = context.server();
            ServerPlayerEntity player = context.player();
            TridentRecaller.RecallStatus requested = payload.status();

            server.execute(() -> {
                LoyalTridentStorage loyalTridentStorage = LoyalTridentStorage.get(player.getServerWorld());
                TridentRecaller.RecallStatus currentRecallStatus = ((TridentRecaller) player).getCurrentRecallStatus();
                TridentRecaller.RecallStatus newRecallStatus;

                if (loyalTridentStorage.hasTridents(player)) {
                    if (currentRecallStatus != requested && requested == TridentRecaller.RecallStatus.RECALLING) {
                        loyalTridentStorage.loadTridents(player);
                        recallingPlayers.put(player.getUuid(), 4);
                    }
                    newRecallStatus = requested;
                } else {
                    newRecallStatus = TridentRecaller.RecallStatus.NONE;
                }

                ((TridentRecaller) player).updateRecallStatus(newRecallStatus);
            });
        });
    }

    private SincereLoyaltyFabric() {}
}
