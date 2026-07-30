package ladysnake.sincereloyalty;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public final class NbtUtil {
    private NbtUtil() {}

    public static NbtCompound getSubNbt(ItemStack stack, String key) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null || comp.isEmpty()) return null;
        NbtCompound nbt = comp.copyNbt();
        if (!nbt.contains(key)) return null;
        return nbt.getCompound(key);
    }

    public static void modifySubNbt(ItemStack stack, String key, java.util.function.Consumer<NbtCompound> modifier) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, root -> {
            NbtCompound sub = root.contains(key) ? root.getCompound(key) : new NbtCompound();
            modifier.accept(sub);
            root.put(key, sub);
        });
    }

    public static NbtCompound getOrCreateSubNbt(ItemStack stack, String key) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound root = comp != null ? comp.copyNbt() : new NbtCompound();
        NbtCompound sub = root.contains(key) ? root.getCompound(key) : new NbtCompound();
        root.put(key, sub);
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, root);
        return sub;
    }

    public static NbtCompound getNbt(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        return comp != null ? comp.copyNbt() : new NbtCompound();
    }

    public static void setNbt(ItemStack stack, NbtCompound nbt) {
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, stack, nbt);
    }

    public static boolean hasNbt(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        return comp != null && !comp.isEmpty();
    }
}
