package top.whitecola.promodule.injection.wrappers;

import net.minecraft.util.Timer;

public interface IMixinMinecraft {
    Timer getTimer();
    void setRightClickDelayTimer(int value);
    int getRightClickDelayTimer();
}
