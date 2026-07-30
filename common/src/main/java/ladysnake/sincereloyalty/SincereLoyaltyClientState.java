/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 */
package ladysnake.sincereloyalty;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Loader-agnostic client state for the trident-recall animation. The actual networking
 * (sending the recall request to the server, receiving the recall status) is handled by
 * each loader's client entrypoint, which drives {@link #tickTridentRecalling(MinecraftClient)}.
 */
public final class SincereLoyaltyClientState {
    public static final SincereLoyaltyClientState INSTANCE = new SincereLoyaltyClientState();
    public static final int RECALL_ANIMATION_START = 10;
    public static final int RECALL_TIME = 35;   // + count 5 forced ticks serverside to load chunks

    private int useTime = 0;
    private int failedUseCountdown = 0;

    private SincereLoyaltyClientState() {}

    public void setFailedUse(int itemUseCooldown) {
        this.failedUseCountdown = itemUseCooldown + 1;
    }

    @Nullable
    public TridentRecaller.RecallStatus tickTridentRecalling(MinecraftClient mc) {
        if (this.failedUseCountdown > 0) {
            PlayerEntity player = mc.player;

            if (player != null && player.getMainHandStack().isEmpty()) {
                ++this.useTime;

                if (this.useTime == RECALL_ANIMATION_START) {
                    return TridentRecaller.RecallStatus.CHARGING;
                } else if (this.useTime == RECALL_TIME) {
                    this.useTime = 0;
                    return TridentRecaller.RecallStatus.RECALLING;
                }
            }
            this.failedUseCountdown--;
        } else if (this.useTime > 0) {
            this.useTime = 0;
            return TridentRecaller.RecallStatus.NONE;
        }
        return null;
    }
}
