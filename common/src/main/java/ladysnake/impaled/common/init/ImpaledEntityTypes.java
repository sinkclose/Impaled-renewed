package ladysnake.impaled.common.init;

import ladysnake.impaled.common.entity.ElderTridentEntity;
import ladysnake.impaled.common.entity.HellforkEntity;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.entity.PitchforkEntity;
import ladysnake.impaled.common.entity.SoulforkEntity;
import net.minecraft.entity.EntityType;

/**
 * Holder class for impaled entity types. The actual registration is performed by each loader's
 * entrypoint, which assigns these fields at the appropriate time.
 */
public final class ImpaledEntityTypes {
    public static EntityType<PitchforkEntity> PITCHFORK;
    public static EntityType<HellforkEntity> HELLFORK;
    public static EntityType<SoulforkEntity> SOULFORK;
    public static EntityType<ElderTridentEntity> ELDER_TRIDENT;
    public static EntityType<ElderTridentEntity> GUARDIAN_TRIDENT;
    public static EntityType<ImpaledTridentEntity> ATLAN;

    private ImpaledEntityTypes() {}
}
