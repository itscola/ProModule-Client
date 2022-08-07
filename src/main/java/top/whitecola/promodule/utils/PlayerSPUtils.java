package top.whitecola.promodule.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import static top.whitecola.promodule.utils.MCWrapper.*;

public class PlayerSPUtils {
    public static boolean isMoving() {



        if(mc==null || mc.thePlayer==null || mc.thePlayer.movementInput==null){
            return false;
        }

        if (mc.thePlayer.movementInput.moveForward != 0f || mc.thePlayer.movementInput.moveStrafe != 0f) {
            return true;
        }
        return false;
    }

    public static boolean isSneaking() {

        if(mc==null || mc.thePlayer==null){
            return false;
        }

        if ((mc.thePlayer.isSneaking())) {
            return true;
        }
        return false;
    }

    public static BlockPos getCurrentFrontBlock(){
        BlockPos blockPos = mc.objectMouseOver.getBlockPos();
        return blockPos;
    }

//    public static void sendClick(int keycode){
//
//    }

    public static void sendMsgToSelf(String content){
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(content));
    }
}
