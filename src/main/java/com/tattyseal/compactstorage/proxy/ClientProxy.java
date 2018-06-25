package com.tattyseal.compactstorage.proxy;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.util.UsefulFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Toby on 06/11/2014.
 */
public class ClientProxy implements IProxy
{
    public void registerRenderers()
    {

        ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

        itemColors.registerItemColorHandler(new IItemColor() {
            @Override
            public int colorMultiplier(ItemStack stack, int color)
            {
                return UsefulFunctions.getColorFromNBT(stack);
            }
        }, CompactStorage.ModItems.backpack, Item.getItemFromBlock(CompactStorage.ModBlocks.chest));

    }


}
