package top.whitecola.promodule.injection.mixins;


import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.whitecola.promodule.ProModule;
import top.whitecola.promodule.events.EventManager;
import top.whitecola.promodule.events.impls.event.WorldRenderEvent;
import top.whitecola.promodule.injection.wrappers.CanBeCollidedWith;
import top.whitecola.promodule.injection.wrappers.IMxinEntityRenderer;
import top.whitecola.promodule.modules.impls.combat.Reach;
import top.whitecola.promodule.modules.impls.render.NoHurtCam;
import top.whitecola.promodule.utils.RandomUtils;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IMxinEntityRenderer {

    @Shadow
    private Minecraft mc;

    @Shadow
    private Entity pointedEntity;

    @Shadow private Random random;

    @Shadow protected abstract boolean isDrawBlockOutline();

    @Shadow protected abstract void updateFogColor(float p_updateFogColor_1_);


    @Shadow protected abstract void setupFog(int p_setupFog_1_, float p_setupFog_2_);

    @Shadow protected abstract float getFOVModifier(float p_getFOVModifier_1_, boolean p_getFOVModifier_2_);

    @Shadow protected abstract void renderCloudsCheck(RenderGlobal p_renderCloudsCheck_1_, float p_renderCloudsCheck_2_, int p_renderCloudsCheck_3_);

    @Shadow private boolean debugView;

    @Shadow public abstract void disableLightmap();

    @Shadow private int frameCount;

    @Shadow private float farPlaneDistance;

    @Shadow public abstract void enableLightmap();

    @Shadow protected abstract void renderRainSnow(float p_renderRainSnow_1_);

    @Shadow private boolean renderHand;

    @Shadow protected abstract void renderWorldDirections(float p_renderWorldDirections_1_);

    @Shadow protected abstract void renderHand(float p_renderHand_1_, int p_renderHand_2_);

    @Shadow protected abstract void setupCameraTransform(float p_setupCameraTransform_1_, int p_setupCameraTransform_2_);

    @Shadow private ShaderGroup theShaderGroup;

    @Shadow private boolean useShader;

    @Shadow @Final private static Logger logger;

    @Shadow private int shaderIndex;

    @Shadow @Final public static int shaderCount;

    @Shadow @Final private IResourceManager resourceManager;

    /**
     * @author white_cola
     * @reason reach module.
     */
    @Overwrite
    public void getMouseOver(float p_getMouseOver_1_) {
        Entity entity = this.mc.getRenderViewEntity();
        if (entity != null && this.mc.theWorld != null) {

            double reach = 3.0;
            Reach reach1 = (Reach) ProModule.getProModule().getModuleManager().getModuleByName("Reach");

            boolean boo = reach1.useChance&&(RandomUtils.nextDouble(0,100)<=reach1.chance);

            reach1.use = boo;

            if(boo && ProModule.getProModule().getModuleManager().getModuleByName("Reach").isEnabled()){
                double minRange = ((Reach)ProModule.getProModule().getModuleManager().getModuleByName("Reach")).minRange;
                double maxRange = ((Reach)ProModule.getProModule().getModuleManager().getModuleByName("Reach")).maxRange;
                reach = RandomUtils.nextDouble(minRange,maxRange);
            }

            this.mc.mcProfiler.startSection("pick");
            this.mc.pointedEntity = null;
            double d0 = (double)this.mc.playerController.getBlockReachDistance();
            this.mc.objectMouseOver = entity.rayTrace(d0, p_getMouseOver_1_);
            double d1 = d0;
            Vec3 vec3 = entity.getPositionEyes(p_getMouseOver_1_);
            boolean flag = false;
            if (this.mc.playerController.extendedReach()) {
                d0 = 6.0D;
                d1 = 6.0D;
            } else if (d0 > reach) {
                flag = true;
            }

            if (this.mc.objectMouseOver != null) {
                d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
            }

            Vec3 vec31 = entity.getLook(p_getMouseOver_1_);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            this.pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0F;
            List<Entity> list = this.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f, (double)f, (double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, new CanBeCollidedWith()));
            double d2 = d1;

            for(int j = 0; j < list.size(); ++j) {
                Entity entity1 = (Entity)list.get(j);
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f1, (double)f1, (double)f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        this.pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        if (entity1 == entity.ridingEntity && !entity.canRiderInteract()) {
                            if (d2 == 0.0D) {
                                this.pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            this.pointedEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (this.pointedEntity != null && flag && vec3.distanceTo(vec33) > reach) {
                this.pointedEntity = null;
                this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, (EnumFacing)null, new BlockPos(vec33));
            }

            if (this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null)) {
                this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);
                if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                    this.mc.pointedEntity = this.pointedEntity;
                }
            }

            this.mc.mcProfiler.endSection();
        }


//
//        Entity entity = this.mc.getRenderViewEntity();
//
//        if (entity != null && this.mc.theWorld != null) {
//
//            double reach = 3.0;
//
//            if(ProModule.getProModule().getModuleManager().getModuleByName("Reach").isEnabled()){
//                double minRange = ((Reach)ProModule.getProModule().getModuleManager().getModuleByName("Reach")).minRange;
//                double maxRange = ((Reach)ProModule.getProModule().getModuleManager().getModuleByName("Reach")).maxRange;
//                reach = RandomUtils.nextDouble(minRange,maxRange);
//            }
//
//
//            this.mc.mcProfiler.startSection("pick");
//            this.mc.pointedEntity = null;
//            double d0 = (double) this.mc.playerController.getBlockReachDistance();
//            this.mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
//            double d1 = d0;
//            Vec3 vec3 = entity.getPositionEyes(partialTicks);
//            boolean flag = false;
//            boolean flag1 = true;
//
//            if (this.mc.playerController.extendedReach()) {
//                d0 = 6.0D;
//                d1 = 6.0D;
//            } else {
//                if (d0 > reach) {
//                    flag = true;
//                }
//
//            }
//
//            if (this.mc.objectMouseOver != null) {
//                d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
//            }
//
//            Vec3 vec31 = entity.getLook(partialTicks);
//            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
//            this.pointedEntity = null;
//            Vec3 vec33 = null;
//            float f = 1.0F;
//            List list = this.mc.theWorld.getEntitiesInAABBexcluding(entity,
//                    entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
//                            .expand((double) f, (double) f, (double) f),
//                    new CanBeCollidedWith());
//            double d2 = d1;
//
//            for (int i = 0; i < list.size(); ++i) {
//                Entity entity1 = (Entity) list.get(i);
//                float f1 = entity1.getCollisionBorderSize();
//                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double) f1, (double) f1,
//                        (double) f1);
//                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
//
//                if (axisalignedbb.isVecInside(vec3)) {
//                    if (d2 >= 0.0D) {
//                        this.pointedEntity = entity1;
//                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
//                        d2 = 0.0D;
//                    }
//                } else if (movingobjectposition != null) {
//                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
//
//                    if (d3 < d2 || d2 == 0.0D) {
//                        boolean flag2 = false;
//
//
//                        if (entity1 == entity.ridingEntity && !flag2) {
//                            if (d2 == 0.0D) {
//                                this.pointedEntity = entity1;
//                                vec33 = movingobjectposition.hitVec;
//                            }
//                        } else {
//                            this.pointedEntity = entity1;
//                            vec33 = movingobjectposition.hitVec;
//                            d2 = d3;
//                        }
//                    }
//                }
//            }
//
//            if (this.pointedEntity != null && flag && vec3.distanceTo(vec33) > reach) {
//                this.pointedEntity = null;
//                this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33,
//                        (EnumFacing) null, new BlockPos(vec33));
//            }
//
//            if (this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null)) {
//                this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec33);
//
//                if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
//                    this.mc.pointedEntity = this.pointedEntity;
//                }
//            }
//
//            this.mc.mcProfiler.endSection();
//        }
    }


    /**
     * @author White_cola
     * @reason For Render Event.
     */
    @Overwrite
    private void renderWorldPass(int pass, float partialTicks, long finishTimeNano) {
        RenderGlobal renderglobal = this.mc.renderGlobal;
        EffectRenderer effectrenderer = this.mc.effectRenderer;
        boolean flag = this.isDrawBlockOutline();
        GlStateManager.enableCull();
        this.mc.mcProfiler.endStartSection("clear");
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        this.updateFogColor(partialTicks);
        GlStateManager.clear(16640);
        this.mc.mcProfiler.endStartSection("camera");
        this.setupCameraTransform(partialTicks, pass);
        ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
        this.mc.mcProfiler.endStartSection("frustum");
        ClippingHelperImpl.getInstance();
        this.mc.mcProfiler.endStartSection("culling");
        ICamera icamera = new Frustum();
        Entity entity = this.mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        icamera.setPosition(d0, d1, d2);
        if (this.mc.gameSettings.renderDistanceChunks >= 4) {
            this.setupFog(-1, partialTicks);
            this.mc.mcProfiler.endStartSection("sky");
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
            GlStateManager.matrixMode(5888);
            renderglobal.renderSky(partialTicks, pass);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
            GlStateManager.matrixMode(5888);
        }

        this.setupFog(0, partialTicks);
        GlStateManager.shadeModel(7425);
        if (entity.posY + (double)entity.getEyeHeight() < 128.0D) {
            this.renderCloudsCheck(renderglobal, partialTicks, pass);
        }

        this.mc.mcProfiler.endStartSection("prepareterrain");
        this.setupFog(0, partialTicks);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        RenderHelper.disableStandardItemLighting();
        this.mc.mcProfiler.endStartSection("terrain_setup");
        renderglobal.setupTerrain(entity, (double)partialTicks, icamera, this.frameCount++, this.mc.thePlayer.isSpectator());
        if (pass == 0 || pass == 2) {
            this.mc.mcProfiler.endStartSection("updatechunks");
            this.mc.renderGlobal.updateChunks(finishTimeNano);
        }

        this.mc.mcProfiler.endStartSection("terrain");
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        renderglobal.renderBlockLayer(EnumWorldBlockLayer.SOLID, (double)partialTicks, pass, entity);
        GlStateManager.enableAlpha();
        renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, (double)partialTicks, pass, entity);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, (double)partialTicks, pass, entity);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1F);
        EntityPlayer entityplayer1;
        if (!this.debugView) {
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            RenderHelper.enableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("entities");
            ForgeHooksClient.setRenderPass(0);
            renderglobal.renderEntities(entity, icamera, partialTicks);
            ForgeHooksClient.setRenderPass(0);
            RenderHelper.disableStandardItemLighting();
            this.disableLightmap();
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            if (this.mc.objectMouseOver != null && entity.isInsideOfMaterial(Material.water) && flag) {
                entityplayer1 = (EntityPlayer)entity;
                GlStateManager.disableAlpha();
                this.mc.mcProfiler.endStartSection("outline");
                if (!ForgeHooksClient.onDrawBlockHighlight(renderglobal, entityplayer1, this.mc.objectMouseOver, 0, entityplayer1.getHeldItem(), partialTicks)) {
                    renderglobal.drawSelectionBox(entityplayer1, this.mc.objectMouseOver, 0, partialTicks);
                }

                GlStateManager.enableAlpha();
            }
        }

        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        if (flag && this.mc.objectMouseOver != null && !entity.isInsideOfMaterial(Material.water)) {
            entityplayer1 = (EntityPlayer)entity;
            GlStateManager.disableAlpha();
            this.mc.mcProfiler.endStartSection("outline");
            if (!ForgeHooksClient.onDrawBlockHighlight(renderglobal, entityplayer1, this.mc.objectMouseOver, 0, entityplayer1.getHeldItem(), partialTicks)) {
                renderglobal.drawSelectionBox(entityplayer1, this.mc.objectMouseOver, 0, partialTicks);
            }

            GlStateManager.enableAlpha();
        }

        this.mc.mcProfiler.endStartSection("destroyProgress");
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        renderglobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getWorldRenderer(), entity, partialTicks);
        this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
        GlStateManager.disableBlend();
        if (!this.debugView) {
            this.enableLightmap();
            this.mc.mcProfiler.endStartSection("litParticles");
            effectrenderer.renderLitParticles(entity, partialTicks);
            RenderHelper.disableStandardItemLighting();
            this.setupFog(0, partialTicks);
            this.mc.mcProfiler.endStartSection("particles");
            effectrenderer.renderParticles(entity, partialTicks);
            this.disableLightmap();
        }

        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        this.mc.mcProfiler.endStartSection("weather");
        this.renderRainSnow(partialTicks);
        GlStateManager.depthMask(true);
        renderglobal.renderWorldBorder(entity, partialTicks);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.alphaFunc(516, 0.1F);
        this.setupFog(0, partialTicks);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GlStateManager.shadeModel(7425);
        this.mc.mcProfiler.endStartSection("translucent");
        renderglobal.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, (double)partialTicks, pass, entity);
        if (!this.debugView) {
            RenderHelper.enableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("entities");
            ForgeHooksClient.setRenderPass(1);
            renderglobal.renderEntities(entity, icamera, partialTicks);
            ForgeHooksClient.setRenderPass(-1);
            RenderHelper.disableStandardItemLighting();
        }

        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();





        EventManager.getEventManager().onRender3D(pass, partialTicks, finishTimeNano);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);









        if (entity.posY + (double)entity.getEyeHeight() >= 128.0D) {
            this.mc.mcProfiler.endStartSection("aboveClouds");
            this.renderCloudsCheck(renderglobal, partialTicks, pass);
        }

        this.mc.mcProfiler.endStartSection("forge_render_last");
        ForgeHooksClient.dispatchRenderLast(renderglobal, partialTicks);
        this.mc.mcProfiler.endStartSection("hand");


        WorldRenderEvent worldRenderEvent = new WorldRenderEvent(partialTicks);
        EventManager.getEventManager().worldRenderEvent(worldRenderEvent);

        if (!ForgeHooksClient.renderFirstPersonHand(renderglobal, partialTicks, pass) && this.renderHand) {
            GlStateManager.clear(256);
            this.renderHand(partialTicks, pass);
            this.renderWorldDirections(partialTicks);
        }

    }


    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void hurtCamEffect(float p_hurtCameraEffect_1_, CallbackInfo ci){
        if(ProModule.getProModule().getModuleManager().getModuleByClass(NoHurtCam.class).isEnabled()){
            ci.cancel();
            return;
        }
    }

    @Override
    public void setupCameraTransform1(float partialTicks, int pass) {
        this.setupCameraTransform(partialTicks,pass);
    }

    @Override
    public void runSetupCameraTransform(float partialTicks, int pass) {
        this.setupCameraTransform1(partialTicks,pass);
    }

    @Override
    public void loadShader2(ResourceLocation resourceLocationIn) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            try {
                this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager,
                        this.mc.getFramebuffer(), resourceLocationIn);
                this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                this.useShader = true;
            } catch (IOException | JsonSyntaxException ioexception) {
                logger.warn("Failed to load shader: " + resourceLocationIn, ioexception);
                this.shaderIndex = shaderCount;
                this.useShader = false;
            }
        }
    }
}
