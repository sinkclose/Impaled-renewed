package ladysnake.sincereloyalty;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record RecallTridentsPayload(TridentRecaller.RecallStatus status) implements CustomPayload {
    public static final CustomPayload.Id<RecallTridentsPayload> ID = new CustomPayload.Id<>(SincereLoyalty.RECALL_TRIDENTS_MESSAGE_ID);

    public static final PacketCodec<PacketByteBuf, RecallTridentsPayload> CODEC = PacketCodec.tuple(
            RecallingTridentsPayload.STATUS_CODEC, RecallTridentsPayload::status,
            RecallTridentsPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
