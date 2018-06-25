package com.tattyseal.compactstorage.client.render;

import com.tattyseal.compactstorage.client.model.ModelCompactChest;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

/**
 * Created by Toby on 06/11/2014.
 */
@SideOnly(Side.CLIENT)
public class TileEntityChestRenderer extends TileEntitySpecialRenderer<TileEntityChest>
{
    private ModelCompactChest innerChestModel;
    private ModelChest outerChestModel;
    private static final ResourceLocation texture = new ResourceLocation("compactstorage", "textures/models/chest.png");
    private Minecraft mc;

    public TileEntityChestRenderer()
    {
        this.innerChestModel = new ModelCompactChest();
        this.outerChestModel = new ModelChest();
        this.mc = Minecraft.getMinecraft();
    }

    public void renderStatic(TileEntityChest entity, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 0);
        GlStateManager.scale(-1.0F, -1.0F, 1.0F);

        GlStateManager.enableRescaleNormal();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        mc.getTextureManager().bindTexture(new ResourceLocation("minecraft", "textures/blocks/stone.png"));
        outerChestModel.render(null, 0f, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }

    @Override
    public void render(TileEntityChest tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GL11.glPushMatrix();

        GL11.glTranslatef((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);

        GL11.glTranslatef(0.5F, 0.5F, 0.5F);

        EnumFacing direction = EnumFacing.NORTH;
        float f = 0;

        if(tile != null)
        {
            direction = tile.direction;
            f = tile.prevLidAngle + (tile.lidAngle - tile.prevLidAngle) * partialTicks;

            f = 1.0F - f;
            f = 1.0F - f * f * f;
        }

        switch (direction)
        {
            case NORTH: break;
            case SOUTH: GL11.glRotatef(180f, 0f, 1f, 0f); break;
            case WEST: GL11.glRotatef(-90f, 0f, 1f, 0f); break;
            case EAST: GL11.glRotatef(90f, 0f, 1f, 0f); break;
        }

        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        ResourceLocation texture = new ResourceLocation("compactstorage", "textures/models/chest_outer.png");

        IBlockState state = Blocks.IRON_BLOCK.getDefaultState();

        if(tile != null && tile.getStackInSlot(0) != null && !tile.getStackInSlot(0).isEmpty())
        {
            ItemStack stack = tile.getStackInSlot(0);

            if(stack.getItem() instanceof ItemBlock)
            {
                state = Block.getBlockFromItem(stack.getItem()).getStateFromMeta(stack.getItemDamage());
            }
        }

        TextureAtlasSprite s = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        ResourceLocation rl = new ResourceLocation(s.getIconName().split(":")[0], "textures/" + s.getIconName().split(":")[1] + ".png");

        int color;

        try
        {
            color = tile.color.brighter().getRGB();
        }
        catch(Exception exception)
        {
            color = Color.white.getRGB();
        }

        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 1F);



        ModelCompactChest m = new ModelCompactChest();
        outerChestModel.chestLid.rotateAngleX = -(f * ((float)Math.PI / 2F));
        m.chestLid.rotateAngleX = -(f * ((float)Math.PI / 2F));

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        outerChestModel.renderAll();

        GL11.glPushMatrix();
        GL11.glScalef(0.999f, 0.999f, 0.999f);
        GL11.glTranslatef(0.0005f, 0.0005f, 0.0001f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(rl);
        m.renderAll(s.getIconWidth(), s.getIconHeight());
        GL11.glPopMatrix();

        GL11.glColor3f(1f, 1f, 1f);

        if(tile != null && tile.getRetaining())
        {
            ItemStack stack = new ItemStack(Items.DIAMOND, 1, 0);
            EntityItem item = new EntityItem(tile.getWorld(), 0D, 0D, 0D, stack);
            item.hoverStart = 0.0F;

            GL11.glRotatef(180, 0, 0, 1);
            GL11.glTranslatef(-0.5f, -1.1f, 0.01f);

            Minecraft.getMinecraft().getRenderManager().renderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
        }

        GL11.glPopMatrix();
    }
}
