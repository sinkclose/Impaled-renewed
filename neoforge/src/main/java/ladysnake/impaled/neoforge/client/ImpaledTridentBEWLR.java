package ladysnake.impaled.neoforge.client;

import ladysnake.impaled.client.render.entity.model.ImpaledTridentEntityModel;
import ladysnake.impaled.common.Impaled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * Renders impaled trident items. In-hand/world uses the entity model; GUI/ground/fixed uses the
 * 2D {@code _in_inventory} sprite (loaded as an additional baked model), mirroring the Fabric
 * builtin item renderer.
 */
public class ImpaledTridentBEWLR extends BuiltinModelItemRenderer {
    public static final EntityModelLayer ATLAN_LAYER = new EntityModelLayer(Identifier.of(Impaled.MODID, "atlan"), "main");

    private ImpaledTridentEntityModel tridentModel;
    private ImpaledTridentEntityModel atlanModel;

    public ImpaledTridentBEWLR() {
        super(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider consumers, int light, int overlay) {
        Identifier itemId = Registries.ITEM.getId(stack.getItem());

        if (mode == ModelTransformationMode.GUI || mode == ModelTransformationMode.GROUND || mode == ModelTransformationMode.FIXED) {
            // Undo the builtin/entity transform applied by the caller before rendering the 2D sprite,
            // so the _in_inventory model's own display transforms apply (matches the Fabric renderer).
            matrices.pop();
            matrices.push();
            BakedModelManager manager = MinecraftClient.getInstance().getBakedModelManager();
            BakedModel inventoryModel = manager.getModel(new ModelIdentifier(Identifier.of(itemId.getNamespace(), "item/" + itemId.getPath() + "_in_inventory"), "standalone"));
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, mode, false, matrices, consumers, light, overlay, inventoryModel);
            return;
        }

        Identifier texture = Identifier.of(itemId.getNamespace(), "textures/entity/" + itemId.getPath() + ".png");
        boolean atlan = "atlan".equals(itemId.getPath());

        ImpaledTridentEntityModel model = getModel(atlan);
        RenderLayer renderLayer = model.getLayer(texture);
        VertexConsumer consumer = ItemRenderer.getDirectItemGlintConsumer(consumers, renderLayer, false, stack.hasGlint());
        matrices.push();
        matrices.scale(1.0F, -1.0F, -1.0F);
        model.render(matrices, consumer, light, overlay, -1);
        matrices.pop();
    }

    private ImpaledTridentEntityModel getModel(boolean atlan) {
        if (atlan) {
            if (atlanModel == null) atlanModel = new ImpaledTridentEntityModel(getPart(ATLAN_LAYER));
            return atlanModel;
        }
        if (tridentModel == null) tridentModel = new ImpaledTridentEntityModel(getPart(EntityModelLayers.TRIDENT));
        return tridentModel;
    }

    private static ModelPart getPart(EntityModelLayer layer) {
        EntityModelLoader loader = MinecraftClient.getInstance().getEntityModelLoader();
        return loader.getModelPart(layer);
    }
}
