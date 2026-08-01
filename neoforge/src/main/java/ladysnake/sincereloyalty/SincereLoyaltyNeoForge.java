/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 */
package ladysnake.sincereloyalty;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import ladysnake.sincereloyalty.storage.LoyalTridentStorage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.UUID;

public final class SincereLoyaltyNeoForge {
    private static final Object2IntMap<UUID> RECALLING_PLAYERS = new Object2IntOpenHashMap<>();

    public static void register(IEventBus modBus) {
        modBus.addListener(SincereLoyaltyNeoForge::registerPayloads);
        NeoForge.EVENT_BUS.addListener(SincereLoyaltyNeoForge::onServerTick);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        // C2S: client requests a recall status change -> handled on the server.
        registrar.playToServer(RecallTridentsPayload.ID, RecallTridentsPayload.CODEC, SincereLoyaltyNeoForge::onRecallRequest);
        // S2C: server broadcasts a player's recall status.
        // The payload type must be registered on both sides (server sends, client receives),
        // but the handler must only touch client classes on the physical client to avoid
        // loading them on a dedicated server.
        if (FMLEnvironment.dist.isClient()) {
            registrar.playToClient(RecallingTridentsPayload.ID, RecallingTridentsPayload.CODEC, SincereLoyaltyNeoForgeClient::onRecalling);
        } else {
            registrar.playToClient(RecallingTridentsPayload.ID, RecallingTridentsPayload.CODEC, (payload, context) -> {});
        }
    }

    private static void onRecallRequest(RecallTridentsPayload payload, IPayloadContext context) {
        ServerPlayerEntity player = (ServerPlayerEntity) context.player();
        MinecraftServer server = player.getServer();
        if (server == null) return;
        TridentRecaller.RecallStatus requested = payload.status();

        LoyalTridentStorage storage = LoyalTridentStorage.get(player.getServerWorld());
        if (requested == TridentRecaller.RecallStatus.NONE) {
            storage.releaseTickets(player);
            RECALLING_PLAYERS.remove(player.getUuid());
            ((TridentRecaller) player).updateRecallStatus(requested);
            return;
        }
        TridentRecaller.RecallStatus current = ((TridentRecaller) player).getCurrentRecallStatus();
        TridentRecaller.RecallStatus next;

        if (storage.hasTridents(player)) {
            if (current == TridentRecaller.RecallStatus.CHARGING
                    && requested == TridentRecaller.RecallStatus.RECALLING
                    && !RECALLING_PLAYERS.containsKey(player.getUuid())) {
                storage.loadTridents(player);
                RECALLING_PLAYERS.put(player.getUuid(), 4);
            }
            next = requested == TridentRecaller.RecallStatus.RECALLING
                    && current != TridentRecaller.RecallStatus.CHARGING
                    ? current
                    : requested;
        } else {
            next = TridentRecaller.RecallStatus.NONE;
        }
        ((TridentRecaller) player).updateRecallStatus(next);
    }

    private static void onServerTick(ServerTickEvent.Pre event) {
        MinecraftServer server = event.getServer();
        RECALLING_PLAYERS.object2IntEntrySet().removeIf(entry -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
            if (player == null) return true;

            if (entry.getIntValue() > 0) {
                entry.setValue(entry.getIntValue() - 1);
                return false;
            }

            LoyalTridentStorage storage = LoyalTridentStorage.get(player.getServerWorld());
            TridentRecaller.RecallStatus next;
            if (storage.recallTridents(player)) {
                next = TridentRecaller.RecallStatus.RECALLING;
            } else {
                player.sendMessage(Text.translatable("impaled:trident_recall_fail"), true);
                next = TridentRecaller.RecallStatus.NONE;
            }
            ((TridentRecaller) player).updateRecallStatus(next);
            return true;
        });
    }

    private SincereLoyaltyNeoForge() {}
}
