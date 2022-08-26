package top.whitecola.promodule.modules.impls.combat;

import top.whitecola.promodule.annotations.ModuleSetting;
import top.whitecola.promodule.modules.AbstractModule;
import top.whitecola.promodule.modules.ModuleCategory;

public class Reach extends AbstractModule {
    @ModuleSetting(name = "MinRange")
    public Float minRange = 3.1f;

    @ModuleSetting(name = "MaxRange")
    public Float maxRange = 3.3f;

    @Override
    public ModuleCategory getModuleType() {
        return ModuleCategory.COMBAT;
    }

    @Override
    public String getModuleName() {
        return "Reach";

    }

    @Override
    public void onEnable() {
        super.onEnable();
    }


    @Override
    public String getDisplayName() {
        return super.getDisplayName() + " (G)";
    }
}
