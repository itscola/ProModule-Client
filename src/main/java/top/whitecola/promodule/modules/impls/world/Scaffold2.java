package top.whitecola.promodule.modules.impls.world;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import top.whitecola.promodule.ProModule;
import top.whitecola.promodule.annotations.ModuleSetting;
import top.whitecola.promodule.events.impls.event.PreMotionEvent;
import top.whitecola.promodule.modules.AbstractModule;
import top.whitecola.promodule.modules.ModuleCategory;
import top.whitecola.promodule.utils.RandomUtils;
import top.whitecola.promodule.utils.RotationUtils;
import top.whitecola.promodule.utils.ScaffoldUtils;

import java.util.concurrent.ThreadLocalRandom;

import static top.whitecola.promodule.utils.MCWrapper.*;

public class Scaffold2 extends AbstractModule {
    private ScaffoldUtils.BlockCache blockCache, lastBlockCache;
    private float rotations[];
    private long last = 0;

    @ModuleSetting(name = "Speed" ,type = "value",addValue = 0.01f)
    public Float speed = 0.82f;

    @Override
    public void onPreMotion(PreMotionEvent e) {
        mc.thePlayer.setSprinting(false);
        // Rotations
        if(lastBlockCache != null) {
            rotations = RotationUtils.getFacingRotations2(lastBlockCache.getPosition().getX(), lastBlockCache.getPosition().getY(), lastBlockCache.getPosition().getZ());
            mc.thePlayer.renderYawOffset = rotations[0];
            mc.thePlayer.rotationYawHead = rotations[0];
            e.setYaw(rotations[0]);
            e.setPitch(81);
//            mc.thePlayer.rotationPitchHead = 81;
        } else {
            e.setPitch(81);
            e.setYaw(mc.thePlayer.rotationYaw + 180);
//            mc.thePlayer.rotationPitchHead = 81;
            mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw + 180;
            mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw + 180;
        }

        // Speed 2 Slowdown
//        if(mc.thePlayer.isPotionActive(Potion.moveSpeed.id)){
            mc.thePlayer.motionX *= speed;
            mc.thePlayer.motionZ *= speed;
//        }

        // Setting Block Cache
        blockCache = ScaffoldUtils.grab();
        if (blockCache != null) {
            lastBlockCache = ScaffoldUtils.grab();
        }else{
            return;
        }

        // Setting Item Slot (Pre)
        int slot = ScaffoldUtils.grabBlockSlot();
        if(slot == -1) return;

        // Setting Slot
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));

//         Placing Blocks (Pre)
//        if(e.isPre()){
//            if (blockCache == null) return;
//            mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(slot), lastBlockCache.position, lastBlockCache.facing, ScaffoldUtils.getHypixelVec3(lastBlockCache));
//            mc.thePlayer.swingItem();
//            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
//            blockCache = null;
//        }

        // Tower
//        if(tower.isEnabled()) {
//            if(mc.gameSettings.keyBindJump.isKeyDown()) {
//                mc.timer.timerSpeed = towerTimer.getValue().floatValue();
//                if(mc.thePlayer.motionY < 0) {
//                    mc.thePlayer.jump();
//                }
//            }else{
//                mc.timer.timerSpeed = 1;
//            }
//        }

        // Setting Item Slot (Post)
        slot = ScaffoldUtils.grabBlockSlot();
        if(slot == -1) return;

        // Placing Blocks (Post)
//        if(e.is){
        if (blockCache == null) return;

//        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(slot), lastBlockCache.position, lastBlockCache.facing, ScaffoldUtils.getHypixelVec3(lastBlockCache));
////            if(swing.isEnabled()){
//        mc.thePlayer.swingItem();
////            }
//        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());

        long n = System.currentTimeMillis();
        if (n - this.last >= 25L) {
            this.last = n;
            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(slot), lastBlockCache.position, lastBlockCache.facing, ScaffoldUtils.getHypixelVec3(lastBlockCache))) {
//                sendRightClick(true);
                mc.thePlayer.swingItem();
                mc.getItemRenderer().resetEquippedProgress();
//                sendRightClick(false);
                blockCache = null;
            }

        }

//        }




        super.onPreMotion(e);
    }


    private void sendRightClick(boolean state){
        int keycode = mc.gameSettings.keyBindUseItem.getKeyCode();

        if (state) {
            KeyBinding.onTick(keycode);
        }
    }

    @Override
    public ModuleCategory getModuleType() {
        return ModuleCategory.WORLD;
    }

    @Override
    public String getModuleName() {
        return "Scaffold";

    }

    @Override
    public String getDisplayName() {
        return super.getDisplayName()+" (NG)";
    }

    @Override
    public void onEnable() {
        lastBlockCache = null;
        this.last = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if(mc.theWorld==null||mc.thePlayer==null){
            return;
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        super.onDisable();
    }
}
