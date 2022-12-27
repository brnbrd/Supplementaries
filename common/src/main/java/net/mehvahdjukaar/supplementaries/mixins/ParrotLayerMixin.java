package net.mehvahdjukaar.supplementaries.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.mehvahdjukaar.moonlight.api.platform.ClientPlatformHelper;
import net.mehvahdjukaar.supplementaries.SupplementariesClient;
import net.mehvahdjukaar.supplementaries.common.misc.mob_container.IMobContainerProvider;
import net.mehvahdjukaar.supplementaries.common.misc.mob_container.MobContainer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(ParrotOnShoulderLayer.class)
public abstract class ParrotLayerMixin<T extends Player> {

    @Shadow @Final private ParrotModel model;

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/player/Player;FFFFZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;getString(Ljava/lang/String;)Ljava/lang/String;",
            shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void renderParty(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, T livingEntity,
                             float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch,
                             boolean leftShoulder, CallbackInfo ci, CompoundTag compoundTag) {
        if(compoundTag.getBoolean("record_playing")) {
            EntityType.byString(compoundTag.getString("id")).filter((entityType) ->
                    entityType == EntityType.PARROT).ifPresent((entityType) -> {
                matrixStack.pushPose();
                matrixStack.translate(leftShoulder ? 0.4000000059604645 : -0.4000000059604645, livingEntity.isCrouching() ? -1.2999999523162842 : -1.5, 0.0);
                VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(ParrotRenderer.PARROT_LOCATIONS[compoundTag.getInt("Variant")]));
                renderOnShoulderPartying(model, matrixStack, vertexConsumer, packedLight,
                        OverlayTexture.NO_OVERLAY, limbSwing, limbSwingAmount, netHeadYaw, headPitch,
                        livingEntity.tickCount, 0);
                matrixStack.popPose();
            });
            ci.cancel();
        }
    }

    public void renderOnShoulderPartying(ParrotModel model, PoseStack poseStack, VertexConsumer buffer,
                                         int packedLight, int packedOverlay, float limbSwing, float limbSwingAmount,
                                         float netHeadYaw, float headPitch, int tickCount, float bob) {
        model.prepare(ParrotModel.State.PARTY);
        model.setupAnim(ParrotModel.State.PARTY, tickCount, limbSwing, limbSwingAmount, bob, netHeadYaw, headPitch);
        model.root().render(poseStack, buffer, packedLight, packedOverlay);
    }
}