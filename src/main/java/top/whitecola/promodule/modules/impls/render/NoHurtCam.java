package top.whitecola.promodule.modules.impls.render;

import top.whitecola.promodule.modules.AbstractModule;
import top.whitecola.promodule.modules.ModuleCategory;

public class NoHurtCam extends AbstractModule {

    @Override
    public ModuleCategory getModuleType() {
        return ModuleCategory.RENDERS;
    }

    @Override
    public String getModuleName() {
        return "NoHurtCam";
    }

}
