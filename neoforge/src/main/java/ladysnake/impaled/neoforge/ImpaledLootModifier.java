package ladysnake.impaled.neoforge;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.condition.LootCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

/**
 * Adds the Ancient Trident to the bastion treasure loot table with a 60% chance.
 * Activation is restricted to the bastion treasure table via a {@code neoforge:loot_table_id}
 * condition declared in the modifier JSON.
 */
public class ImpaledLootModifier extends LootModifier {
    public static final MapCodec<ImpaledLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).apply(inst, ImpaledLootModifier::new));

    public ImpaledLootModifier(LootCondition[] conditions) {
        super(conditions);
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (ImpaledItems.ANCIENT_TRIDENT != null && context.getRandom().nextFloat() < 0.6f) {
            generatedLoot.add(new ItemStack(ImpaledItems.ANCIENT_TRIDENT));
        }
        return generatedLoot;
    }
}
