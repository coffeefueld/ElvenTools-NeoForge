package com.mod.elventools;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.mod.elventools.blocks.ElvenCherryTomatoesCropBlock;
import com.mod.elventools.blocks.ElvenParsnipCropBlock;
import com.mod.elventools.items.ElvenPickaxe;
import com.mod.elventools.items.ElvenRapier;
import com.mod.elventools.items.ElvenSword;
import com.mod.elventools.items.ParsnipAndPotatoStew;
import com.mod.elventools.items.SummoningCrystal;
import com.mod.elventools.items.TheodoraAmulet;
import com.mod.elventools.items.TheodoraCharm;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ElvenTools.MODID)
public class ElvenTools
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "elventools";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    /* Blocks */

    // Ruby Ore Block
    public static final DeferredBlock<DropExperienceBlock> RUBY_ORE_BLOCK = BLOCKS.register("ruby_ore_block",
            () -> new DropExperienceBlock(UniformInt.of(4, 10), //Experience drop range 
                     BlockBehaviour.Properties.of()
                     .mapColor(MapColor.STONE)
                     .strength(3f)
                     .sound(SoundType.STONE)
                     .requiresCorrectToolForDrops()
            )
    );
    
    //Ruby Ore Block item
    public static final DeferredItem<Item> RUBY_ORE_BLOCK_ITEM = ITEMS.register("ruby_ore_block", () -> new BlockItem(RUBY_ORE_BLOCK.get(), new Item.Properties()));
    
    //Ruby Block 
    public static final DeferredBlock<Block> RUBY_BLOCK = BLOCKS.register("ruby_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK))

    );

    //Ruby Block item
    public static final DeferredItem<Item> RUBY_BLOCK_ITEM = ITEMS.register("ruby_block", () -> new BlockItem(RUBY_BLOCK.get(), new Item.Properties()));

    //Elven Parsnip Crop Block
    public static final DeferredBlock<Block> ELVEN_PARSNIP_CROP = BLOCKS.register("elven_parsnip_crop",
                () -> new ElvenParsnipCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));
    
    //Elven Cherry Tomatoes Crop Block
    public static final DeferredBlock<Block> ELVEN_CHERRY_TOMATOES_CROP = BLOCKS.register("elven_cherry_tomatoes_crop",
                () -> new ElvenCherryTomatoesCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT)));
    
    /* Tags */

    // Block taggs for elven steel tier
    public static final TagKey<Block> NEEDS_ELVEN_STEEL = BlockTags.NEEDS_DIAMOND_TOOL;
    public static final TagKey<Block> INCORRECT_FOR_ELVEN_STEEL = BlockTags.INCORRECT_FOR_DIAMOND_TOOL;

    /* Food and Husbandry */

    // Elven Bread
    public static final DeferredItem<Item> ELVEN_BREAD = ITEMS.register("elven_bread",
        () -> new Item(new Item.Properties()
            .food(new FoodProperties.Builder()
                .nutrition(8)
                .saturationModifier(8f)
                .build()
            )
        )
    );
   
    // Elven Parsnip
    public static final DeferredItem<Item> ELVEN_PARSNIP = ITEMS.register("elven_parsnip",
        () -> new ItemNameBlockItem(ELVEN_PARSNIP_CROP.get() , new Item.Properties()
            .food(new FoodProperties.Builder()
                .nutrition(1)
                .saturationModifier(2f)
                .build()
            )
        )
    );
    // Roast Elven Parsnip
    public static final DeferredItem<Item> ROASTED_ELVEN_PARSNIP = ITEMS.register("roasted_elven_parsnip",
        () -> new ItemNameBlockItem(ELVEN_PARSNIP_CROP.get() , new Item.Properties()
            .food(new FoodProperties.Builder()
                .nutrition(4)
                .saturationModifier(3f)
                .build()
            )
        )
    );
    // Elven Cherry Tomatoes
    public static final DeferredItem<Item> ELVEN_CHERRY_TOMATOES = ITEMS.register("elven_cherry_tomatoes",
        () -> new Item(new Item.Properties()
            .food(new FoodProperties.Builder()
                .nutrition(3)
                .saturationModifier(2f)
                .build()
            )
        )
    );
    // Elven Cherry Tomatoes seeds
    public static final DeferredItem<Item> ELVEN_CHERRY_TOMATOES_SEEDS = ITEMS.register("elven_cherry_tomatoes_seeds",
        () -> new ItemNameBlockItem(ELVEN_CHERRY_TOMATOES_CROP.get() , new Item.Properties()
        )
    );
    // Parsnip and potato stew
    public static final DeferredItem<Item> PARSNIP_AND_POTATO_STEW = ITEMS.register("parsnip_and_potato_stew",
    () -> new ParsnipAndPotatoStew(new Item.Properties().stacksTo(1)));

    /* Coins and Gems */

    // Gold coin
    public static final DeferredItem<Item> GOLD_COIN = ITEMS.register("gold_coin",
        () -> new Item(new Item.Properties()
        )
    );
    // Elven star coin
    public static final DeferredItem<Item> GOLD_AND_STAR_COIN = ITEMS.register("gold_and_star_coin",
        () -> new Item(new Item.Properties()
        )
    );
    // Gold and ruby coin
     public static final DeferredItem<Item> GOLD_AND_RUBY_COIN = ITEMS.register("gold_and_ruby_coin",
        () -> new Item(new Item.Properties()
        )
    );
    // Gold and emerald coin
    public static final DeferredItem<Item> GOLD_AND_EMERALD_COIN = ITEMS.register("gold_and_emerald_coin",
        () -> new Item(new Item.Properties()
        )
    );
    // Elven steel ingot, it's necessary for the elven steel tier, it's not used in game
    public static final DeferredItem<Item> ELVEN_STEEL_INGOT = ITEMS.register("elven_steel_ingot",
        () -> new Item(new Item.Properties()
            .fireResistant()            
        )
    );
     // Ruby
    public static final DeferredItem<Item> RUBY = ITEMS.register("ruby",
        () -> new Item(new Item.Properties()
            .fireResistant()            
        )
    );

    /* Tools */

    // Elven steel tier
    public static final Tier ELVEN_STEEL_TIER = new SimpleTier(INCORRECT_FOR_ELVEN_STEEL, 4000, 20.0F, 8, 15, () -> Ingredient.of(ELVEN_STEEL_INGOT.get())
    //4000, //Level
    //20.0F, //Mining Speed
    //8, //Attack Damage Bonus
    //15, //Enchantment Value, it's not used, but it's necessary for the constructor to work
    //NEEDS_ELVEN_STEEL, 
    //() -> Ingredient.of(ELVEN_STEEL_INGOT.get()), 
    //INCORRECT_FOR_ELVEN_STEEL
    );

    // Elven sword
    public static final DeferredItem<SwordItem> ELVEN_SWORD = ITEMS.register("elven_sword",
       () -> new ElvenSword(
        ELVEN_STEEL_TIER,
        new Item.Properties()
        .attributes(SwordItem.createAttributes(ELVEN_STEEL_TIER, 10, -1.5F))
        .fireResistant()
        )
    );
    // Elven sword V2
    public static final DeferredItem<SwordItem> ELVEN_SWORD_V2 = ITEMS.register("elven_sword_v2",
       () -> new ElvenSword(
        ELVEN_STEEL_TIER,
        new Item.Properties()
        .attributes(SwordItem.createAttributes(ELVEN_STEEL_TIER, 12, -2.2F))
        .fireResistant()
        )
    );
    //Elven Rapier
    public static final DeferredItem<SwordItem> ELVEN_RAPIER = ITEMS.register("elven_rapier",
       () -> new ElvenRapier(
        ELVEN_STEEL_TIER,
        new Item.Properties()
        .attributes(SwordItem.createAttributes(ELVEN_STEEL_TIER, 4, 2))
        .fireResistant()
        )
    );    
    //Elven Rapier V2
    public static final DeferredItem<SwordItem> ELVEN_RAPIER_V2 = ITEMS.register("elven_rapier_v2",
       () -> new ElvenRapier(
        ELVEN_STEEL_TIER,
        new Item.Properties()
        .attributes(SwordItem.createAttributes(ELVEN_STEEL_TIER, 5, 1.5f))
        .fireResistant()
        )
    );
    
    // Elven pickaxe
    public static final DeferredItem<PickaxeItem> ELVEN_PICKAXE = ITEMS.register("elven_pickaxe",
        () -> new ElvenPickaxe(
        ELVEN_STEEL_TIER,
        new Item.Properties()
        .attributes(PickaxeItem.createAttributes(ELVEN_STEEL_TIER, -1f, -2.8f))
        .fireResistant()
        )
    );
    // Elven pickaxe V2
    public static final DeferredItem<PickaxeItem> ELVEN_PICKAXE_V2 = ITEMS.register("elven_pickaxe_v2",
        () -> new ElvenPickaxe(
        ELVEN_STEEL_TIER,
        new Item.Properties()
        .attributes(PickaxeItem.createAttributes(ELVEN_STEEL_TIER, 3f, -3.5f))
        .fireResistant()
        )
    );
    
    /* Novelty Items */
    
    // Theodora Charm
    public static final DeferredItem<Item> THEODORA_CHARM = ITEMS.register("theodora_charm",
        () -> new TheodoraCharm(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
        )
    );
    // Theodora Amulet
    public static final DeferredItem<Item> THEODORA_AMULET = ITEMS.register("theodora_amulet",
        () -> new TheodoraAmulet(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
        )
    );
    // Summoning Crystal
    public static final DeferredItem<Item> SUMMONING_CRYSTAL = ITEMS.register("summoning_crystal",
        () -> new SummoningCrystal(new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
        )
    );

    // Creates a creative tab with the id "elventools:cmode_tab" for the items, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CMODE_TAB = CREATIVE_MODE_TABS.register("cmode_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ELVEN_SWORD_V2.get().getDefaultInstance())
            .title(Component.translatable(MODID + ".cmode_tab"))
            .displayItems((parameters, output) -> {
                    output.accept(RUBY_ORE_BLOCK.get());
                    output.accept(RUBY_BLOCK.get());
                    output.accept(RUBY.get());
                    output.accept(ELVEN_BREAD.get());
                    output.accept(ELVEN_PARSNIP.get());
                    output.accept(ROASTED_ELVEN_PARSNIP.get());
                    output.accept(ELVEN_CHERRY_TOMATOES.get());
                    output.accept(ELVEN_CHERRY_TOMATOES_SEEDS.get());
                    output.accept(PARSNIP_AND_POTATO_STEW.get());
                    output.accept(GOLD_COIN.get());
                    output.accept(GOLD_AND_RUBY_COIN.get());
                    output.accept(GOLD_AND_EMERALD_COIN.get());
                    output.accept(GOLD_AND_STAR_COIN.get());
                    output.accept(THEODORA_CHARM.get());
                    output.accept(THEODORA_AMULET.get());
                    output.accept(SUMMONING_CRYSTAL.get());
                    output.accept(ELVEN_SWORD.get());
                    output.accept(ELVEN_SWORD_V2.get());
                    output.accept(ELVEN_RAPIER.get());
                    output.accept(ELVEN_RAPIER_V2.get());
                    output.accept(ELVEN_PICKAXE.get());
                    output.accept(ELVEN_PICKAXE_V2.get());

            }).build());

    public ElvenTools(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        //if (Config.logDirtBlock)
        //    LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));

        /* Making parsnip and cherry tomatoes and tomato seeds compostable */
        event.enqueueWork(() -> {
            ComposterBlock.COMPOSTABLES.put(ELVEN_PARSNIP.get(), 0.4F);
            ComposterBlock.COMPOSTABLES.put(ELVEN_CHERRY_TOMATOES.get(), 0.4F);
            ComposterBlock.COMPOSTABLES.put(ELVEN_CHERRY_TOMATOES_SEEDS.get(), 0.15F);
            
        });
    
    }

    // Add the ruby_ore_block_item and the ruby_block_item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(RUBY_ORE_BLOCK_ITEM);
            event.accept(RUBY_BLOCK_ITEM);
        }
    }
    // Adds trades
    private static void addTradesToWanderingTrader(List<ItemListing> generic, List<ItemListing> rare) {
        LOGGER.info("Adding trades to wandering trader");
        VillagerTrades.WANDERING_TRADER_TRADES.put(1, generic.toArray(new ItemListing[0]));
        VillagerTrades.WANDERING_TRADER_TRADES.put(2, rare.toArray(new ItemListing[0]));
        LOGGER.info("Trades added: Generic - {}, Rare - {}", generic.size(), rare.size());
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
        // Trader
        LOGGER.info("Server about to start - posting WandererTradesEvent");
        List<ItemListing> genericTrades = new ArrayList<>();
        List<ItemListing> rareTrades = new ArrayList<>();

        // Adding custom trades

        // Generic Trades
        genericTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_COIN.get(), 1), //Asking for
                new ItemStack(ELVEN_BREAD.get(), 1), //Selling
                10, //Uses
                5,  //XP
                0.05F //Price multiplier
            )
        );
        genericTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_COIN.get(), 1), //Asking for
                new ItemStack(ELVEN_PARSNIP.get(), 3), //Selling
                10, //Uses
                5,  //XP
                0.05F //Price multiplier
            )
        );
        genericTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_COIN.get(), 1), //Asking for
                new ItemStack(ELVEN_CHERRY_TOMATOES_SEEDS.get(), 3), //Selling
                10, //Uses
                5,  //XP
                0.05F //Price multiplier
            )
        );
        genericTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_COIN.get(), 5), //Asking for
                new ItemStack(PARSNIP_AND_POTATO_STEW.get(), 1), //Selling
                10, //Uses
                5,  //XP
                0.05F //Price multiplier
            )
        );
        genericTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_COIN.get(), 1), //Asking for
                new ItemStack(ELVEN_CHERRY_TOMATOES.get(), 2), //Selling
                10, //Uses
                5,  //XP
                0.05F //Price multiplier
            )
        );

        // Rare Trades
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 14), //Asking for
                new ItemStack(ELVEN_SWORD.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 12), //Asking for
                new ItemStack(ELVEN_RAPIER.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 9), //Asking for
                new ItemStack(ELVEN_PICKAXE.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );

        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 12), //Asking for
                new ItemStack(ELVEN_SWORD.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 14), //Asking for
                new ItemStack(ELVEN_RAPIER.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 11), //Asking for
                new ItemStack(ELVEN_PICKAXE.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );

        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 13), //Asking for
                new ItemStack(ELVEN_SWORD_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 12), //Asking for
                new ItemStack(ELVEN_RAPIER_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 11), //Asking for
                new ItemStack(ELVEN_PICKAXE_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );

        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 14), //Asking for
                new ItemStack(ELVEN_SWORD_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 17), //Asking for
                new ItemStack(ELVEN_RAPIER_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 14), //Asking for
                new ItemStack(ELVEN_PICKAXE_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 17), //Asking for
                new ItemStack(ELVEN_SWORD.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 14), //Asking for
                new ItemStack(ELVEN_RAPIER.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 13), //Asking for
                new ItemStack(ELVEN_PICKAXE.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );

        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 9), //Asking for
                new ItemStack(ELVEN_SWORD.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 7), //Asking for
                new ItemStack(ELVEN_RAPIER.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 17), //Asking for
                new ItemStack(ELVEN_PICKAXE.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );

        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 8), //Asking for
                new ItemStack(ELVEN_SWORD_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 8), //Asking for
                new ItemStack(ELVEN_RAPIER_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_RUBY_COIN.get(), 13), //Asking for
                new ItemStack(ELVEN_PICKAXE_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );

        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 7), //Asking for
                new ItemStack(ELVEN_SWORD_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 7), //Asking for
                new ItemStack(ELVEN_RAPIER_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_EMERALD_COIN.get(), 15), //Asking for
                new ItemStack(ELVEN_PICKAXE_V2.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );

        rareTrades.add(
            (entity, random) -> new MerchantOffer(
                new ItemCost(GOLD_AND_STAR_COIN.get(), 1), //Asking for
                new ItemStack(THEODORA_CHARM.get(), 1), //Selling
                1, //Uses 
                20, //XP
                0.05F //Price multiplier
            )
        );
        MinecraftServer server = event.getServer();
        RegistryAccess registryAccess = server.registryAccess();
        NeoForge.EVENT_BUS.post(new WandererTradesEvent(genericTrades, rareTrades, registryAccess));
        addTradesToWanderingTrader(genericTrades, rareTrades);
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
