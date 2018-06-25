package com.tattyseal.compactstorage.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCompactChest extends ModelBase
{
    /** The chest lid in the chest's model. */
    public ModelRenderer chestLid = (new ModelRenderer(this, 0, 0)).setTextureSize(16, 16);
    /** The model of the bottom of the chest. */
    public ModelRenderer chestBelow;
    /** The chest's knob in the chest model. */
    public ModelRenderer chestKnob;

    public ModelCompactChest()
    {
        this.chestLid= (new ModelRenderer(this, 2, 2)).setTextureSize(16, 16);
        this.chestLid.cubeList.add(new ModelBoxFix(chestLid, 2, 2, 0, -5f, -14f, 14, 4.9f, 14, 0, false));
        this.chestLid.rotationPointX = 1.0F;
        this.chestLid.rotationPointY = 7F;
        this.chestLid.rotationPointZ = 15.0F;

        this.chestKnob = (new ModelRenderer(this, 2, 2)).setTextureSize(16, 16);
        this.chestKnob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
        this.chestKnob.rotationPointX = 8.0F;
        this.chestKnob.rotationPointY = 7.0F;
        this.chestKnob.rotationPointZ = 15.0F;
        this.chestBelow = (new ModelRenderer(this, 2, 6)).setTextureSize(16, 16);
        this.chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
        this.chestBelow.rotationPointX = 1.0F;
        this.chestBelow.rotationPointY = 6.0F;
        this.chestBelow.rotationPointZ = 1.0F;
    }

    /**
     * This method renders out all parts of the chest model.
     */
    public void renderAll(int w, int h)
    {
        this.chestKnob.rotateAngleX = this.chestLid.rotateAngleX;
        this.chestLid.setTextureSize(w, h);
        this.chestLid.render(0.0625F);
        this.chestKnob.setTextureSize(w, h);
        this.chestKnob.render(0.0625F);
        this.chestBelow.setTextureSize(w, h);
        this.chestBelow.render(0.0625F);
    }
}