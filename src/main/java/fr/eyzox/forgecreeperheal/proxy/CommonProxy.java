package fr.eyzox.forgecreeperheal.proxy;

import fr.eyzox.bsc.config.IConfigProvider;
import fr.eyzox.bsc.config.loader.JSONConfigLoader;
import fr.eyzox.forgecreeperheal.ForgeCreeperHeal;
import fr.eyzox.forgecreeperheal.builder.blockdata.*;
import fr.eyzox.forgecreeperheal.builder.dependency.*;
import fr.eyzox.forgecreeperheal.builder.dependency.property.*;
import fr.eyzox.forgecreeperheal.commands.ForgeCreeperHealCommands;
import fr.eyzox.forgecreeperheal.config.ConfigProvider;
import fr.eyzox.forgecreeperheal.config.FastConfig;
import fr.eyzox.forgecreeperheal.factory.DefaultFactory;
import fr.eyzox.forgecreeperheal.factory.keybuilder.BlockKeyBuilder;
import fr.eyzox.forgecreeperheal.handler.ChunkEventHandler;
import fr.eyzox.forgecreeperheal.handler.ExplosionEventHandler;
import fr.eyzox.forgecreeperheal.handler.PlayerConnectionHandler;
import fr.eyzox.forgecreeperheal.handler.WorldEventHandler;
import fr.eyzox.forgecreeperheal.healer.HealerManager;
import fr.eyzox.forgecreeperheal.scheduler.TickTimelineFactory;
import net.minecraft.block.*;
import net.minecraft.block.BlockBanner.BlockBannerHanging;
import net.minecraft.block.BlockBanner.BlockBannerStanding;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CommonProxy {

    private Logger logger;

    private ConfigProvider configProvider;
    private FastConfig config;
    //private SimpleNetworkWrapper channel;

    private TickTimelineFactory healerFactory;
    private Map<WorldServer, HealerManager> healerManagers;

    private DefaultFactory<Block, IBlockDataBuilder> blockDataFactory;
    private DefaultFactory<Block, IDependencyBuilder> dependencyFactory;

    private ChunkEventHandler chunkEventHandler;
    private ExplosionEventHandler explosionEventHandler;
    private WorldEventHandler worldTickEventHandler;
    private PlayerConnectionHandler playerConnectionHandler;


    public void onPreInit(FMLPreInitializationEvent event) {
        this.logger = event.getModLog();

        ForgeCreeperHeal.getInstance().registerReflection();

        this.configProvider = new ConfigProvider(new JSONConfigLoader(event.getSuggestedConfigurationFile()), new File(ForgeCreeperHeal.MODID + "-config-error.log"));

        this.healerFactory = new TickTimelineFactory();
        this.blockDataFactory = loadBlockDataFactory();
        this.dependencyFactory = loadDependencyFactory();

        this.config = new FastConfig();
        this.configProvider.addConfigListener(config);
        this.loadConfig();

    }

    public void onInit(FMLInitializationEvent event) {
        this.chunkEventHandler = new ChunkEventHandler();
        this.chunkEventHandler.register();

        this.explosionEventHandler = new ExplosionEventHandler();
        this.explosionEventHandler.register();

        this.worldTickEventHandler = new WorldEventHandler();
        this.worldTickEventHandler.register();

        this.playerConnectionHandler = new PlayerConnectionHandler();
        this.playerConnectionHandler.register();

        //channel = NetworkRegistry.INSTANCE.newSimpleChannel(ForgeCreeperHeal.MODID+":"+"ch0");
        //channel.registerMessage(ModDataMessage.Handler.class, ModDataMessage.class, 0, Side.SERVER);
        //channel.registerMessage(ProfilerInfoMessage.Handler.class, ProfilerInfoMessage.class, 1, Side.CLIENT);
    }

    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        this.healerManagers = new HashMap<WorldServer, HealerManager>();
    }

    public void serverStarting(FMLServerStartingEvent event) {
        ForgeCreeperHealCommands.register();
    }

    public Logger getLogger() {
        return logger;
    }

    public IConfigProvider getConfigProvider() {
        return configProvider;
    }

    public FastConfig getConfig() {
        return this.config;
    }

	/*
    public SimpleNetworkWrapper getChannel() {
		return channel;
	}
	 */

    public Map<WorldServer, HealerManager> getHealerManagers() {
        return healerManagers;
    }

    public TickTimelineFactory getHealerFactory() {
        return healerFactory;
    }

    public DefaultFactory<Block, IBlockDataBuilder> getBlockDataFactory() {
        return blockDataFactory;
    }

    public DefaultFactory<Block, IDependencyBuilder> getDependencyFactory() {
        return dependencyFactory;
    }

    public ChunkEventHandler getChunkEventHandler() {
        return chunkEventHandler;
    }

    public void loadConfig() {
        this.configProvider.loadConfig();

        //Save config to have a clean config file (add new options or suppress old options / mistake from user)
        try {
            this.configProvider.getConfigLoader().save(this.configProvider.getConfig());
        } catch (Exception e) {
            e.printStackTrace();
            ForgeCreeperHeal.getLogger().error("Unable to save config : " + e.getMessage());
        }

        //Notify listener to update
        this.configProvider.fireConfigChanged();
        //Unload config to free some RAM
        this.configProvider.unloadConfig();
    }

    private DefaultFactory<Block, IBlockDataBuilder> loadBlockDataFactory() {
        final DefaultFactory<Block, IBlockDataBuilder> blockDataFactory = new DefaultFactory<Block, IBlockDataBuilder>(BlockKeyBuilder.getInstance(), new DefaultBlockDataBuilder());
        blockDataFactory.getCustomHandlers().add(new DoorBlockDataBuilder());
        blockDataFactory.getCustomHandlers().add(new BedBlockDataBuilder());
        blockDataFactory.getCustomHandlers().add(new PistonBlockDataBuilder());
        return blockDataFactory;
    }

    private DefaultFactory<Block, IDependencyBuilder> loadDependencyFactory() {
        final DefaultFactory<Block, IDependencyBuilder> dependencyFactory = new DefaultFactory<Block, IDependencyBuilder>(BlockKeyBuilder.getInstance(), new NoDependencyBuilder());
        dependencyFactory.getCustomHandlers().add(new VineDependencyBuilder());
        dependencyFactory.getCustomHandlers().add(new LeverDependencyBuilder());
        dependencyFactory.getCustomHandlers().add(new OppositeFacingDependencyBuilder(BlockTorch.class, new TorchPropertySelector()));
        dependencyFactory.getCustomHandlers().add(new OppositeFacingDependencyBuilder(BlockLadder.class, new LadderPropertySelector()));
        dependencyFactory.getCustomHandlers().add(new OppositeFacingDependencyBuilder(BlockWallSign.class, new WallSignPropertySelector()));
        dependencyFactory.getCustomHandlers().add(new OppositeFacingDependencyBuilder(BlockTrapDoor.class, new TrapDoorPropertySelector()));
        dependencyFactory.getCustomHandlers().add(new OppositeFacingDependencyBuilder(BlockButton.class, new ButtonPropertySelector()));
        dependencyFactory.getCustomHandlers().add(new OppositeFacingDependencyBuilder(BlockBannerHanging.class, new BannerHangingPropertySelector()));
        dependencyFactory.getCustomHandlers().add(new OppositeFacingDependencyBuilder(BlockTripWireHook.class, new TripWireHookPropertySelector()));
        dependencyFactory.getCustomHandlers().add(new FacingDependencyBuilder(BlockCocoa.class, new CocoaPropertySelector()));

        final AbstractGenericDependencyBuilder supportByBottomDependencyBuilder = SupportByBottomDependencyBuilder.getInstance();
        supportByBottomDependencyBuilder.register(BlockFalling.class);
        supportByBottomDependencyBuilder.register(BlockDoor.class);
        supportByBottomDependencyBuilder.register(BlockBasePressurePlate.class);
        supportByBottomDependencyBuilder.register(BlockBannerStanding.class);
        supportByBottomDependencyBuilder.register(BlockRedstoneDiode.class);
        supportByBottomDependencyBuilder.register(BlockRedstoneWire.class);
        supportByBottomDependencyBuilder.register(BlockStandingSign.class);
        supportByBottomDependencyBuilder.register(BlockCrops.class);
        supportByBottomDependencyBuilder.register(BlockCactus.class);
        supportByBottomDependencyBuilder.register(BlockRailBase.class);
        supportByBottomDependencyBuilder.register(BlockReed.class);
        supportByBottomDependencyBuilder.register(BlockSnow.class);
        supportByBottomDependencyBuilder.register(BlockTripWire.class);
        supportByBottomDependencyBuilder.register(BlockCake.class);
        supportByBottomDependencyBuilder.register(BlockCarpet.class);
        supportByBottomDependencyBuilder.register(BlockDragonEgg.class);
        supportByBottomDependencyBuilder.register(BlockFlowerPot.class);
        supportByBottomDependencyBuilder.register(BlockGrass.class);
        supportByBottomDependencyBuilder.register(BlockTallGrass.class);

        dependencyFactory.getCustomHandlers().add(supportByBottomDependencyBuilder);

        return dependencyFactory;
    }

}
