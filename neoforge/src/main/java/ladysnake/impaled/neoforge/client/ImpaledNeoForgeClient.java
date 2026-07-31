package ladysnake.impaled.neoforge.client;

import ladysnake.impaled.client.render.entity.ImpaledTridentEntityRenderer;
import ladysnake.impaled.client.render.entity.model.ImpaledTridentEntityModel;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.neoforge.ImpaledNeoForge;
import ladysnake.sincereloyalty.SincereLoyaltyNeoForgeClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public final class ImpaledNeoForgeClient {
    public static void register(IEventBus modBus) {
        if (!FMLEnvironment.dist.isClient()) return;

        modBus.addListener(ImpaledNeoForgeClient::registerRenderers);
        modBus.addListener(ImpaledNeoForgeClient::registerLayerDefinitions);
        modBus.addListener(ImpaledNeoForgeClient::registerClientExtensions);
        modBus.addListener(ImpaledNeoForgeClient::registerAdditionalModels);
        modBus.addListener(ImpaledNeoForgeClient::clientSetup);

        SincereLoyaltyNeoForgeClient.register();
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ImpaledNeoForge.PITCHFORK_ENTITY.get(), ctx -> new ImpaledTridentEntityRenderer(ctx, tex("pitchfork"), EntityModelLayers.TRIDENT));
        event.registerEntityRenderer(ImpaledNeoForge.HELLFORK_ENTITY.get(), ctx -> new ImpaledTridentEntityRenderer(ctx, tex("hellfork"), EntityModelLayers.TRIDENT));
        event.registerEntityRenderer(ImpaledNeoForge.SOULFORK_ENTITY.get(), ctx -> new ImpaledTridentEntityRenderer(ctx, tex("soulfork"), EntityModelLayers.TRIDENT));
        event.registerEntityRenderer(ImpaledNeoForge.ELDER_TRIDENT_ENTITY.get(), ctx -> new ImpaledTridentEntityRenderer(ctx, tex("elder_trident"), EntityModelLayers.TRIDENT));
        event.registerEntityRenderer(ImpaledNeoForge.GUARDIAN_TRIDENT_ENTITY.get(), ctx -> new ImpaledTridentEntityRenderer(ctx, tex("guardian_trident"), EntityModelLayers.TRIDENT));
        event.registerEntityRenderer(ImpaledNeoForge.ATLAN_ENTITY.get(), ctx -> new ImpaledTridentEntityRenderer(ctx, tex("atlan"), ImpaledTridentBEWLR.ATLAN_LAYER));
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ImpaledTridentBEWLR.ATLAN_LAYER, ImpaledTridentEntityModel::getAtlanTexturedModelData);
    }

    private static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        ImpaledTridentItemExtensions extensions = new ImpaledTridentItemExtensions();
        // Only the tridents use a builtin (entity) item renderer; ancient trident / elder guardian
        // eye / maelstrom use normal 2D item models and must NOT be registered here.
        event.registerItem(extensions,
                ImpaledNeoForge.PITCHFORK.get(),
                ImpaledNeoForge.HELLFORK.get(),
                ImpaledNeoForge.SOULFORK.get(),
                ImpaledNeoForge.ELDER_TRIDENT.get(),
                ImpaledNeoForge.ATLAN.get()
        );
    }

    private static final Item[] TRIDENTS = {
            ImpaledNeoForge.PITCHFORK.get(),
            ImpaledNeoForge.HELLFORK.get(),
            ImpaledNeoForge.SOULFORK.get(),
            ImpaledNeoForge.ELDER_TRIDENT.get(),
            ImpaledNeoForge.ATLAN.get()
    };

    private static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        for (Item item : TRIDENTS) {
            Identifier id = Registries.ITEM.getId(item);
            event.register(new ModelIdentifier(Identifier.of(id.getNamespace(), "item/" + id.getPath() + "_in_inventory"), "standalone"));
        }
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (Item item : TRIDENTS) {
                // "throwing" model predicate (the _throwing variant shown while charging/throwing)
                ModelPredicateProviderRegistry.register(item, Identifier.of("throwing"),
                        (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
            }
        });
    }

    private static Identifier tex(String name) {
        return Identifier.of(Impaled.MODID, "textures/entity/" + name + ".png");
    }

    private ImpaledNeoForgeClient() {}
}
