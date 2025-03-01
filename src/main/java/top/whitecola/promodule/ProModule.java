package top.whitecola.promodule;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.whitecola.promodule.config.HiConfig;
import top.whitecola.promodule.config.struct.HypixelConfig;
import top.whitecola.promodule.config.struct.ModuleConfig;
import top.whitecola.promodule.events.EventManager;
import top.whitecola.promodule.events.KeyEvent;
import top.whitecola.promodule.events.impls.*;
import top.whitecola.promodule.fonts.font2.FontLoaders;
import top.whitecola.promodule.fonts.font3.FontHelper;
import top.whitecola.promodule.fonts.font4.FontUtil;
import top.whitecola.promodule.fonts.font4.Fonts;
import top.whitecola.promodule.gui.widgets.WidgetManager;
import top.whitecola.promodule.keybinds.*;
import top.whitecola.promodule.modules.ModuleManager;
import top.whitecola.promodule.modules.impls.combat.*;
import top.whitecola.promodule.modules.impls.movement.*;
import top.whitecola.promodule.modules.impls.other.*;
import top.whitecola.promodule.modules.impls.render.*;
import top.whitecola.promodule.modules.impls.world.*;
import top.whitecola.promodule.services.apis.HypixelAPIWrapper;
import top.whitecola.promodule.utils.AntiDump;

import java.nio.charset.Charset;

@Mod(modid = ProModule.MODID, version = ProModule.VERSION)
public class ProModule {
    public static final String MODID = "promodule";
    public static final String VERSION = "1.0";
    private static ProModule proModule = null; {
        proModule = this;
    }
    private EventManager eventManager = EventManager.getEventManager();
    private WidgetManager widgetManager = new WidgetManager();
    private ModuleManager moduleManager = new ModuleManager();

    private HiConfig<ModuleConfig> moduleConfig = new HiConfig<ModuleConfig>("./ProModule/Modules.json",ModuleConfig.class, Charset.forName("utf8"));
    private HiConfig<HypixelConfig> hypixelConfig = new HiConfig<HypixelConfig>("./ProModule/HypixelConfig.json",HypixelConfig.class, Charset.forName("utf8"));
    public HypixelAPIWrapper hypixelAPIWrapper = new HypixelAPIWrapper();
//    public FontHelper font = new FontHelper();
    public Fonts fonts;


    @EventHandler
    public void init(FMLInitializationEvent event) {
//        FontLoaders.loadAllFonts();
        fonts = new Fonts();
        registerEvent();
        registerKeyBinds();
        registerModules();
        ProModule.getProModule().getModuleConfig().config.loadConfigForModules();

    }



    public void registerEvent(){
        MinecraftForge.EVENT_BUS.register(eventManager);
        MinecraftForge.EVENT_BUS.register(this);
        EventManager.getEventManager().addEvent(new MainMenuEvent());
        EventManager.getEventManager().addEvent(new EventToInvokeModules());
        EventManager.getEventManager().addEvent(new KeyEvent());
        EventManager.getEventManager().addEvent(new HypixelMenuEvnet());
        EventManager.getEventManager().addEvent(new EventToInvokeHypixelFeatures());
        EventManager.getEventManager().addEvent(new RotationEvent());


    }

    public void registerKeyBinds(){
        ClientRegistry.registerKeyBinding(MainMenuInGameKeybind.getInstance());
        ClientRegistry.registerKeyBinding(EagleKeyBind.getInstance());
        ClientRegistry.registerKeyBinding(ClearTargetKeybind.getInstance());
        ClientRegistry.registerKeyBinding(HypixelMenuKeybinds.getInstance());
        ClientRegistry.registerKeyBinding(AutoPlaceKeyBind.getInstance());
        ClientRegistry.registerKeyBinding(KillauraKeyBind.getInstance());
        ClientRegistry.registerKeyBinding(ScaffoldKeyBind.getInstance());
        ClientRegistry.registerKeyBinding(SpeedKeyBind.getInstance());


    }

    public void registerModules(){
        //render
        getModuleManager().addModule(new Chams());
        getModuleManager().addModule(new FullBright());
        getModuleManager().addModule(new OldAnimation());
        getModuleManager().addModule(new DamageBlood());
        getModuleManager().addModule(new NoFov());
        getModuleManager().addModule(new BlockOverlay());
        getModuleManager().addModule(new ItemPhysic());
        getModuleManager().addModule(new DamageColor());
        getModuleManager().addModule(new BetterChatLine());
//        getModuleManager().addModule(new TargetHud());
        getModuleManager().addModule(new ESP());
        getModuleManager().addModule(new BedESP());
        getModuleManager().addModule(new ChestESP());
        getModuleManager().addModule(new ScoreBoardGUI());
        getModuleManager().addModule(new Keystroke());
        getModuleManager().addModule(new Comestic());
        getModuleManager().addModule(new NoHurtCam());

        getModuleManager().addModule(new TargetHud());




        //combat
        getModuleManager().addModule(new AutoClicker());
        getModuleManager().addModule(new Reach());
        getModuleManager().addModule(new Velocity());
        getModuleManager().addModule(new AntiBot());
        getModuleManager().addModule(new AimAssist());
//        getModuleManager().addModule(new AimAssist2());

        getModuleManager().addModule(new HitBox());
        getModuleManager().addModule(new NoClickDelay());
//        getModuleManager().addModule(new ComboOneHit());
        getModuleManager().addModule(new AutoTool());
        getModuleManager().addModule(new Killaura());




        //movement
        getModuleManager().addModule(new Eagle());
        getModuleManager().addModule(new WTap());
        getModuleManager().addModule(new KeepSprint());
        getModuleManager().addModule(new Sprint());

//        getModuleManager().addModule(new EagleJump());

        getModuleManager().addModule(new LegitSafeWalk());
        getModuleManager().addModule(new AutoPlace());
        getModuleManager().addModule(new Speed());
        getModuleManager().addModule(new Scaffold3());


        //world
        getModuleManager().addModule(new FastPlace());
        getModuleManager().addModule(new Scaffold2());
        getModuleManager().addModule(new ChestStealer());
        getModuleManager().addModule(new AutoArmor());
        getModuleManager().addModule(new InvCleaner());

//        getModuleManager().addModule(new BedBreaker());




        //other
        getModuleManager().addModule(new NoClickGUISound());
        getModuleManager().addModule(new GUIBlur());
        getModuleManager().addModule(new Notification());
        getModuleManager().addModule(new Hud());
        getModuleManager().addModule(new MiddleClick());
        getModuleManager().addModule(new Disabler());
        getModuleManager().addModule(new AntiForge());
        getModuleManager().addModule(new GUICloser());
        getModuleManager().addModule(new HypixelPlus());

        getModuleManager().getModuleByName("AntiForge").setEnabled(true);
        getModuleManager().getModuleByClass(Hud.class).setEnabled(true);
        getModuleManager().addModule(new BetterFont());
//        BetterFont betterFont = (BetterFont) ProModule.getProModule().getModuleManager().getModuleByName("BetterFont");
//        betterFont.enable();

    }

    @Mod.EventHandler
    public void init(FMLPostInitializationEvent e){
        AntiDump.check();
    }



    public static ProModule getProModule() {
        return proModule;
    }


    public EventManager getEventManager() {
        return eventManager;
    }

    public WidgetManager getWidgetManager() {
        return widgetManager;
    }


    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public HiConfig<ModuleConfig> getModuleConfig() {
        return moduleConfig;
    }


    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        if (e.message.getFormattedText().contains("jndi") || e.message.getFormattedText().contains("ldap") || e.message.getFormattedText().contains("$") || e.message.getFormattedText().contains("{") || e.message.getFormattedText().contains("}")) {
            e.setCanceled(true);
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("White_cola: Intercepted a message for you : " + e.message.getFormattedText().replace("$", "").replace("{", "")));
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent e) {
        if (e.message.contains("jndi") || e.message.contains("ldap") || e.message.contains("$")) {
            e.setCanceled(true);
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("White_cola: Intercepted a message for you : " + e.message.replace("$", "").replace("{", "")));


        }

    }

    public HypixelAPIWrapper getHypixelAPIWrapper() {
        return hypixelAPIWrapper;
    }

    public HiConfig<HypixelConfig> getHypixelConfig() {
        return hypixelConfig;
    }
}
