package ladysnake.impaled.common.init;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import ladysnake.impaled.common.item.ImpaledTridentItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.TridentItem;

import java.util.Set;

/**
 * Holder class for impaled items. The actual registration is performed by each loader's
 * entrypoint, which assigns these fields at the appropriate time.
 */
public final class ImpaledItems {
    public static final Set<ImpaledTridentItem> ALL_TRIDENTS = new ReferenceOpenHashSet<>();
    public static Item ELDER_GUARDIAN_EYE;
    public static Item ANCIENT_TRIDENT;
    public static Item PITCHFORK;
    public static Item HELLFORK;
    public static Item SOULFORK;
    public static Item ELDER_TRIDENT;
    public static Item ATLAN;
    public static Item MAELSTROM;

    public static Item.Settings tridentSettings(int maxDamage) {
        return new Item.Settings()
                .maxDamage(maxDamage)
                .component(DataComponentTypes.TOOL, TridentItem.createToolComponent())
                .attributeModifiers(TridentItem.createAttributeModifiers());
    }

    private ImpaledItems() {}
}
