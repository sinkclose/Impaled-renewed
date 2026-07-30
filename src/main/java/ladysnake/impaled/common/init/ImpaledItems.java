package ladysnake.impaled.common.init;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.item.*;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.Set;

public class ImpaledItems {
    public static final Set<ImpaledTridentItem> ALL_TRIDENTS = new ReferenceOpenHashSet<>();
    public static Item ELDER_GUARDIAN_EYE;
    public static Item ANCIENT_TRIDENT;
    public static Item PITCHFORK;
    public static Item HELLFORK;
    public static Item SOULFORK;
    public static Item ELDER_TRIDENT;
    public static Item ATLAN;
    public static Item MAELSTROM;

    public static void init() {
        ELDER_GUARDIAN_EYE = registerItem(new Item((new Item.Settings()).rarity(Rarity.UNCOMMON)), "elder_guardian_eye");
        ANCIENT_TRIDENT = registerItem(new Item((new Item.Settings()).rarity(Rarity.UNCOMMON).fireproof()), "ancient_trident");

        PITCHFORK = registerTrident(new PitchforkItem(tridentSettings(150), ImpaledEntityTypes.PITCHFORK), "pitchfork", true);
        HELLFORK = registerTrident(new HellforkItem(tridentSettings(325).fireproof(), ImpaledEntityTypes.HELLFORK), "hellfork", true);
        SOULFORK = registerTrident(new HellforkItem(tridentSettings(325).fireproof(), ImpaledEntityTypes.SOULFORK), "soulfork", true);
        ELDER_TRIDENT = registerTrident(new ElderTridentItem(tridentSettings(250), ImpaledEntityTypes.ELDER_TRIDENT), "elder_trident", true);
        ATLAN = registerTrident(new AtlanItem(tridentSettings(250), ImpaledEntityTypes.ATLAN), "atlan", true);
        MAELSTROM = registerItem(new MaelstromItem(new Item.Settings().maxDamage(80)), "maelstrom");
    }

    private static Item.Settings tridentSettings(int maxDamage) {
        return new Item.Settings()
                .maxDamage(maxDamage)
                .component(DataComponentTypes.TOOL, TridentItem.createToolComponent())
                .attributeModifiers(TridentItem.createAttributeModifiers());
    }

    public static ImpaledTridentItem registerTrident(ImpaledTridentItem item, String name, boolean registerDispenserBehavior) {
        Registry.register(Registries.ITEM, Identifier.of(Impaled.MODID, name), item);
        ALL_TRIDENTS.add(item);
        if (registerDispenserBehavior) {
            DispenserBlock.registerBehavior(item, new ProjectileDispenserBehavior(item));
        }
        return item;
    }

    public static Item registerItem(Item item, String name) {
        Registry.register(Registries.ITEM, Identifier.of(Impaled.MODID, name), item);
        return item;
    }
}
