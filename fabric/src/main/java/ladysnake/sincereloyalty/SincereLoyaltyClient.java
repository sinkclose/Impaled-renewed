/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 */
package ladysnake.sincereloyalty;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public final class SincereLoyaltyClient implements ClientModInitializer {
    public static final SincereLoyaltyClient INSTANCE = new SincereLoyaltyClient();

    @Override
    public void onInitializeClient() {
        // The "owned by" tooltip is appended by the common ItemStackTooltipMixin (applies to both
        // Fabric and NeoForge) instead of a loader-specific ItemTooltipCallback.
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            TridentRecaller.RecallStatus recalling = SincereLoyaltyClientState.INSTANCE.tickTridentRecalling(mc);
            if (recalling != null) {
                ClientPlayNetworking.send(new RecallTridentsPayload(recalling));
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(RecallingTridentsPayload.ID, (payload, context) -> {
            int playerId = payload.playerId();
            TridentRecaller.RecallStatus recalling = payload.status();
            MinecraftClient client = context.client();
            client.execute(() -> {
                Entity player = client.world.getEntityById(playerId);
                if (player instanceof TridentRecaller) {
                    ((TridentRecaller) player).updateRecallStatus(recalling);
                }
            });
        });
    }
}
