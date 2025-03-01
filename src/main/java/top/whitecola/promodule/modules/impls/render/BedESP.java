package top.whitecola.promodule.modules.impls.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import top.whitecola.promodule.injection.wrappers.IMixinRenderManager;
import top.whitecola.promodule.modules.AbstractModule;
import top.whitecola.promodule.modules.ModuleCategory;
import top.whitecola.promodule.utils.Render3DUtils;
import static top.whitecola.promodule.utils.MCWrapper.*;

import java.awt.*;
import java.util.Iterator;
import java.util.Vector;

public class BedESP extends AbstractModule {
    public Vector<BlockPos> bedBlocks = new Vector<BlockPos>();
    protected Color color = new Color(210, 111, 111);


    @Override
    public void onRender3D(int pass, float partialTicks, long finishTimeNano) {
        Iterator<BlockPos> it = bedBlocks.iterator();
        try {
            while(it.hasNext()){
                BlockPos blockPos = it.next();
                Render3DUtils.drawSolidBlockESP(
                        blockPos.getX() - ((IMixinRenderManager) mc.getRenderManager()).getRenderPosX(),
                        blockPos.getY() - ((IMixinRenderManager) mc.getRenderManager()).getRenderPosY(),
                        blockPos.getZ()- ((IMixinRenderManager) mc.getRenderManager()).getRenderPosZ(),
                        color.getRed(),color.getGreen(),color.getBlue(),0.2f
                );

            }
        }catch (Throwable e){

        }

        super.onRender3D(pass, partialTicks, finishTimeNano);
    }

    @Override
    public void onRenderBlock(int x, int y, int z, Block block) {
        if(block instanceof BlockBed){
            BlockPos pos = new BlockPos(x,y,z);
            if(!bedBlocks.contains(pos)){
                this.bedBlocks.add(pos);
            }
        }
        super.onRenderBlock(x, y, z, block);
    }

    @Override
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {


        if(e.entity instanceof EntityPlayerSP){
            bedBlocks.clear();
        }
        super.onEntityJoinWorld(e);
    }

    @Override
    public ModuleCategory getModuleType() {
        return ModuleCategory.RENDERS;
    }

    @Override
    public String getModuleName() {
        return "BedESP";
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
//        mc.renderGlobal.loadRenderers();
        this.bedBlocks.clear();
        super.onEnable();
    }
}
