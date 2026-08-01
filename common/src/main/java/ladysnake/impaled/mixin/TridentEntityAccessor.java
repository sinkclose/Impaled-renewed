package ladysnake.impaled.mixin;

import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TridentEntity.class)
public interface TridentEntityAccessor {
    @Accessor("dealtDamage")
    boolean impaled$hasDealtDamage();

    @Accessor("dealtDamage")
    void impaled$setDealtDamage(boolean dealtDamage);
}
