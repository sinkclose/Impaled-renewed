/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 */
package ladysnake.sincereloyalty;

import net.minecraft.client.MinecraftClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-only driver for the trident-recall animation: ticks the shared client state each
 * client tick, sends any status change to the server, and applies received recall-status
 * updates from the server.
 */
public final class SincereLoyaltyNeoForgeClient {

    public static void register() {
        NeoForge.EVENT_BUS.addListener(SincereLoyaltyNeoForgeClient::onClientTick);
    }

    public static void onRecalling(RecallingTridentsPayload payload, IPayloadContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> SincereLoyaltyClientState.INSTANCE.queueRecallStatus(payload.playerId(), payload.status()));
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        TridentRecaller.RecallStatus status = SincereLoyaltyClientState.INSTANCE.tickTridentRecalling(mc);
        if (status != null) {
            PacketDistributor.sendToServer(new RecallTridentsPayload(status));
        }
    }

    private SincereLoyaltyNeoForgeClient() {}
}
