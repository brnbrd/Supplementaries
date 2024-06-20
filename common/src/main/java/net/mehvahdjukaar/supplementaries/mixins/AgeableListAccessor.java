package net.mehvahdjukaar.supplementaries.mixins;

import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AgeableListModel.class)
public interface AgeableListAccessor {

    @Invoker("bodyParts")
    Iterable<ModelPart> invokeBodyParts();

}
