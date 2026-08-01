package ladysnake.impaled.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TridentEntity.class)
public interface TridentEntityAccessor {
    @Accessor("LOYALTY")
    static TrackedData<Byte> impaled$getLoyalty() {
        throw new AssertionError();
    }

    @Accessor("ENCHANTED")
    static TrackedData<Boolean> impaled$getEnchanted() {
        throw new AssertionError();
    }

    @Accessor("dealtDamage")
    boolean impaled$hasDealtDamage();

    @Accessor("dealtDamage")
    void impaled$setDealtDamage(boolean dealtDamage);
}
