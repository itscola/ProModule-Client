package top.whitecola.promodule.gui.screens;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import top.whitecola.promodule.fonts.font2.FontLoaders;
import top.whitecola.promodule.gui.UICache;
import top.whitecola.promodule.gui.components.clickables.buttons.LabelButton;
import top.whitecola.promodule.utils.GUIUtils;
import top.whitecola.promodule.utils.Render2DUtils;

import java.awt.*;
import java.io.IOException;

public class MainClickGUIIngame extends GuiScreen {
    protected float width = 300;
    protected float height = 200;
    protected float xPosition = 90;
    protected float yPosition = 16;

    protected float dragX;
    protected float dragY;

    protected boolean draged;

    private boolean needClose;
    private boolean closed;

    protected Color backgroundColor = new Color(224, 224, 224);
    protected Color mainColor = new Color(255, 255, 255);
    protected Color titleColor = new Color(0, 0, 0);
    protected Color subColor = new Color(4, 115, 130);

    protected Color barColor = new Color(189, 189, 189);
    protected Color githubColor = new Color(50, 50, 50);
    protected Color textColor = new Color(64, 45, 45);

    protected LabelButton combatLabel;
    protected LabelButton movementLabel;
    protected LabelButton renderLabel;
    protected LabelButton worldLabel;
    protected LabelButton miscLabel;


    public MainClickGUIIngame(){

    }

    @Override
    public void initGui() {
        super.initGui();
//        combatLabel = new LabelButton()
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        if(GUIUtils.isHovered(this.xPosition + width/3.5f, this.yPosition+3, this.xPosition + (this.width)/1.5f+3, this.yPosition + 20,mouseX,mouseY) && Mouse.isButtonDown(0)){

            if (dragX == 0 && dragY == 0) {
                dragX = mouseX - xPosition;
                dragY = mouseY - yPosition;
            } else {
                xPosition = mouseX - dragX;
                yPosition = mouseY - dragY;
            }
            draged = true;

        } else if (dragX != 0 || dragY != 0) {
            dragX = 0;
            dragY = 0;
            if(draged){
                UICache.mainUIPosX = xPosition;
                UICache.mainUIPosY = yPosition;
                draged = false;
            }
        }


        int round = 2;

        //background
        Render2DUtils.drawRoundedRect2(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, round,this.backgroundColor.getRGB());
        //left
        Render2DUtils.drawRoundedRect2(this.xPosition, this.yPosition, this.xPosition + (this.width)/4f, this.yPosition + this.height, round,this.mainColor.getRGB());



        //title
//        FontRenderer fontRenderer = mc.fontRendererObj;
//        fontRenderer.drawString("ProModule",(int)this.xPosition+8 ,(int)this.yPosition+7,titleColor.getRGB());
//        CustomFont.getCustomFont().fontRenderer.drawString("ProModule",(int)this.xPosition+8 ,(int)this.yPosition+6,titleColor.getRGB(),false);
        FontLoaders.msFont19.drawString("ProModule",(int)this.xPosition+8 ,(int)this.yPosition+8,titleColor.getRGB());


        int range = 22;

        //clickableSubTitles
        FontLoaders.msFont18.drawString("Combat",(int)this.xPosition+13 ,(int)this.yPosition+45,titleColor.getRGB());
        FontLoaders.msFont18.drawString("Movement",(int)this.xPosition+13 ,(int)this.yPosition+45 +range*1,titleColor.getRGB());
        FontLoaders.msFont18.drawString("Render",(int)this.xPosition+13 ,(int)this.yPosition+45+range*2,titleColor.getRGB());
        FontLoaders.msFont18.drawString("World",(int)this.xPosition+13 ,(int)this.yPosition+45+range*3,titleColor.getRGB());
        FontLoaders.msFont18.drawString("Misc",(int)this.xPosition+13 ,(int)this.yPosition+45+range*4,titleColor.getRGB());


        //middle
        Render2DUtils.drawRoundedRect2(this.xPosition + width/3.5f, this.yPosition+3, this.xPosition + (this.width)/1.5f+3, this.yPosition + 20, 8,this.barColor.getRGB());

        //github
        Render2DUtils.drawRoundedRect2(this.xPosition, this.yPosition+height/1.3f+18, this.xPosition + (this.width)/4f, this.yPosition + this.height, round,this.githubColor.getRGB());
        FontLoaders.msFont18.drawString("Github",this.xPosition+23, this.yPosition+height/1.3f+28,mainColor.getRGB(),false);


        //right
        Render2DUtils.drawRoundedRect2(this.xPosition+(width/1.4f), this.yPosition, this.xPosition + this.width, this.yPosition + this.height, round,this.mainColor.getRGB());


        //items
        for(int i=0;i<6;i++){
            float yRange = this.height/7 * i +26;
            Render2DUtils.drawRoundedRect2(this.xPosition + width/3.8f, this.yPosition+yRange, this.xPosition + (this.width)/1.39f-6, this.yPosition + 24+yRange, 3,this.mainColor.getRGB());
//            FontRenderer fontRenderer = mc.fontRendererObj;
//            fontRenderer.drawString("Reach",(int)(this.xPosition + width/3.8f)+19, (int)(this.yPosition+yRange)+8,textColor.getRGB());
//            FontLoaders.msFont18.drawString("Reach",(this.xPosition + width/3.8f)+9, (this.yPosition+yRange)+8,textColor.getRGB(),false);
        }




        super.drawScreen(mouseX,mouseY,partialTicks);
    }



    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    protected void mouseClicked(int p_mouseClicked_1_, int p_mouseClicked_2_, int p_mouseClicked_3_) throws IOException {
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_);
    }

    @Override
    protected void mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_2_, int p_mouseReleased_3_) {
        super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_2_, p_mouseReleased_3_);
    }

    @Override
    protected void actionPerformed(GuiButton p_actionPerformed_1_) throws IOException {
        super.actionPerformed(p_actionPerformed_1_);
    }


}
