package org.ladysnake.impaled.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.ladysnake.impaled.common.Impaled;

import java.util.stream.Stream;

public class ImpaledSmithingRecipe implements SmithingRecipe {
    public static void register() {
        Registry.register(Registries.RECIPE_SERIALIZER, Impaled.id("smithing_upgrade"), Serializer.INSTANCE);
    }

    private final Identifier id;
    final Ingredient base;
    final Ingredient addition;
    final ItemStack result;

    public ImpaledSmithingRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        this.id = id;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return true;
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return this.base.test(inventory.getStack(1)) && this.addition.test(inventory.getStack(2));
    }

    @Override
    public boolean isEmpty() {
        return Stream.of(this.base, this.addition).anyMatch(Ingredient::isEmpty);
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager registryManager) {
        ItemStack result = new ItemStack(this.result.getItem(), 1);
        ItemStack base = inventory.getStack(1);
        if (base.hasNbt()) {
            result.setNbt(base.getNbt().copy());
        }
        return result;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<ImpaledSmithingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        public ImpaledSmithingRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient base = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "base"));
            Ingredient addition = Ingredient.fromJson(JsonHelper.getElement(jsonObject, "addition"));
            ItemStack result = readResult(JsonHelper.getObject(jsonObject, "result"));
            return new ImpaledSmithingRecipe(identifier, base, addition, result);
        }

        private static ItemStack readResult(JsonObject json) {
            Identifier id = new Identifier(JsonHelper.getString(json, "item"));
            ItemStack stack = new ItemStack(Registries.ITEM.get(id));
            stack.setCount(JsonHelper.getInt(json, "count", 1));
            return stack;
        }

        public ImpaledSmithingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient base = Ingredient.fromPacket(packetByteBuf);
            Ingredient addition = Ingredient.fromPacket(packetByteBuf);
            ItemStack result = packetByteBuf.readItemStack();
            return new ImpaledSmithingRecipe(identifier, base, addition, result);
        }

        public void write(PacketByteBuf packetByteBuf, ImpaledSmithingRecipe recipe) {
            recipe.base.write(packetByteBuf);
            recipe.addition.write(packetByteBuf);
            packetByteBuf.writeItemStack(recipe.result);
        }
    }
}
