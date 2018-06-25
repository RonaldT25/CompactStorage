package com.tattyseal.compactstorage.util;

import com.tattyseal.compactstorage.CompactStorage;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.ModelLoader;

import java.awt.*;

public class UsefulFunctions
{
    public static final ResourceLocation slotTexture = new ResourceLocation("compactstorage", "textures/gui/chestslots.png");
    public static final ResourceLocation backgroundTexture = new ResourceLocation("compactstorage", "textures/gui/chest.png");

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static double slotTextureWidth = 432d;
    private static double slotTextureHeight = 216d;
    private static double chestTextureSize = 15d;

    /**
     * Entity Utils
     */

    public static EnumFacing get2dOrientation(EntityLivingBase entityliving)
    {
        EnumFacing[] orientationTable = {EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST};
        int orientationIndex = MathHelper.floor((entityliving.rotationYaw + 45.0) / 90.0) & 3;

        return orientationTable[orientationIndex];
    }

    /**
     * Model Registration Cleanup
     */

    public static void registerItem(Item item, int metadata, String itemName)
    {
        ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(itemName, "inventory"));
    }

    public static void registerChest()
    {
        Item chestItem = Item.getItemFromBlock(CompactStorage.ModBlocks.chest);
        ModelLoader.setCustomModelResourceLocation(chestItem, 0, new ModelResourceLocation("compactstorage:compactchest", "inventory"));
    }

    public static void registerBlock(Block block, int metadata, String blockName)
    {
        registerItem(Item.getItemFromBlock(block), metadata, blockName);
    }

    /**
     * Log helper
     */

    public static void dump(String string)
    {
        System.out.println("CompactStorage: " + string);
    }

    /**
     * Helper for Colours
     */

    public static int getColorFromHue(int hue)
    {
        Color color = (hue == -1 ? Color.white : Color.getHSBColor(hue / 360f, 0.5f, 0.5f).brighter());
        return color.getRGB();
    }

    public static int getColorFromNBT(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();

        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("info"))
        {
            return getColorFromHue(StorageInfo.fromTag(stack.getTagCompound().getCompoundTag("info")).getHue());
        }
        else
        {
            if(stack.hasTagCompound() && stack.getTagCompound().hasKey("hue"))
            {
                int hue = stack.getTagCompound().getInteger("hue");
                return getColorFromHue(hue);
            }

            if(stack.hasTagCompound() && !stack.getTagCompound().hasKey("hue") && stack.getTagCompound().hasKey("color"))
            {
                String color = "";

                if(tag.getTag("color") instanceof NBTTagInt)
                {
                    color = String.format("#%06X", (0xFFFFFF & tag.getInteger("color")));
                }
                else
                {
                    color = tag.getString("color");

                    if(color.startsWith("0x"))
                    {
                        color = "#" + color.substring(2);
                    }
                }

                if(!color.isEmpty())
                {
                    Color c = Color.decode(color);
                    float[] hsbVals = new float[3];

                    hsbVals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsbVals);
                    tag.setInteger("hue", (int) (hsbVals[0] * 360));
                }
            }
        }

        return 0xFFFFFF;
    }

    /** Render util **/

    public static void renderSlots(int x, int y, int width, int height)
    {
        mc.renderEngine.bindTexture(slotTexture);

        int realWidth = (width * 18);
        int realHeight = (height * 18);

        double ux = (1D / slotTextureWidth) * realWidth;
        double uz = (1D / slotTextureHeight) * realHeight;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(x, y + realHeight, 0).tex(0, uz).endVertex();
        worldRenderer.pos(x + realWidth, y + realHeight, 0).tex(ux, uz).endVertex();
        worldRenderer.pos(x + realWidth, y, 0).tex(ux, 0).endVertex();
        worldRenderer.pos(x, y, 0).tex(0, 0).endVertex();
        tessellator.draw();
    }

    public static void renderChestBackground(GuiContainer gui, int x, int y, int width, int height)
    {
        renderBackground(gui, x, y, Math.max(9, width) * 18, height * 18);
    }

    public static void renderBackground(GuiContainer gui, int x, int y, int width, int height)
    {
        mc.renderEngine.bindTexture(backgroundTexture);

        int realWidth = 7 + (width) + 7;
        int realHeight = 15 + (height) + 13 + 54 + 4 + 18 + 7;

        int by = y + (realHeight - 7);

        renderPartBackground(x, y, 0, 0, 7, 7, 7, 7);
        renderPartBackground(x + 7, y, 8, 0, 8, 7, (width), 7);
        renderPartBackground(x + 7 + (width), y, 9, 0, 15, 7, 7, 7);

        renderPartBackground(x, by, 0, 8, 7, 15, 7, 7);
        renderPartBackground(x + 7, by, 8, 8, 7, 15, (width), 7);
        renderPartBackground(x + 7 + (width), by, 9, 8, 15, 15, 7, 7);

        renderPartBackground(x, y + 7, 0, 7, 7, 7, 7, (realHeight - 14));
        renderPartBackground(x + realWidth - 8, y + 7, 8, 7, 15, 7, 8, (realHeight - 14));

        renderPartBackground(x + 7, y + 7, 8, 8, 8, 8, (width), realHeight - 14);
    }

    private static void renderPartBackground(int x, int y, int startX, int startY, int endX, int endY, int width, int height)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

        worldRenderer.pos((double) x, (double) y + height, 0).tex(getEnd(chestTextureSize, startX), getEnd(chestTextureSize, endY)).endVertex();
        worldRenderer.pos((double) x + width, (double) y + height, 0).tex(getEnd(chestTextureSize, endX), getEnd(chestTextureSize, endY)).endVertex();
        worldRenderer.pos((double) x + width, (double) y + 0, 0).tex(getEnd(chestTextureSize, endX), getEnd(chestTextureSize, startY)).endVertex();
        worldRenderer.pos((double) x, (double) y, 0).tex(getEnd(chestTextureSize, startX), getEnd(chestTextureSize, startY)).endVertex();

        tessellator.draw();
    }

    private static double getEnd(double width, double other)
    {
        return (1D / width) * other;
    }
}
