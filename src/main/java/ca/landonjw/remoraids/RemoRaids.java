package ca.landonjw.remoraids;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.BossAPIProvider;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossCreator;
import ca.landonjw.remoraids.implementation.BossAPI;
import ca.landonjw.remoraids.implementation.boss.Boss;
import ca.landonjw.remoraids.implementation.boss.BossCreator;
import ca.landonjw.remoraids.implementation.commands.TestCommand;
import ca.landonjw.remoraids.implementation.listeners.BattleEndListener;
import ca.landonjw.remoraids.implementation.listeners.BossDropListener;
import ca.landonjw.remoraids.implementation.listeners.BossUpdateListener;
import ca.landonjw.remoraids.implementation.listeners.EngageListener;
import ca.landonjw.remoraids.implementation.spawning.TimedSpawnListener;
import ca.landonjw.remoraids.implementation.ui.Base;
import ca.landonjw.remoraids.internal.api.APIRegistrationUtil;
import ca.landonjw.remoraids.internal.config.GeneralConfig;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.config.RestraintsConfig;
import ca.landonjw.remoraids.internal.inventory.api.InventoryAPI;
import ca.landonjw.remoraids.internal.tasks.TaskTickListener;
import ca.landonjw.remoraids.internal.text.Callback;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(
        modid = RemoRaids.MOD_ID,
        name = RemoRaids.MOD_NAME,
        version = RemoRaids.VERSION,
        acceptableRemoteVersions = "*"
)
public class RemoRaids {

    public static final String MOD_ID = "remoraids";
    public static final String MOD_NAME = "RemoRaids";
    public static final String VERSION = "1.0.0";

    public static final EventBus EVENT_BUS = new EventBus();
    public static final Logger logger = LogManager.getLogger(MOD_NAME);

    private static GeneralConfig generalConfig;
    private static RestraintsConfig restraintsConfig;
    private static MessageConfig messageConfig;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        File directory = new File(event.getModConfigurationDirectory(), "remoraids");

        Configuration general = new Configuration(new File(directory.getPath(),
                "General.cfg"));
        generalConfig = new GeneralConfig(general);

        Configuration restraints = new Configuration(new File(directory.getPath(),
                "Restraints.cfg"));
        restraintsConfig = new RestraintsConfig(restraints);

        Configuration messages = new Configuration(new File(directory.getPath(),
                "Messages.cfg"));
        messageConfig = new MessageConfig(messages);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        InventoryAPI.register();

        MinecraftForge.EVENT_BUS.register(new TaskTickListener());
        MinecraftForge.EVENT_BUS.register(new BossUpdateListener());
        RemoRaids.EVENT_BUS.register(new TimedSpawnListener());

        Pixelmon.EVENT_BUS.register(new EngageListener());
        Pixelmon.EVENT_BUS.register(new BossDropListener());
        RemoRaids.EVENT_BUS.register(new BattleEndListener());

        APIRegistrationUtil.register(new BossAPI());
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IBossCreator.class, BossCreator::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IBoss.IBossBuilder.class, Boss.BossBuilder::new);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event){
        event.registerServerCommand(new Base());
        event.registerServerCommand(new Callback());
        event.registerServerCommand(new TestCommand());
    }

    public static IBossAPI getBossAPI(){
        return BossAPIProvider.get();
    }

    public static GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public static MessageConfig getMessageConfig() {
        return messageConfig;
    }

    public static RestraintsConfig getRestraintsConfig() {
        return restraintsConfig;
    }

}