package ladysnake.impaled.neoforge.client;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ImpaledTridentItemExtensions implements IClientItemExtensions {
    private ImpaledTridentBEWLR bewlr;

    @Override
    public BuiltinModelItemRenderer getCustomRenderer() {
        if (bewlr == null) bewlr = new ImpaledTridentBEWLR();
        return bewlr;
    }
}
