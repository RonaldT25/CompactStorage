package com.tattyseal.compactstorage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tattyseal.compactstorage.block.BlockBarrel;
import com.tattyseal.compactstorage.block.BlockChest;
import com.tattyseal.compactstorage.block.BlockChestBuilder;
import com.tattyseal.compactstorage.block.BlockFluidBarrel;
import com.tattyseal.compactstorage.client.render.TileEntityBarrelFluidRenderer;
import com.tattyseal.compactstorage.client.render.TileEntityBarrelRenderer;
import com.tattyseal.compactstorage.client.render.TileEntityChestRenderer;
import com.tattyseal.compactstorage.creativetabs.CreativeTabCompactStorage;
import com.tattyseal.compactstorage.event.CompactStorageEventHandler;
import com.tattyseal.compactstorage.item.ItemBackpack;
import com.tattyseal.compactstorage.item.ItemBlockChest;
import com.tattyseal.compactstorage.network.handler.C01HandlerUpdateBuilder;
import com.tattyseal.compactstorage.network.handler.C02HandlerCraftChest;
import com.tattyseal.compactstorage.network.packet.C01PacketUpdateBuilder;
import com.tattyseal.compactstorage.network.packet.C02PacketCraftChest;
import com.tattyseal.compactstorage.proxy.IProxy;
import com.tattyseal.compactstorage.tileentity.TileEntityBarrel;
import com.tattyseal.compactstorage.tileentity.TileEntityBarrelFluid;
import com.tattyseal.compactstorage.tileentity.TileEntityChest;
import com.tattyseal.compactstorage.tileentity.TileEntityChestBuilder;
import com.tattyseal.compactstorage.util.UsefulFunctions;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by Toby on 06/11/2014.
 * Updated for 3.0 on the 16/02/2018
 */
@Mod(modid = CompactStorage.ID, name = "CompactStorage", version = "3.1", guiFactory = "com.tattyseal.compactstorage.client.gui.factory.CompactStorageGuiFactory")
@Mod.EventBusSubscriber
public class CompactStorage
{
    @Mod.Instance(CompactStorage.ID)
    public static CompactStorage instance;

    @SidedProxy(clientSide = "com.tattyseal.compactstorage.proxy.ClientProxy", serverSide = "com.tattyseal.compactstorage.proxy.ServerProxy", modId = CompactStorage.ID)
    public static IProxy proxy;

    public static final CreativeTabs tabCS = new CreativeTabCompactStorage();
    public static final Logger logger = LogManager.getLogger("CompactStorage");
    public SimpleNetworkWrapper wrapper;

    public static Map<String, IBlockState> fullBlocks;
    public static List<ItemStack> stairItems;
    
    public static final String ID = "compactstorage";

    @GameRegistry.ObjectHolder(ID)
    public static class ModBlocks
    {
        public static Block chest;
        public static Block chestBuilder;
        public static Block barrel;
        public static Block barrel_fluid;
    }

    @GameRegistry.ObjectHolder(ID)
    public static class ModItems
    {
        public static ItemBlock itemBlockBarrel;
        public static ItemBlock itemBlockBarrel_fluid;
        public static ItemBlockChest ibChest;
        public static Item backpack;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> e)
    {
        e.getRegistry().registerAll(
                ModBlocks.chest = new BlockChest(),
                ModBlocks.chestBuilder = new BlockChestBuilder(),
                ModBlocks.barrel = new BlockBarrel(),
                ModBlocks.barrel_fluid = new BlockFluidBarrel()
        );

        GameRegistry.registerTileEntity(TileEntityChest.class, "tileChest");
        GameRegistry.registerTileEntity(TileEntityChestBuilder.class, "tileChestBuilder");
        GameRegistry.registerTileEntity(TileEntityBarrel.class, "tileBarrel");
        GameRegistry.registerTileEntity(TileEntityBarrelFluid.class, "tileBarrel_fluid");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e)
    {
        ModItems.ibChest = new ItemBlockChest(ModBlocks.chest);
        ModItems.ibChest.setRegistryName("compactChest");
        e.getRegistry().register(ModItems.ibChest);

        ItemBlock ibChestBuilder = new ItemBlock(ModBlocks.chestBuilder);
        ibChestBuilder.setRegistryName("chestBuilder");
        ibChestBuilder.setCreativeTab(tabCS);
        e.getRegistry().register(ibChestBuilder);

        ModItems.backpack = new ItemBackpack();
        ModItems.backpack.setRegistryName("backpack");
        e.getRegistry().register(ModItems.backpack);

        ModItems.itemBlockBarrel = new ItemBlock(ModBlocks.barrel);
        ModItems.itemBlockBarrel.setRegistryName(ModBlocks.barrel.getRegistryName());
        e.getRegistry().register(ModItems.itemBlockBarrel);

        ModItems.itemBlockBarrel_fluid = new ItemBlock(ModBlocks.barrel_fluid);
        ModItems.itemBlockBarrel_fluid.setRegistryName(ModBlocks.barrel_fluid.getRegistryName());
        e.getRegistry().register(ModItems.itemBlockBarrel_fluid);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChest.class, new TileEntityChestRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBarrel.class, new TileEntityBarrelRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBarrelFluid.class, new TileEntityBarrelFluidRenderer());

        UsefulFunctions.registerChest();
        UsefulFunctions.registerBlock(ModBlocks.chestBuilder, 0, "compactstorage:chestBuilder");

        UsefulFunctions.registerItem(ModItems.itemBlockBarrel, 0, "compactstorage:barrel");
        UsefulFunctions.registerItem(ModItems.itemBlockBarrel_fluid, 0, "compactstorage:barrel_fluid");

        UsefulFunctions.registerBlock(ModBlocks.chest, 0, "compactstorage:compactchest");
        UsefulFunctions.registerItem(ModItems.backpack, 0, "compactstorage:backpack");

    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        OreDictionary.registerOre("barsIron", Blocks.IRON_BARS);
        OreDictionary.registerOre("blockChest", Blocks.CHEST);
        OreDictionary.registerOre("itemClay", Items.CLAY_BALL);

        OreDictionary.registerOre("string", Items.STRING);
        OreDictionary.registerOre("wool", Blocks.WOOL);
        
        wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(CompactStorage.ID);
        wrapper.registerMessage(C01HandlerUpdateBuilder.class, C01PacketUpdateBuilder.class, 0, Side.SERVER);
        wrapper.registerMessage(C02HandlerCraftChest.class, C02PacketCraftChest.class, 1, Side.SERVER);

        ConfigurationHandler.configFile = event.getSuggestedConfigurationFile();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.registerRenderers();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        MinecraftForge.EVENT_BUS.register(new CompactStorageEventHandler());

        GameRegistry.addShapedRecipe(new ResourceLocation("compactstorage", "chest_builder"), null, new ItemStack(ModBlocks.chestBuilder, 1), "ILI", "ICI", "ILI", 'I', new ItemStack(Items.IRON_INGOT, 1), 'C', new ItemStack(Blocks.CHEST, 1), 'L', new ItemStack(Blocks.LEVER, 1));
        GameRegistry.addShapedRecipe(new ResourceLocation("compactstorage", "barrel"), null, new ItemStack(ModBlocks.barrel, 1), "III", "GCG", "III", 'I', new ItemStack(Items.IRON_INGOT, 1), 'G', new ItemStack(Blocks.IRON_BLOCK, 1), 'C', new ItemStack(Blocks.CHEST, 1));
        GameRegistry.addShapedRecipe(new ResourceLocation("compactstorage", "drum"), null, new ItemStack(ModBlocks.barrel_fluid, 1), "ICI", "GIG", "ICI", 'I', new ItemStack(Items.IRON_INGOT, 1), 'G', new ItemStack(Blocks.IRON_BLOCK, 1), 'C', new ItemStack(Blocks.GLASS_PANE, 1));

        ConfigurationHandler.init();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        if(fullBlocks == null)
            fullBlocks = Maps.newHashMap();

        if(stairItems == null)
            stairItems = Lists.newArrayList();

        stairItems.clear();
        fullBlocks.clear();
        ForgeRegistries.BLOCKS.getEntries().forEach((entry) -> {
            // get the block
            Block b = entry.getValue();

            //get sub states
            NonNullList<ItemStack> subBlocks = NonNullList.create();
            b.getSubBlocks(null, subBlocks);

            //convert the meta to block states
            NonNullList<IBlockState> states = NonNullList.create();
            subBlocks.forEach((stack) -> {
                states.add(b.getStateFromMeta(stack.getItemDamage()));
            });

            //add states to list
            states.forEach((state) -> {
                if(state.isFullBlock() && !b.hasTileEntity(state))
                {
                    ItemStack stack = new ItemStack(b, 1, b.getMetaFromState(state));

                    stairItems.add(stack);
                    fullBlocks.put(state.toString(), state);
                }
            });
        });
    }
}
