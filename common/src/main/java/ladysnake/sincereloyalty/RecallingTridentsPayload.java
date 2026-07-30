package ladysnake.sincereloyalty;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RecallingTridentsPayload(int playerId, TridentRecaller.RecallStatus status) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, TridentRecaller.RecallStatus> STATUS_CODEC = PacketCodec.of(
            (status, buf) -> buf.writeEnumConstant(status),
            buf -> buf.readEnumConstant(TridentRecaller.RecallStatus.class)
    );

    public static final CustomPayload.Id<RecallingTridentsPayload> ID = new CustomPayload.Id<>(SincereLoyalty.RECALLING_MESSAGE_ID);

    public static final PacketCodec<PacketByteBuf, RecallingTridentsPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, RecallingTridentsPayload::playerId,
            STATUS_CODEC, RecallingTridentsPayload::status,
            RecallingTridentsPayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
