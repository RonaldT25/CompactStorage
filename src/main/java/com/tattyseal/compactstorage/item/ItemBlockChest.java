package com.tattyseal.compactstorage.item;

import com.tattyseal.compactstorage.CompactStorage;
import com.tattyseal.compactstorage.util.StorageInfo;
import com.tattyseal.compactstorage.util.UsefulFunctions;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Toby on 06/11/2014.
 */
public class ItemBlockChest extends ItemBlock
{
    public ItemBlockChest(Block block)
    {
        super(block);
    }

	@Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items)
    {
		if(tab == CompactStorage.tabCS)
		{
			ItemStack stack = new ItemStack(this, 1);

			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("info", new StorageInfo(9, 3, -1, 1, StorageInfo.Type.CHEST).getTag());

			stack.setTagCompound(tag);
			items.add(stack);
		}
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> list, @Nonnull ITooltipFlag flagIn)
    {
    	if(stack.hasTagCompound() && stack.getTagCompound().hasKey("info"))
    	{
    		StorageInfo info = StorageInfo.fromTag(stack.getTagCompound().getCompoundTag("info"));
			list.add(TextFormatting.GREEN + "Slots: " + (info.getSizeX() * info.getSizeY()));
			list.add(TextFormatting.GREEN + "Pages: " + (info.getPages()));
			list.add(TextFormatting.AQUA + (info.getHue() == -1 ? "White" : "Hue: " + info.getHue()));

			if(stack.getTagCompound().hasKey("chestData"))
			{
				list.add("");
				list.add(TextFormatting.AQUA + "" + TextFormatting.ITALIC + "Retaining");
			}
    	}
    	
    	super.addInformation(stack, worldIn, list, flagIn);
    }
}
