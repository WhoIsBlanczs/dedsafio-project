package com.novastudio.dedsafio4;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.core.registries.BuiltInRegistries;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoBlockRenderer;

@Environment(EnvType.CLIENT)
public final class DinoFruitClient {
   private static final Identifier ANIMATIONS = Identifier.fromNamespaceAndPath("nexo", "block/dino_fruit");
   static final com.geckolib.constant.dataticket.DataTicket<BlockState> ESTADO = com.geckolib.constant.dataticket.DataTicket.create("nexo_fruta_estado", BlockState.class);
   static final com.geckolib.constant.dataticket.DataTicket<Direction> LADO = com.geckolib.constant.dataticket.DataTicket.create("nexo_fruta_lado", Direction.class);

   private DinoFruitClient() {
   }

   public static void register() {
      BlockEntityRendererRegistry.register(DinoFruitRender.TYPE, var0 -> new DinoFruitClient.Renderer(var0));
   }

   private static boolean bud(BlockState var0) {
      return (Integer)var0.getValue(DinoFruitBlock.AGE) == 0;
   }

   private static String path(BlockState var0) {
      return BuiltInRegistries.BLOCK.getKey(var0.getBlock()).getPath();
   }

   @Environment(EnvType.CLIENT)
   private static final class Model extends GeoModel<DinoFruitBlockEntity> {
      @Override
      public void addAdditionalStateData(DinoFruitBlockEntity var1, Object relacionado, com.geckolib.renderer.base.GeoRenderState estado) {
         super.addAdditionalStateData(var1, relacionado, estado);
         estado.addGeckolibData(ESTADO, var1.getBlockState());
      }

      public Identifier getModelResource(com.geckolib.renderer.base.GeoRenderState estado) {
         BlockState var1 = estado.getGeckolibData(ESTADO);
         return Identifier.fromNamespaceAndPath("nexo", var1 == null || DinoFruitClient.bud(var1) ? "block/fruit_bud" : "block/" + path(var1));
      }

      public Identifier getTextureResource(com.geckolib.renderer.base.GeoRenderState estado) {
         BlockState var1 = estado.getGeckolibData(ESTADO);
         int var2 = var1 == null ? 0 : (Integer)var1.getValue(DinoFruitBlock.AGE);
         return var2 == 0
            ? Identifier.fromNamespaceAndPath("nexo", "textures/block/fruit_bud.png")
            : Identifier.fromNamespaceAndPath("nexo", "textures/block/" + path(var1) + (var2 == 8 ? "_rotten.png" : ".png"));
      }

      public Identifier getAnimationResource(DinoFruitBlockEntity var1) {
         return DinoFruitClient.ANIMATIONS;
      }
   }

   @Environment(EnvType.CLIENT)
   private static final class Renderer extends GeoBlockRenderer<DinoFruitBlockEntity, net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState> {
      Renderer(net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context ctx) {
         super(ctx, new DinoFruitClient.Model());
      }

      @Override
      public void captureDefaultRenderState(DinoFruitBlockEntity var1, Void rel, net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState estado, float pt) {
         super.captureDefaultRenderState(var1, rel, estado, pt);
         estado.addGeckolibData(LADO, var1.getLevel() == null ? Direction.SOUTH : DinoFruitShapes.trunkSide(var1.getLevel(), var1.getBlockPos()));
      }

      @Override
      protected void tryRotateByBlockstate(com.geckolib.renderer.base.RenderPassInfo<net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState> info, PoseStack var2) {
         BlockState current = info.getGeckolibData(ESTADO);
         Direction var1 = info.getOrDefaultGeckolibData(LADO, Direction.SOUTH);
         if (current == null || DinoFruitClient.bud(current)) {
            var2.rotate(Axis.YP.rotationDegrees(DinoFruitShapes.yaw(var1)));
         } else {
            DinoFruitShapes.Pose var3 = DinoFruitShapes.pose(current.getBlock());
            var2.rotate(Axis.YP.rotationDegrees(DinoFruitShapes.yaw(var1)));
            var2.translate(var3.dx() / 16.0, var3.dy() / 16.0, var3.dz() / 16.0);
            if (var3.horizontal()) {
               var2.rotate(Axis.XP.rotationDegrees(-90.0F));
            }

            float var4 = DinoFruitBlock.scale(current);
            if (var4 < 1.0F) {
               var2.scale(var4, var4, var4);
            }
         }
      }
   }
}
